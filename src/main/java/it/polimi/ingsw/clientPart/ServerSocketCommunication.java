package it.polimi.ingsw.clientPart;

import it.polimi.ingsw.clientPart.custom_exception.*;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

public class ServerSocketCommunication implements ServerCommunicatingInterface {

    private transient Socket socket;
    private transient DataInputStream inputStream;
    private transient DataOutputStream outputStream;

    private static int serverPort = 11000;
    private static String serverAddress="127.0.0.1";


    private static final String REQUEST_GRID = "get_grids";

    @Override
    public void setUpConnection() throws ServerIsDownException {
        final String HELLO_MESSAGE = "hello";
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
        final String LOGIN_MESSAGE_FROM_SERVER = "login";
        final String SUCCESSFULLY_LOGGED = "logged";
        final String SERVER_FULL = "notLogged_server_full";
        final String USERNAME_NOT_AVAILABLE= "notLogged_username_not_available";
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
        final String gameIsStarting = "launching_game";
        final String gameIsStarted = "game_started";
        final String gameAlreadyInProgress = "reconnected";
        try {
            if(inputStream.available()>0){
                read= inputStream.readUTF();
                switch (read){
                    case gameIsStarting:
                        throw new GameStartingException();
                    case gameIsStarted:
                        throw new GameStartedException();
                    case gameAlreadyInProgress:
                        throw new GameInProgressException();
                    default:
                        //do nothing, messages are to test connection
                }
            }
            if(starting){
                read = inputStream.readUTF();
                switch (read){
                    case gameIsStarting:
                        throw new GameStartingException();
                    case gameIsStarted:
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
        try{
            outputStream.writeUTF("try_logout");
            String response= inputStream.readUTF();
            switch (response) {
                case "logged_out":
                    return true;
                case "launching_game":
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
        final String NOT_OK_MESSAGE ="retry";

        String result;
        try{
            do {
                outputStream.writeUTF(REQUEST_GRID);
                do {
                    result = inputStream.readUTF();
                }while (result.equals(""));
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
                }while (gridsNames.equals(""));
                gridsNames.add(name);
                int difficulty = inputStream.readInt();
                gridsDifficulties.add(difficulty);
                String structure;
                do {
                    structure = inputStream.readUTF();
                }while (structure.equals(""));
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
}



