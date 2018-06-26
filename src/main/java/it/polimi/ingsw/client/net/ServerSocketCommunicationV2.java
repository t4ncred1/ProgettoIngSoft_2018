package it.polimi.ingsw.client.net;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import it.polimi.ingsw.client.MainClient;
import it.polimi.ingsw.client.Proxy;
import it.polimi.ingsw.client.configurations.ConfigHandler;
import it.polimi.ingsw.client.custom_exception.*;
import it.polimi.ingsw.server.custom_exception.InvalidOperationException;
import it.polimi.ingsw.server.custom_exception.NotValidConfigPathException;
import it.polimi.ingsw.server.model.components.Grid;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ServerSocketCommunicationV2 extends Thread implements ServerCommunicatingInterfaceV2{


    private transient Socket socket;
    private transient DataInputStream inputStream;
    private transient DataOutputStream outputStream;

    private int serverPort = 11000;
    private String serverAddress="127.0.0.1";

    private Lock lock;
    private Condition condition;
    private Logger logger;
    private static final long SLEEP_TIME = 50;

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

    private static final String OK_MESSAGE="ok";
    private static final String NOT_OK_MESSAGE= "retry";
    private static final String REQUEST_GRID = "get_grids";
    private static final String GRID_ALREADY_SELECTED= "grid_selected";
    private static final String CHOOSE_GRID="set_grid";


    private static final String GET_TURN_PLAYER = "get_turn_player";
    private static final String GET_DICE_POOL = "get_dice_pool";
    private static final String GET_PRIVATE_OBJECTIVE= "get_private_obj";
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
    private static final String DICE_POOL_DATA= "dice_pool";
    private static final String TURN_FINISHED= "finish";
    private static final String DISCONNECTION = "disconnected";

    public ServerSocketCommunicationV2(){
        this.lock = new ReentrantLock();
        this.condition= lock.newCondition();
        logger = Logger.getLogger(ServerSocketCommunicationV2.class.getName());
    }

    @Override
    public void run(){

        try {
            handleWaitForGame();
            handleGameStarting();
            setGridSelection();
        } catch (IOException e) {
            logger.log(Level.WARNING,"Can't connect to server, something went wrong!");
            System.exit(0);
        }

    }

    private void setGridSelection() throws IOException {
        String serverResponse;
        String logUnexpectedResponse= "Unexpected response: {0}";
        try{
            do {
                outputStream.writeUTF(REQUEST_GRID);
                serverResponse=readRemoteInput();
                logger.log(Level.SEVERE, logUnexpectedResponse, serverResponse);
            }while (serverResponse.equals(NOT_OK_MESSAGE));
            serverResponse=readRemoteInput();
            switch (serverResponse){
                case OK_MESSAGE:
                    ArrayList<Grid> grids;
                    TypeToken<ArrayList<Grid>> typeToken= new TypeToken<ArrayList<Grid>>(){};
                    Gson gson= new Gson();
                    serverResponse=readRemoteInput();
                    System.err.println("Response \n"+serverResponse);
                    grids= gson.fromJson(serverResponse, typeToken.getType());
                    Proxy.getInstance().setGridsSelection(grids);
                    MainClient.getInstance().notifyGridsAreInProxy();
                    break;
                case GRID_ALREADY_SELECTED:
                    //todo throw new GameInProgressException() in MainClient (Somehow)
                    break;
                default:
                    logger.log(Level.SEVERE, logUnexpectedResponse, serverResponse);
            }
        } catch (InvalidOperationException e) {
            logger.log(Level.SEVERE,"A null-pointer was passed instead of a list of grids");    //thrown by proxy if passed grid list is null (also, should it be thrown if the grids were already chosen?)
        }
    }

    private void handleGameStarting() throws IOException {
        boolean ok=false;
        String serverResponse;
        do{
            serverResponse=readRemoteInput();
            switch (serverResponse){
                case LAUNCHING_GAME:
                    MainClient.getInstance().notifyGameStarting();
                    break;
                case GAME_STARTED:
                    MainClient.getInstance().notifyGameStarted();
                    ok=true;
                    break;
                default:
                    logger.log(Level.SEVERE, "Unexpected response from server ({0}) while waiting game start",serverResponse);
            }
        }while (!ok);
    }

    private void handleWaitForGame() throws IOException {
        String serverResponse;
        do{
            sleepForASec();
            lock.lock();
            serverResponse=inputStream.readUTF();
            lock.unlock();
        }while (!serverResponse.equals(LAUNCHING_GAME));
        MainClient.getInstance().notifyGameStarting();
    }

    private void sleepForASec() {
        try {
            Thread.sleep(SLEEP_TIME);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }


    public void setUpConnection() throws ServerIsDownException {
        try {
            try {
                serverPort = ConfigHandler.getInstance().getSocketPort();
                serverAddress = ConfigHandler.getInstance().getServerIp();
            } catch (NotValidConfigPathException e) {
                logger.log(Level.WARNING,"Wrong configuration file, using defaults.");
            }
            socket= new Socket(serverAddress, serverPort);
            inputStream = new DataInputStream(socket.getInputStream());
            outputStream= new DataOutputStream(socket.getOutputStream());
            outputStream.writeUTF(HELLO_MESSAGE);
        } catch (IOException e) {
            throw new ServerIsDownException();
        }
    }

    public void login(String username) throws ServerIsFullException, InvalidUsernameException, ServerIsDownException {
        String read;
        try {
            do {
                read = inputStream.readUTF();
            }
            while (!read.equals(LOGIN_MESSAGE_FROM_SERVER));
            outputStream.writeUTF(username);
            do {
                read = inputStream.readUTF();
            }
            while (!(read.equals(SUCCESSFULLY_LOGGED) || read.equals(SERVER_FULL) || read.equals(USERNAME_NOT_AVAILABLE)));
            switch (read) {
                case SERVER_FULL:
                    throw new ServerIsFullException();
                case USERNAME_NOT_AVAILABLE:
                    throw new InvalidUsernameException();
                default:
                    this.start();
            }
        }
        catch (IOException e){
            throw new ServerIsDownException();
        }
    }

    public void askForLogout() throws ServerIsDownException, GameStartingException, LoggedOutException {
        try{
            lock.lock();
            outputStream.writeUTF(TRY_LOGOUT);
            String response= readRemoteInput();
            switch (response) {
                case SUCCESSFULLY_LOGGED_OUT:
                    throw new LoggedOutException();
                case LAUNCHING_GAME:
                    throw new GameStartingException();
                default:
                    logger.log(Level.SEVERE,"Unexpected response from server: {0}", response);
            }
            lock.unlock();
        } catch (IOException e) {
            throw new ServerIsDownException();

        }
    }

    private String readRemoteInput() throws IOException {
        String read;
        do{
            read=inputStream.readUTF();
        }while (read.equals(PING_MESSAGE));
        return read;
    }

    public void selectGrid(int gridIndex) throws ServerIsDownException {
        try {
            outputStream.writeUTF(CHOOSE_GRID);
            String response= readRemoteInput();
            if(!response.equals(OK_MESSAGE)) {
                System.err.println("Something went wrong");
                return;
            }

            outputStream.writeInt(gridIndex);
            response=readRemoteInput();
            if(response.equals(NOT_OK_MESSAGE))
                logger.log(Level.SEVERE,"Unexpected response from server");
        } catch (IOException e) {
            throw new ServerIsDownException();
        }
    }
}
