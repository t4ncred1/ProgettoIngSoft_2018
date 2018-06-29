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
    private Player[] playersNotInGame;

    private PlayersIterator iterator;

    private final int GRIDS_FOR_A_PLAYER =4;
    private final int PRIV_FOR_A_PLAYER=1;

    private static final String GREEN_OBJ= "green";
    private static final String RED_OBJ= "red";
    private static final String YELLOW_OBJ= "yellow";
    private static final String BLUE_OBJ= "blue";
    private static final String PURPLE_OBJ= "purple";

    public MatchModel(Set<String> playersUserNames) throws NotValidParameterException, NotValidConfigPathException{

        if (playersUserNames==null) throw new NullPointerException();
        try {
            MAX_PLAYERS_NUMBER =ConfigurationHandler.getInstance().getMaxPlayersNumber();
        } catch (NotValidConfigPathException e) {
            e.printStackTrace();
        }
        try {
            MIN_PLAYERS_NUMBER =ConfigurationHandler.getInstance().getMinPlayersNumber();
        } catch (NotValidConfigPathException e) {
            e.printStackTrace();
        }

        roundTrack=new ArrayList<>();
        if (playersUserNames.size()< MIN_PLAYERS_NUMBER ||playersUserNames.size()> MAX_PLAYERS_NUMBER) throw new NotValidParameterException("Number of players in game: "+Integer.toString(playersUserNames.size()),"Between 2 and "+Integer.toString(MAX_PLAYERS_NUMBER));

        grids=ConfigurationHandler.getInstance().getGrids();

        publicObjectives= selectPublicObjectives();
        toolCards= selectToolCards();

        privateObjectives = new ArrayList<>();
        privateObjectives.add(new PrivateObjective(GREEN_OBJ));
        privateObjectives.add(new PrivateObjective(RED_OBJ));
        privateObjectives.add(new PrivateObjective(PURPLE_OBJ));
        privateObjectives.add(new PrivateObjective(YELLOW_OBJ));
        privateObjectives.add(new PrivateObjective(BLUE_OBJ));



        //player initialization:
        playersInGame= new ArrayList<>();
        for (String username: playersUserNames){
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
        playersNotInGame=new Player[playersInGame.size()];
        matchDicePool = new DicePool();
        initializeRound();
    }

    public MatchModel(Set<String> players, MatchController controller) throws NotValidConfigPathException, NotValidParameterException {
        this(players);
        if (controller == null) {
            throw new NullPointerException();
        }
        this.controller=controller;
    }

    public void updateTurn(int maxRounds) throws TooManyRoundsException, NotEnoughPlayersException {
        if (playersInGame.size()<MIN_PLAYERS_NUMBER) throw new NotEnoughPlayersException();
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
     private List<PublicObjective> selectPublicObjectives() throws NotValidConfigPathException {
         List<PublicObjective> pubs;
         pubs = ConfigurationHandler.getInstance().getPublicObjectives();
         List<PublicObjective> publicObj = new ArrayList<>();
         for (int i = 0; i<ConfigurationHandler.getInstance().getPublicObjectivesDistributed();i++)
             publicObj.add(pubs.remove(new Random().nextInt(pubs.size())));
         return publicObj;
     }

    public List<PublicObjective> getPublicObjectives() {
        return publicObjectives;
    }

     private List<ToolCard> selectToolCards() throws NotValidConfigPathException {
         List<ToolCard> tools;
         tools = ConfigurationHandler.getInstance().getToolCards();
         List<ToolCard> tc = new ArrayList<>();
         for (int i = 0; i<ConfigurationHandler.getInstance().getToolCardsDistributed();i++)
             tc.add(tools.remove(new Random().nextInt(tools.size())));
         return tc;
     }

    public List<ToolCard> getToolCards() {
        return toolCards;
    }

    private void prepareForNextRound(int maxRounds) throws TooManyRoundsException, NotValidParameterException {
        if (roundTrack.size()>= maxRounds) throw new TooManyRoundsException(); // throw exception if roundtrack is more than ten.
        initializeRound();
    }

    private void initializeRound() throws NotValidParameterException{
        matchDicePool.generateDiceForPull(playersInGame.size() * 2 + 1); //(launching this methods later throws a nullPointerExc)
    }

    public Player getCurrentPlayer(){
        return currentPlayer;
    }

    public String askTurn() {
        return currentPlayer.getUsername();
    }

    public void insertDieOperation(int x, int y, int dpIndex) throws InvalidOperationException, NotInPoolException, NotValidParameterException {
        final boolean CHECK_COLOR_CONSTRAINT =true;
        final boolean CHECK_VALUE_CONSTRAINT =true;
        Die dieToInsert= matchDicePool.getDieFromPool(dpIndex);
        Player playerOfTheTurn = this.currentPlayer;
        playerOfTheTurn.getSelectedGrid().insertDieInXY(x,y,CHECK_COLOR_CONSTRAINT, CHECK_VALUE_CONSTRAINT, dieToInsert);
        matchDicePool.removeDieFromPool(dpIndex);
    }

    public boolean useToolCardOperation() {
        //TODO implement me. Should this be implemented under controller?
        return false;
    }

    public DicePool getDicePool(){
        return matchDicePool;
    }

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

    public void setPlayerGrid(String player, int grid) throws InvalidOperationException, NotValidParameterException {
        if(grid>=GRIDS_FOR_A_PLAYER) throw new InvalidOperationException();
        for (Player current : playersInGame){
            if(current.getUsername().equals(player)){
                current.setGrid(grid); //invalidOpExc is thrown if the possible grids are not initialized, NotValidParameter is thrown if grid is not 0 or 1.
            }
        }
    }

    public boolean checkEndInitialization() {
        for (Player current : playersInGame){
            if (current.getSelectedGrid()==null) return false;
        }
        return true;
    }

    public boolean hasPlayerChosenAGrid(String username) throws NotValidParameterException {
       for (Player current : playersInGame){
           if (current.getUsername().equals(username)) {
               return current.getSelectedGrid() != null;
           }
       }
       throw new NotValidParameterException("username: "+username,"Should be a player inside this match.");
    }

    public void setPlayerToDisconnect(String username) throws InvalidUsernameException{
        boolean flag = false;
        int i;
        for (i=0; i<playersInGame.size();i++){
            if (playersInGame.get(i).getUsername().equals(username)) {
                System.err.println("Removing player "+username);
                playersNotInGame[i]=playersInGame.remove(i);
                flag=true;
            }
        }
        if (!flag) throw new InvalidUsernameException();
    }

    public void setPlayerToConnect(String username) throws NotValidParameterException{
        //todo if a player is already connected exception shouldn't be thrown.
        // This happen when player reconnect before the timeout event of his turn
    }

    private PrivateObjective selectPrivateObjective() throws InvalidOperationException {

        if(privateObjectives.size()<PRIV_FOR_A_PLAYER) throw new InvalidOperationException();
        return (privateObjectives.remove(new Random().nextInt(privateObjectives.size())));
    }

    public PrivateObjective getPrivateObjective(String username) throws InvalidUsernameException {
        List<PrivateObjective> stream=  playersInGame.stream().filter(i->i.getUsername().equals(username)).map(Player::getObjective).collect(Collectors.toList());
        if (stream.isEmpty()) throw new InvalidUsernameException();
        return stream.get(0);
    }

    public Die getDieFromRoundTrack(int index) throws NotInPoolException {
        if(index>=0&&index<=roundTrack.size())
            return this.roundTrack.get(index);
        else
            throw new NotInPoolException();
    }

    public void removeDieFromRoundTrack(int index) throws NotInPoolException {
        this.getDieFromRoundTrack(index);
        this.roundTrack.remove(index);
    }


    public Grid getPlayerCurrentGrid(String username) {
        for(Player player: playersInGame){
            if(player.getUsername().equals(username)) return player.getSelectedGrid();
        }
        for(Player player: playersNotInGame){
            if(player!=null&&player.getUsername().equals(username)) return player.getSelectedGrid();
        }
        return null; //throw an exception? I think we shall not, it's ok like this.
    }

    public void insertDieInRT(Die die, int roundTrackIndex) throws NotValidParameterException {
        if (die==null) throw new NotValidParameterException("die:null","a valid die");
        if(roundTrackIndex>roundTrack.size()) throw new NotValidParameterException("IndexOutOfBounds:"+roundTrackIndex,"A value betweeen");
        roundTrack.add(roundTrackIndex,die);
    }

    public void insertDieInPool(Die die, int index) throws NotValidParameterException {
        matchDicePool.insertDieInPool(die,index);
    }

    public void removeDiePool(int index) throws  NotInPoolException {
        matchDicePool.removeDieFromPool(index);
    }

    public List<Die> getRoundTrack() {
        return roundTrack;
    }

    public MatchController getController() {
        return controller;
    }


    public Map<String,Grid> getAllGrids() {
        Map<String, Grid> toReturn = new HashMap<>();
        for (Player player : playersInGame) {
            toReturn.put(player.getUsername(), player.getSelectedGrid());
        }
        return toReturn;
    }

    public Map<String,Integer> calculatePoints(){
        Map<String,Integer> map = new HashMap<>();
        for(Player player : this.playersInGame){
            map.put(player.getUsername(),getPointsForPlayer(player));
        }
        return map;

    }

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
