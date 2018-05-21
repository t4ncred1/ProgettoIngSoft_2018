package it.polimi.ingsw.serverPart;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import it.polimi.ingsw.serverPart.card_container.PublicObjective;
import it.polimi.ingsw.serverPart.component_container.Box;
import it.polimi.ingsw.serverPart.component_container.Grid;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.List;

public class MatchModel{

    private List<Grid> grids;

    public MatchModel(MatchConfigurationsInterface config) throws FileNotFoundException{

        this.lookForGrids(config.getGridsPath());
        this.lookForPublicObjectives(config.getPublicObjectivesPath());

    }

    private void lookForGrids(String path) throws FileNotFoundException{
        Gson gson = new Gson();
        TypeToken<List<Grid>> listType = new TypeToken<List<Grid>>(){};

        grids = gson.fromJson(new FileReader(path), listType.getType());
        for(Grid i : grids){
            //TODO initialize grids with box observers
//          System.out.println(i.toString() + "\n");
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

//            System.out.println(i.toString() + "\n");
        }
    }
}
