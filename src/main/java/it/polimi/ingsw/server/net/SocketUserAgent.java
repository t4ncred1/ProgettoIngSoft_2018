package it.polimi.ingsw.server.net;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import it.polimi.ingsw.server.App;
import it.polimi.ingsw.server.MatchController;
import it.polimi.ingsw.server.MatchHandler;
import it.polimi.ingsw.server.configurations.ConfigurationHandler;
import it.polimi.ingsw.server.custom_exception.connection_exceptions.IllegalRequestException;
import it.polimi.ingsw.server.model.cards.ToolCard;
import it.polimi.ingsw.server.model.components.Die;
import it.polimi.ingsw.server.model.components.DieConstraints;
import it.polimi.ingsw.server.model.components.DieToConstraintsAdapter;
import it.polimi.ingsw.server.model.components.Grid;
import it.polimi.ingsw.server.configurations.RuntimeTypeAdapterFactory;
import it.polimi.ingsw.server.custom_exception.*;
import it.polimi.ingsw.server.custom_exception.DisconnectionException;
import it.polimi.ingsw.server.custom_exception.InvalidOperationException;
import it.polimi.ingsw.server.custom_exception.InvalidUsernameException;
import it.polimi.ingsw.server.custom_exception.ReconnectionException;


import java.io.*;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.*;

public class SocketUserAgent extends Thread implements UserInterface {

    private DataInputStream inputStream;
    private DataOutputStream outputStream;
    private MatchController gameHandling;
    private boolean inGame;
    private boolean connected;
    private boolean gridSet;
    private boolean gameFinished;
    private boolean statusChanged;
    private boolean readyToReceiveGridsRequest;
    private Lock lock;
    private Lock syncLock;
    private Condition conditionLock;
    private Condition conditionSyncLock;
    private static final String DISCONNECTED_LOG="Disconnected";

    private String username;

    private static final String UNEXPECTED_MESSAGE_RECEIVED = "Unexpected message from {0}: received {1} instead of {2}";
    private static long connectionNumber= 0L;
    private long actualConnectionNumber;
    private Handler handler;
    private Logger logger;



    private static final String PING_MESSAGE= "";
    private static final String HELLO_MESSAGE = "hello";

    private static final String LOGIN_MESSAGE_FROM_SERVER = "login";
    private static final String SUCCESSFULLY_LOGGED = "logged";
    private static final String SERVER_FULL = "notLogged_server_full";
    private static final String USERNAME_NOT_AVAILABLE= "notLogged_username_not_available";
    private static final String RECONNECTED ="reconnected";


    private static final String LAUNCHING_GAME = "launching_game";
    private static final String GAME_STARTED = "game_started";
    private static final String GAME_ALREADY_IN_PROGRESS = "in_progress";

    private static final String TRY_LOGOUT= "try_logout";
    private static final String SUCCESSFULLY_LOGGED_OUT= "logged_out";

    private static final String OK_REQUEST = "ok";
    private static final String NOT_OK_REQUEST = "retry";
    private static final String SECURITY_VIOLATION = "illegal_access";


    private static final String REQUEST_GRID = "get_grids";
    private static final String GRID_ALREADY_SELECTED= "grid_selected";
    private static final String CHOOSE_GRID="set_grid";

    private static final String TURN_PLAYER = "turn_player";
    private static final String GAME_FINISHED= "finished";

    private static final String INSERT_DIE = "insert_die";
    private static final String USE_TOOL_CARD="tool_card";
    private static final String EXECUTE_TOOL_CARD = "execute_tool";
    private static final String INVALID_POSITION = "invalid_index";
    private static final String ALREADY_DONE_OPERATION = "already_done";
    private static final String NOT_VALID_REQUEST = "not_valid_request";
    private static final String END_TURN ="end_turn";
    private static final String GRID_DATA= "grid";
    private static final String GRID_SELECTION_DATA = "grid_selection";
    private static final String ALL_GRIDS_DATA= "all_grid";
    private static final String TOOL_DATA="tool";
    private static final String ROUND_TRACK_DATA= "round_track";
    private static final String END_DATA= "end_data";
    private static final String DICE_POOL_DATA= "dice_pool";
    private static final String DISCONNECTION = "disconnected";

    private static final String PLAYERS_POINTS="points";

    private static final String DEFAULT_LOG_DIR="src/main/resources/log_files/SocketUserAg_%u.log";

    /**
     * Constructor for SocketUserAgent.
     *
     * @param client Client connecting.
     */
    SocketUserAgent(Socket client) {
        boolean succeeded =true;
        connected=true;
        actualConnectionNumber =connectionNumber++;
        String LOG_DIR = new File(App.class.getProtectionDomain().getCodeSource().getLocation().getPath()).getParentFile().getAbsolutePath()+"/resources/client_log/ClientLog_%u.log";

        try {
            logger = Logger.getLogger(SocketUserAgent.class.getName()+actualConnectionNumber);
            handler = new FileHandler(LOG_DIR);
            SimpleFormatter formatter = new SimpleFormatter();
            handler.setFormatter(formatter);
            logger.setLevel(Level.FINER);
            logger.addHandler(handler);
        } catch (IOException e) {
            logger.log(Level.CONFIG, "Unable to get log directory: "+ LOG_DIR+", trying default directory "+ DEFAULT_LOG_DIR);
            succeeded = false;
        }
        if (!succeeded){
            try {
                logger = Logger.getLogger(SocketUserAgent.class.getName()+actualConnectionNumber);
                handler = new FileHandler(DEFAULT_LOG_DIR);
                SimpleFormatter formatter = new SimpleFormatter();
                handler.setFormatter(formatter);
                logger.setLevel(Level.FINER);
                logger.addHandler(handler);
            } catch (IOException e) {
                logger.log(Level.CONFIG, "Unable to get log directory: "+ DEFAULT_LOG_DIR,e);
            }
            logger.log(Level.CONFIG,"correctly got loggers at "+DEFAULT_LOG_DIR);
        }
        gridSet=false;
        gameFinished=false;
        statusChanged=false;
        syncLock = new ReentrantLock();
        lock = new ReentrantLock();
        conditionLock = lock.newCondition();
        conditionSyncLock= syncLock.newCondition();
        try{
            this.inputStream= new DataInputStream(client.getInputStream());
            this.outputStream= new DataOutputStream(client.getOutputStream());
        }
        catch(IOException e){
            logger.log(Level.SEVERE,"Can't get streams from socket!",e);
        }
    }


    @Override
    public void run(){
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            handler.flush();
            handler.close();
        }));
        logger.fine("Connection request received on Socket system");
        try {
            lock.lock();
            handleConnection();
            handleLogin();
            lock.unlock();
            handleLogoutRequestBeforeStart();
            handleGameInitialization();
            handleGameLogic();
        } catch (IOException e) {
            if(username==null)logger.log(Level.INFO, "A client disconnected during login");
            else logger.log(Level.INFO, "{0} disconnected during login",username);
            logger.log(Level.FINE, "",e);
        } catch (DisconnectionException e) {
            logger.log(Level.FINE, "Client logged out",e );
        } catch (IllegalRequestException e) {
            logger.log(Level.SEVERE,"Security error: invalid access!",e);
            try {
                outputStream.writeUTF(SECURITY_VIOLATION);
            } catch (IOException e1) {
                logger.log(Level.WARNING, "Client disconnected on security notification");
            }
        } finally {
            logger.log(Level.FINE, "Logger file closed. SocketUserAgent {0} shut down",actualConnectionNumber);
            handler.close();
        }
    }
    private void handleGameLogic() throws IOException, IllegalRequestException {
        do{
            String command=inputStream.readUTF();
            switch (command){
                case INSERT_DIE:
                    logger.log(Level.FINE,"{0} requested to insert die",username);
                    handleDieInsertion();
                    break;
                case USE_TOOL_CARD:
                    logger.log(Level.FINE,"{0} requested to use tool card",username);
                    handleToolCardLogic();
                    break;
                case END_TURN:
                    outputStream.writeUTF(OK_REQUEST);
                    logger.log(Level.FINE,"{0} requested to end turn",username);
                    gameHandling.notifyEnd(this);
                    break;
                default:
                    logger.log(Level.SEVERE, "Unexpected command while handling game logic: {0}", command);
            }
        }while (!gameFinished);
    }


    private void handleToolCardLogic() throws IOException, IllegalRequestException {
        int toolCardIndex= inputStream.readInt();
        try {
            gameHandling.tryToUseToolCard(this,toolCardIndex);
            logger.log(Level.FINE, "{0} notified about OK REQUEST", username);
            outputStream.writeUTF(OK_REQUEST);
            handleEffects(toolCardIndex);
        } catch (OperationAlreadyDoneException e) {
            logger.log(Level.FINE, "{0} notified about tool card already used", username);
            outputStream.writeUTF(NOT_OK_REQUEST);
        } catch (NotValidParameterException e) {
            logger.log(Level.FINE, "{0} notified that does not exist a tool card at that index", username);
            outputStream.writeUTF(INVALID_POSITION);
        }
    }

    private void handleEffects(int toolCardIndex) throws IOException, IllegalRequestException {
        boolean ok=false;
        while (!ok){
            String request= inputStream.readUTF();
            if(request.equals(EXECUTE_TOOL_CARD)){
                logger.log(Level.FINE, "Received execute tool card request");
                gameHandling.executeToolCard(this, toolCardIndex);
                ok=true;
            } else{
                handleEffectsParamsSetting(request);
            }
        }
    }

    private void handleEffectsParamsSetting(String request) throws IOException, IllegalRequestException {
        logger.log(Level.FINE, "Received set effect parameters request. Effect: {0}", request);
        String read=inputStream.readUTF();
        Gson gson= new Gson();
        TypeToken<ArrayList<String>> typeToken= new TypeToken<ArrayList<String>>(){};
        ArrayList<String> params=gson.fromJson(read, typeToken.getType());
        logger.log(Level.FINE, "Received effect's parameters from client");
        try {
            gameHandling.setEffectParameters(this, request, params);
            outputStream.writeUTF(OK_REQUEST);
            logger.log(Level.FINE,"Set effect parameters");
        } catch (InvalidOperationException e) {
            logger.log(Level.SEVERE,"Not proper effect requested" , e);
            outputStream.writeUTF(NOT_VALID_REQUEST);
            throw new IllegalRequestException();
        } catch (NotValidParameterException e) {
            logger.log(Level.SEVERE,"Invalid parameter passed" , e);
            outputStream.writeUTF(NOT_OK_REQUEST);
        }
    }



    /**
     * Handles die insertion operation.
     *
     * @throws IOException Thrown when an I/O error occurs.
     * @throws IllegalRequestException See securityControl doc in MatchController class.
     */

    private void handleDieInsertion() throws IOException, IllegalRequestException {
        int position=inputStream.readInt();
        int x = inputStream.readInt();
        int y= inputStream.readInt();
        try {
            lock.lock();
            gameHandling.insertDie(this,position,x,y);
            outputStream.writeUTF(OK_REQUEST);
        } catch (InvalidOperationException e) {
            outputStream.writeUTF(NOT_OK_REQUEST);
        } catch (NotInPoolException e) {
            outputStream.writeUTF(INVALID_POSITION);
        } catch (OperationAlreadyDoneException e) {
            outputStream.writeUTF(ALREADY_DONE_OPERATION);
        } finally {
            lock.unlock();
        }

    }

    private void sendUpdatedGrid() throws IOException, IllegalRequestException {
        Grid grid =gameHandling.getPlayerGrid(this);
        Gson gson = getGsonForGrid();
        String toSend= gson.toJson(grid);
        outputStream.writeUTF(toSend);
    }

    /**
     *
     * @return A Gson to parse grids.
     */
    private Gson getGsonForGrid() {
        GsonBuilder builder= new GsonBuilder();
        RuntimeTypeAdapterFactory<DieConstraints> adapterFactory= RuntimeTypeAdapterFactory.of(DieConstraints.class)
                .registerSubtype(DieToConstraintsAdapter.class, DieToConstraintsAdapter.class.getName());

        builder.registerTypeAdapterFactory(adapterFactory);
        return builder.create();
    }

    /**
     * Handles game initialization (grids request, grid choice).
     *
     * @throws IOException Thrown when an I/O error occurs.
     * @throws IllegalRequestException See securityControl doc in MatchController class.
     */
    private void handleGameInitialization() throws IOException, IllegalRequestException {
        try {
            handleGridsRequest();
            boolean localGridSet;
            do {
                localGridSet = handleGridSet();
            }
            while (!localGridSet);
        } catch (InvalidOperationException e) {
            outputStream.writeUTF(GRID_ALREADY_SELECTED);
            logger.log(Level.CONFIG, "Player already selected a grid", e);
        }
        lock.lock();
        gridSet=true;
        conditionLock.signal();
        lock.unlock();
    }

    /**
     * Handles grid choice.
     *
     * @return True if grid choice is fine.
     * @throws IOException Thrown when an I/O error occurs.
     * @throws IllegalRequestException See securityControl doc in MatchController class.
     */
    private boolean handleGridSet() throws IOException, IllegalRequestException {
        String request;
        do {
            request = inputStream.readUTF();
             // FIXME: 26/06/2018
            if (request.equals(CHOOSE_GRID)) {
                outputStream.writeUTF(OK_REQUEST);
            }
            else{
                outputStream.writeUTF(NOT_OK_REQUEST);
                logger.log(Level.SEVERE, UNEXPECTED_MESSAGE_RECEIVED, new Object[]{username,request,CHOOSE_GRID});
            }
        }
        while (!request.equals(CHOOSE_GRID));
        int gridChosen = inputStream.readInt();
        logger.log(Level.FINE, "{0} selected grid {1}",new Object[]{username, gridChosen});
        try {
            gameHandling.setGrid(this, gridChosen);
            outputStream.writeUTF(OK_REQUEST);
        } catch (InvalidOperationException e) {
            outputStream.writeUTF(NOT_OK_REQUEST);
            logger.log(Level.SEVERE, "Invalid index received from {0}",username);
            return false;
        }
        return true;
    }

    /**
     * Handles grids request.
     *
     * @throws IOException Thrown when an I/O error occurs.
     * @throws InvalidOperationException See getGridsForPlayer doc in MatchModel class.
     * @throws IllegalRequestException See securityControl doc in MatchController class.
     */
    private void handleGridsRequest() throws IOException, InvalidOperationException, IllegalRequestException {
        String request;
        lock.lock();
        readyToReceiveGridsRequest=true;
        conditionLock.signal();
        lock.unlock();
        do{
            request = inputStream.readUTF();
            if(!request.equals(REQUEST_GRID)) {
                outputStream.writeUTF(NOT_OK_REQUEST);
                logger.log(Level.SEVERE, UNEXPECTED_MESSAGE_RECEIVED, new Object[]{username,request,REQUEST_GRID});

            }
        }while (!request.equals(REQUEST_GRID));
        logger.log(Level.FINE, "Grids requested by {0}", username);
        outputStream.writeUTF(OK_REQUEST);
        ArrayList<Grid> grids;
        do {
            grids = (ArrayList<Grid>) gameHandling.getPlayerGrids(this);
        }
        while (grids == null);
        outputStream.writeUTF(GRID_SELECTION_DATA);
        Gson gson= new Gson();
        outputStream.writeUTF(gson.toJson(grids));
        logger.log(Level.FINE, "send grid selection to {0}", username);
    }

    /**
     * Handles a logout request before match starts.
     *
     * @throws IOException Thrown when an I/O error occurs.
     * @throws DisconnectionException Thrown when the client is disconnecting.
     */
    private void handleLogoutRequestBeforeStart() throws IOException, DisconnectionException {
        lock.lock();
        new Thread(this::checkForRequestFromClient).start();
        while(!inGame&&connected){
            while (!statusChanged){
                try {
                    conditionLock.await();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
            statusChanged=false;
            handleLogoutRequest();
        }
        lock.unlock();
    }

    /**
     * Handles a logout request.
     *
     * @throws IOException Thrown when an I/O error occurs.
     * @throws DisconnectionException Thrown when the client is disconnecting.
     */
    private void handleLogoutRequest() throws IOException, DisconnectionException {
        if(inputStream.available()>0) {
            String read = inputStream.readUTF();
            if (read.equals(TRY_LOGOUT)) {
                logger.log(Level.FINE, "Logout request received from {0}", username);
                try {
                    MatchHandler.getInstance().logOut(this);
                    outputStream.writeUTF(SUCCESSFULLY_LOGGED_OUT);
                    logger.log(Level.FINE, "{0} logged out", username);
                    connected = false;
                    throw new DisconnectionException();
                } catch (InvalidOperationException e) {
                    outputStream.writeUTF(LAUNCHING_GAME);
                    logger.log(Level.WARNING, "{0} could not log out cause the game is starting", username);
                }
            } else {
                logger.log(Level.FINE, "Unexpected request from {0}: {1}", new Object[]{username, read});
            }
        }
    }

    /**
     * Checks for client requests.
     */
    private void checkForRequestFromClient() {
        lock.lock();
        while (!inGame&&connected){
            lock.unlock();
            try {
                if(inputStream.available()>0){
                    lock.lock();
                    statusChanged=true;
                    conditionLock.signal();
                    lock.unlock();
                }
            } catch (IOException e) {
                logger.severe("Something went wrong");
                Thread.currentThread().interrupt();
            }
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            lock.lock();
        }
        lock.unlock();
    }

    /**
     * Handles login.
     *
     * @throws IOException Thrown when an I/O error occurs.
     */
    private void handleLogin() throws IOException {
        boolean logged =false;
        do {
            try {
                MatchHandler.getInstance().login(this);
                logger.log(Level.FINE,"Connection protocol ended. Connected");
                outputStream.writeUTF(SUCCESSFULLY_LOGGED);
                logged=true;
            } catch (InvalidOperationException e) {
                logger.log(Level.INFO,"Connection protocol ended. Server is full",e);
                outputStream.writeUTF(SERVER_FULL);
                return;
            } catch (DisconnectionException e) {
                logger.log(Level.INFO,"Connection protocol ended. Client disconnected.");
                throw new IOException();
            } catch (InvalidUsernameException e) {
                outputStream.writeUTF(USERNAME_NOT_AVAILABLE);
                logger.log(Level.INFO, "Username not available", e);
            } catch (ReconnectionException e) {
                outputStream.writeUTF(RECONNECTED);
                outputStream.writeUTF(LAUNCHING_GAME);
                outputStream.writeUTF(GAME_STARTED);
                logger.log(Level.INFO,"Connection protocol ended. Client joined the game left before",e);
                logged=true;
            }
        }
        while(!logged);
    }

    /**
     * Handles connection establishment.
     *
     * @throws IOException Thrown when an I/O error occurs.
     */
    private void handleConnection() throws IOException {
        String hello;
        boolean ok=false;
        do{
            hello= inputStream.readUTF();
            if(hello.equals(HELLO_MESSAGE)) {
                logger.log(Level.FINE,"hello message received");
                ok=true;
            } else {
                logger.log(Level.SEVERE,"unexpected message waiting for hello: {0}",hello);
            }
        }
        while(!ok);
    }

    @Override
    public boolean isConnected() {
        try{
            outputStream.writeUTF(PING_MESSAGE);
            return true;
        }
        catch (IOException e){
            return false;
        }
    }

    //Observer

    /**
     * Getter for client's username.
     *
     * @return A string containing the username.
     */
    public String getUsername(){
        return this.username;
    }


    //-----------------------------------------------------------------------------
    //                             methods for login
    //-----------------------------------------------------------------------------
    @Override
    public void chooseUsername() throws DisconnectionException {
        try {
            outputStream.writeUTF(LOGIN_MESSAGE_FROM_SERVER);
        } catch (IOException e) {
            connected=false;
            throw new DisconnectionException();
        }
    }

    @Override
    public void arrangeForUsername() throws InvalidOperationException, DisconnectionException, ReconnectionException, InvalidUsernameException {
        try {
            username= inputStream.readUTF();
            logger.log(Level.FINE,"Received {0} as username. Trying to reserve it.", username);
        } catch (IOException e) {
            throw new DisconnectionException();
        }
        MatchHandler.getInstance().requestUsername(username);
    }


    @Override
    public void notifyStarting() throws DisconnectionException {
        try {
            lock.lock();
            outputStream.writeUTF(LAUNCHING_GAME);
            logger.log(Level.FINE,"{0} notified about game starting", username);
        } catch (IOException e) {
            connected=false;
            throw new DisconnectionException();
        } finally {
            lock.unlock();
        }

    }

    @Override
    public void notifyStart() throws DisconnectionException {
        try {
            lock.lock();
            waitReadyToReceiveGridRequest();
            outputStream.writeUTF(GAME_STARTED);
            logger.log(Level.FINE,"{0} notified about game start", username);
        } catch (IOException e) {
            connected=false;
            throw new DisconnectionException();
        } finally {
            lock.unlock();
        }

    }

    /**
     * Waits until all grids to sends are set.
     */
    private void waitReadyToReceiveGridRequest() {
        while(!readyToReceiveGridsRequest){
            try {
                conditionLock.await();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    @Override
    public void notifyReconnection() throws DisconnectionException {
        //fixme
        try {
            outputStream.writeUTF(SUCCESSFULLY_LOGGED);
            outputStream.writeUTF(GAME_ALREADY_IN_PROGRESS);
            logger.log(Level.FINE,"{0} notified about game is in progress", username);
        } catch (IOException e) {
            connected=false;
            throw new DisconnectionException();
        }
    }

    @Override
    public void setController(MatchController matchController) {
        lock.lock();
        this.gameHandling =matchController;
        this.inGame=true;
        this.statusChanged=true;
        conditionLock.signal();
        logger.log(Level.FINER,"Set controller for socketUA");
        lock.unlock();
    }

    @Override
    public void notifyDieInsertion() {

    }

    @Override
    public void notifyToolUsed() {

    }


    @Override
    public void sendGrids(Map<String, Grid> playersGrids, List<String> connectedPlayers) {
        HashMap toSent= (HashMap) playersGrids;
        ArrayList<String> playersToSend= (ArrayList<String>) connectedPlayers;
        Gson gson = getGsonForGrid();
        String mapToJson= gson.toJson(toSent);
        try {
            outputStream.writeUTF(ALL_GRIDS_DATA);
            outputStream.writeUTF(mapToJson);
            outputStream.writeUTF(gson.toJson(playersToSend));
            logger.log(Level.FINE, "Sent grids to {0}", username);
        } catch (IOException e) {
            logger.fine("Disconnected");
        }
    }

    @Override
    public void notifyTurnInitialized() {
        try {
            outputStream.writeUTF(END_DATA);
        } catch (IOException e) {
            logger.fine("Disconnected");
        }
    }

    @Override
    public void notifyTurnOf(String username) {
        sendData(TURN_PLAYER,username);
    }

    @Override
    public void syncWithReconnectingUserAgent() {
        lock.lock();
        while(!gridSet){
            try {
                conditionLock.await();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        lock.unlock();
    }

    @Override
    public void sendDicePool(List<Die> dicePool) {
        ArrayList<Die> dieList = (ArrayList<Die>) dicePool;
        Gson gson = new Gson();
        String dataToSend = gson.toJson(dieList);
        sendData(DICE_POOL_DATA,dataToSend);
    }

    @Override
    public void sendGrid(Grid grid) {
        Gson gson = getGsonForGrid();
        String dataToSend= gson.toJson(grid);
        sendData(GRID_DATA,dataToSend);
    }

    @Override
    public void synchronizeEndTurn(boolean disconnected, Grid grid, List<Die> dicePool) {
        //this method is launched at the end of the turn to synchronizeEndTurn with eventual reconnecting players
        try {
            if(disconnected)outputStream.writeUTF(DISCONNECTION);
            sendGrid(grid);
            sendDicePool(dicePool);
            // TODO: 05/07/2018 send other data
            outputStream.writeUTF(END_TURN);
        } catch (IOException e) {
            logger.fine("Disconnected");
        }
    }

    @Override
    public void notifyEndGame() {
        try {
            outputStream.writeUTF(GAME_FINISHED);
        } catch (IOException e) {
            logger.fine("Disconnected");
        }
    }

    @Override
    public void sendPoints(Map<String, String> playersPoints) {
        Gson gson= new Gson();
        LinkedHashMap<String,String> data = (LinkedHashMap<String,String>) playersPoints;
        String toSend = gson.toJson(data);
        sendData(PLAYERS_POINTS, toSend);
    }

    @Override
    public void sendRoundTrack(List<Die> roundTrack) {
        ArrayList<Die> dieList = (ArrayList<Die>) roundTrack;
        Gson gson = new Gson();
        String dataToSend = gson.toJson(dieList);
        sendData(ROUND_TRACK_DATA,dataToSend);
    }

    @Override
    public void sendToolCards(List<ToolCard> toolCards) {
        ArrayList<ToolCard> toolCardArrayList= (ArrayList<ToolCard>) toolCards;
        try {
            Gson gson = ConfigurationHandler.getInstance().getGsonForToolCards();
            String dataToSend = gson.toJson(toolCardArrayList);
            sendData(TOOL_DATA,dataToSend);
        } catch (NotValidConfigPathException e) {
            logger.severe("Configurations error");
        }
    }

    @Override
    public void notifyGameInitialized() {
        try {
            outputStream.writeUTF(END_DATA);
        } catch (IOException e) {
            logger.fine(DISCONNECTED_LOG);
        }
    }


    /**
     * Sends data.
     *
     * @param dataType Type of the data to send.
     * @param dataToSend String representation of the json to send.
     */
    private void sendData(String dataType, String dataToSend) {
        try {
            lock.lock();
            outputStream.writeUTF(dataType);
            outputStream.writeUTF(dataToSend);
        } catch (IOException e) {
            logger.fine(DISCONNECTED_LOG);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void notifyDisconnection() {
        try {
            outputStream.writeUTF(DISCONNECTION);
            logger.log(Level.FINE,"{0} notified about disconnection due to timeout", username);
        } catch (IOException e) {
            logger.fine(DISCONNECTED_LOG);
        }
    }


}
