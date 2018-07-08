package it.polimi.ingsw.client.configurations;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

@FunctionalInterface
interface Function<T,R>{
    /**
     * Define a model for retrieve method.
     *
     * @throws IOException Thrown if an I/O error occurs.
     */
    void retrieve(T t,R r) throws IOException;
}

public final class DataRetriever {
    private static final String GRID_DATA= "grid";
    private static final String ALL_GRIDS_DATA= "all_grid";
    private static final String TOOL_DATA="tool";
    private static final String DICE_POOL_DATA= "dice_pool";
    private static final String GRID_SELECTION_DATA = "grid_selection";
    private static final String ROUND_TRACK_DATA= "round_track";
    private static final String PUBLIC_OBJ="public_objectives";
    private static final String PRIVATE_OBJ = "private_objective";

    private static final Map<String, Function<DataInputStream,Logger>>
            DATA = new HashMap<>();
    static {
        DATA.put(GRID_DATA, DataHandler::retrieveGrid);
        DATA.put(ALL_GRIDS_DATA, DataHandler::retrieveAllGrids);
        DATA.put(DICE_POOL_DATA, DataHandler::retrieveDicePool);
        DATA.put(ROUND_TRACK_DATA, DataHandler::retrieveRoundTrack);
        DATA.put(TOOL_DATA, DataHandler::retrieveToolCards);
        DATA.put(GRID_SELECTION_DATA, DataHandler::retrieveGridSelection);
        DATA.put(PUBLIC_OBJ,DataHandler::retrievePublicObjectives);
        DATA.put(PRIVATE_OBJ,DataHandler::retrievePrivateObjective);
    }

    /**
     * Constructor for DataRetriever.
     */
    private DataRetriever(){
        throw new AssertionError();
    }

    /**
     * Retrieves data from server.
     *
     * @param dataType The type of the data.
     * @param inputStream Network input stream.
     * @param logger A logger.
     * @throws IOException Thrown if an I/O error occurs.
     */
    public static void retrieve(String dataType, DataInputStream inputStream, Logger logger) throws IOException {
        Function<DataInputStream,Logger> function= DATA.get(dataType);
        if (function == null) {
            logger.log(Level.SEVERE, "Unexpected dataType from server: {0}", dataType);
        }else {
            function.retrieve(inputStream, logger);
        }
    }
}
