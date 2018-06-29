package it.polimi.ingsw.client;

import it.polimi.ingsw.client.configurations.adapters.DicePoolAdapterCLI;
import it.polimi.ingsw.client.configurations.adapters.DicePoolInterface;
import it.polimi.ingsw.client.configurations.adapters.GridAdapterCLI;
import it.polimi.ingsw.client.configurations.adapters.GridInterface;
import it.polimi.ingsw.client.custom_exception.GameFinishedException;
import it.polimi.ingsw.client.custom_exception.InvalidUsernameException;
import it.polimi.ingsw.client.custom_exception.invalid_operations.InvalidMoveException;
import it.polimi.ingsw.client.custom_exception.invalid_operations.DieNotExistException;
import it.polimi.ingsw.server.custom_exception.NotValidParameterException;
import it.polimi.ingsw.server.model.components.Die;
import it.polimi.ingsw.server.model.components.Grid;
import it.polimi.ingsw.server.custom_exception.InvalidOperationException;

import java.util.*;

public class Proxy {

    private static Proxy instance;

    private String myUsername;
    private String turnPlayer;
    private ArrayList<GridInterface> gridsSelection;
    private GridInterface gridSelected;
    private Map<String,GridInterface> connectedPlayers;
    private Map<String,GridInterface> disconnectedPlayers;
    private Map<String,String> playersRanking;
    private DicePoolInterface dicePool;
    private boolean gameFinished;
    private boolean useGUI;

    private Proxy(){
        gridsSelection= new ArrayList<>();
        connectedPlayers = new LinkedHashMap<>();
        disconnectedPlayers = new LinkedHashMap<>();
        playersRanking = new LinkedHashMap<>();
        useGUI=false;
    }

    public static synchronized Proxy getInstance(){
        if(instance==null) instance= new Proxy();
        return instance;
    }

    public synchronized void setMyUsername(String username) {
        this.myUsername=username;
    }

    public synchronized void setGridsSelection(List<Grid> gridsSelection) throws InvalidOperationException {
        if (!this.gridsSelection.isEmpty()) throw new InvalidOperationException();   //thrown in case grids have already been chosen
        for(Grid thisGrid:gridsSelection){
            this.gridsSelection.add(newGridAdapter(thisGrid));
        }
    }

    public synchronized List<GridInterface> getGridsSelection() {
        return gridsSelection;
    }

    public synchronized int getGridsSelectionDimension() {
        return gridsSelection.size();
    }

    public synchronized void tryToInsertDieInXY(int position, int row, int column) throws InvalidMoveException, DieNotExistException {
        final boolean checkColorConstraint= true;
        final boolean checkValueConstraint= true;
        Die die;
        try {
            die=dicePool.getDie(position);
        }catch (IndexOutOfBoundsException e){
            throw new DieNotExistException();
        }
        try {
            gridSelected.insertDieInXY(row, column, checkColorConstraint, checkValueConstraint, die);
        } catch (InvalidOperationException | NotValidParameterException e) {
            throw new InvalidMoveException();
        }
    }

    public synchronized void updateGridSelected(Grid grid) {
        this.gridSelected=newGridAdapter(grid);
    }

    public synchronized GridInterface getGridSelected() {
        return this.gridSelected;
    }

    public synchronized void setDicePool(List<Die> dicePool) {
        this.dicePool=newDicePoolAdapter(dicePool);
    }

    public synchronized void setGridsForEachPlayer(Map<String,Grid> gridsOfEachPlayer) {
        Map<String,GridInterface> gridInterfaces= new LinkedHashMap<>();
        gridsOfEachPlayer.forEach((username,grid)-> gridInterfaces.put(username, newGridAdapter(grid)));
        gridSelected = gridInterfaces.remove(myUsername);
        connectedPlayers = gridInterfaces;
        //todo, if there's a void grid put player into disconnected
    }

    public synchronized void setTurnPlayer(String serverResponse) {
        turnPlayer=serverResponse;
    }

    public synchronized void setGameToFinished() {
        gameFinished=true;
    }

    public synchronized boolean askIfItsMyTurn() throws GameFinishedException {
        if(gameFinished) throw new GameFinishedException();
        return myUsername.equals(turnPlayer);
    }

    public synchronized String getTurnPlayer() {
        return turnPlayer;
    }

    public synchronized String getMyUsername() {
        return myUsername;
    }

    public synchronized void updateGrid(Grid grid) {
        if(turnPlayer.equals(myUsername)){
            gridSelected= newGridAdapter(grid);
        }else{
            connectedPlayers.put(turnPlayer, newGridAdapter(grid));
        }
    }

    public synchronized DicePoolInterface getDicePool() {
        return this.dicePool;
    }

    private GridInterface newGridAdapter(Grid grid){
        if(useGUI){
            // TODO: 27/06/2018
            return null;
        }else {
            return new GridAdapterCLI(grid);
        }
    }

    private DicePoolInterface newDicePoolAdapter(List<Die> dicePool){
        if(useGUI){
            // TODO: 27/06/2018
            return null;
        }else {
            return new DicePoolAdapterCLI(dicePool);
        }
    }

    public synchronized GridInterface getGridsOf(String turnPlayer) throws InvalidUsernameException {
        if(!connectedPlayers.containsKey(turnPlayer)) throw new InvalidUsernameException();
        return connectedPlayers.get(turnPlayer);
    }

    public synchronized void setPlayerToDisconnected() {
        disconnectedPlayers.put(turnPlayer, connectedPlayers.remove(turnPlayer));
    }

    public synchronized boolean isGameFinished() {
        return gameFinished;
    }


    public void setPoints(Map<String,String> playerPoints) {
        playersRanking= playerPoints;
    }

    public Map<String,String> getPlayerRanking() {
        return this.playersRanking;
    }
}
