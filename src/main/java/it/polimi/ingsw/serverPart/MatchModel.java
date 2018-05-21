package it.polimi.ingsw.serverPart;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import it.polimi.ingsw.serverPart.card_container.PublicObjective;
import it.polimi.ingsw.serverPart.component_container.Grid;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.List;

public class MatchModel{

    private static final String CONFIG_PATH = "configurations/config.json";

    public MatchModel() throws FileNotFoundException{

        Configurations config = new Gson().fromJson(new FileReader(CONFIG_PATH), Configurations.class);
        this.lookForGrids(config.getGridsPath());
        this.lookForPublicObjectives(config.getPublicObjectivesPath());}

    private void lookForGrids(String path) throws FileNotFoundException{
        Gson gson = new Gson();
        TypeToken<List<Grid>> listType = new TypeToken<List<Grid>>(){};
        List<Grid> grids;
        grids = gson.fromJson(new FileReader(path), listType.getType());
        for(Grid i : grids){
            //TODO initialize grids
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
}
