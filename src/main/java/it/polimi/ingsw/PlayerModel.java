package it.polimi.ingsw;

import it.polimi.ingsw.CustomException.NotProperParameterException;
import it.polimi.ingsw.cardContainer.PrivateObjective;

public class PlayerModel{

    //gestiti da PlayerHandler
    private boolean connected;
    private String username;
    private String password;
    private long id;
    private boolean inQueue;
    private boolean inGame;

    //gestiti da PlayerController
//    private Grid gameGrid;  //TODO make a setter to initialize grid.
    private PrivateObjective privateObjective;
//    private MatchModel game;
    private int points;


    public PlayerModel(String username, String password) throws NotProperParameterException {
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

//    public int calculatePrivateObjectivePoints(String color) throws NotProperParameterException{
//        return this.gameGrid.calcPrivateObjPoints(color);
//    }


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




    //

    private void throwDiceForPool(){
        //TODO
    }

    public void useToolCard(){
        //TODO
    }



}
