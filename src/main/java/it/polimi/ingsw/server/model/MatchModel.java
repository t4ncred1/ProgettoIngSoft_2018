package it.polimi.ingsw.server.model;


import it.polimi.ingsw.server.model.cards.PrivateObjective;
import it.polimi.ingsw.server.model.cards.PublicObjective;
import it.polimi.ingsw.server.model.cards.ToolCard;
import it.polimi.ingsw.server.model.components.DicePool;
import it.polimi.ingsw.server.model.components.Die;
import it.polimi.ingsw.server.model.components.Grid;
import it.polimi.ingsw.server.model.components.Player;
import it.polimi.ingsw.server.configurations.ConfigurationHandler;
import it.polimi.ingsw.server.custom_exception.*;

import java.util.*;
import java.util.stream.Collectors;

public class MatchModel{

    private int MAX_PLAYERS_NUMBER =0;
    private int MIN_PLAYERS_NUMBER =0;

    private ArrayList<PrivateObjective> privateObjectives;
    private List<Grid> grids;
    private ArrayList<ToolCard> toolCards;
    private List<PublicObjective> publicObjectives;


    private ArrayList<Die> roundTrack;
    private DicePool matchDicePool;
    private int currentTurn;
    private boolean leftToRight;
    private boolean justChanged;
    private boolean turnEnded;
    private ArrayList<Player> playersInGame;
    private Player[] playersNotInGame;

    private final int GRIDS_FOR_A_PLAYER =4;
    private final int PRIV_FOR_A_PLAYER=1;

    private static final String GREEN_OBJ= "green";
    private static final String RED_OBJ= "red";
    private static final String YELLOW_OBJ= "yellow";
    private static final String BLUE_OBJ= "blue";
    private static final String PURPLE_OBJ= "purple";

    MatchModel(Set<String> playersUserNames) throws NotValidParameterException, NotValidConfigPathException{
        if (playersUserNames==null) throw new NullPointerException();
        try {
            MAX_PLAYERS_NUMBER =ConfigurationHandler.getMaxPlayersNumber();
        } catch (NotValidConfigPathException e) {
            e.printStackTrace();
        }
        try {
            MIN_PLAYERS_NUMBER =ConfigurationHandler.getMinPlayersNumber();
        } catch (NotValidConfigPathException e) {
            e.printStackTrace();
        }

        roundTrack=new ArrayList<>();
        if (playersUserNames.size()< MIN_PLAYERS_NUMBER ||playersUserNames.size()> MAX_PLAYERS_NUMBER) throw new NotValidParameterException("Number of players in game: "+Integer.toString(playersUserNames.size()),"Between 2 and "+Integer.toString(MAX_PLAYERS_NUMBER));

        grids=ConfigurationHandler.getGrids();

        publicObjectives=ConfigurationHandler.getPublicObjectives();

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
                e.printStackTrace();    //this error shall be thrown if there aren't enough grids for the player (should not happen because of the boundaries above)
            }
            playersInGame.add(playerToAdd);
        }
        currentTurn=0;
        leftToRight =true;
        justChanged =true;
        turnEnded= false;
        matchDicePool = new DicePool();

        try {
            initializeRound();
        } catch (NotInPoolException e) {
            e.printStackTrace();
        }
    }

    public void updateTurn(int maxRounds) throws TooManyRoundsException, NotEnoughPlayersException {
        if(playersInGame.size()<MIN_PLAYERS_NUMBER){
            throw new NotEnoughPlayersException();
        }
        if(leftToRight){
            if(justChanged) {  //Se il verso di percorrenza è appena stato modificato currentTurn non deve cambiare.
                justChanged = false;
                if(turnEnded) {
                    playersInGame.add(playersInGame.remove(0));     //reorders players for new Round.
                    try {
                        roundTrack.add(matchDicePool.getDieFromPool(0));    //if there aren't any dice in DicePool at the end of the turn, throws NotInPoolException.
                        matchDicePool.removeDieFromPool(0);
                        this.prepareForNextRound(maxRounds);
                    } catch (NotInPoolException | NotValidParameterException e) {
                        e.printStackTrace();
                    }
                }
            }
            else
                currentTurn++;
            if(currentTurn==playersInGame.size()-1){
                leftToRight=false;
                justChanged=true;
            }
        }
        else {
            if(justChanged)  //Se il verso di percorrenza è appena stato modificato currentTurn non deve cambiare.
                justChanged=false;
            else
                currentTurn--;
            if(currentTurn==0){
                leftToRight=true;
                justChanged=true;
                turnEnded=true;
            }
        }
    }

    private void prepareForNextRound(int maxRounds) throws NotInPoolException, TooManyRoundsException, NotValidParameterException {
        if (roundTrack.size()>= maxRounds) throw new TooManyRoundsException(); // throw exception if roundtrack is more than ten.
        initializeRound();
    }

    private void initializeRound() throws NotValidParameterException, NotInPoolException {
        matchDicePool.generateDiceForPull(playersInGame.size() * 2 + 1); //(launching this methods later throws a nullPointerExc)
    }


    public String askTurn() {
        return playersInGame.get(currentTurn).getUsername();
    }

    public void insertDieOperation(int x, int y, int dpIndex) throws InvalidOperationException, NotInPoolException, NotValidParameterException {
        final boolean CHECK_COLOR_CONSTRAINT =true;
        final boolean CHECK_VALUE_CONSTRAINT =true;
        Die dieToInsert= matchDicePool.getDieFromPool(dpIndex);
        Player playerOfTheTurn = this.playersInGame.get(currentTurn);
        playerOfTheTurn.getSelectedGrid().insertDieInXY(x,y,CHECK_COLOR_CONSTRAINT, CHECK_VALUE_CONSTRAINT, dieToInsert);
        matchDicePool.removeDieFromPool(dpIndex);
    }

    public boolean useToolCardOperation() {
        //TODO implement me.
        return false;
    }

    public List<Die> getDicePool(){
        return matchDicePool.showDiceInPool();
    }

    private ArrayList<Grid> selectGridsForPlayer() throws InvalidOperationException {
        int randomGridEvenIndex;
        /*
        * Grids are taken from a file and ordered in pairs (on even indexes we have a fronts and on odd indexes we have backs)
        * If i take twice a grids from the same index i will pick for sure a front grids and its back cause of arrayList proprieties.
        */
        if (grids.size()<GRIDS_FOR_A_PLAYER) throw new InvalidOperationException();
        randomGridEvenIndex = ((new Random().nextInt(grids.size()/2)+1)*2)-2;
        ArrayList<Grid> currentGrids = new ArrayList<Grid>();
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
        for (Player currentPlayer : playersInGame){
            if (currentPlayer.getSelectedGrid()==null) return false;
        }
        return true;
    }

    public boolean hasPlayerChosenAGrid(String username) throws NotValidParameterException {
       for (Player currentPlayer : playersInGame){
           if (currentPlayer.getUsername().equals(username)) {
               return currentPlayer.getSelectedGrid() != null;
           }
       }
       throw new NotValidParameterException("username: "+username,"Should be a player inside this match.");
    }

    public void setPlayerToDisconnect(String username) throws NotValidParameterException {
        if (playersNotInGame==null) playersNotInGame=new Player[playersInGame.size()];
        boolean flag = false;
        int i;
        for (i=0; i<playersInGame.size();i++){
            if (playersInGame.get(i).getUsername().equals(username)) {playersNotInGame[i]=playersInGame.remove(i); flag=true;}
        }
        if (!flag) throw new NotValidParameterException("username: "+username,"username should belong to a player in this match");
    }

    public void setPlayerToConnect(String username) throws NotValidParameterException{
        //todo if a player is already connected exception shouldn't be launched.
        // This happen when player reconnect before the timeout event of his turn
    }

    private PrivateObjective selectPrivateObjective() throws InvalidOperationException {

        if(privateObjectives.size()<PRIV_FOR_A_PLAYER) throw new InvalidOperationException();
        return (privateObjectives.remove(new Random().nextInt(privateObjectives.size())));
    }

    public PrivateObjective getPrivateObjective(String username) throws InvalidUsernameException {
        List<PrivateObjective> stream=  playersInGame.stream().filter(i->i.getUsername().equals(username)).map(Player::getObjective).collect(Collectors.toList());
        if (stream.stream().count()==0) throw new InvalidUsernameException();
        return stream.get(0);
    }

    public Die getDieFromRoundtrack(int index) throws NotInPoolException {
        if(index>=0&&index<=roundTrack.size())
            return this.roundTrack.get(index);
        else
            throw new NotInPoolException();
    }

    public void removeDieFromRoundTrack(int index) throws NotInPoolException {
        this.getDieFromRoundtrack(index);
        this.roundTrack.remove(index);
    }


    public Grid getPlayerCurrentGrid(String username) {
        //FIXME
        for(Player player: playersInGame){
            if(player.getUsername().equals(username)) return player.getSelectedGrid();
        }
        return null; //FIXME throw an exception?
    }

    public void insertdieinRT(Die die, int RTindex) throws NotValidParameterException {
        if (die==null) throw new NotValidParameterException("die:null","a valid die");
        if(RTindex>roundTrack.size()) throw new NotValidParameterException("IndexOutOfBounds:"+RTindex,"A value betweeen");
        roundTrack.add(RTindex,die);
    }

    public void insertDieInPool(Die die, int index) throws NotValidParameterException {
        matchDicePool.insertDieInPool(die,index);
    }

    public void removeDiePool(int index) throws  NotInPoolException {
        matchDicePool.removeDieFromPool(index);
    }

    public void getPublicObjectives(){
        
    }

}
