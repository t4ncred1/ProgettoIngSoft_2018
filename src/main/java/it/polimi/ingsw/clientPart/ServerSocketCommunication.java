package it.polimi.ingsw.clientPart;

import it.polimi.ingsw.clientPart.custom_exception.*;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class ServerSocketCommunication implements ServerCommunicatingInterface {

    private transient Socket socket;
    private transient DataInputStream inputStream;
    private transient DataOutputStream outputStream;

    private static int serverPort = 11000;
    private static String serverAddress="127.0.0.1";

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









    public void waitForGame(boolean starting) throws GameStartingException, GameStartedException, TimerRestartedException, ServerIsDownException {
        String read;
        try {
            if(inputStream.available()>0){
                read= inputStream.readUTF();
                if(read.equals("launching_game")) {
                    throw new GameStartingException();
                }
                if(read.equals("game_started")){
                    throw new GameStartedException();
                }
            }
            if(starting){
                read = inputStream.readUTF();
                    if (read.equals("game_started")) {
                        throw new GameStartedException();
                    }
                    else if(read.equals("launching_game")){
                        throw new TimerRestartedException();
                    }
            }
        } catch (IOException e) {
            throw new ServerIsDownException();
        }
    }

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
}



