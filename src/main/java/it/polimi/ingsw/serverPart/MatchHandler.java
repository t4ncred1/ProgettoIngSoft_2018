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

    private static ArrayList<UserInterface> connectedPlayers;
    private static ArrayList<UserInterface> disconnectedInGamePlayers;
    private static MatchController startingMatch;
    private final static Object startingMatchGuard= new Object();
    private static Lock lock;
    private static Condition condition;


    private boolean timeout;
    private final int maximumMatchNumber =1;
    private final int minimumPlayerForAGame =2;
    private ArrayList<MatchController> startedMatches; //nel caso volessimo implementare multi-game
    private GameTimer timer;
    private static long currentGame=0;

    private MatchHandler(){
        connectedPlayers= new ArrayList<UserInterface>();
        lock= new ReentrantLock();
        condition= lock.newCondition();
        startedMatches= new ArrayList<MatchController>();
        disconnectedInGamePlayers= new ArrayList<UserInterface>();
        timeout=true;
    }

    public static MatchHandler getInstance(){
        if(instance==null)
            instance= new MatchHandler();
        return instance;
    }

    public static void notifyMatchCanStart() {
        lock.lock();
        condition.signal();
        lock.unlock();
    }


    //Observer
    public int connectedPlayers(){
        synchronized (connectedPlayers) {
            return connectedPlayers.size();
        }
    }

    public int notSynchronizedConnectedPlayers(){  //use only fot tests
        return connectedPlayers.size();
    }

    public int getMaximumMatchNumber(){
        return this.maximumMatchNumber;
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
        while (true) {
            boolean ok;
            startingMatch = new MatchController();
            do{
                ok= setUpPhase();
            }while (!ok);
            timeout=true;
            do {
                ok=startGameCountdown();
            }
            while (!ok);
            startGame(currentGame);
            while(startedMatches.size()==maximumMatchNumber){
                lock.lock();
                try {
                    condition.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    //FIXME
                    Thread.currentThread().interrupt();
                }
                lock.unlock();
            }
        }
    }

    public boolean setUpPhase(){
        boolean result;
        try {
            lock.lock();
            condition.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
            //FIXME
            Thread.currentThread().interrupt();
        }finally {
            lock.unlock();
        }
        synchronized (startingMatchGuard){
            result =(startingMatch.playerIngame()>=minimumPlayerForAGame);
        }
        return result;
    }

    //@requires timeout=true (*at first execution*);
    private boolean startGameCountdown() {
        lock.lock();
        try {
            if(instance.timeout){
                System.out.println("A game will start soon...");
                synchronized (startingMatch){
                    startingMatch.setGameStartingSoon();
                }
                timer =new GameTimer("game");
                timeout=false;
            }
            condition.await();

            System.out.println("Resumed. Timeout: "+instance.timeout);
            synchronized (startingMatchGuard) {
                synchronized (startedMatches) {
                    if (startingMatch.playerIngame() == 4) {
                        startingMatch.setGameToStarted();
                        startedMatches.add(startingMatch);
                        startingMatch = null;
                        timer.stop();
                        return true;
                    } else if (instance.timeout && startingMatch.playerIngame() > 1) {
                        startingMatch.setGameToStarted();
                        instance.timeout = false;
                        startedMatches.add(startingMatch);
                        startingMatch = null;
                        timer.stop();
                        return true;

                    }
                    else if (instance.timeout && startingMatch.playerIngame() <= 1){
                        timer.stop();
                    }
                }
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
            //FIXME
            Thread.currentThread().interrupt();

        } finally {
            lock.unlock();
        }
        return false;
    }

    private void startGame(long currentGame) {
        System.out.println("Game Started");
        //TODO notify players that the game is started
    }


    public static void login(UserInterface client) throws InvalidOperationException, DisconnectionException {
        client.chooseUsername();
        boolean ok;
        int trial =0;
        do {
            try {
                trial++;
                synchronized (connectedPlayers) {
                    client.arrangeForUsername(trial);
                    connectedPlayers.add(client);
                }
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
                if (startingMatch==null) {
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
                for (UserInterface cl : connectedPlayers) {
                    if (cl.getUsername().equals(username)) {
                        throw new InvalidUsernameException();
                    }
                }
            }
        }

    }

    //this method should be invoked when the lock on "connectedPlayers" is already acquired
    public void notifyAboutDisconnection(UserInterface client, boolean gameStarted) {
        if(gameStarted){
            //TODO handle this case.
        }
        else {
            connectedPlayers.remove(client);
        }
    }

    public void logOut(UserInterface client) throws InvalidOperationException {
        int gameCode;
        synchronized (connectedPlayers){
            gameCode= client.getGameCode();
        }
        synchronized (startingMatchGuard) {
            synchronized (startedMatches) {
                if (gameCode <= startedMatches.size()) /*TODO handle this*/ ;
                else
                    startingMatch.remove(client);
            }
        }
        synchronized (connectedPlayers){
            connectedPlayers.remove(client);
        }


    }
}
