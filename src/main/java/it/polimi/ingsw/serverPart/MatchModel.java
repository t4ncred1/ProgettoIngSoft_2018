package it.polimi.ingsw.serverPart;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import it.polimi.ingsw.serverPart.card_container.PublicObjective;
import it.polimi.ingsw.serverPart.component_container.DicePool;
import it.polimi.ingsw.serverPart.component_container.Die;
import it.polimi.ingsw.serverPart.component_container.Grid;
import it.polimi.ingsw.serverPart.component_container.Player;
import it.polimi.ingsw.serverPart.custom_exception.InvalidOperationException;
import it.polimi.ingsw.serverPart.custom_exception.NotInPoolException;
import it.polimi.ingsw.serverPart.custom_exception.NotValidParameterException;
import it.polimi.ingsw.serverPart.custom_exception.TooManyTurns;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class MatchModel{

    //todo let these be read from file.
    private static final int MAXPLAYERSNUMBER=4;
    private static final int ROUNDNUMBER=10;

    private List<Grid> grids;
    private List<PublicObjective> publicObjectives;

    private ArrayList<Die> roundTrack;
    private MatchController controller;
    private DicePool matchDicepool;
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

        if (playersUserNames.size()<=2||playersUserNames.size()>MAXPLAYERSNUMBER) throw new NotValidParameterException("Number of players in game: "+Integer.toString(playersUserNames.size()),"Between 2 and "+Integer.toString(MAXPLAYERSNUMBER));
        for (String i: playersUserNames){
            playersInGame.add(new Player(i));
        }

        currentRound=1;
        currentTurn=0;
        leftToRight =true;
        justChanged =true;
        playersInGame= new ArrayList<>();
        matchDicepool = new DicePool();

        try {
            this.initializeRound(); //Please note: When Matchmodel is created, it actually initializes a new round and, so, it calls some methods of MatchController
        } catch (NotInPoolException | TooManyTurns e) {
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

    public int updateTurn(){
        controller.updatePlayersGrids(); //FIXME
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
                    this.initializeRound();
                } catch (NotInPoolException | TooManyTurns e) {
                    e.printStackTrace();
                }
            }
        }
        return currentRound;
    }

    private void initializeRound() throws NotInPoolException, TooManyTurns{

        if (roundTrack.size()>=ROUNDNUMBER) throw new TooManyTurns(); // throw exception if roundtrack is more than ten.
        roundTrack.add(matchDicepool.getDieFromPool(0));    //if there aren't any dice in DicePool at the end of the turn, throws NotInPoolException.
        try {
            matchDicepool.generateDiceForPull(playersInGame.size()*2+1);
        } catch (NotValidParameterException e) {
            e.printStackTrace();
        }
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
        return matchDicepool.showDiceInPool();
    }

}
