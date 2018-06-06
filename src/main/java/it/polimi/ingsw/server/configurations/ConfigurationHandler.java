package it.polimi.ingsw.server.configurations;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import it.polimi.ingsw.server.cards.PublicObjective;
import it.polimi.ingsw.server.components.Grid;
import it.polimi.ingsw.server.custom_exception.NotValidConfigPathException;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.List;

public class ConfigurationHandler {
    private static final String CONFIG_PATH="src/main/resources/config.json";
    private static ConfigurationHandler instance;
    private static Configurations config;

    private ConfigurationHandler() throws NotValidConfigPathException {
        Gson gson = new Gson();
        try {
            config = gson.fromJson(new FileReader(CONFIG_PATH), Configurations.class);
        } catch (FileNotFoundException e) {
            throw new NotValidConfigPathException("No config.json found in "+CONFIG_PATH);
        }
    }

    public static List<Grid> getGrids() throws NotValidConfigPathException {
        if (instance==null) instance=new ConfigurationHandler();
        Gson gson = new Gson();
        TypeToken<List<Grid>> listType = new TypeToken<List<Grid>>(){};
        try {
            List<Grid> grids=gson.fromJson(new FileReader(config.getGridsPath()), listType.getType());
            for(Grid grid: grids){
                grid.initializeAllObservers();
            }
            if (grids==null) throw new NotValidConfigPathException("Grids are not read correctly.");
            return grids;
        } catch (FileNotFoundException e) {
            throw new NotValidConfigPathException("Incorrect grids path in configuration file: "+config.getGridsPath());
        }
    }

    public static List<PublicObjective> getPublicObjectives() throws NotValidConfigPathException {
        if (instance==null) instance=new ConfigurationHandler();
        Gson gson = new Gson();
        TypeToken<List<PublicObjective>> listType = new TypeToken<List<PublicObjective>>(){};
        try {
            return gson.fromJson(new FileReader(config.getPublicObjectivesPath()), listType.getType());
        } catch (FileNotFoundException e) {
            throw new NotValidConfigPathException("Incorrect public objectives path in configuration file: "+config.getPublicObjectivesPath());
        }
    }

    public static int getMinPlayersNumber() throws NotValidConfigPathException{
        if (instance==null) instance=new ConfigurationHandler();
        if (config.getMinPlayersNumber()!=0)
            return config.getMinPlayersNumber();
        else throw new NotValidConfigPathException("Incorrect config.json file: MinPlayersNumber needs to be instanced");
    }

    public static int getMaxPlayersNumber() throws NotValidConfigPathException{
        if (instance==null) instance=new ConfigurationHandler();
        if (config.getMaxPlayersNumber()!=0)
            return config.getMaxPlayersNumber();
        else throw new NotValidConfigPathException("Incorrect config.json file: MaxPlayersNumber needs to be instanced");
    }

    public static int getTimerBeforeMatch() throws NotValidConfigPathException{
        if (instance==null) instance=new ConfigurationHandler();
        if (config.getTimerBeforeMatch()!=0)
            return config.getTimerBeforeMatch();
        else throw new NotValidConfigPathException("Incorrect config.json file: TimerBeforeMatch needs to be instanced");
    }

    public static int getTimerToChooseGrids() throws NotValidConfigPathException{
        if (instance==null) instance=new ConfigurationHandler();
        if (config.getTimerToChooseGrid()!=0)
            return config.getTimerToChooseGrid();
        else throw new NotValidConfigPathException("Incorrect config.json file: TimerToChooseGrids needs to be instanced");
    }

}
