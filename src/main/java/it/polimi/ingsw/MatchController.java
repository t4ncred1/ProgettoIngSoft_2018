package it.polimi.ingsw;

import java.util.ArrayList;
import java.util.Iterator;

public class MatchController extends Thread{
    private ArrayList<ClientInterface> playersInMatch;
    private boolean gameStarted;

    public MatchController(){
        this.playersInMatch= new ArrayList<ClientInterface>();
    }

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
                    MatchHandler.getInstance().notifyAboutDisconnection(playersInMatch.remove(i), this.gameStarted);
                }
            }
        }
    }

    public void insert(ClientInterface client) {
        synchronized (playersInMatch) {
            playersInMatch.add(client);
        }
    }
}
