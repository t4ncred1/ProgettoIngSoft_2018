package it.polimi.ingsw.serverPart.netPart_container;

import it.polimi.ingsw.serverPart.MatchHandler;
import it.polimi.ingsw.serverPart.custom_exception.DisconnectionException;
import it.polimi.ingsw.serverPart.custom_exception.InvalidOperationException;
import it.polimi.ingsw.serverPart.custom_exception.InvalidUsernameException;
import it.polimi.ingsw.serverPart.custom_exception.ReconnectionException;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class SocketUserAgent extends Thread implements UserInterface {

    private Socket socket;
    private DataInputStream inputStream;
    private DataOutputStream outputStream;
    private int gameCode;
    private boolean inGame;

    private String username;

    public SocketUserAgent(Socket client) {
        this.socket=client;
        try{
            this.inputStream= new DataInputStream(socket.getInputStream());
            this.outputStream= new DataOutputStream(socket.getOutputStream());
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }


    @Override
    public void run(){
        System.out.println("Connection request received on Socket system");
        try {
            String hello= new String();
            while(!hello.equals("hello")) hello= inputStream.readUTF();
        } catch (IOException e) {
            e.printStackTrace();
        }


        try {
            MatchHandler.login(this);
            System.out.println("Connection protocol ended. Connected");
            try {
                outputStream.writeUTF("logged");
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (InvalidOperationException e) {
            System.out.println("Connection protocol ended. Server is full");
            e.printStackTrace();
            try {
                outputStream.writeUTF("notLogged_server_full");
            } catch (IOException e2) {
                e2.printStackTrace();
            }
            return;
        } catch (DisconnectionException e) {
            System.out.println("Connection protocol ended. Client disconnected.");
            e.printStackTrace();
            return;
        }

        while(!inGame){
            try {
                if(inputStream.available()>0){
                    String read= inputStream.readUTF();
                    if(read.equals("try_logout"))
                        try {
                            MatchHandler.getInstance().logOut(this);
                            outputStream.writeUTF("logged_out");
                        }
                        catch (InvalidOperationException e){
                            outputStream.writeUTF("launching_game");
                        }
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public boolean isConnected() {
        try{
            outputStream.writeUTF("");
            return true;
        }
        catch (IOException e){
            System.err.println(this.username+" disconnected.");
            return false;
        }
    }

    //Observer
    public String getUsername(){
        return new String(this.username);
    }


    //-----------------------------------------------------------------------------
    //                             funzioni per login
    //-----------------------------------------------------------------------------
    @Override
    public void chooseUsername() throws DisconnectionException {
        final String chooseUsername = new String("login");
        try {
            outputStream.writeUTF(chooseUsername);
        } catch (IOException e) {
            throw new DisconnectionException();
        }
    }

    @Override
    public void arrangeForUsername(int trial) throws InvalidOperationException, DisconnectionException, ReconnectionException, InvalidUsernameException {
        final String notAvailableMessage = new String("notLogged_username_not_available");

        try {
            if(trial>1) outputStream.writeUTF(notAvailableMessage);
            username= inputStream.readUTF();
            System.out.println("received: " + username);
        } catch (IOException e) {
            throw new DisconnectionException();
        }
        MatchHandler.getInstance().requestUsername(username);
    }

    @Override
    public void setGameCode(int i) {
        this.gameCode=i;
    }

    @Override
    public int getGameCode(){
        return this.gameCode;
    }

    @Override
    public void notifyStarting() throws DisconnectionException {
        try {
            outputStream.writeUTF("launching_game");
        } catch (IOException e) {
            e.printStackTrace();
            throw new DisconnectionException();
        }
    }

    @Override
    public void notifyStart() throws DisconnectionException {
        try {
            outputStream.writeUTF("game_started");
        } catch (IOException e) {
            e.printStackTrace();
            throw new DisconnectionException();
        }
    }

    //TODO from here.

    @Override
    public String getOperation() {
        return null;
    }

    @Override
    public void notifyAlreadyDoneOperation() {

    }

    @Override
    public void askForOperation() {

    }

    @Override
    public void sendGrids() {

    }

    @Override
    public void notifyDisconnection() {

    }

    @Override
    public void notifyTurnOf(String username, String status) {

    }

    @Override
    public void sendConnectedPlayers(ArrayList<String> connectedPlayers) {

    }

}
