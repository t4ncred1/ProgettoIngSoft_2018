package it.polimi.ingsw;

import java.util.ArrayList;
import java.util.Iterator;

public class MatchController {
    private ArrayList<ClientInterface> playersInMatch;

    public MatchController(){
        this.playersInMatch= new ArrayList<ClientInterface>();
    }


    public int playerIngame() {

           for (int i=0; i<playersInMatch.size();i++) {
               if(!playersInMatch.get(i).isConnected()){
                   playersInMatch.remove(i);
               }
            }
            System.out.println("player in queue: "+playersInMatch.size());
            return playersInMatch.size();

    }

    public void insert(ClientInterface client) {
        synchronized (playersInMatch) {
            playersInMatch.add(client);
        }
    }
}
