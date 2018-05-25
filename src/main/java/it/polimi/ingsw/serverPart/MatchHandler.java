package it.polimi.ingsw.serverPart;


import it.polimi.ingsw.serverPart.custom_exception.DisconnectionException;
import it.polimi.ingsw.serverPart.custom_exception.InvalidOperationException;
import it.polimi.ingsw.serverPart.custom_exception.InvalidUsernameException;
import it.polimi.ingsw.serverPart.custom_exception.ReconnectionException;
import it.polimi.ingsw.serverPart.netPart_container.UserInterface;

import java.util.ArrayList;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class MatchHandler extends Thread {

    private static MatchHandler instance;

    private static ArrayList<String> connectedPlayers;
    private static final Object connectedPlayersGuard= new Object();
    private static ArrayList<UserInterface> disconnectedInGamePlayers;
    private static MatchController startingMatch;
    private static final Object startingMatchGuard= new Object();
    private static ArrayList<MatchController> startedMatches; //nel caso volessimo implementare multi-game
    private static final Object startedMatchesGuard= new Object();
    private static Lock lock;
    private static Condition condition;


    private boolean timeout;
    private static int maximumMatchNumber =2;
    private static final int MINIMUM_PLAYER_FOR_A_GAME =2;
    private GameTimer timer;
    private boolean shutdown;

    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";

    public static MatchHandler getInstance(){
        if(instance==null) {
            instance = new MatchHandler();
            connectedPlayers= new ArrayList<>();
            lock= new ReentrantLock();
            condition= lock.newCondition();
            startedMatches= new ArrayList<>();
            disconnectedInGamePlayers= new ArrayList<>();
            instance.timeout=true;
            instance.shutdown= false;
        }
        return instance;
    }

    public static void notifyMatchCanStart() {
        lock.lock();
        condition.signal();
        lock.unlock();
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
        System.out.println(ANSI_GREEN +"A new match is ready to been handled." +ANSI_RESET);
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

        System.out.println("Player inserted");
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
                for (String connectedUsername : connectedPlayers) {
                    if (connectedUsername.equals(username)) {
                        throw new InvalidUsernameException();
                    }
                }
                connectedPlayers.add(username);
            }
        }

    }

    //this method should be invoked when the lock on "connectedPlayers" is already acquired
    public void notifyAboutDisconnection(UserInterface client, boolean gameStarted) {
        if(gameStarted){
            //TODO handle this case.
        }
        else {
            connectedPlayers.remove(client.getUsername());
        }
    }

    public void logOut(UserInterface client) throws InvalidOperationException {
        int gameCode;
        synchronized (connectedPlayersGuard){
            gameCode= client.getGameCode();
        }
        synchronized (startingMatchGuard) {
            synchronized (startedMatchesGuard) {
                if (gameCode <= startedMatches.size()) /*TODO handle this*/ ;
                else
                    startingMatch.remove(client);
            }
        }
        synchronized (connectedPlayersGuard){
            connectedPlayers.remove(client.getUsername());
        }
        System.out.println(client.getUsername() + " disconnected.");

    }
}
