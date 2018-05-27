package it.polimi.ingsw.serverPart;


import it.polimi.ingsw.serverPart.custom_exception.DisconnectionException;
import it.polimi.ingsw.serverPart.custom_exception.InvalidOperationException;
import it.polimi.ingsw.serverPart.custom_exception.InvalidUsernameException;
import it.polimi.ingsw.serverPart.custom_exception.ReconnectionException;
import it.polimi.ingsw.serverPart.netPart_container.UserInterface;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class MatchHandler extends Thread {

    private static MatchHandler instance;

    private static Map<String, MatchController> connectedPlayers;
    private static final Object connectedPlayersGuard= new Object();
    private static ArrayList<UserInterface> disconnectedInGamePlayers;
    private static MatchController startingMatch;
    private static final Object startingMatchGuard= new Object();
    private static ArrayList<MatchController> startedMatches; //to handle multi-game.
    private static final Object startedMatchesGuard= new Object();
    private static Lock lock;
    private static Condition condition;


    private boolean timeout;
    private static int maximumMatchNumber =2;
    private static final int MINIMUM_PLAYER_FOR_A_GAME =2;
    private GameTimer timer;
    private boolean shutdown;


    //these color are used to highlight server log message and they are
    //not supposed in any way to be part of the view.
    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_GREEN = "\u001B[32m";
    private static final String ANSI_YELLOW = "\u001B[33m";
    private static final String ANSI_BLUE = "\u001B[34m";
    private static final String ANSI_PURPLE = "\u001B[35m";
    private static final String ANSI_CYAN = "\u001B[36m";

    public static MatchHandler getInstance(){
        if(instance==null) {
            instance = new MatchHandler();
            connectedPlayers= new HashMap<>();
            lock= new ReentrantLock();
            condition= lock.newCondition();
            startedMatches= new ArrayList<>();
            disconnectedInGamePlayers= new ArrayList<>();
            instance.timeout=true;
            instance.shutdown= false;
        }
        return instance;
    }

    public static void setPlayerInGame(String username, MatchController game) {
        synchronized (connectedPlayersGuard){
            connectedPlayers.put(username, game);
        }
    }


    //Observer
    public int connectedPlayers(){
        synchronized (connectedPlayersGuard) {
            return connectedPlayers.size();
        }
    }


    public int getMaximumMatchNumber(){
        return maximumMatchNumber;
    }


    //

    public static void notifyTimeout() {
        instance.timeout=true;
        lock.lock();
        condition.signal();
        lock.unlock();
    }

    public static void notifyMatchCanStart() {
        lock.lock();
        condition.signal();
        lock.unlock();
    }

    @Override
    public void run() {
        System.out.println("MatchHandlerStarted");
        while (!shutdown) {
            boolean ok;
            MatchHandler.loadNewGame();
            do{
                ok= setUpPhase();
            }while (!ok);
            timeout=true;
            do {
                ok=startGameCountdown();
            }
            while (!ok);
            while(startedMatches.size()==maximumMatchNumber){
                lock.lock();
                try {
                    condition.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    Thread.currentThread().interrupt();
                }
                lock.unlock();
            }
        }
    }

    public static void loadNewGame(){
        System.out.println(ANSI_GREEN +"A new match is ready to be handled." +ANSI_RESET);
        startingMatch = new MatchController();
        startingMatch.start();
    }

    public boolean setUpPhase(){
        boolean result;
        try {
            lock.lock();
            condition.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        }finally {
            lock.unlock();
        }
        synchronized (startingMatchGuard){
            result =(startingMatch.playerInGame()>= MINIMUM_PLAYER_FOR_A_GAME);
        }
        return result;
    }

    //@requires timeout=true (*at first execution*);
    private boolean startGameCountdown() {
        lock.lock();
        try {
            if(instance.timeout){
                System.out.println("A game will start soon...");
                timer =new GameTimer("game");
                timeout=false;
            }
            synchronized (startingMatchGuard){
                startingMatch.setGameStartingSoon(timeout);
            }
            condition.await();

            System.out.println("Resumed. Timeout: "+instance.timeout);
            synchronized (startingMatchGuard) {
                synchronized (startedMatchesGuard) {
                    if (startingMatch.playerInGame() == 4) {
                        startingMatch.setGameToStarted();
                        startedMatches.add(startingMatch);
                        startingMatch = null;
                        timer.stop();
                        return true;
                    } else if (instance.timeout && startingMatch.playerInGame() > 1) {
                        startingMatch.setGameToStarted();
                        instance.timeout = false;
                        startedMatches.add(startingMatch);
                        startingMatch = null;
                        timer.stop();
                        return true;

                    }
                    else if (instance.timeout && startingMatch.playerInGame() <= 1){
                        timer.stop();
                    }
                }
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();

        } finally {
            lock.unlock();
        }
        return false;
    }


    public static void login(UserInterface client) throws InvalidOperationException, DisconnectionException {
        client.chooseUsername();
        boolean ok;
        int trial =0;
        do {
            try {
                trial++;
                client.arrangeForUsername(trial);
                lock.lock();
                synchronized (startingMatchGuard) {
                    startingMatch.insert(client);
                    //TODO remove game code. Now should be useless.
                    client.setGameCode(instance.startedMatches.size()+1);
                }
                condition.signal();
                lock.unlock();
                ok=true;
            }
            catch (InvalidUsernameException e){
                ok=false;
            }
            catch (ReconnectionException e){
                ok=true;
                //TODO handle this case.
            }
        }
        while (!ok);

        System.out.println(ANSI_BLUE +client.getUsername()+ " connected successfully." + ANSI_RESET);
    }

    public void requestUsername(String username) throws InvalidOperationException, ReconnectionException, InvalidUsernameException {
        if(username.equals("")) throw new InvalidUsernameException();
        synchronized (startingMatchGuard) {
            synchronized (disconnectedInGamePlayers) {
                if (startingMatch == null) {
                    //FIXME, disconnected list won't probably be an ArrayList of ClientInterfaces
                    for (UserInterface cl : disconnectedInGamePlayers) {
                        if (cl.getUsername() == username) {
                            throw new ReconnectionException();
                        }
                    }
                    throw new InvalidOperationException();
                }
                else
                    startingMatch.updateQueue();
            }

            synchronized (connectedPlayersGuard) {
                if (connectedPlayers.containsKey(username)) {
                    throw new InvalidUsernameException();
                }
                connectedPlayers.put(username, null);
            }
        }

    }


    public void notifyAboutDisconnection(String username) {
        MatchController gameHandlingPlayer;
        synchronized (connectedPlayersGuard) {
            gameHandlingPlayer=connectedPlayers.remove(username);
        }
        if (gameHandlingPlayer == null) {
            //I don't have to do nothing. Players wasn't in a started game so he should be just removed from connected players
        } else {
            //TODO handle this case: player should be inserted in disconnected players.
        }

    }

    public void logOut(UserInterface client) throws InvalidOperationException {
        String username= client.getUsername();
        MatchController gameHandlingClient;
        synchronized (connectedPlayersGuard){
            gameHandlingClient = connectedPlayers.remove(username);
        }
        synchronized (startingMatchGuard) {
            synchronized (startedMatchesGuard) {
                if (gameHandlingClient!=null) /*TODO handle this*/ ;
                else
                    startingMatch.remove(client);
            }
        }
        System.out.println(ANSI_PURPLE+ username + " logged out."+ ANSI_RESET);

    }
}
