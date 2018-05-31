package it.polimi.ingsw.server.net;

import com.google.gson.Gson;
import it.polimi.ingsw.server.MatchController;
import it.polimi.ingsw.server.MatchHandler;
import it.polimi.ingsw.server.components.Die;
import it.polimi.ingsw.server.components.Grid;
import it.polimi.ingsw.server.custom_exception.*;
import it.polimi.ingsw.server.custom_exception.DisconnectionException;
import it.polimi.ingsw.server.custom_exception.InvalidOperationException;
import it.polimi.ingsw.server.custom_exception.InvalidUsernameException;
import it.polimi.ingsw.server.custom_exception.ReconnectionException;


import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class SocketUserAgent extends Thread implements UserInterface {

    private Socket socket;
    private DataInputStream inputStream;
    private DataOutputStream outputStream;
    private MatchController gameHandling;
    private boolean inGame;

    private String username;


    private static final String PING_MESSAGE= "";
    private static final String HELLO_MESSAGE = "hello";

    private static final String LOGIN_MESSAGE_FROM_SERVER = "login";
    private static final String SUCCESSFULLY_LOGGED = "logged";
    private static final String SERVER_FULL = "notLogged_server_full";
    private static final String USERNAME_NOT_AVAILABLE= "notLogged_username_not_available";
    private static final String OK_REQUEST = "ok";
    private static final String NOT_OK_REQUEST = "retry";
    private static final String REQUEST_GRID = "get_grids";
    private static final String GRID_ALREADY_SELECTED= "grid_selected";
    private static final String CHOOSE_GRID="set_grid";

    private static final String GET_TURN_PLAYER = "get_turn_player";
    private static final String GET_DICE_POOL = "get_dice_pool";
    private static final String GAME_FINISHED= "finished";

    private static final String OPERATION_MESSAGE= "operation";
    private static final String GRID_DATA= "grid";
    private static final String TOOL_DATA="tool";
    private static final String END_DATA= "end_data";
    private static final String TURN_FINISHED= "finish";
    private static final String DICE_POOL_DATA= "dice_pool";
    private static final String DISCONNECTION = "disconnected";

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
        handleConnection();

        handleLogin();

        handleLogoutRequestBeforeStart();

        try {
            handleInitialization();
            boolean gameFinished=false;
            do{
                handleTurnLogic();
            }while (!gameFinished);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void handleTurnLogic() throws IOException {
        try {
            handleTurnPlayerRequest();
            sendDicePool();
        } catch (TooManyRoundsException e) {
            outputStream.writeUTF(GAME_FINISHED);
        }
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
        String request;
        boolean turnSent=false;
        do {
            waitForTurnPlayerRequest();
            try {
                String player = gameHandling.requestTurnPlayer();
                outputStream.writeUTF(player);
                turnSent = true;
            } catch (InvalidOperationException e) {
                outputStream.writeUTF(NOT_OK_REQUEST);
            }
        } while (!turnSent);
    }

    private void waitForTurnPlayerRequest() throws IOException {
        String request;
        do {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            request = inputStream.readUTF();
        }
        while (!request.equals(GET_TURN_PLAYER));
    }

    private void handleInitialization() throws IOException {
        try {
            handleGridsRequest();
            boolean gridSet;
            do {
                gridSet = handleGridSet();
            }
            while (!gridSet);
        } catch (InvalidOperationException e) {
            outputStream.writeUTF(GRID_ALREADY_SELECTED);
        }
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
            gameHandling.setGrid(this, gridChosen);
            outputStream.writeUTF(OK_REQUEST);
        } catch (InvalidOperationException e) {
            outputStream.writeUTF(NOT_OK_REQUEST);
            return false;
        }
        return true;
    }

    private void handleGridsRequest() throws IOException, InvalidOperationException {
        String request;
        do{
            request = inputStream.readUTF();
            if(!request.equals(REQUEST_GRID)) outputStream.writeUTF(NOT_OK_REQUEST);
        }while (!request.equals(REQUEST_GRID));
        outputStream.writeUTF(OK_REQUEST);
        ArrayList<Grid> grids;
        do{
            grids = (ArrayList<Grid>) gameHandling.getPlayerGrids(this);
        }
        while (grids==null);
        outputStream.writeUTF(OK_REQUEST);
        Gson gson= new Gson();
        outputStream.writeUTF(gson.toJson(grids));
    }

    private void handleLogoutRequestBeforeStart() {
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

    public void handleLogin(){
        boolean connected =false;
        do {
            try {
                MatchHandler.login(this);
                System.out.println("Connection protocol ended. Connected");
                try {
                    outputStream.writeUTF(SUCCESSFULLY_LOGGED);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                connected=true;
            } catch (InvalidOperationException e) {
                System.out.println("Connection protocol ended. Server is full");
                e.printStackTrace();
                try {
                    outputStream.writeUTF(SERVER_FULL);
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
                    outputStream.writeUTF(USERNAME_NOT_AVAILABLE);
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            } catch (ReconnectionException e) {
                System.out.println("Connection protocol ended. Client joined the game left before");
                connected=true;
            }
        }
        while(!connected);
    }

    private void handleConnection() {
        System.out.println("Connection request received on Socket system");
        try {
            String hello;
            do{
                hello= inputStream.readUTF();
            }
            while(!hello.equals(HELLO_MESSAGE));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean isConnected() {
        try{
            outputStream.writeUTF(PING_MESSAGE);
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
        final String chooseUsername = new String(LOGIN_MESSAGE_FROM_SERVER);
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
        this.gameHandling =matchController;
        this.inGame=true;
    }

    @Override
    public void notifyDieInsertion() {

    }

    @Override
    public void notifyToolUsed() {

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
    public void notifyDisconnection() {
        try {
            outputStream.writeUTF(OPERATION_MESSAGE);
            outputStream.writeUTF(DISCONNECTION);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
