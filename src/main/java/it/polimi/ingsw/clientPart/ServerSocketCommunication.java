package it.polimi.ingsw.clientPart;

import it.polimi.ingsw.clientPart.custom_exception.*;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

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
    private static final String CHOOSE_GRID="set_grid";


    private static final String LAUNCHING_GAME = "launching_game";
    private static final String GAME_STARTED = "game_started";
    private static final String GAME_ALREADY_IN_PROGRESS = "reconnected";

    private static final String TRY_LOGOUT= "try_logout";
    private static final String SUCCESSFULLY_LOGGED_OUT= "logged_out";

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
    public void getGrids() throws ServerIsDownException {
        String result;
        try{
            do {
                outputStream.writeUTF(REQUEST_GRID);
                do {
                    result = inputStream.readUTF();
                }while (result.equals(PING_MESSAGE));
                System.out.println(result);
            }while (result.equals(NOT_OK_MESSAGE));
            int number= inputStream.readInt();
            ArrayList<String> gridsNames= new ArrayList<>();
            ArrayList<Integer> gridsDifficulties= new ArrayList<>();
            ArrayList<String> gridsStructure= new ArrayList<>();
            for(int i=0; i<number; i++) {
                String name;
                do {
                    name = inputStream.readUTF();
                }while (name.equals(PING_MESSAGE));
                gridsNames.add(name);
                int difficulty = inputStream.readInt();
                gridsDifficulties.add(difficulty);
                String structure;
                do {
                    structure = inputStream.readUTF();
                }while (structure.equals(PING_MESSAGE));
                gridsStructure.add(structure);

                //TODO delete these:
                System.out.println(name);
                System.out.println(difficulty);
                System.out.println(structure);
            }

            //TODO
        } catch (IOException e) {
            throw new ServerIsDownException();

        }
    }

    @Override
    public void setGrid(int gridIndex) throws ServerIsDownException, InvalidMoveException {
        try {
            outputStream.writeUTF(CHOOSE_GRID);
            String response;
            do{
                response=inputStream.readUTF();
            }while (response.equals(PING_MESSAGE));
            if(!response.equals(OK_MESSAGE)) {
                System.err.println("Something went wrong");
                return;
            }

            outputStream.writeInt(gridIndex);
            do{
                response=inputStream.readUTF();
            }while (response.equals(PING_MESSAGE));
            if(response.equals(NOT_OK_MESSAGE))
                throw new InvalidMoveException();
        } catch (IOException e) {
            throw new ServerIsDownException();
        }
    }
}



