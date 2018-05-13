package it.polimi.ingsw;

import it.polimi.ingsw.customException.InvalidOperationException;

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
        System.out.println("Connected");
        try {
            MatchHandler.login(this);
            try {
                System.out.println("Connection protocol ended.");
                outputStream.writeUTF("Connected");
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (InvalidOperationException e) {
            e.printStackTrace();
            try {
                outputStream.writeUTF("Server is full, can't connect!");
            } catch (IOException e2) {
                e2.printStackTrace();
            }
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
    //funzioni per login
    //-----------------------------------------------------------------------------
    @Override
    public void chooseUsername() {
        final String chooseUsername = new String("Chose a username: ");
        try {
            outputStream.writeUTF(chooseUsername);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void arrangeForUsername() throws InvalidOperationException {

        boolean result;
        final String notAvailableMessage = new String("not available");
        int trials = 0;
        do
        {
            trials++;
            try {
                if(trials>1) outputStream.writeUTF(notAvailableMessage);
                username= inputStream.readUTF();
                System.out.println("letto: " + username);
            } catch (IOException e) {
                e.printStackTrace();
                //mettere qualcosa per bloccare ciclo infinito in caso di disconnessione
            }
            result = MatchHandler.getInstance().requestUsername(username);
        }
        while (!result);
        System.out.println(username);
        this.username=username;
        MatchHandler.getInstance().tryToConnect(username);
    }




}
