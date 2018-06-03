package it.polimi.ingsw.client.net;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import it.polimi.ingsw.client.Proxy;
import it.polimi.ingsw.client.custom_exception.*;
import it.polimi.ingsw.server.cards.PrivateObjective;
import it.polimi.ingsw.server.components.Grid;
import it.polimi.ingsw.server.custom_exception.DisconnectionException;
import it.polimi.ingsw.server.custom_exception.InvalidOperationException;


import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ServerSocketCommunication implements ServerCommunicatingInterface {

    private transient Socket socket;
    private transient DataInputStream inputStream;
    private transient DataOutputStream outputStream;

    private static int serverPort = 11000;
    private static String serverAddress="127.0.0.1";



    private static final String PING_MESSAGE= "";
    private static final String HELLO_MESSAGE = "hello";

    private static final String LOGIN_MESSAGE_FROM_SERVER = "login";
    private static final String SUCCESSFULLY_LOGGED = "logged";
    private static final String SERVER_FULL = "notLogged_server_full";
    private static final String USERNAME_NOT_AVAILABLE= "notLogged_username_not_available";


    private static final String OK_MESSAGE="ok";
    private static final String NOT_OK_MESSAGE= "retry";
    private static final String REQUEST_GRID = "get_grids";
    private static final String GRID_ALREADY_SELECTED= "grid_selected";
    private static final String CHOOSE_GRID="set_grid";


    private static final String LAUNCHING_GAME = "launching_game";
    private static final String GAME_STARTED = "game_started";
    private static final String GAME_ALREADY_IN_PROGRESS = "reconnected";

    private static final String TRY_LOGOUT= "try_logout";
    private static final String SUCCESSFULLY_LOGGED_OUT= "logged_out";


    private static final String GET_TURN_PLAYER = "get_turn_player";
    private static final String GET_DICE_POOL = "get_dice_pool";
    private static final String GET_PRIVATE_OBJECTIVE= "get_private_obj";
    private static final String GAME_FINISHED= "finished";

    private static final String OPERATION_MESSAGE= "operation";
    private static final String GRID_DATA= "grid";
    private static final String TOOL_DATA="tool";
    private static final String END_DATA= "end_data";
    private static final String DICE_POOL_DATA= "dice_pool";
    private static final String TURN_FINISHED= "finish";
    private static final String DISCONNECTION = "disconnected";

    @Override
    public void setUpConnection() throws ServerIsDownException {
        try {
            socket= new Socket(serverAddress, serverPort);
            inputStream = new DataInputStream(socket.getInputStream());
            outputStream= new DataOutputStream(socket.getOutputStream());
            outputStream.writeUTF(HELLO_MESSAGE);
        } catch (IOException e) {
            throw new ServerIsDownException();
        }
    }

    @Override
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
                    //do nothing, logged successfully
            }
        }
        catch (IOException e){
            throw new ServerIsDownException();
        }
    }








    @Override
    public void waitForGame(boolean starting) throws GameStartingException, GameStartedException, TimerRestartedException, ServerIsDownException, GameInProgressException {
        String read;
        try {
            if(inputStream.available()>0){
                read= inputStream.readUTF();
                switch (read){
                    case LAUNCHING_GAME:
                        throw new GameStartingException();
                    case GAME_STARTED:
                        throw new GameStartedException();
                    case GAME_ALREADY_IN_PROGRESS:
                        throw new GameInProgressException();
                    default:
                        //do nothing, messages are to test connection
                }
            }
            if(starting){
                read = inputStream.readUTF();
                switch (read){
                    case LAUNCHING_GAME:
                        throw new GameStartingException();
                    case GAME_STARTED:
                        throw new GameStartedException();
                    default:
                        //do nothing, messages are to test connection
                }
            }
        } catch (IOException e) {
            throw new ServerIsDownException();
        }
    }

    @Override
    public boolean logout() throws ServerIsDownException {
        //FIXME
        try{
            outputStream.writeUTF(TRY_LOGOUT);
            String response= inputStream.readUTF();
            switch (response) {
                case SUCCESSFULLY_LOGGED_OUT:
                    return true;
                case LAUNCHING_GAME:
                    return false;
                default:
                    System.err.println(response);
            }

        } catch (IOException e) {
            throw new ServerIsDownException();

        }
        return false;
    }

    @Override
    public void getGrids() throws ServerIsDownException, GameInProgressException {
        String result;
        try{
            do {
                outputStream.writeUTF(REQUEST_GRID);
                result=readRemoteInput();
            }while (result.equals(NOT_OK_MESSAGE));
            result=readRemoteInput();
            if(result.equals(OK_MESSAGE)) {
                ArrayList<Grid> grids;
                TypeToken<ArrayList<Grid>> typeToken= new TypeToken<ArrayList<Grid>>(){};
                Gson gson= new Gson();
                result=readRemoteInput();
                grids= gson.fromJson(result, typeToken.getType());

                Proxy.getInstance().setGridsSelection(grids);

            }else if(result.equals(GRID_ALREADY_SELECTED)){
                throw new GameInProgressException();
            }

            //TODO
        } catch (IOException e) {
            throw new ServerIsDownException();

        } catch (InvalidOperationException e) {
            e.printStackTrace();    //thrown by proxy if passed grid list is null (also, should it be thrown if the grids were already chosen?)
        }
    }

    @Override
    public void setGrid(int gridIndex) throws ServerIsDownException, InvalidMoveException {
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
                throw new InvalidMoveException();
        } catch (IOException e) {
            throw new ServerIsDownException();
        }
    }

    @Override
    public void getPrivateObjective() throws ServerIsDownException{
        try {
            outputStream.writeUTF(GET_PRIVATE_OBJECTIVE);
            String response;
            Gson gson = new Gson();
            response= readRemoteInput();
            PrivateObjective privateObjective= gson.fromJson(response, PrivateObjective.class);
            //fixme what to do with this private objective? shall this be passed to Proxy?
        } catch (IOException e) {
            throw new ServerIsDownException();
        }
    }

    @Override
    public String askTurn() throws ServerIsDownException, ServerNotReadyException, GameFinishedException {
        try {
            outputStream.writeUTF(GET_TURN_PLAYER);
            String response=readRemoteInput();
            if(response.equals(NOT_OK_MESSAGE)) throw new ServerNotReadyException();
            else if(response.equals(GAME_FINISHED)) throw new GameFinishedException();
            else return response;
        } catch (IOException e) {
            throw new ServerIsDownException();
        }


    }

    @Override
    public void listen(String username) throws ServerIsDownException, TurnFinishedException, DisconnectionException {
        try {
            String read=inputStream.readUTF();
            if (read.equals(OPERATION_MESSAGE)) {
                handleOperation(username);
            }else if(read.equals(TURN_FINISHED)){
                throw new TurnFinishedException();
            }
        } catch (IOException e) {
            throw new ServerIsDownException();
        }

    }

    @Override
    public void getUpdatedDicePool() throws ServerIsDownException {
        try {
            outputStream.writeUTF(GET_DICE_POOL);
            String response;
            do {
                response = readRemoteInput();
            }
            while (!response.equals(DICE_POOL_DATA));
            response=readRemoteInput();
            System.out.println(response); //FIXME
        } catch (IOException e) {
            throw new ServerIsDownException();
        }
    }

    private void handleOperation(String username) throws IOException, DisconnectionException {
        String read;
        do{
            read=readRemoteInput();
            String data;
            switch (read){
                case GRID_DATA:
                    Grid grid;
                    /*TODO convert string from Json*/
                    break;
                case DISCONNECTION:
                    throw new DisconnectionException();
                default:
            }
        }while (read.equals(END_DATA));
    }

    private String readRemoteInput() throws IOException {
        String read;
        do{
            read=inputStream.readUTF();
        }while (read.equals(PING_MESSAGE));
        return read;
    }
}



