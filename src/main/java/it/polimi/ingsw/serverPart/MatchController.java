package it.polimi.ingsw.serverPart;

import it.polimi.ingsw.serverPart.custom_exception.DisconnectionException;
import it.polimi.ingsw.serverPart.custom_exception.InvalidOperationException;
import it.polimi.ingsw.serverPart.netPart_container.UserInterface;

import java.util.ArrayList;

public class MatchController extends Thread{
    private ArrayList<UserInterface> playersInMatch;
    private boolean gameStarted;
    private boolean gameStartingSoon;

    public MatchController(){
        this.playersInMatch= new ArrayList<UserInterface>();
    }

    @Override
    public void run(){
        while(!gameStarted){
            updateQueue();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                e.printStackTrace();
            }
        }

        System.err.println("Exit.");



        //TODO handle the game logic from now on.

    }


    public int playerIngame() {

            System.out.println("player in queue: "+playersInMatch.size());
            return playersInMatch.size();

    }

    public void updateQueue(){
        synchronized (playersInMatch){
            for (int i=0; i<playersInMatch.size();i++) {
                if(!playersInMatch.get(i).isConnected()){
                    //in this instruction player is removed both from playersInMatch and connectedPlayers
                    System.err.println("-------------------------------------------");
                    MatchHandler.getInstance().notifyAboutDisconnection(playersInMatch.remove(i), this.gameStarted);
                    System.err.println("-------------------------------------------");
                }
            }
            if(playersInMatch.size()>1&&!this.gameStartingSoon) MatchHandler.notifyMatchCanStart();
        }
    }

    public void insert(UserInterface client) {
        synchronized (playersInMatch) {
            playersInMatch.add(client);
        }
    }

    //state Observer
    public boolean isGameStartingSoon() {
        return gameStarted;
    }

    //state modifier
    public void setGameStartingSoon() {
        final boolean startingSoonState =true;
        this.gameStartingSoon = startingSoonState;
        this.notifyStartingSoonToPlayers();
    }

    public void remove(UserInterface client) throws InvalidOperationException {
        if(gameStartingSoon) throw new InvalidOperationException();
        else {
            synchronized (playersInMatch) {
                playersInMatch.remove(client);
                MatchHandler.getInstance().notifyAboutDisconnection(client, this.gameStarted);
            }
        }
    }


    private void notifyStartingSoonToPlayers() {
        synchronized (playersInMatch){
            for(int i=0; i<playersInMatch.size();i++){
                try {
                    playersInMatch.get(i).notifyStarting();
                } catch (DisconnectionException e) {
                    MatchHandler.getInstance().notifyAboutDisconnection(playersInMatch.remove(i), this.gameStarted);
                    e.printStackTrace();
                }
            }
        }
    }

    private void notifyStartToPlayers() {
        synchronized (playersInMatch){
            for(UserInterface cl: playersInMatch){
                try {
                    cl.notifyStart();
                } catch (DisconnectionException e) {
                    //FIXME if necessary
                    MatchHandler.getInstance().notifyAboutDisconnection(cl, this.gameStarted);
                    e.printStackTrace();
                }
            }
        }
    }

    public void setGameToStarted() {
        final boolean gameStartedStatus =true;
        final boolean gameIsNotStartingAnymore = false;

        this.gameStartingSoon=gameIsNotStartingAnymore;
        this.gameStarted=gameStartedStatus;
        this.notifyStartToPlayers();
    }
}
