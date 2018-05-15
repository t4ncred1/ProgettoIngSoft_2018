package it.polimi.ingsw;


import it.polimi.ingsw.custom_exception.DisconnectionException;
import it.polimi.ingsw.custom_exception.InvalidOperationException;
import it.polimi.ingsw.custom_exception.InvalidUsernameException;
import it.polimi.ingsw.custom_exception.ReconnectionException;

import java.io.*;
import java.net.Socket;

public class SocketUserAgent extends Thread implements ClientInterface{

    private Socket socket;
    private MatchController currentMatch;
    private DataInputStream inputStream;
    private DataOutputStream outputStream;

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
        System.out.println("Connection request received");
        try {
            MatchHandler.login(this);
            System.out.println("Connection protocol ended. Connected");
            try {
                outputStream.writeUTF("Connected");
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (InvalidOperationException e) {
            System.out.println("Connection protocol ended. Server is full");
            e.printStackTrace();
            try {
                outputStream.writeUTF("Server is full, can't connect!");
            } catch (IOException e2) {
                e2.printStackTrace();
            }
            return;
        } catch (DisconnectionException e) {
            System.out.println("Connection protocol ended. Client disconnected.");
            e.printStackTrace();
            return;
        }
    }

    @Override
    public boolean isConnected() {
        try{
            outputStream.writeUTF("");
            return true;
        }
        catch (IOException e){
            System.out.println("Disconnected");
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
        final String chooseUsername = new String("Chose a username: ");
        try {
            outputStream.writeUTF(chooseUsername);
        } catch (IOException e) {
            throw new DisconnectionException();
        }
    }

    public void arrangeForUsername(int trial) throws InvalidOperationException, DisconnectionException, ReconnectionException, InvalidUsernameException {
        boolean result;
        final String notAvailableMessage = new String("Not available, choose another username:");

        try {
            if(trial>1) outputStream.writeUTF(notAvailableMessage);
            username= inputStream.readUTF();
            System.out.println("received: " + username);
        } catch (IOException e) {
            throw new DisconnectionException();
        }
        MatchHandler.getInstance().requestUsername(username);

        System.out.println(username);
    }




}
