package it.polimi.ingsw.server.model;


import it.polimi.ingsw.server.MatchController;
import it.polimi.ingsw.server.model.cards.PrivateObjective;
import it.polimi.ingsw.server.model.cards.PublicObjective;
import it.polimi.ingsw.server.model.cards.ToolCard;
import it.polimi.ingsw.server.model.components.*;
import it.polimi.ingsw.server.configurations.ConfigurationHandler;
import it.polimi.ingsw.server.custom_exception.*;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class MatchModel{

    private int MAX_PLAYERS_NUMBER =0;
    private int MIN_PLAYERS_NUMBER =0;

    private ArrayList<PrivateObjective> privateObjectives;
    private List<Grid> grids;
    private List<ToolCard> toolCards;
    private List<PublicObjective> publicObjectives;
    private MatchController controller;

    private ArrayList<Die> roundTrack;
    private DicePool matchDicePool;
    private Player currentPlayer;
    private ArrayList<Player> playersInGame;

    private PlayersIterator iterator;

    private final int GRIDS_FOR_A_PLAYER =4;
    private final int PRIV_FOR_A_PLAYER=1;
    private static final String DISCONNECTED_STATUS ="disconnected";

    private static final String GREEN_OBJ= "green";
    private static final String RED_OBJ= "red";
    private static final String YELLOW_OBJ= "yellow";
    private static final String BLUE_OBJ= "blue";
    private static final String PURPLE_OBJ= "purple";

    /**
     * Constructor for MatchModel.
     *
     * @param playersUserNames Players in a match.
     * @throws NotValidParameterException Thrown when the number of players is not between 2 and 4 (minimum and maximum number of players).
     *                                      or when colors of private objectives is not one of the admitted ones
     * @throws NotValidConfigPathException Thrown when configurations can't get the configurations from a file
     */
    public MatchModel(Set<String> playersUserNames) throws NotValidParameterException, NotValidConfigPathException{

        if (playersUserNames==null) throw new NullPointerException();
        //initialize game parameters
        getGameParametersFromConfig();
        if (playersUserNames.size()< MIN_PLAYERS_NUMBER ||playersUserNames.size()> MAX_PLAYERS_NUMBER) throw new NotValidParameterException("Number of players in game: "+Integer.toString(playersUserNames.size()),"Between 2 and "+Integer.toString(MAX_PLAYERS_NUMBER));
        //components initialization
        initializeGameComponents();
        //player initialization:
        playersInGame= new ArrayList<>();
        playersUserNames.forEach(this::createPlayerData);
        //initializing round
        initializeRound();
    }

    /**
     * This method is used to initialize game components and cards
     *
     * @throws NotValidConfigPathException Thrown when configurations can't get the configurations from a file
     * @throws NotValidParameterException Thrown when colors of private objectives is not one of the admitted ones
     */
    private void initializeGameComponents() throws NotValidConfigPathException, NotValidParameterException {
        //initialize round track
        roundTrack=new ArrayList<>();
        //initialize grids
        grids=ConfigurationHandler.getInstance().getGrids();
        //initialize public objectives
        publicObjectives= selectPublicObjectives();
        //selecting 3 tool cards
        toolCards= selectToolCards();
        //setting model in tool cards
        toolCards.forEach(toolCard -> toolCard.setModel(this));
        //initialize private objectives
        initializePrivateObjective();
        //dice pool initialization
        matchDicePool = new DicePool();
    }

    /**
     * Used to get match configurations parameters
     */
    private void getGameParametersFromConfig() {
        try {
            MAX_PLAYERS_NUMBER =ConfigurationHandler.getInstance().getMaxPlayersNumber();
        } catch (NotValidConfigPathException e) {
            Logger.getLogger(this.getClass().getName()).log(Level.CONFIG,"Failed retrieving of Max Players Number");
        }
        try {
            MIN_PLAYERS_NUMBER =ConfigurationHandler.getInstance().getMinPlayersNumber();
        } catch (NotValidConfigPathException e) {
            Logger.getLogger(this.getClass().getName()).log(Level.CONFIG,"Failed retrieving of Min Players Number");
        }
    }

    /**
     * Used to initialize private objectives
     *
     * @throws NotValidParameterException when colors of private objectives is not one of the admitted ones
     */
    private void initializePrivateObjective() throws NotValidParameterException {
        privateObjectives = new ArrayList<>();
        privateObjectives.add(new PrivateObjective(GREEN_OBJ));
        privateObjectives.add(new PrivateObjective(RED_OBJ));
        privateObjectives.add(new PrivateObjective(PURPLE_OBJ));
        privateObjectives.add(new PrivateObjective(YELLOW_OBJ));
        privateObjectives.add(new PrivateObjective(BLUE_OBJ));

    }

    /**
     * Given a player's username create the relative data structure
     *
     * @param username of a player in this match
     */
    private void createPlayerData(String username) {
        Player playerToAdd = new Player(username);
        try {
            playerToAdd.setObjective(selectPrivateObjective());
            playerToAdd.setGridsSelection(selectGridsForPlayer());
        } catch (InvalidOperationException e) {
            Logger logger = Logger.getLogger(this.getClass().getName());
            logger.log(Level.WARNING, "Error while creating a new player.", e);
        }
        playersInGame.add(playerToAdd);
    }

    /**
     * Constructor for MatchModel (with a controller reference).
     *
     * @param players Players in a match.
     * @param controller Controller associated.
     * @throws NotValidConfigPathException Thrown when the method can't get minimum and maximum number of players because of an invalid configuration path.
     * @throws NotValidParameterException Thrown when the number of players is not between 2 and 4 (minimum and maximum number of players).
     */
    public MatchModel(Set<String> players, MatchController controller) throws NotValidConfigPathException, NotValidParameterException {
        this(players);
        if (controller == null) {
            throw new NullPointerException();
        }
        this.controller=controller;
    }

    /**
     * Update a turn.
     *
     * @param maxRounds Maximum number of rounds in a match.
     * @throws TooManyRoundsException Thrown when the round track size is greater than the maximum number of rounds.
     * @throws NotEnoughPlayersException Thrown when the number of players in a match is less than the minimum number of players admitted in a match.
     */
    public void updateTurn(int maxRounds) throws TooManyRoundsException, NotEnoughPlayersException {
        int onlinePlayers = 0;
        for(Player player : playersInGame){
            if (!player.isDisconnected()) onlinePlayers++;
        }
        if (onlinePlayers<MIN_PLAYERS_NUMBER) throw new NotEnoughPlayersException();
        if (iterator == null) iterator = new PlayersIterator(playersInGame);
        if(iterator.hasNext()) {
           currentPlayer = iterator.next();
        } else{
            playersInGame.add(playersInGame.remove(0));     //reorders players for new Round.
            //round + 1 --> il numero del round é aumentato di uno.
            try {
                roundTrack.add(matchDicePool.getDieFromPool(0));    //if there aren't any dice in DicePool at the end of the turn, throws NotInPoolException.
                matchDicePool.removeDieFromPool(0);
                this.prepareForNextRound(maxRounds);
            } catch (NotInPoolException | NotValidParameterException e) {
                Logger logger = Logger.getLogger(getClass().getName());
                logger.log(Level.WARNING,"Error updating the turn.",e);
            }
            iterator = new PlayersIterator(playersInGame);
            currentPlayer = iterator.next();
        }
    }

    /**
     * Method that randomly selects the public objectives of the match.
     *
     * @return A list containing the public objectives of a match.
     * @throws NotValidConfigPathException Thrown when the method can't get the public objectives because of an invalid configuration path.
     */
     private List<PublicObjective> selectPublicObjectives() throws NotValidConfigPathException {
         List<PublicObjective> pubs;
         pubs = ConfigurationHandler.getInstance().getPublicObjectives();
         List<PublicObjective> publicObj = new ArrayList<>();
         for (int i = 0; i<ConfigurationHandler.getInstance().getPublicObjectivesDistributed();i++)
             publicObj.add(pubs.remove(new Random().nextInt(pubs.size())));
         return publicObj;
     }

    /**
     * Getter for publicObjectives.
     *
     * @return The list of public objectives of a match.
     */
    public List<PublicObjective> getPublicObjectives() {
        return publicObjectives;
    }

    /**
     * Method that selects the tool cards of the match.
     *
     * @return A list containing the tool cards of a match.
     * @throws NotValidConfigPathException Thrown when the method can't get the tool cards because of an invalid configuration path.
     */
     private List<ToolCard> selectToolCards() throws NotValidConfigPathException {
         List<ToolCard> tools;
         tools = ConfigurationHandler.getInstance().getToolCards();
         List<ToolCard> tc = new ArrayList<>();
         for (int i = 0; i<ConfigurationHandler.getInstance().getToolCardsDistributed();i++)
             tc.add(tools.remove(new Random().nextInt(tools.size())));
         return tc;
     }

    /**
     * Getter for toolCards.
     *
     * @return The list of tool cards of a match.
     */
    public List<ToolCard> getToolCards() {
        return toolCards;
    }

    /**
     * Initialization of next round.
     *
     * @param maxRounds Maximum number of rounds in the match.
     * @throws TooManyRoundsException Thrown when round track size is greater than maxRounds.
     * @throws NotValidParameterException See generateDiceForPull doc in DicePool class.
     */
    private void prepareForNextRound(int maxRounds) throws TooManyRoundsException, NotValidParameterException {
        if (roundTrack.size()>= maxRounds) throw new TooManyRoundsException(); // throw exception if roundtrack is more than ten.
        initializeRound();
    }

    /**
     *
     * @throws NotValidParameterException See generateDiceForPull doc in DicePool class.
     */
    private void initializeRound() throws NotValidParameterException{
        matchDicePool.generateDiceForPull(playersInGame.size() * 2 + 1); //(launching this methods later throws a nullPointerExc)
    }

    /**
     * Getter for currentPlayer.
     *
     * @return The current player.
     */
    public Player getCurrentPlayer(){
        return currentPlayer;
    }

    /**
     *
     * @return Current player's username.
     */
    public String askTurn() {
        return currentPlayer.getUsername();
    }

    /**
     *
     * @param x Abscissa of the box.
     * @param y Ordinate of the box.
     * @param dpIndex The index of the pool where to get the die.
     * @throws InvalidOperationException See insertDieInXY doc in Grid class.
     * @throws NotInPoolException See getDieFromPool doc in DicePool class.
     * @throws NotValidParameterException See insertDieInXY doc in Grid class.
     */
    public void insertDieOperation(int x, int y, int dpIndex) throws InvalidOperationException, NotInPoolException, NotValidParameterException {
        final boolean CHECK_COLOR_CONSTRAINT =true;
        final boolean CHECK_VALUE_CONSTRAINT =true;
        Die dieToInsert= matchDicePool.getDieFromPool(dpIndex);
        Player playerOfTheTurn = this.currentPlayer;
        playerOfTheTurn.getSelectedGrid().insertDieInXY(x,y,CHECK_COLOR_CONSTRAINT, CHECK_VALUE_CONSTRAINT, dieToInsert);
        matchDicePool.removeDieFromPool(dpIndex);
    }

    /**
     *
     * @param i Tool card index.
     * @return A tool card.
     * @throws NotValidParameterException Thrown when the index is not valid.
     */
    public ToolCard getToolCard(int i) throws NotValidParameterException {
        if(i<0||i>=toolCards.size()) throw new NotValidParameterException("Invalid tool card index: "+i, "A value between 0 and "+ (toolCards.size()-1));
        return toolCards.get(i);
    }

    /**
     * Getter for matchDicePool.
     *
     * @return The dice pool of the match.
     */
    public DicePool getDicePool(){
        return matchDicePool;
    }

    /**
     * Method that selects 4 unique grids for a player.
     *
     * @return An arraylist containing 4 grids for a player.
     * @throws InvalidOperationException Thrown when there aren't enough grids for a player.
     */
    private ArrayList<Grid> selectGridsForPlayer() throws InvalidOperationException {
        int randomGridEvenIndex;
        /*
        * Grids are taken from a file and ordered in pairs (on even indexes we have a fronts and on odd indexes we have backs)
        * If i take twice a grids from the same index i will pick for sure a front grids and its back cause of arrayList proprieties.
        */

        if (grids.size()<GRIDS_FOR_A_PLAYER) throw new InvalidOperationException();
        randomGridEvenIndex = ((new Random().nextInt(grids.size()/2)+1)*2)-2;
        ArrayList<Grid> currentGrids = new ArrayList<>();
        currentGrids.add(grids.remove(randomGridEvenIndex));
        currentGrids.add(grids.remove(randomGridEvenIndex));
        randomGridEvenIndex = ((new Random().nextInt(grids.size()/2)+1)*2)-2;
        currentGrids.add(grids.remove(randomGridEvenIndex));
        currentGrids.add(grids.remove(randomGridEvenIndex));

        return currentGrids;
    }

    /**
     *
     * @param username Player's username.
     * @return A list containing the 4 grids chosen for a player.
     * @throws InvalidOperationException Thrown when the player has already selected a grid.
     * @throws InvalidUsernameException Thrown when there isn't a player whose username matches 'username'.
     */
    public List<Grid> getGridsForPlayer(String username) throws InvalidOperationException, InvalidUsernameException {
        /*
        * The idea is that when matchModel create players, it automatically takes 2 pairs of grids and put it in player
        * in a proper field.
        * This methods should return these grids that the model set in initialization.
        * NOTE: this return the grids of a single player, not all the grids!
        */

        Player playerPassed= null;
        for(Player player: playersInGame) {
            if (player == null) throw new NullPointerException();
            if (player.getUsername().equals(username))
                playerPassed = player;
        }
        if (playerPassed==null) throw new InvalidUsernameException();
        if (playerPassed.hasSelectedAGrid()) throw new InvalidOperationException();
        return playerPassed.getGridsSelection(); //returns null if playerPassed doesn't have any grids.
    }

    /**
     * Method that sets the grid for a player.
     *
     * @param player Player.
     * @param grid Index of the grid chosen by the player.
     * @throws InvalidOperationException Thrown when 'grid' is not a valid parameter (i.e. 'grid' greater than 4).
     * @throws NotValidParameterException See setGrid doc in Player class.
     */
    public void setPlayerGrid(String player, int grid) throws InvalidOperationException, NotValidParameterException {
        if(grid>=GRIDS_FOR_A_PLAYER) throw new InvalidOperationException();
        for (Player current : playersInGame){
            if(current.getUsername().equals(player)){
                current.setGrid(grid); //invalidOpExc is thrown if the possible grids are not initialized, NotValidParameter is thrown if grid is not 0 or 1.
            }
        }
    }

    /**
     *
     * @return True if all players have chosen a grid, false if not.
     */
    public boolean checkEndInitialization() {
        for (Player current : playersInGame){
            if (current.getSelectedGrid()==null) return false;
        }
        return true;
    }

    /**
     *
     * @param username Player's username.
     * @return True if the player has chosen a grid, false if not.
     * @throws NotValidParameterException Thrown when there isn't a player whose username matches 'username'.
     */
    public boolean hasPlayerChosenAGrid(String username) throws NotValidParameterException {
       for (Player current : playersInGame){
           if (current.getUsername().equals(username)) {
               return current.getSelectedGrid() != null;
           }
       }
       throw new NotValidParameterException("username: "+username,"Should be a player inside this match.");
    }

    /**
     * Method that disconnects a player.
     *
     * @param username Player's username.
     * @throws InvalidUsernameException Thrown when there isn't a player whose username matches 'username'.
     */
    public void setPlayerToDisconnect(String username) throws InvalidUsernameException{
        boolean flag = false;
        int i;
        for (i=0; i<playersInGame.size();i++){
            if (playersInGame.get(i).getUsername().equals(username)) {
                System.err.println("Removing player "+username);
                playersInGame.get(i).setDisconnected();
                flag=true;
            }
        }
        if (!flag) throw new InvalidUsernameException();
    }

    public void setPlayerToConnect(String username) throws InvalidUsernameException{
        boolean flag = false;
        for(Player player : playersInGame){
            if (player.getUsername().equals(username)) player.setConnected();
            flag = true;
        }
        if (!flag) throw new InvalidUsernameException();
    }

    /**
     * Method that selects a random private objective unique for one player.
     *
     * @return A private objective.
     * @throws InvalidOperationException Thrown when there aren't enough private objective for a player.
     */
    private PrivateObjective selectPrivateObjective() throws InvalidOperationException {

        if(privateObjectives.size()<PRIV_FOR_A_PLAYER) throw new InvalidOperationException();
        return (privateObjectives.remove(new Random().nextInt(privateObjectives.size())));
    }

    /**
     *
     * @param username Player's username.
     * @return Player's private objective.
     * @throws InvalidUsernameException Thrown when there isn't a player whose username matches 'username'.
     */
    public PrivateObjective getPrivateObjective(String username) throws InvalidUsernameException {
        List<PrivateObjective> stream=  playersInGame.stream().filter(i->i.getUsername().equals(username)).map(Player::getObjective).collect(Collectors.toList());
        if (stream.isEmpty()) throw new InvalidUsernameException();
        return stream.get(0);
    }

    /**
     *
     * @param index Die position in round track.
     * @return The chosen die.
     * @throws NotInPoolException Thrown when 'index' is out of bounds.
     */
    public Die getDieFromRoundTrack(int index) throws NotInPoolException {
        if(index>=0&&index<=roundTrack.size())
            return this.roundTrack.get(index);
        else
            throw new NotInPoolException();
    }

    public List<String> getConnectedPlayers(){
        List<String> connectedPlayers= new ArrayList<>();
        playersInGame.forEach(player -> {if(!currentPlayer.isDisconnected())connectedPlayers.add(player.getUsername());});
        return connectedPlayers;
    }

    /**
     *
     * @param index Die position in round track.
     * @throws NotInPoolException Thrown when 'index' is out of bounds.
     */
    public void removeDieFromRoundTrack(int index) throws NotInPoolException {
        this.getDieFromRoundTrack(index);
        this.roundTrack.remove(index);
    }

    /**
     * Getter for player's grid.
     *
     * @param username Player's username.
     * @return Player's grid.
     */
    public Grid getPlayerCurrentGrid(String username) {
        for(Player player: playersInGame){
            if(player.getUsername().equals(username)) return player.getSelectedGrid();
        }
        return null; //throw an exception? I think we shall not, it's ok like this.
    }

    /**
     *
     * @param die The die to be inserted.
     * @param roundTrackIndex Where to insert 'die'.
     * @throws NotValidParameterException Thrown if 'die' is null or 'roundTrackIndex' is greater than round track size.
     */
    public void insertDieInRT(Die die, int roundTrackIndex) throws NotValidParameterException {
        if (die==null) throw new NotValidParameterException("die:null","a valid die");
        if(roundTrackIndex>roundTrack.size()) throw new NotValidParameterException("IndexOutOfBounds:"+roundTrackIndex,"A value betweeen");
        roundTrack.add(roundTrackIndex,die);
    }

    /**
     *
     * @param die The die to be inserted.
     * @param index Where to insert 'die'.
     * @throws NotValidParameterException See insertDieInPool doc in DicePool class.
     */
    public void insertDieInPool(Die die, int index) throws NotValidParameterException {
        matchDicePool.insertDieInPool(die,index);
    }

    /**
     *
     * @param index Where to remove the die.
     * @throws NotInPoolException See removeDieFromPool doc in DicePool class.
     */
    public void removeDiePool(int index) throws  NotInPoolException {
        matchDicePool.removeDieFromPool(index);
    }

    /**
     * Getter for roundTrack.
     *
     * @return A list of dice.
     */
    public List<Die> getRoundTrack() {
        return roundTrack;
    }

    /**
     * Getter for a copy of the roundTrack.
     *
     * @return A list of dice.
     */
    public List<Die> getRoundTrackCopy() {
        List<Die> roundTrackCopy= new ArrayList<>();
        roundTrack.forEach(die -> roundTrackCopy.add(new Die(die)));
        return roundTrackCopy;
    }

    /**
     * Getter for controller.
     *
     * @return A matchController.
     */
    public MatchController getController() {
        return controller;
    }

    /**
     *
     * @return All players' usernames associated with their grid.
     */
    public Map<String,Grid> getAllGrids() {
        Map<String, Grid> toReturn = new HashMap<>();
        for (Player player : playersInGame) {
            toReturn.put(player.getUsername(), new Grid(player.getSelectedGrid()));
        }
        return toReturn;
    }

    /**
     *
     * @return Match results: all usernames associated with their result (username and result are both a string).
     */
    public Map<String,String> calculatePoints(){
        Map<String,String> map = new LinkedHashMap<>();
        SortedMap<String,Integer> temp= new TreeMap<>();
        for(Player player : this.playersInGame){
            temp.put(player.getUsername(),getPointsForPlayer(player));
        }
        while (!temp.isEmpty()){
            Map.Entry<String,Integer> entry= ((TreeMap<String, Integer>) temp).lastEntry();
            temp.remove(entry.getKey());
            map.put(entry.getKey(), Integer.toString(entry.getValue()));
        }
        return map;
    }

    /**
     * Method that calculates player's points.
     *
     * @param player Player.
     * @return An integer containing player's points.
     */
    private Integer getPointsForPlayer(Player player){
        int pubObjPoints;
        int emptyBoxesPoints=0;
        int privateObjectivePoints;
        int favorTokensPoints;

        pubObjPoints = getPublicObjectives().stream().mapToInt(publicObjective ->publicObjective.calculatePoints(player.getSelectedGrid())).sum();
        for(Box[] boxArray : player.getSelectedGrid().getGrid()){
            for(Box box : boxArray){
                if (box.getDieConstraint()==null) emptyBoxesPoints++;
            }
        }
        privateObjectivePoints = player.getObjective().calculatePoints(player.getSelectedGrid());
        favorTokensPoints = player.getFavorTokens();

        return pubObjPoints + privateObjectivePoints + favorTokensPoints - emptyBoxesPoints;
    }
}
