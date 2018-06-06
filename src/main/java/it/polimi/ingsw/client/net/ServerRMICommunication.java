package it.polimi.ingsw.client.net;

import it.polimi.ingsw.client.Proxy;
import it.polimi.ingsw.client.custom_exception.*;
import it.polimi.ingsw.server.MatchController;
import it.polimi.ingsw.server.components.Grid;
import it.polimi.ingsw.server.custom_exception.DisconnectionException;
import it.polimi.ingsw.server.custom_exception.InvalidOperationException;
import it.polimi.ingsw.server.custom_exception.NotValidParameterException;
import it.polimi.ingsw.server.custom_exception.TooManyRoundsException;
import it.polimi.ingsw.server.net.ServerRemoteInterface;

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

    public ServerRMICommunication(){
        try {
            thisClient=new ClientRMI();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void setUpConnection() throws ServerIsDownException{
        try {
            Registry registry = LocateRegistry.getRegistry(serverAddress,serverPort);
            stub = (ServerRemoteInterface) registry.lookup(registerName);
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
    public void logout() throws ServerIsDownException, GameStartingException {
        try{
            stub.logout(thisClient);
        } catch (InvalidOperationException e) {
            throw new GameStartingException();    //should print "You can't logout" in caller method.
        }
        catch (RemoteException e) {
            throw new ServerIsDownException();  //Only thrown if server is not reachable. (Remote Exception in RMI)
        }

    }

    @Override
    public void getGrids() throws ServerIsDownException, GameInProgressException {
        List<Grid> grids=null;
//        try {
//            stub.setControllerForClient(thisClient, controller);    //controller is set here because it's the first request to controller.
//        } catch (RemoteException e) {
//            throw new ServerIsDownException();
//        } catch (InvalidOperationException e) {
//            e.printStackTrace(); //should only happen if get grids was already called.
//        } catch (NotValidParameterException e) {
//            e.printStackTrace(); //should not be thrown ad parameters passed are not null.
//
//        }
        try {
            grids = stub.getGrids(thisClient);
        } catch (InvalidOperationException e) {
            throw new GameInProgressException();
        } catch (RemoteException e) {
            e.printStackTrace();
            throw new ServerIsDownException();
        } catch (NotValidParameterException e) {
            e.printStackTrace();    //should not happen if this client is correctly registered.
        }
        try {
            Proxy.getInstance().setGridsSelection(grids);
        } catch (InvalidOperationException e) {
            e.printStackTrace();    //thrown if passed grids are null. Can only happen if the above NotValidParameterException occurs.
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
            e.printStackTrace(); //only thrown if parameter thisClient is invalid, should not happen.
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
    public String askTurn() throws GameFinishedException, ServerIsDownException, ServerNotReadyException {
        String username=null;
        try {
             username=stub.askTurn(thisClient);
        } catch (NotValidParameterException e) {
            e.printStackTrace();    //shall be thrown only if this client is not in the associated match.
        } catch (InvalidOperationException e) {
            throw new ServerNotReadyException();
        } catch (TooManyRoundsException e) {
            throw new GameFinishedException();
        } catch (RemoteException e){
            throw new ServerIsDownException();
        }
        return username;    //it shall return null only if NotValidParameterException is thrown.
    }

    @Override
    public void listen(String username) throws ServerIsDownException, TurnFinishedException, DisconnectionException {
        //TODO
    }

    @Override
    public void getUpdatedDicePool() throws ServerIsDownException {
        try {
            stub.getUpdatedDicepool(thisClient);    //fixme see method in ServerSocketCommunication.
        } catch (NotValidParameterException e) {
            e.printStackTrace();    //should only happen if current client isn't registered to the game.
        } catch (RemoteException e) {
            throw new ServerIsDownException();
        }
    }

    @Override
    public void insertDie(int position, int x, int y) throws ServerIsDownException, InvalidMoveException {

    }

    @Override
    public void endTurn() throws ServerIsDownException {

    }

    @Override
    public void getSelectedGrid() throws ServerIsDownException {
        // TODO: 05/06/2018  
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
