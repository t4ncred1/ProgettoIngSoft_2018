package it.polimi.ingsw;

import it.polimi.ingsw.customException.NotProperParameterException;
import it.polimi.ingsw.cardContainer.PrivateObjective;

public class PlayerData {

    //gestiti da PlayerHandler
    private boolean connected;
    private String username;
    private String password;
    private long id;
    private boolean inQueue;
    private boolean inGame;

    public PlayerData(String username, String password) throws NotProperParameterException {
        if (username == null || password.equals("")) throw new NotProperParameterException("username : null or empty.", "a not null string.");
        if (password == null || password.equals("")) throw new NotProperParameterException("username : null or empty.", "a not null string.");
        this.connected=false;
        this.username=username;
        this.password=password;
    }


    //Osservatori
    public boolean isConnected() {
        return connected;
    }

    public String getPassword() {
        return password;
    }

    public String getUsername() {
        return username;
    }

    public boolean isInQueue() {
        return this.inQueue;
    }


    //modificatori
    public synchronized void setConnected(boolean connected) {
        this.connected = connected;
    }

    public synchronized void setInQueue(boolean inQueue) {
        this.inQueue= inQueue;
    }

    public synchronized void setInGame(boolean inGame) {
        this.inGame= inGame;
    }




}
