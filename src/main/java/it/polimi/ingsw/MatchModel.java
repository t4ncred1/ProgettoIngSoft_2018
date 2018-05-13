package it.polimi.ingsw;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import it.polimi.ingsw.grid_container.Grid;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.List;

public class MatchModel {

    private final static String CONFIG_PATH = "configurations/config.json";

    public MatchModel(){

        Gson gson_conf = new Gson();
        try {
            Configurations config = gson_conf.fromJson(new FileReader(CONFIG_PATH), Configurations.class);
            this.lookForGrids(config.getGridsPath());
        } catch (FileNotFoundException e){
            System.out.println("il file " +CONFIG_PATH+" non esiste.");
            e.printStackTrace();
        }
    }

    private void lookForGrids(String json){
        Gson gson = new Gson();
        TypeToken<List<Grid>> listType = new TypeToken<List<Grid>>(){};
        List<Grid> grids;
        try {
            grids = gson.fromJson(new FileReader(json), listType.getType());

            for(Grid i : grids){
                System.out.println(i.getName() + "\n");
            }
        }catch (FileNotFoundException e){
            System.out.println("il file " +json+" , specificato in "+CONFIG_PATH+" non esiste.");
        }


    }
}
