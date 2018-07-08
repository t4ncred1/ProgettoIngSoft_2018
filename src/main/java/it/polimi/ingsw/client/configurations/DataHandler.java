package it.polimi.ingsw.client.configurations;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import it.polimi.ingsw.client.MainClient;
import it.polimi.ingsw.client.Proxy;
import it.polimi.ingsw.server.configurations.RuntimeTypeAdapterFactory;
import it.polimi.ingsw.server.custom_exception.InvalidOperationException;
import it.polimi.ingsw.server.model.cards.ToolCard;
import it.polimi.ingsw.server.model.cards.effects.*;
import it.polimi.ingsw.server.model.components.Die;
import it.polimi.ingsw.server.model.components.DieConstraints;
import it.polimi.ingsw.server.model.components.DieToConstraintsAdapter;
import it.polimi.ingsw.server.model.components.Grid;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class DataHandler {
    private static final String PING_MESSAGE="";
    private static final long SAMPLE_TIME = 30;

    /**
     * Constructor for DataHandler.
     */
    private DataHandler(){
        throw new AssertionError();
    }

    /**
     *
     * @param inputStream Network input stream.
     * @return A string containing a server message.
     * @throws IOException
     */
    private static String readRemoteInput(DataInputStream inputStream) throws IOException {
        String read;
        do{
            try {
                Thread.sleep(SAMPLE_TIME);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            read=inputStream.readUTF();
        }while (read.equals(PING_MESSAGE));
        return read;
    }

    /**
     *
     * @return A Gson containing the grids.
     */
    private static Gson getGsonForGrid() {
        GsonBuilder builder= new GsonBuilder();
        RuntimeTypeAdapterFactory<DieConstraints> adapterFactory= RuntimeTypeAdapterFactory.of(DieConstraints.class)
                .registerSubtype(DieToConstraintsAdapter.class, DieToConstraintsAdapter.class.getName());

        builder.registerTypeAdapterFactory(adapterFactory);
        return builder.create();
    }

    /**
     *
     * @return A Gson containing the tool cards.
     */
    private static Gson getGsonForToolCards() {
        GsonBuilder builder= new GsonBuilder();
        //Create a RuntimeTypeAdapterFactory for Effect interface
        RuntimeTypeAdapterFactory<Effect> adapterFactory= RuntimeTypeAdapterFactory.of(Effect.class)
                .registerSubtype(ChangeValueDiceEffect.class,ChangeValueDiceEffect.class.getName())
                .registerSubtype(IncrementDiceEffect.class, IncrementDiceEffect.class.getName())
                .registerSubtype(InsertDieInGridEffect.class,InsertDieInGridEffect.class.getName())
                .registerSubtype(InsertDieInPoolEffect.class, InsertDieInPoolEffect.class.getName())
                .registerSubtype(InsertDieInRoundTrackEffect.class,InsertDieInRoundTrackEffect.class.getName())
                .registerSubtype(InverseDieValueEffect.class,InverseDieValueEffect.class.getName())
                .registerSubtype(RemoveDieFromRoundTrackEffect.class,RemoveDieFromRoundTrackEffect.class.getName())
                .registerSubtype(RemoveDieFromPoolEffect.class, RemoveDieFromPoolEffect.class.getName())
                .registerSubtype(RemoveDieFromGridEffect.class, RemoveDieFromGridEffect.class.getName())
                .registerSubtype(SwapRTDieAndDPDieEffect.class, SwapRTDieAndDPDieEffect.class.getName())
                .registerSubtype(SwapDieEffect.class, SwapDieEffect.class.getName());

        //associate the factory and the builder
        builder.registerTypeAdapterFactory(adapterFactory);
        return builder.create();
    }

    /**
     * Retrieves a single grid from server.
     *
     * @param inputStream Network input stream.
     * @param logger A logger.
     * @throws IOException Thrown if an I/O error occurs.
     */
    static void retrieveGrid(DataInputStream inputStream, Logger logger) throws IOException {
        logger.log(Level.FINE,"Retrieving a single grid from server");
        String serverResponse;
        Gson gson= getGsonForGrid();
        serverResponse=readRemoteInput(inputStream);
        Grid grid = gson.fromJson(serverResponse, Grid.class);
        Proxy.getInstance().updateGrid(grid);
        logger.log(Level.FINE,"Grid retrieved and set");
    }

    /**
     * Retrieve all grids from server.
     *
     * @param inputStream Network input stream.
     * @param logger A logger.
     * @throws IOException Thrown if an I/O error occurs.
     */
    static void retrieveAllGrids(DataInputStream inputStream, Logger logger) throws IOException {
        logger.log(Level.FINE,"Retrieving all grids from server");
        String serverResponse;
        HashMap<String,Grid> playersGrids;
        TypeToken<HashMap<String,Grid>> typeToken= new TypeToken<HashMap<String, Grid>>(){};
        Gson gson= getGsonForGrid();
        serverResponse=readRemoteInput(inputStream);
        playersGrids= gson.fromJson(serverResponse, typeToken.getType());
        List<String> connectedPlayers;
        TypeToken<ArrayList<String>> typeToken2= new TypeToken<ArrayList<String>>(){};
        serverResponse=readRemoteInput(inputStream);
        connectedPlayers=gson.fromJson(serverResponse, typeToken2.getType());
        Proxy.getInstance().setGridsForEachPlayer(playersGrids,connectedPlayers);
        logger.log(Level.FINE,"Grids retrieved and set");
    }

    /**
     * Retrieve the dice pool from server.
     *
     * @param inputStream Network input stream.
     * @param logger A logger.
     * @throws IOException Thrown if an I/O error occurs.
     */
    static void retrieveDicePool(DataInputStream inputStream, Logger logger) throws IOException {
        logger.log(Level.FINE,"Retrieving dice pool from server");
        ArrayList<Die> dicePool;
        TypeToken<ArrayList<Die>> typeToken= new TypeToken<ArrayList<Die>>(){};
        Gson gson = new Gson();
        dicePool=gson.fromJson(readRemoteInput(inputStream), typeToken.getType());
        Proxy.getInstance().setDicePool(dicePool);
        logger.log(Level.FINE,"Dice pool retrieved and set");
    }

    /**
     * Retrieve the tool cards from server.
     *
     * @param inputStream Network input stream.
     * @param logger A logger.
     * @throws IOException Thrown if an I/O error occurs.
     */
    static void retrieveToolCards(DataInputStream inputStream, Logger logger) throws IOException {
        logger.log(Level.FINE,"Retrieving tool cards from server");
        ArrayList<ToolCard> toolCards;
        TypeToken<ArrayList<ToolCard>> typeToken= new TypeToken<ArrayList<ToolCard>>(){};
        Gson gson= getGsonForToolCards();
        toolCards=gson.fromJson(readRemoteInput(inputStream), typeToken.getType());
        Proxy.getInstance().setToolCards(toolCards);
        logger.log(Level.FINE,"Tool Cards retrieved and set");
    }

    /**
     * Retrieve the round track from server.
     * @param inputStream Network input stream.
     * @param logger A logger.
     * @throws IOException Thrown if an I/O error occurs.
     */
    static void retrieveRoundTrack(DataInputStream inputStream, Logger logger) throws IOException {
        logger.log(Level.FINE,"Retrieving round track from server");
        ArrayList<Die> roundTrack;
        Gson gson = new Gson();
        TypeToken<ArrayList<Die>> typeToken= new TypeToken<ArrayList<Die>>(){};
        roundTrack=gson.fromJson(readRemoteInput(inputStream), typeToken.getType());
        Proxy.getInstance().setRoundTrack(roundTrack);
        logger.log(Level.FINE,"Round track retrieved and set");
    }

    /**
     * Retrieve the grid selection (4 grids, 1 choice).
     *
     * @param inputStream Network input stream.
     * @param logger A logger.
     * @throws IOException Thrown if an I/O error occurs.
     */
    static void retrieveGridSelection(DataInputStream inputStream, Logger logger) throws IOException{
        logger.log(Level.FINE,"Server will send grids within the next stream");
        ArrayList<Grid> grids;
        TypeToken<ArrayList<Grid>> typeToken= new TypeToken<ArrayList<Grid>>(){};
        Gson gson= getGsonForGrid();
        String serverResponse=readRemoteInput(inputStream);
        grids= gson.fromJson(serverResponse, typeToken.getType());
        try {
            Proxy.getInstance().setGridsSelection(grids);
        } catch (InvalidOperationException e) {
            logger.log(Level.SEVERE,"A null-pointer was passed instead of a list of grids");    //thrown by proxy if passed grid list is null
        }
        logger.log(Level.FINE,"Grid retrieved and set in proxy");
        MainClient.getInstance().notifyGridsAreInProxy();
    }
}
