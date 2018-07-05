package it.polimi.ingsw.client.net;

import it.polimi.ingsw.client.MainClient;
import it.polimi.ingsw.client.Proxy;
import it.polimi.ingsw.client.configurations.ConfigHandler;
import it.polimi.ingsw.client.custom_exception.*;
import it.polimi.ingsw.client.custom_exception.InvalidUsernameException;
import it.polimi.ingsw.client.custom_exception.invalid_operations.*;
import it.polimi.ingsw.server.MatchController;
import it.polimi.ingsw.server.custom_exception.*;
import it.polimi.ingsw.server.model.components.Grid;
import it.polimi.ingsw.server.net.ServerRemoteInterface;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ServerRMICommunication implements ServerCommunicatingInterface {

    private Logger logger;
    private ClientRMI thisClient;
    private ServerRemoteInterface stub;
    private boolean disconnected;
    private Lock lock;
    private Condition condition;

    private String registerName = "MatchHandler";
    private int serverPort = 11001;
    private String serverAddress="127.0.0.1";

    private transient boolean startingGame; //this parameter should be set to false once used.
    private transient boolean gameStarted;
    private transient boolean reconnection;
    private transient MatchController controller;
    private boolean dataRetrieved;

    public ServerRMICommunication(){
        logger= Logger.getLogger(ServerRMICommunication.class.getName());
        lock= new ReentrantLock();
        condition= lock.newCondition();
        try {
            thisClient=new ClientRMI();
            disconnected=false;
        } catch (RemoteException e) {
            logger.log(Level.SEVERE,"Can't initialize ClientRMI" , e);
        }
        dataRetrieved=false;
    }
    @Override
    public void selectGrid(int gridIndex) throws ServerIsDownException, DisconnectionException, InvalidIndexException {
        try {
            stub.selectGrid(thisClient,gridIndex);
            lock.lock();
            if(disconnected){
                lock.unlock();
                throw new DisconnectionException();
            }
            lock.unlock();
        } catch (RemoteException e) {
            throw new ServerIsDownException();
        } catch (InvalidOperationException e) {
            throw new InvalidIndexException();
        }
    }

    @Override
    public void askForLogout() throws ServerIsDownException, GameStartingException, LoggedOutException {
        // TODO: 04/07/2018  
    }

    @Override
    public void setUpConnection() throws ServerIsDownException {
        try {
            try {
                serverPort = ConfigHandler.getInstance().getRmiPort();
                serverAddress = ConfigHandler.getInstance().getServerIp();
                registerName = ConfigHandler.getInstance().getRegisterName();
            } catch (NotValidConfigPathException e) {
                System.out.println("Wrong configuration file, using defaults.");
            }
            Registry registry = LocateRegistry.getRegistry(serverAddress,serverPort);
            stub = (ServerRemoteInterface) registry.lookup(registerName);
            thisClient.setRMICommunication(this);
        } catch (Exception e) {
            throw new ServerIsDownException();
        }
    }

    @Override
    public void login(String username) throws ServerIsFullException, InvalidUsernameException, ServerIsDownException, ReconnectionException {
        try {
            thisClient.setUsername(username);
            stub.login(thisClient);
            Proxy.getInstance().setMyUsername(username);
        } catch (RemoteException e) {
            throw new ServerIsDownException();
        } catch (InvalidOperationException e) {
            throw new ServerIsFullException();
        } catch (it.polimi.ingsw.server.custom_exception.InvalidUsernameException e) {
            throw new InvalidUsernameException();
        }
        // TODO: 04/07/2018 handle reconnection
    }

    @Override
    public void insertDie(int position, int column, int row) throws ServerIsDownException, InvalidMoveException, DieNotExistException, AlreadyDoneOperationException, DisconnectionException {
        try {
            stub.insertDie(thisClient,position,column,row);
            lock.lock();
            if(disconnected){
                lock.unlock();
                throw new DisconnectionException();
            }
            lock.unlock();
            waitForDataRetrieve();

        } catch (RemoteException e) {
            throw new ServerIsDownException();
        } catch (OperationAlreadyDoneException e) {
            throw new AlreadyDoneOperationException();
        } catch (NotInPoolException e) {
            throw new DieNotExistException();
        } catch (InvalidOperationException e) {
            throw new InvalidMoveException();
        }
    }

    private void waitForDataRetrieve() {
        lock.lock();
        while (!dataRetrieved){
            try {
                condition.await();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        dataRetrieved=false;
        lock.unlock();
    }

    @Override
    public void endTurn() throws ServerIsDownException, DisconnectionException {
        try {
            stub.endTurn(thisClient);
            lock.lock();
            if(disconnected){
                lock.unlock();
                throw new DisconnectionException();
            }
            lock.unlock();
        } catch (RemoteException e) {
            throw new ServerIsDownException();
        }
    }

    @Override
    public void doEffect(String effectName, List<String> params) throws ServerIsDownException, DisconnectionException, InvalidMoveException {

    }

    @Override
    public void useToolCard(int i) throws ServerIsDownException, DisconnectionException, ToolCardNotExistException, AlreadyDoneOperationException {

    }

    @Override
    public void launchToolCards() throws ServerIsDownException, DisconnectionException {

    }

    public void getGridSelectionFromServer(){
        new Thread(this::retrieveGridSelection).start();
    }

    private void retrieveGridSelection() {
        try {
            List<Grid> gridSelection=stub.getGridSelection(thisClient);;
            Proxy.getInstance().setGridsSelection(gridSelection);
            MainClient.getInstance().notifyGridsAreInProxy();
        } catch (InvalidOperationException e) {
            MainClient.getInstance().setGridsAlreadySelected(true);
        } catch (RemoteException e) {
            logger.severe("Grave error");
        }
    }

    public void setToDisconnected() {
        lock.lock();
        disconnected=true;
        lock.unlock();
    }

    protected void notifyDataRetrieved(){
        lock.lock();
        dataRetrieved=true;
        condition.signal();
        lock.unlock();
    }
}
