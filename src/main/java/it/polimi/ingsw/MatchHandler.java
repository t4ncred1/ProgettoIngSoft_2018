package it.polimi.ingsw;

import it.polimi.ingsw.customException.InvalidOperationException;

import java.util.ArrayList;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class MatchHandler extends Thread {

    private static MatchHandler instance;

    private static ArrayList<ClientInterface> connectedPlayers;
    private static ArrayList<ClientInterface> disconnectedInGamePlayers;
    private static MatchController startingMatch;
    private static Lock lock;
    private static Condition condition;


    private boolean timeout;
    private final int maximumMatchNumber =1;
    private ArrayList<MatchController> startedMatches; //nel caso volessimo implementare multi-game
    private GameTimer timer;

    private MatchHandler(){
        connectedPlayers= new ArrayList<ClientInterface>();
        lock= new ReentrantLock();
        condition= lock.newCondition();
        startedMatches= new ArrayList<MatchController>();
        disconnectedInGamePlayers= new ArrayList<ClientInterface>();
        timeout=true;
    }

    public static MatchHandler getInstance(){
        if(instance==null)
            instance= new MatchHandler();
        return instance;
    }

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
            startingMatch = new MatchController();
            while (startingMatch.playerIngame()<2){
                try {
                    lock.lock();
                    condition.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }finally {
                    lock.unlock();
                }


            }
            boolean ok;
            timeout=true;
            do {
                ok=startGameCountdown();
            }
            while (!ok);
            startGame();
            while(startedMatches.size()==maximumMatchNumber){
                lock.lock();
                try {
                    condition.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                lock.unlock();
            }
        }
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
            condition.await();

            System.out.println("Resumed. Timeout: "+instance.timeout);
            if(startingMatch.playerIngame()==4){
                it.polimi.ingsw.SocketHandler.closeConnection();
                timer.stop();
                return true;
            }
            else if(instance.timeout&&startingMatch.playerIngame()>1) {
                instance.timeout=false;
                timer.stop();
                return true;

            }

        } catch (InterruptedException e) {
            e.printStackTrace();

        } finally {
            lock.unlock();
        }
        return false;
    }

    private void startGame() {
        System.out.println("Game Started");
        startedMatches.add(startingMatch);
        startingMatch=null;
    }


    public static void login(ClientInterface client) throws InvalidOperationException {
        client.chooseUsername();
        synchronized (connectedPlayers) {
            client.arrangeForUsername();
            connectedPlayers.add(client);
        }
        lock.lock();
        startingMatch.insert(client);
        condition.signal();
        lock.unlock();
        System.out.println("Player inserted");
    }

    public boolean requestUsername(String username) {
        if(username.equals("")) return false;
        System.out.println("size " +connectedPlayers.size());
        for(ClientInterface cl: connectedPlayers){
            if(cl.getUsername().equals(username)) {
                System.out.println("Cheked with: " + cl.getUsername());
                return false;
            }
        }
        return true;
    }

    public void tryToConnect(String username) throws InvalidOperationException {
        if(startedMatches.size()==maximumMatchNumber){
            for(ClientInterface cl: disconnectedInGamePlayers){
                if(cl.getUsername()==username)
                    return;
            }
            throw new InvalidOperationException();
        }

        return;
    }
}
