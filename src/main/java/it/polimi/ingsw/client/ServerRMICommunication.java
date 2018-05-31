package it.polimi.ingsw.client;

import it.polimi.ingsw.client.custom_exception.*;
import it.polimi.ingsw.server.custom_exception.DisconnectionException;
import it.polimi.ingsw.server.custom_exception.InvalidOperationException;
import it.polimi.ingsw.server.netPart_container.ServerRemoteInterface;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class ServerRMICommunication implements ServerCommunicatingInterface {

    private transient ServerRemoteInterface stub;
    private transient ClientRMI thisClient;


    private static String registerName = "MatchHandler";
    private static int serverPort = 11001;
    private static String serverAddress="127.0.0.1";

    private transient boolean startingGame; //this parameter should be set to false once used.
    private transient boolean gameStarted;
    private transient boolean reconnection;


    @Override
    public void setUpConnection() throws ServerIsDownException{
        try {
            Registry registry = LocateRegistry.getRegistry(serverAddress,serverPort);
            stub = (ServerRemoteInterface) registry.lookup(registerName);
            thisClient=new ClientRMI();
            thisClient.setRMICommunication(this);
        } catch (Exception e) {
            throw new ServerIsDownException();
        }
    }

    @Override
    public void login(String username) throws ServerIsFullException, InvalidUsernameException, ServerIsDownException {
        try {
            thisClient.setUsername(username);
            stub.login(thisClient);
        } catch (RemoteException e) {
            throw new ServerIsDownException();
        } catch (InvalidOperationException e) {
            throw new ServerIsFullException();
        } catch (it.polimi.ingsw.server.custom_exception.InvalidUsernameException e) {
            throw new InvalidUsernameException();
        }

    }

    @Override
    public void waitForGame(boolean starting) throws GameStartingException, GameStartedException, TimerRestartedException, GameInProgressException {

        if (gameStarted) throw new GameStartedException();
        if (reconnection) throw new GameInProgressException();
        if (!starting) {
            if (startingGame) {
                startingGame = false;
                throw new GameStartingException();
            }
        } else {
            if (startingGame) {
                startingGame = false;
                throw new TimerRestartedException();
            }
        }
    }

    @Override
    public boolean logout() {
        Boolean ok=false;
        try{
            stub.logout(thisClient);
            ok=true;
        } catch (InvalidOperationException e) {
            e.printStackTrace();
        } catch (RemoteException e) {
            //TODO
            e.printStackTrace();
        }
        return ok;

    }

    @Override
    public void getGrids() throws ServerIsDownException, GameInProgressException {
        //TODO
    }

    @Override
    public void setGrid(int gridIndex) throws ServerIsDownException, InvalidMoveException {
        //TODO
    }

    @Override
    public String askTurn() {
        //TODO
        return null;
    }

    @Override
    public void listen(String username) throws ServerIsDownException, TurnFinishedException, DisconnectionException {
        //TODO
    }

    @Override
    public void getUpdatedDicePool() throws ServerIsDownException {

    }


    public void notifyStarting() {
        this.startingGame=true;
    }

    public void notifyStarted() {
        this.gameStarted=true;
        this.startingGame=false;
    }

    public void notifyReconnection() {
        this.reconnection=true;
    }
}
