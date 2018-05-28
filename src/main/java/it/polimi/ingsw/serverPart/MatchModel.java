package it.polimi.ingsw.serverPart;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import it.polimi.ingsw.serverPart.card_container.PublicObjective;
import it.polimi.ingsw.serverPart.component_container.DicePool;
import it.polimi.ingsw.serverPart.component_container.Die;
import it.polimi.ingsw.serverPart.component_container.Grid;
import it.polimi.ingsw.serverPart.component_container.Player;
import it.polimi.ingsw.serverPart.configurations.ConfigurationHandler;
import it.polimi.ingsw.serverPart.custom_exception.*;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class MatchModel{

    private static int MAXPLAYERSNUMBER=0;
    private static int MINPLAYERSNUMBER=0;

    private List<Grid> grids;
    private List<PublicObjective> publicObjectives;

    private ArrayList<Die> roundTrack;
    private MatchController controller;
    private DicePool matchDicePool;
//    private int currentRound;
    private int currentTurn;
    private boolean leftToRight;
    private boolean justChanged;
    private ArrayList<Player> playersInGame;
    private ArrayList<Player> playersNotInGame;

    MatchModel(Set<String> playersUserNames, MatchController controller) throws NotValidParameterException, NotValidConfigPathException{
        try {
            MAXPLAYERSNUMBER=ConfigurationHandler.getMaxPlayersNumber();
        } catch (NotValidConfigPathException e) {
            e.printStackTrace();
        }
        try {
            MINPLAYERSNUMBER=ConfigurationHandler.getMinPlayersNumber();
        } catch (NotValidConfigPathException e) {
            e.printStackTrace();
        }
        if (controller==null) throw new NullPointerException();
        this.controller=controller;
        roundTrack=new ArrayList<>();
        if (playersUserNames.size()<MINPLAYERSNUMBER||playersUserNames.size()> MAXPLAYERSNUMBER) throw new NotValidParameterException("Number of players in game: "+Integer.toString(playersUserNames.size()),"Between 2 and "+Integer.toString(MAXPLAYERSNUMBER));

        grids=ConfigurationHandler.getGrids();

        publicObjectives=ConfigurationHandler.getPublicObjectives();

        //player initialization:
        playersInGame= new ArrayList<>();
        for (String username: playersUserNames){
            Player playerToAdd = new Player(username);
            try {
                playerToAdd.setGridsSelection(selectGridsForPlayer());
            } catch (InvalidOperationException e) {
                e.printStackTrace();    //this error shall be thrown if there aren't enough grids for the player (should not happen because of the boundaries above)
            }
            playersInGame.add(playerToAdd);
        }
//        currentRound=1;
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
//                currentRound++;
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
        if (roundTrack.size()>= maxRounds) throw new TooManyRoundsException(); // throw exception if roundtrack is more than ten.
        initializeRound();
    }

    private void initializeRound() throws NotValidParameterException, NotInPoolException {
        matchDicePool.generateDiceForPull(playersInGame.size()*2+1); //(launching this methods later throws a nullPointerExc)
        roundTrack.add(matchDicePool.getDieFromPool(0));    //if there aren't any dice in DicePool at the end of the turn, throws NotInPoolException.
    }

    public String requestTurnPlayer() {
        return playersInGame.get(currentTurn).getUsername();
    }

    public boolean insertDieOperation() throws InvalidOperationException {
        //call the controller to get parameters (index of the die in dicepool, position of the box in grid )

        int[]coordinates;
        int index;
        if(playersInGame.get(currentTurn).getSelectedGrid()==null) throw new InvalidOperationException(); //player was not initialized.
        controller.sendDicePool(this.getDicePool(),playersInGame.get(currentTurn).getUsername());
        try{
            coordinates=controller.askForDieCoordinates(playersInGame.get(currentTurn).getUsername());
        } catch (InvalidOperationException e){
            return false;
        }
        try{
            index=controller.askForDieIndex(playersInGame.get(currentTurn).getUsername());
        } catch (InvalidOperationException e){
            return false;
        }
        try {
            playersInGame.get(currentTurn).getSelectedGrid().insertDieInXY(coordinates[0],coordinates[1],true,true,this.getDicePool().get(index));
        } catch (NotValidParameterException e) {
            return false;
        }

        return true;
    }

    public boolean useToolCardOperation() {
        //TODO implement me.
        return false;
    }

    public List<Die> getDicePool(){
        return matchDicePool.showDiceInPool();
    }

    private ArrayList<Grid> selectGridsForPlayer() throws InvalidOperationException {
        if (grids.size()<4) throw new InvalidOperationException();
        int cardNumber = ((new Random().nextInt(Integer.divideUnsigned(grids.size(),2))+1)*2);
        ArrayList<Grid> currentGrids = new ArrayList<Grid>();
        currentGrids.add(grids.remove(cardNumber-1));
        currentGrids.add(grids.remove(cardNumber-1));
        cardNumber = ((new Random().nextInt(Integer.divideUnsigned(grids.size(),2))+1)*2);
        currentGrids.add(grids.remove(cardNumber-1));
        currentGrids.add(grids.remove(cardNumber-1));
        return currentGrids;
    }

    public List<Grid> getGridsForPlayer(String username) throws InvalidOperationException {
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
        if (playerPassed==null) throw new InvalidOperationException();
        return playerPassed.getGridsSelection(); //returns null if playerPassed doesn't have any grids.
    }

    public void setPlayerGrid(String player, int grid) throws InvalidOperationException, NotValidParameterException {
        for (Player current : playersInGame){
            if(current.getUsername().equals(player)) current.setGrid(grid); //invalidOpExc is thrown if the possible grids are not initialized, NotValidParameter is thrown if grid is not 0 or 1.
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

    public Grid getSelectedGrid(String username) {
        //TODO return grid selected from the player

        //possible implementation
        Player playerPassed= null;
        for(Player player: playersInGame)
            if(player.getUsername().equals(username)) playerPassed=player;
        Grid toReturn = playerPassed.getSelectedGrid();  //FIXME throw an exception if toReturn=null ?
        return toReturn;
    }

    public void setPlayerToDisconnect(String username) throws NotValidParameterException {
        Player playerPassed = null;
        for (int i=0; i<playersInGame.size();i++){
            if (playersInGame.get(i).getUsername().equals(username)) playerPassed=playersInGame.remove(i);
        }
        if (playerPassed==null) throw new NotValidParameterException("username: "+username,"username should belong to a player in this match");
    }
}
