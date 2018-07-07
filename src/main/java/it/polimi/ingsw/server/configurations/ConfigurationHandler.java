package it.polimi.ingsw.server.configurations;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import it.polimi.ingsw.server.App;
import it.polimi.ingsw.server.model.cards.effects.*;
import it.polimi.ingsw.server.model.cards.PublicObjective;
import it.polimi.ingsw.server.model.cards.ToolCard;
import it.polimi.ingsw.server.model.components.Grid;
import it.polimi.ingsw.server.custom_exception.NotValidConfigPathException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ConfigurationHandler {

    private static final int NUMBER_OF_TOOL_CARDS = 12;
    private static ConfigurationHandler instance;
    private Configurations config;

    private static final String DEFAULT_CONFIG_PATH = "src/main/resources/config.json";
    private static String CONFIG_PATH;
    private static final Object toolCardsGuard= new Object();
    private static final Object gridsGuard=new Object();
    private static final Object publicObjectivesGuard=new Object();

    private static final int NUMBER_OF_PUBLIC_OBJECTIVES = 10;

    /**
     * Constructor for ConfigurationHandler.
     *
     * @throws NotValidConfigPathException Thrown when no config.json file is found in the given path.
     */
    private ConfigurationHandler() throws NotValidConfigPathException {
        boolean succeed=false;
        File jarPath=new File(App.class.getProtectionDomain().getCodeSource().getLocation().getPath());
        CONFIG_PATH=jarPath.getParentFile().getAbsolutePath()+"/resources/config.json";
        Gson gson = new Gson();
        try {
            config = gson.fromJson(new FileReader(CONFIG_PATH), Configurations.class);
        } catch (FileNotFoundException e) {
            Logger.getLogger(this.getClass().getName()).log(Level.FINE,"File not found "+CONFIG_PATH+", trying default: "+DEFAULT_CONFIG_PATH);
            succeed = true;
        }
        if(succeed) {
            try {
                config = gson.fromJson(new FileReader(DEFAULT_CONFIG_PATH),Configurations.class);
            } catch (FileNotFoundException e) {
                throw new NotValidConfigPathException("No config.json found in "+DEFAULT_CONFIG_PATH);
            }
            Logger.getLogger(this.getClass().getName()).log(Level.FINE,"correctly loaded configurations at "+DEFAULT_CONFIG_PATH);
        }

    }

    /**
     * Getter for ConfigurationHandler.
     *
     * @return ConfigurationHandler.
     * @throws NotValidConfigPathException Thrown when no config.json file is found in the given path.
     */
    public static synchronized ConfigurationHandler getInstance() throws NotValidConfigPathException {
        if (instance==null) instance=new ConfigurationHandler();
        return instance;
    }

    /**
     * Getter for 'grids'.
     *
     * @return The list of grids.
     * @throws NotValidConfigPathException Thrown when no config.json file is found in the given path.
     */
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

    /**
     * Getter for 'publicObjectives'.
     *
     * @return The list of public objective cards.
     * @throws NotValidConfigPathException Thrown when no config.json file is found in the given path.
     */
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

    /**
     * Getter for 'minPlayersNumber'.
     *
     * @return An integer containing the minimum number of players per match.
     * @throws NotValidConfigPathException Thrown when no config.json file is found in the given path.
     */
    public int getMinPlayersNumber() throws NotValidConfigPathException{
        if (config.getMinPlayersNumber()!=0)
            return config.getMinPlayersNumber();
        else throw new NotValidConfigPathException("Incorrect config.json file: MinPlayersNumber needs to be instanced");
    }

    /**
     * Getter for 'publicObjectivesDistributed'.
     *
     * @return An integer containing the number of public objective cards per match.
     * @throws NotValidConfigPathException Thrown when no config.json file is found in the given path.
     */
    public int getPublicObjectivesDistributed() throws NotValidConfigPathException{
        if (config.getPublicObjectivesDistributed()>0 && config.getPublicObjectivesDistributed()<=NUMBER_OF_PUBLIC_OBJECTIVES)
            return config.getPublicObjectivesDistributed();
        else throw new NotValidConfigPathException("Incorrect config.json file: PublicObjectivesDistributed needs to be instanced");
    }

    /**
     * Getter for 'maxPlayersNumber'.
     *
     * @return An integer containing the maximum number of players in a match.
     * @throws NotValidConfigPathException Thrown when no config.json file is found in the given path.
     */
    public int getMaxPlayersNumber() throws NotValidConfigPathException{
        if (config.getMaxPlayersNumber()!=0)
            return config.getMaxPlayersNumber();
        else throw new NotValidConfigPathException("Incorrect config.json file: MaxPlayersNumber needs to be instanced");
    }

    /**
     * Getter for 'timerBeforeMatch'.
     *
     * @return An integer containing the timer before a match starts.
     * @throws NotValidConfigPathException Thrown when no config.json file is found in the given path.
     */
    public int getTimerBeforeMatch() throws NotValidConfigPathException{
        if (config.getTimerBeforeMatch()!=0)
            return config.getTimerBeforeMatch();
        else throw new NotValidConfigPathException("Incorrect config.json file: TimerBeforeMatch needs to be instanced");
    }

    /**
     * Getter for 'timerToChooseGrids'.
     *
     * @return An integer containing the timer to choose a grid.
     * @throws NotValidConfigPathException Thrown when no config.json file is found in the given path.
     */
    public int getTimerToChooseGrids() throws NotValidConfigPathException{
        if (config.getTimerToChooseGrid()!=0)
            return config.getTimerToChooseGrid();
        else throw new NotValidConfigPathException("Incorrect config.json file: TimerToChooseGrids needs to be instanced");
    }

    /**
     * Getter for 'rmiPort'.
     *
     * @return An integer containing the rmi port.
     * @throws NotValidConfigPathException Thrown when no config.json file is found in the given path.
     */
    public int getRmiPort() throws NotValidConfigPathException{
        if (config.getRmiPort()!=0){
            return config.getRmiPort();
        }
        else throw new NotValidConfigPathException("Incorrect config.json file: rmiPort needs to be instanced");
    }

    /**
     * Getter for 'socketPort'.
     *
     * @return An integer containing the socket port.
     * @throws NotValidConfigPathException Thrown when no config.json file is found in the given path.
     */
    public int getSocketPort() throws NotValidConfigPathException {
        if (config.getSocketPort() != 0) {
            return config.getSocketPort();
        } else throw new NotValidConfigPathException("Incorrect config.json file: socketPort needs to be instanced");
    }

    /**
     * Getter for 'toolCards'.
     *
     * @return The list of the tool cards.
     * @throws NotValidConfigPathException Thrown when no config.json file is found in the given path.
     */
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

    /**
     * Getter for 'toolCardsDistributed'.
     *
     * @return An integer containing the number of tool cards per match.
     * @throws NotValidConfigPathException Thrown when no config.json file is found in the given path.
     */
    public int getToolCardsDistributed() throws NotValidConfigPathException{
        if (config.getToolCardsDistributed()>0 && config.getToolCardsDistributed()<=NUMBER_OF_TOOL_CARDS)
            return config.getToolCardsDistributed();
        else throw new NotValidConfigPathException("Incorrect config.json file: ToolCardsDistributed needs to be instanced");
    }

    /**
     *
     * @return A Gson containing the tool cards.
     */
    public Gson getGsonForToolCards() {
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

    /**
     * Getter for 'timerForOperation'.
     *
     * @return An integer containing the timer for an operation.
     * @throws NotValidConfigPathException Thrown when no config.json file is found in the given path.
     */
    public int getTimerForOperation() throws NotValidConfigPathException {
        if (config.getTimerForOperation()>0) return config.getTimerForOperation();
        else throw new NotValidConfigPathException("Incorrect config.json file: timerForOperation needs to be instanced");
    }

    /**
     * Getter for 'numberOfMatchHandled'.
     *
     * @return An integer containing the number of match handled.
     */
    public int getNumberOfMatchHandled() {
        return config.getNumberOfMatchHandled();
    }
}
