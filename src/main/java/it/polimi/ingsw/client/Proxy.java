package it.polimi.ingsw.client;

import it.polimi.ingsw.client.configurations.adapters.*;
import it.polimi.ingsw.client.configurations.adapters.cli.DicePoolAdapterCLI;
import it.polimi.ingsw.client.configurations.adapters.cli.GridAdapterCLI;
import it.polimi.ingsw.client.configurations.adapters.cli.RoundTrackAdapterCLI;
import it.polimi.ingsw.client.configurations.adapters.cli.ToolCardAdapterCLI;
import it.polimi.ingsw.client.custom_exception.GameFinishedException;
import it.polimi.ingsw.client.custom_exception.InvalidUsernameException;
import it.polimi.ingsw.client.custom_exception.NoDisconnectionException;
import it.polimi.ingsw.client.custom_exception.invalid_operations.InvalidMoveException;
import it.polimi.ingsw.client.custom_exception.invalid_operations.DieNotExistException;
import it.polimi.ingsw.client.custom_exception.invalid_operations.ToolCardNotExistException;
import it.polimi.ingsw.server.custom_exception.NotValidParameterException;
import it.polimi.ingsw.server.model.cards.ToolCard;
import it.polimi.ingsw.server.model.components.Die;
import it.polimi.ingsw.server.model.components.Grid;
import it.polimi.ingsw.server.custom_exception.InvalidOperationException;

import java.util.*;

public class Proxy {

    private static Proxy instance;

    private String myUsername;
    private String turnPlayer;
    private ArrayList<GridInterface> gridsSelection;
    private List<String> playersJustReconnected;
    private GridInterface gridSelected;
    private Map<String,GridInterface> connectedPlayers;
    private Map<String,GridInterface> disconnectedPlayers;
    private Map<String,String> playersRanking;
    private List<ToolCardInterface> toolCards;
    private DicePoolInterface dicePool;
    private RoundTrackInterface roundTrack;
    private boolean gameFinished;
    private boolean useGUI;
    private String turnPlayerDisconnected;
    private boolean isTurnPlayerDisconnected;

    /**
     * Constructor for proxy. All "new<Name>Adapter" methods are used for a GUI.
     */
    private Proxy(){
        gridsSelection= new ArrayList<>();
        connectedPlayers = new LinkedHashMap<>();
        disconnectedPlayers = new LinkedHashMap<>();
        playersRanking = new LinkedHashMap<>();
        toolCards= new ArrayList<>();
        useGUI=false;
        playersJustReconnected = new ArrayList<>();
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

    /**
     * Inserts a die in X,Y.
     *
     * @param position Die position in dicePool.
     * @param row Row coordinate.
     * @param column Column coordinate.
     * @throws InvalidMoveException Thrown if the insertion is not valid.
     * @throws DieNotExistException Thrown if the die selected does not exist.
     */
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

    public synchronized GridInterface getGridSelected() {
        return this.gridSelected;
    }

    public synchronized void setDicePool(List<Die> dicePool) {
        this.dicePool=newDicePoolAdapter(dicePool);
    }

    public synchronized void setGridsForEachPlayer(Map<String, Grid> gridsOfEachPlayer, List<String> connectedPlayersList) {
        connectedPlayersList.stream()
                .filter(username -> !connectedPlayers.isEmpty() && !myUsername.equals(username) && !connectedPlayers.containsKey(username))
                .forEach(username -> playersJustReconnected.add(username));
        Map<String,GridInterface> gridInterfaces= new LinkedHashMap<>();
        gridsOfEachPlayer.forEach((username,grid)-> gridInterfaces.put(username, newGridAdapter(grid)));
        gridSelected = gridInterfaces.remove(myUsername);
        connectedPlayers.entrySet().removeIf(entry->!connectedPlayersList.contains(entry.getKey()));
        gridsOfEachPlayer.forEach(this::setDisconnectedPlayers);
        this.connectedPlayers = gridInterfaces;
    }

    public synchronized void setTurnPlayer(String serverResponse) {
        turnPlayer=serverResponse;
    }

    public synchronized void setGameToFinished() {
        gameFinished=true;
    }

    /**
     * @return True if this is "player"'s turn.
     * @throws GameFinishedException Thrown if the game is already finished.
     */
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

    /**
     * Update current player's grid after an operation.
     *
     * @param grid Current player's grid.
     */
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
            return null;
        }else {
            return new GridAdapterCLI(grid);
        }
    }

    private DicePoolInterface newDicePoolAdapter(List<Die> dicePool){
        if(useGUI){
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
        if(!turnPlayer.equals(myUsername))disconnectedPlayers.put(turnPlayer, connectedPlayers.remove(turnPlayer));
        turnPlayerDisconnected=turnPlayer;
        isTurnPlayerDisconnected=true;
    }

    public synchronized String getDisconnection() throws NoDisconnectionException {
        if(isTurnPlayerDisconnected) return turnPlayerDisconnected;
        else throw new NoDisconnectionException();
    }

    /**
     *
     * @return True if the game is finished.
     */
    public synchronized boolean isGameFinished() {
        return gameFinished;
    }


    public synchronized void setPoints(Map<String,String> playerPoints) {
        playersRanking= playerPoints;
    }

    public synchronized Map<String,String> getPlayerRanking() {
        return this.playersRanking;
    }

    public synchronized void setRoundTrack(List<Die> roundTrack) {
        this.roundTrack= newRoundTrackAdapter(roundTrack);
    }

    private RoundTrackInterface newRoundTrackAdapter(List<Die> roundTrack) {
        if(useGUI){
            // TODO: 29/06/2018
            return null;
        }else{
            return new RoundTrackAdapterCLI(roundTrack);
        }
    }

    public synchronized RoundTrackInterface getRoundTrack() {
        return this.roundTrack;
    }

    public synchronized void setToolCards(List<ToolCard> toolCards) {
        List<ToolCardInterface> toSet= new ArrayList<>();
        for(ToolCard toolCard: toolCards){
            toSet.add(newToolCardAdapter(toolCard));
        }
        this.toolCards=toSet;
    }

    private ToolCardInterface newToolCardAdapter(ToolCard toolCard){
        if(useGUI){
            return null;
        }else {
            return new ToolCardAdapterCLI(toolCard);
        }
    }

    public synchronized List<ToolCardInterface> getToolCards(){
        return this.toolCards;
    }

    public synchronized ToolCardInterface getToolCard(int index) throws ToolCardNotExistException {
        try{
            return toolCards.get(index);
        }catch (IndexOutOfBoundsException e){
            throw new ToolCardNotExistException();
        }
    }


    private void setDisconnectedPlayers(String username, Grid grid) {
        if (!username.equals(myUsername) && !connectedPlayers.containsKey(username))
            disconnectedPlayers.put(username, newGridAdapter(grid));
    }
}
