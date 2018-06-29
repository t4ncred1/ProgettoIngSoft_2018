package it.polimi.ingsw.server.configurations;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import it.polimi.ingsw.server.model.cards.effects.*;
import it.polimi.ingsw.server.model.cards.PublicObjective;
import it.polimi.ingsw.server.model.cards.ToolCard;
import it.polimi.ingsw.server.model.components.Grid;
import it.polimi.ingsw.server.custom_exception.NotValidConfigPathException;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.List;

public class ConfigurationHandler {

    private static final int NUMBER_OF_TOOL_CARDS = 12;
    private static ConfigurationHandler instance;
    private Configurations config;

    private static final String CONFIG_PATH="src/main/resources/config.json";
    private static final Object toolCardsGuard= new Object();
    private static final Object gridsGuard=new Object();
    private static final Object publicObjectivesGuard=new Object();

    private static final int NUMBER_OF_PUBLIC_OBJECTIVES = 10;
    //TODO do this for timers too.

    private ConfigurationHandler() throws NotValidConfigPathException {
        Gson gson = new Gson();
        try {
            config = gson.fromJson(new FileReader(CONFIG_PATH), Configurations.class);
        } catch (FileNotFoundException e) {
            throw new NotValidConfigPathException("No config.json found in "+CONFIG_PATH);
        }
    }

    public static synchronized ConfigurationHandler getInstance() throws NotValidConfigPathException {
        if (instance==null) instance=new ConfigurationHandler();
        return instance;
    }

    public List<Grid> getGrids() throws NotValidConfigPathException {
        Gson gson = new Gson();
        TypeToken<List<Grid>> listType = new TypeToken<List<Grid>>(){};
        try {
            List<Grid> grids;
            synchronized (gridsGuard) {
                grids = gson.fromJson(new FileReader(config.getGridsPath()), listType.getType());
            }
            for (Grid grid : grids) {
                if (grid == null) throw new NotValidConfigPathException("Grids are not read correctly.");
                grid.initializeAllObservers();
            }
            for(Grid grid: grids){
                grid.initializeAllObservers();
            }
            return grids;
        } catch (FileNotFoundException e) {
            throw new NotValidConfigPathException("Incorrect grids path in configuration file: "+config.getGridsPath());
        }
    }

    public List<PublicObjective> getPublicObjectives() throws NotValidConfigPathException {
        Gson gson = new Gson();
        TypeToken<List<PublicObjective>> listType = new TypeToken<List<PublicObjective>>(){};
        synchronized (publicObjectivesGuard) {
            try {
                return gson.fromJson(new FileReader(config.getPublicObjectivesPath()), listType.getType());
            } catch (FileNotFoundException e) {
                throw new NotValidConfigPathException("Incorrect public objectives path in configuration file: " + config.getPublicObjectivesPath());
            }
        }
    }

    public int getMinPlayersNumber() throws NotValidConfigPathException{
        if (config.getMinPlayersNumber()!=0)
            return config.getMinPlayersNumber();
        else throw new NotValidConfigPathException("Incorrect config.json file: MinPlayersNumber needs to be instanced");
    }

    public int getPublicObjectivesDistributed() throws NotValidConfigPathException{
        if (config.getPublicObjectivesDistributed()>0 && config.getPublicObjectivesDistributed()<=NUMBER_OF_PUBLIC_OBJECTIVES)
            return config.getPublicObjectivesDistributed();
        else throw new NotValidConfigPathException("Incorrect config.json file: PublicObjectivesDistributed needs to be instanced");
    }

    public int getMaxPlayersNumber() throws NotValidConfigPathException{
        if (config.getMaxPlayersNumber()!=0)
            return config.getMaxPlayersNumber();
        else throw new NotValidConfigPathException("Incorrect config.json file: MaxPlayersNumber needs to be instanced");
    }

    public int getTimerBeforeMatch() throws NotValidConfigPathException{
        if (config.getTimerBeforeMatch()!=0)
            return config.getTimerBeforeMatch();
        else throw new NotValidConfigPathException("Incorrect config.json file: TimerBeforeMatch needs to be instanced");
    }

    public int getTimerToChooseGrids() throws NotValidConfigPathException{
        if (config.getTimerToChooseGrid()!=0)
            return config.getTimerToChooseGrid();
        else throw new NotValidConfigPathException("Incorrect config.json file: TimerToChooseGrids needs to be instanced");
    }


    public int getRmiPort() throws NotValidConfigPathException{
        if (config.getRmiPort()!=0){
            return config.getRmiPort();
        }
        else throw new NotValidConfigPathException("Incorrect config.json file: rmiPort needs to be instanced");
    }

    public int getSocketPort() throws NotValidConfigPathException {
        if (config.getSocketPort() != 0) {
            return config.getSocketPort();
        } else throw new NotValidConfigPathException("Incorrect config.json file: socketPort needs to be instanced");
    }

    public List<ToolCard> getToolCards() throws NotValidConfigPathException {
        Gson gson = getGsonForToolCards();
        TypeToken<List<ToolCard>> listTypeToken = new TypeToken<List<ToolCard>>(){};

        synchronized (toolCardsGuard){
            try {
                return gson.fromJson(new FileReader(config.getToolCardsPath()), listTypeToken.getType());
            } catch (FileNotFoundException e) {
                throw new NotValidConfigPathException("Incorrect toolcards path in configuration file: "+config.getToolCardsPath());
            }
        }
    }

    public int getToolCardsDistributed() throws NotValidConfigPathException{
        if (config.getToolCardsDistributed()>0 && config.getToolCardsDistributed()<=NUMBER_OF_TOOL_CARDS)
            return config.getToolCardsDistributed();
        else throw new NotValidConfigPathException("Incorrect config.json file: ToolCardsDistributed needs to be instanced");
    }

    private Gson getGsonForToolCards() {
        GsonBuilder builder= new GsonBuilder();
        //Create a RuntimeTypeAdapterFactory for Effect interface
        RuntimeTypeAdapterFactory<Effect> adapterFactory= RuntimeTypeAdapterFactory.of(Effect.class);

        //Register all classes implementing Effect interface
        adapterFactory.registerSubtype(ChangeValueDiceEffect.class,ChangeValueDiceEffect.class.getName());
        adapterFactory.registerSubtype(IncrementDiceEffect.class, IncrementDiceEffect.class.getName());
        adapterFactory.registerSubtype(InsertDieInGridEffect.class,InsertDieInGridEffect.class.getName());
        adapterFactory.registerSubtype(InsertDieInPoolEffect.class, InsertDieInPoolEffect.class.getName());
        adapterFactory.registerSubtype(InsertDieInRoundTrackEffect.class,InsertDieInRoundTrackEffect.class.getName());
        adapterFactory.registerSubtype(InverseDieValueEffect.class,InverseDieValueEffect.class.getName());
        adapterFactory.registerSubtype(RemoveDieFromRoundTrackEffect.class,RemoveDieFromRoundTrackEffect.class.getName());
        adapterFactory.registerSubtype(RemoveDieFromPoolEffect.class, RemoveDieFromPoolEffect.class.getName());
        adapterFactory.registerSubtype(RemoveDieFromGridEffect.class, RemoveDieFromGridEffect.class.getName());
        adapterFactory.registerSubtype(SwapRTDieAndDPDieEffect.class, SwapRTDieAndDPDieEffect.class.getName());
        adapterFactory.registerSubtype(SwapDieEffect.class, SwapDieEffect.class.getName());
        //associate the factory and the builder
        builder.registerTypeAdapterFactory(adapterFactory);
        return builder.create();
    }

    public int getTimerForOperation() throws NotValidConfigPathException {
        if (config.getTimerForOperation()>0) return config.getTimerForOperation();
        else throw new NotValidConfigPathException("Incorrect config.json file: timerForOperation needs to be instanced");
    }
}
