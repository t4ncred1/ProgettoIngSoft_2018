package it.polimi.ingsw.serverPart.netPart_container;

import it.polimi.ingsw.serverPart.MatchController;
import it.polimi.ingsw.serverPart.MatchHandler;
import it.polimi.ingsw.serverPart.component_container.Grid;
import it.polimi.ingsw.serverPart.custom_exception.DisconnectionException;
import it.polimi.ingsw.serverPart.custom_exception.InvalidOperationException;
import it.polimi.ingsw.serverPart.custom_exception.InvalidUsernameException;
import it.polimi.ingsw.serverPart.custom_exception.ReconnectionException;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.NavigableMap;

public class SocketUserAgent extends Thread implements UserInterface {

    private Socket socket;
    private DataInputStream inputStream;
    private DataOutputStream outputStream;
    private MatchController gameHandiling;
    private boolean inGame;

    private String username;

    private static final String HELLO_MESSAGE="hello";
    private static final String OK_REQUEST = "ok";
    private static final String NOT_OK_REQUEST = "retry";
    private static final String REQUEST_GRID = "get_grids";
    private static final String CHOOSE_GRID="set_grid";

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
            while(!hello.equals(HELLO_MESSAGE)) hello= inputStream.readUTF();
        } catch (IOException e) {
            e.printStackTrace();
        }

        boolean connected =false;
        //FIXME make messages private static final
        do {
            final String notAvailableMessage = new String("notLogged_username_not_available");
            try {
                MatchHandler.login(this);
                System.out.println("Connection protocol ended. Connected");
                try {
                    outputStream.writeUTF("logged");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                connected=true;
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
            } catch (InvalidUsernameException e) {
                try {
                    outputStream.writeUTF(notAvailableMessage);
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            } catch (ReconnectionException e) {
                System.out.println("Connection protocol ended. Client joined the game left before");
                e.printStackTrace();
            }
        }
        while(!connected);

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

        try {
            handleInitialization();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void handleInitialization() throws IOException {
        handleGridsRequest();
        boolean gridSet;
        do {
            gridSet = handleGridSet();
        }
        while (!gridSet);
    }

    private boolean handleGridSet() throws IOException {
        String request;
        do {
            request = inputStream.readUTF();
            if (request.equals(CHOOSE_GRID)) {
                outputStream.writeUTF(OK_REQUEST);
            }
            else{
                outputStream.writeUTF(NOT_OK_REQUEST);
            }
        }
        while (!request.equals(CHOOSE_GRID));
        int gridChosen = inputStream.readInt();
        try {
            gameHandiling.setGrid(this, gridChosen);
            outputStream.writeUTF(OK_REQUEST);
        } catch (InvalidOperationException e) {
            outputStream.writeUTF(NOT_OK_REQUEST);
            return false;
        }
        return true;
    }

    private void handleGridsRequest() {
        try {
            String request;
            do{
                request = inputStream.readUTF();
                if(!request.equals(REQUEST_GRID)) outputStream.writeUTF(NOT_OK_REQUEST);
            }while (!request.equals(REQUEST_GRID));
            outputStream.writeUTF(OK_REQUEST);
            ArrayList<Grid> grids;
            do{
                grids = (ArrayList<Grid>) gameHandiling.getPlayerGrids(this);
            }
            while (grids==null);
            outputStream.writeInt(grids.size());
            for(Grid grid: grids){
                outputStream.writeUTF(grid.getName());
                outputStream.writeInt(grid.getDifficulty());
                String toSend = grid.getStructure();
                outputStream.writeUTF(toSend);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean isConnected() {
        try{
            outputStream.writeUTF("");
            return true;
        }
        catch (IOException e){
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
    public void arrangeForUsername() throws InvalidOperationException, DisconnectionException, ReconnectionException, InvalidUsernameException {
        try {
            username= inputStream.readUTF();
            System.out.println("received: " + username);
        } catch (IOException e) {
            throw new DisconnectionException();
        }
        MatchHandler.getInstance().requestUsername(username);
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

    @Override
    public void notifyReconnection() throws DisconnectionException {
        try {
            outputStream.writeUTF("logged");
            outputStream.writeUTF("reconnected");
        } catch (IOException e) {
            e.printStackTrace();
            throw new DisconnectionException();
        }
    }

    @Override
    public void setController(MatchController matchController) {
        this.gameHandiling=matchController;
        this.inGame=true;
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
