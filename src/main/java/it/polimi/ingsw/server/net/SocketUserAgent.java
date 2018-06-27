package it.polimi.ingsw.server.net;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import it.polimi.ingsw.server.MatchController;
import it.polimi.ingsw.server.MatchHandler;
import it.polimi.ingsw.server.custom_exception.connection_exceptions.IllegalRequestException;
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
import java.util.ArrayList;
import java.util.List;
import java.util.logging.*;

public class SocketUserAgent extends Thread implements UserInterface {

    private DataInputStream inputStream;
    private DataOutputStream outputStream;
    private MatchController gameHandling;
    private boolean inGame;
    private boolean connected;

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


    private static final String LAUNCHING_GAME = "launching_game";
    private static final String GAME_STARTED = "game_started";
    private static final String GAME_ALREADY_IN_PROGRESS = "reconnected";

    private static final String TRY_LOGOUT= "try_logout";
    private static final String SUCCESSFULLY_LOGGED_OUT= "logged_out";

    private static final String OK_REQUEST = "ok";
    private static final String NOT_OK_REQUEST = "retry";
    private static final String SECURITY_VIOLATION = "illegal_access";


    private static final String REQUEST_GRID = "get_grids";
    private static final String GRID_ALREADY_SELECTED= "grid_selected";
    private static final String CHOOSE_GRID="set_grid";

    private static final String GET_TURN_PLAYER = "get_turn_player";
    private static final String GET_DICE_POOL = "get_dice_pool";
    private static final String GET_SELECTED_GRID = "get_my_grid";
    private static final String GAME_FINISHED= "finished";

    private static final String LISTEN_STATE = "listen";
    private static final String END_LISTEN="listen_end";
    private static final String OPERATION_MESSAGE= "operation";
    private static final String INSERT_DIE = "insert_die";
    private static final String INVALID_POSITION = "invalid_index";
    private static final String END_TURN ="end_turn";
    private static final String GRID_DATA= "grid";
    private static final String TOOL_DATA="tool";
    private static final String END_DATA= "end_data";
    private static final String TURN_FINISHED= "finish";
    private static final String DICE_POOL_DATA= "dice_pool";
    private static final String DISCONNECTION = "disconnected";

    SocketUserAgent(Socket client) {
        connected=true;
        actualConnectionNumber =connectionNumber++;
//        try {
            logger = Logger.getLogger(SocketUserAgent.class.getName()+actualConnectionNumber);
//            handler = new FileHandler("src/main/resources/log_files/clientLog_"+ actualConnectionNumber+".log");
            SimpleFormatter formatter = new SimpleFormatter();
//            handler.setFormatter(formatter);
            logger.setLevel(Level.FINER);
//            logger.addHandler(handler);
//        } catch (IOException e) {
//            logger.log(Level.SEVERE, "FileHandler not found",e);
//        }
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
            handleConnection();
            handleLogin();
            handleLogoutRequestBeforeStart();

            handleGameInitialization();
            handleGameLogic();
        } catch (IOException e) {
            logger.log(Level.WARNING, "Client disconnected",e );
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
//            handler.flush();
//            handler.close();
        }
    }

    private void handleGameLogic() throws IOException, IllegalRequestException {
        boolean gameFinished=false;
        do{
            try {
                handleTurnLogic();
            }  catch (TooManyRoundsException e) {
                outputStream.writeUTF(GAME_FINISHED);
                logger.log(Level.FINE, "{0} andre notified about the end of the game.",username );
                gameFinished=true;
            }
        }while (!gameFinished);
    }

    private void handleTurnLogic() throws IOException, TooManyRoundsException, IllegalRequestException {
            handleTurnPlayerRequest();
            sendDicePool();
            handleTurn();
    }

    private void handleTurn() throws IOException, IllegalRequestException {
        String request;
        request=inputStream.readUTF();
        logger.log(Level.FINE, "Request: {0} from {1} ", new Object[]{request,username});
        if(request.equals(LISTEN_STATE)){
            request=inputStream.readUTF();
            if(!request.equals(END_LISTEN)){
                logger.log(Level.SEVERE, UNEXPECTED_MESSAGE_RECEIVED, new Object[]{username,request,END_LISTEN});
            }
        }
        else{
            boolean turnFinished=false;
            do{
                switch (request){
                    case INSERT_DIE:
                        handleDieInsertion();
                        break;
                    case END_TURN:
                        turnFinished=true;
                        gameHandling.notifyEnd();
                        break;
                    default:
                        logger.log(Level.SEVERE, "Unexpected message from {0}: received {1} instead of an operation", new Object[]{username,request});
                }
                if(!turnFinished) request=inputStream.readUTF();
            }while (!turnFinished&&connected);
        }
    }

    private void handleDieInsertion() throws IOException, IllegalRequestException {
        int position=inputStream.readInt();
        int x = inputStream.readInt();
        int y= inputStream.readInt();
        try {
            gameHandling.insertDie(this,position,x,y);
            outputStream.writeUTF(OK_REQUEST);
            sendUpdatedGrid();
        } catch (InvalidOperationException e) {
            outputStream.writeUTF(NOT_OK_REQUEST);
        } catch (NotInPoolException e) {
            outputStream.writeUTF(INVALID_POSITION);
        }

    }

    private void sendUpdatedGrid() throws IOException, IllegalRequestException {
        Grid grid =gameHandling.getPlayerGrid(this);
        Gson gson = getGsonForGrid();
        String toSend= gson.toJson(grid);
        outputStream.writeUTF(toSend);
    }

    private Gson getGsonForGrid() {
        GsonBuilder builder= new GsonBuilder();
        RuntimeTypeAdapterFactory<DieConstraints> adapterFactory= RuntimeTypeAdapterFactory.of(DieConstraints.class)
                .registerSubtype(DieToConstraintsAdapter.class, DieToConstraintsAdapter.class.getName());

        builder.registerTypeAdapterFactory(adapterFactory);
        return builder.create();
    }

    private void sendDicePool() throws IOException {
        String request;
        do {
            request = inputStream.readUTF();
        }
        while (!request.equals(GET_DICE_POOL));
        outputStream.writeUTF(DICE_POOL_DATA);
        List<Die> dieList= gameHandling.getDicePool();
        Gson gson= new Gson();
        outputStream.writeUTF(gson.toJson(dieList));
    }

    private void handleTurnPlayerRequest() throws IOException, TooManyRoundsException {
        String player;
        boolean turnSent=false;
        do {
            waitForTurnPlayerRequest();
            logger.log(Level.FINE,"{0} started turn player request.",username);
            try {
                player = gameHandling.requestTurnPlayer();
                outputStream.writeUTF(player);
                logger.log(Level.FINE,"Sent: turnPlayer ({0}) to {1}",new Object[]{player,username});
                turnSent = true;
            } catch (InvalidOperationException e) {
                outputStream.writeUTF(NOT_OK_REQUEST);
            }
        } while (!turnSent);
    }

    private void waitForTurnPlayerRequest() throws IOException {
        String request;
        do {
            request = inputStream.readUTF();
            //This sleep is used to avoid continuous request from clients
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                logger.log(Level.SEVERE, "Interrupted!");
            }
        }
        while (!request.equals(GET_TURN_PLAYER));
    }

    private void handleGameInitialization() throws IOException, IllegalRequestException {
        try {
            handleGridsRequest();
            boolean gridSet;
            do {
                gridSet = handleGridSet();
            }
            while (!gridSet);
            handleSelectedGridRequest();
        } catch (InvalidOperationException e) {
            outputStream.writeUTF(GRID_ALREADY_SELECTED);
            logger.log(Level.CONFIG, "Player already selected a grid", e);
        }
    }

    private void handleSelectedGridRequest() throws IOException, IllegalRequestException {
        String request = inputStream.readUTF();
        if(request.equals(GET_SELECTED_GRID)) logger.log(Level.FINE, "Grid requested by {0}", username);
        else  logger.log(Level.SEVERE, UNEXPECTED_MESSAGE_RECEIVED, new Object[]{username,request,GET_SELECTED_GRID});
        Grid grid;
        grid= gameHandling.getPlayerGrid(this);

        outputStream.writeUTF(OK_REQUEST);
        sendGridSelected(grid);
    }

    private void sendGridSelected(Grid grid) throws IOException {
        String toSend= getGsonForGrid().toJson(grid);
        outputStream.writeUTF(toSend);
        logger.log(Level.FINE, "Sent grid to {0}", username);
    }

    private boolean handleGridSet() throws IOException, IllegalRequestException {
        String request;
        do {
            request = inputStream.readUTF();
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
        try {
            gameHandling.setGrid(this, gridChosen);
            outputStream.writeUTF(OK_REQUEST);
        } catch (InvalidOperationException e) {
            outputStream.writeUTF(NOT_OK_REQUEST);
            return false;
        }
        return true;
    }

    private void handleGridsRequest() throws IOException, InvalidOperationException, IllegalRequestException {
        String request;
        do{
            request = inputStream.readUTF();
            if(!request.equals(REQUEST_GRID)) {
                outputStream.writeUTF(NOT_OK_REQUEST);
                logger.log(Level.SEVERE, UNEXPECTED_MESSAGE_RECEIVED, new Object[]{username,request,REQUEST_GRID});

            }
        }while (!request.equals(REQUEST_GRID));
        outputStream.writeUTF(OK_REQUEST);
        ArrayList<Grid> grids;
        do {
            grids = (ArrayList<Grid>) gameHandling.getPlayerGrids(this);
        }
        while (grids == null);
        outputStream.writeUTF(OK_REQUEST);
        Gson gson= new Gson();
        outputStream.writeUTF(gson.toJson(grids));
    }

    private void handleLogoutRequestBeforeStart() throws IOException, DisconnectionException {
        while(!inGame&&connected){
            if(inputStream.available()>0) {
                String read = inputStream.readUTF();
                if (read.equals(TRY_LOGOUT)) {
                    logger.log(Level.FINE,"Logout request received from {0}",username);
                    try {
                        MatchHandler.getInstance().logOut(this);
                        outputStream.writeUTF(SUCCESSFULLY_LOGGED_OUT);
                        logger.log(Level.FINE,"{0} logged out",username);
                        connected=false;
                        throw new DisconnectionException();
                    } catch (InvalidOperationException e) {
                        outputStream.writeUTF(LAUNCHING_GAME);
                        logger.log(Level.WARNING,"{0} could not log out cause the game is starting",username);
                    }
                }
            }
        }
    }

    private void handleLogin() throws IOException {
        boolean logged =false;
        do {
            try {
                MatchHandler.login(this);
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
                logger.log(Level.CONFIG,"Connection protocol ended. Client joined the game left before",e);
                logged=true;
            }
        }
        while(!logged);
    }

    private void handleConnection() throws IOException {
        String hello;
        do{
            hello= inputStream.readUTF();
        }
        while(!hello.equals(HELLO_MESSAGE));
    }

    @Override
    public boolean isConnected() {
        try{
            outputStream.writeUTF(PING_MESSAGE);
            return true;
        }
        catch (IOException e){
            connected=false;
            return false;
        }
    }

    //Observer
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
            outputStream.writeUTF(LAUNCHING_GAME);
            logger.log(Level.FINE,"{0} notified about game starting", username);
        } catch (IOException e) {
            connected=false;
            throw new DisconnectionException();
        }
    }

    @Override
    public void notifyStart() throws DisconnectionException {
        try {
            outputStream.writeUTF(GAME_STARTED);
            logger.log(Level.FINE,"{0} notified about game start", username);
        } catch (IOException e) {
            connected=false;
            throw new DisconnectionException();
        }
    }

    @Override
    public void notifyReconnection() throws DisconnectionException {
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
        this.gameHandling =matchController;
        this.inGame=true;
    }

    @Override
    public void notifyDieInsertion() {

    }

    @Override
    public void notifyToolUsed() {

    }

    @Override
    public void notifyEndTurn() {
        try {
            outputStream.writeUTF(TURN_FINISHED);
            logger.log(Level.FINE,"{0} notified of the end of the turn. ",username);
        } catch (IOException e) {
            connected=false;
        }

    }


    //TODO from here.



    @Override
    public void notifyDisconnection() {
        try {
            outputStream.writeUTF(OPERATION_MESSAGE);
            outputStream.writeUTF(DISCONNECTION);
            logger.log(Level.FINE,"{0} notified about disconnection due to timeout", username);
        } catch (IOException e) {
            connected=false;
        }
    }


}
