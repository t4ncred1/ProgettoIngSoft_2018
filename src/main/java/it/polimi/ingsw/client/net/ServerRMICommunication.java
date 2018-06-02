package it.polimi.ingsw.client.net;

import it.polimi.ingsw.client.custom_exception.*;
import it.polimi.ingsw.server.MatchController;
import it.polimi.ingsw.server.components.Grid;
import it.polimi.ingsw.server.custom_exception.DisconnectionException;
import it.polimi.ingsw.server.custom_exception.InvalidOperationException;
import it.polimi.ingsw.server.custom_exception.NotValidParameterException;
import it.polimi.ingsw.server.net.ServerRemoteInterface;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.List;

public class ServerRMICommunication implements ServerCommunicatingInterface {

    private transient ServerRemoteInterface stub;
    private transient ClientRMI thisClient;


    private static String registerName = "MatchHandler";
    private static int serverPort = 11001;
    private static String serverAddress="127.0.0.1";

    private transient boolean startingGame; //this parameter should be set to false once used.
    private transient boolean gameStarted;
    private transient boolean reconnection;
    private transient MatchController controller;


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
    public boolean logout() throws ServerIsDownException{
        Boolean ok=false;
        try{
            stub.logout(thisClient);
            ok=true;
        } catch (InvalidOperationException e) {
            e.printStackTrace();    //should print "You can't logout" in caller method.
        }
        catch (RemoteException e) {
            throw new ServerIsDownException();  //Only thrown if server is not reachable. (Remote Exception in RMI)
        }
        return ok;  //returns false if server is down?

    }

    @Override
    public void getGrids() throws ServerIsDownException, GameInProgressException {
        try {
            stub.setControllerForClient(thisClient, controller);    //controller is set here because it's the first request to controller.
            List<Grid> grids = stub.getGrids(thisClient);
            //todo implement proxy.
        } catch (InvalidOperationException e) {
            throw new GameInProgressException();
        } catch (RemoteException e) {
            throw new ServerIsDownException();
        } catch (NotValidParameterException e) {
            e.printStackTrace();    //should not happen if this client is correctly registered.
        }
    }

    @Override
    public void setGrid(int gridIndex) throws ServerIsDownException, InvalidMoveException {
        try {
            stub.setGrid(thisClient,gridIndex);
        } catch (InvalidOperationException e) {
            throw new InvalidMoveException();
        } catch (RemoteException e){
            throw new ServerIsDownException();
        } catch (NotValidParameterException e) {
            e.printStackTrace(); //
        }
    }

    @Override
    public void getPrivateObjective() throws ServerIsDownException {
        try{
            stub.getPrivateObjective(thisClient);
        } catch (RemoteException e){
            throw new ServerIsDownException();
        } catch (NotValidParameterException e) {
            e.printStackTrace();//Shall happen if this client is not registered to the match. Should not be the case.
        }
    }

    @Override
    public String askTurn() {
        //stub.askTurn() todo
        return null;
    }

    @Override
    public void listen(String username) throws ServerIsDownException, TurnFinishedException, DisconnectionException {
        //TODO
    }

    @Override
    public void getUpdatedDicePool() throws ServerIsDownException {
        //TODO
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

    public void setController(MatchController matchController) {
        this.controller=matchController;
    }
}
