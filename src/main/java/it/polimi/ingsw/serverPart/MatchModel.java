package it.polimi.ingsw.serverPart;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import it.polimi.ingsw.serverPart.card_container.PublicObjective;
import it.polimi.ingsw.serverPart.component_container.DicePool;
import it.polimi.ingsw.serverPart.component_container.Die;
import it.polimi.ingsw.serverPart.component_container.Grid;
import it.polimi.ingsw.serverPart.component_container.Player;
import it.polimi.ingsw.serverPart.custom_exception.NotInPoolException;
import it.polimi.ingsw.serverPart.custom_exception.NotValidParameterException;
import it.polimi.ingsw.serverPart.custom_exception.TooManyRoundsException;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class MatchModel{

    //todo let these be read from file.
    private static final int MAX_PLAYERS_NUMBER =4;
    private static final int MIN_PLAYERS_NUMBER=2;
    private static final int ROUND_NUMBER =10;

    private List<Grid> grids;
    private List<PublicObjective> publicObjectives;

    private ArrayList<Die> roundTrack;
    private MatchController controller;
    private DicePool matchDicePool;
    private int currentRound;
    private int currentTurn;
    private boolean leftToRight;
    private boolean justChanged;
    private ArrayList<Player> playersInGame;
    private ArrayList<Player> playersNotInGame;

    MatchModel(Set<String> playersUserNames, MatchController controller) throws NotValidParameterException, NullPointerException{
        if (controller==null) throw new NullPointerException();
        this.controller=controller;
        roundTrack=new ArrayList<>();

        if (playersUserNames.size()<MIN_PLAYERS_NUMBER||playersUserNames.size()> MAX_PLAYERS_NUMBER) throw new NotValidParameterException("Number of players in game: "+Integer.toString(playersUserNames.size()),"Between 2 and "+Integer.toString(MAX_PLAYERS_NUMBER));

        //player initialization:
        playersInGame= new ArrayList<>();
        for (String username: playersUserNames){
            Player playerToAdd = new Player(username);
            //FIXME here set grids to the player. Create a custom methods to choose grid
            playerToAdd.setGridsSelection(selectGridsForPlayer());
            //TODO NOTE: select grids for player could be a method returning an ArrayList of grids, need to be implemented.
            playersInGame.add(playerToAdd);
        }
        currentRound=1;
        currentTurn=0;
        leftToRight =true;
        justChanged =true;
        matchDicePool = new DicePool();

        try {
            initializeRound();
        } catch (NotInPoolException e) {
            e.printStackTrace();
        }

    }

    private void lookForGrids(String path) throws FileNotFoundException{
        Gson gson = new Gson();
        TypeToken<List<Grid>> listType = new TypeToken<List<Grid>>(){};

        grids = gson.fromJson(new FileReader(path), listType.getType());
        for(Grid i : grids){
            i.associateBoxes();
        }
        //todo pass these to who's on duty.
    }

    private void lookForPublicObjectives(String path) throws FileNotFoundException {
        Gson gson = new Gson();
        TypeToken<List<PublicObjective>> listType = new TypeToken<List<PublicObjective>>() {
        };
        publicObjectives = gson.fromJson(new FileReader(path), listType.getType());
        //TODO pass these to who's on duty.
    }

    public void updateTurn(int maxRounds) throws TooManyRoundsException {
        if(leftToRight){
            if(justChanged)  //Se il verso di percorrenza è appena stato modificato currentTurn non deve cambiare.
                justChanged=false;
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
                currentRound++;
                playersInGame.add(playersInGame.remove(0));     //reorders players for new Round.
                try {
                    this.prepareForNextRound(maxRounds);
                } catch (NotInPoolException | NotValidParameterException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void prepareForNextRound(int maxRounds) throws NotInPoolException, TooManyRoundsException, NotValidParameterException {
        //FIXME added this method to get better logic implementation (andre)
        if (roundTrack.size()>= maxRounds) throw new TooManyRoundsException(); // throw exception if roundtrack is more than ten.
        initializeRound();
    }

    private void initializeRound() throws NotValidParameterException, NotInPoolException {
        matchDicePool.generateDiceForPull(playersInGame.size()*2+1); //FIXME added by andre (launching this methods later throws a nullPointerExc)
        roundTrack.add(matchDicePool.getDieFromPool(0));    //if there aren't any dice in DicePool at the end of the turn, throws NotInPoolException.
    }

    public String requestTurnPlayer() {
        return playersInGame.get(currentTurn).getUsername();
    }

    public boolean insertDieOperation() {
        //call the controller to get parameters (index of the die in dicepool, position of the box in grid )
        //TODO implement me. When this operation goes well it has to return true; This operation could be interrupted.
        return false;
    }

    public boolean useToolCardOperation() {
        //TODO implement me.
        return false;
    }

    public List<Die> getDicePool(){
        return matchDicePool.showDiceInPool();
    }

    private ArrayList<Grid> selectGridsForPlayer() {
        //TODO this method should select 2 pair of grids from grids and return them. These grids will be given to a player
        return null;
    }

    public ArrayList<Grid> getGridsForPlayer(String username) {
        /*TODO ask to the selected player his initials grids.
        * The idea is that when matchModel create players, it automatically takes 2 pairs of grids and put it in player
        * in a proper field.
        * This methods should return these grids that the model set in initialization.
        * NOTE: this return the grids of a single player, not all the grids!
        */

        //Possible implementation
        Player playerPassed= null;
        for(Player player: playersInGame)
            if(player.getUsername().equals(username)) playerPassed=player;
        //FIXME throw an exception if playerPassed remains null ?
        ArrayList<Grid> toReturn= playerPassed.getGridsSelection();  //FIXME throw an exception if toReturn=null ?
        return toReturn;
    }

    public void setPlayerGrid(String player, int grid) {
        //TODO
        //the integer grid represent the index of the grid chosen by the player in gridSelection
        //Set the grid and throw and exception if necessary
    }

    public boolean checkEndInitialization() {
        //TODO return true if all players had chosen a grid
        return false;
    }

    public boolean hasPlayerChosenAGrid(String username) {
        //TODO this method return true if the player passed have chosen a grid.
       return false;
    }

    public Grid getSelectedGrid(String username) {
        //TODO return grid selected from the player

        //possible implementation
        Player playerPassed= null;
        for(Player player: playersInGame)
            if(player.getUsername().equals(username)) playerPassed=player;
        Grid toReturn = playerPassed.getSelectedGrid();  //FIXME throw an exception if toReturn=null ?
        return toReturn;
    }

    public void setPlayerToDisconnect(String username){
        //TODO
    }
}
