package it.polimi.ingsw.serverPart;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import it.polimi.ingsw.serverPart.card_container.PublicObjective;
import it.polimi.ingsw.serverPart.component_container.Grid;
import it.polimi.ingsw.serverPart.component_container.Player;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class MatchModel{

    private List<Grid> grids;
    private int currentRound;
    private int currentTurn;
    private boolean leftToRight; //FIXME verso di percorrenza.
    private boolean justChanged; //FIXME se verso di percorrenza è appena stato cambiato
    private ArrayList<Player> playersInGame;

    public MatchModel(/*MatchConfigurationsInterface config*/) throws FileNotFoundException{
        currentRound=1;
        currentTurn=0;
        leftToRight =true;
        playersInGame= new ArrayList<>();

        //TODO implement me.

    }

    private void lookForGrids(String path) throws FileNotFoundException{
        Gson gson = new Gson();
        TypeToken<List<Grid>> listType = new TypeToken<List<Grid>>(){};

        grids = gson.fromJson(new FileReader(path), listType.getType());
        for(Grid i : grids){
            //TODO initialize grids with box observers
        }
    }

    private void lookForPublicObjectives(String path) throws FileNotFoundException {
        Gson gson = new Gson();
        TypeToken<List<PublicObjective>> listType = new TypeToken<List<PublicObjective>>() {
        };
        List<PublicObjective> publicObjectives;
        publicObjectives = gson.fromJson(new FileReader(path), listType.getType());
        for (PublicObjective i : publicObjectives) {
            //TODO pass public obj to who's on duty
        }
    }

    public int updateTurn(){
        if(leftToRight){ //FIXME verso di percorrenza.
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
            }
        }
        return currentRound;
    }

    public String requestTurnPlayer() {
        //TODO implement me.
        return null;
    }

    public boolean insertDieOperation() {
        //TODO implement me. When this operation goes well have to return true; This operation could be interrupted.
        return false;
    }

    public boolean useToolCardOperation() {
        //TODO implement me.
        return false;

    }

    //FIXME
    //se ti servono metodi di matchController creali e lasciali vuoti con un TODO.

}
