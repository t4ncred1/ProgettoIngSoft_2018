package it.polimi.ingsw.client.net;

import it.polimi.ingsw.client.MainClient;
import it.polimi.ingsw.client.Proxy;

import it.polimi.ingsw.client.custom_exception.GameFinishedException;
import it.polimi.ingsw.server.custom_exception.InvalidOperationException;

import it.polimi.ingsw.server.model.cards.ToolCard;
import it.polimi.ingsw.server.model.components.Die;
import it.polimi.ingsw.server.model.components.Grid;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Logger;

public class ClientRMI extends UnicastRemoteObject implements ClientRemoteInterface {

    private transient ServerRMICommunication serverRemoteInterfaceAdapter;

    private String username;
    private Logger logger;
    private Lock lock;
    private boolean endTurn;
    private boolean firstInitialization;
    private boolean retrievingData;

    protected ClientRMI()  throws RemoteException{
        logger= Logger.getLogger(ClientRMI.class.getName());
        lock= new ReentrantLock();
        firstInitialization=true;
        endTurn=false;
        retrievingData=false;
    }

    @Override
    public void isConnected() {
        //here just to throw remote exception if client disconnect
    }

    @Override
    public void chooseUsername() {

    }

    @Override
    public String requestAUsername() {
        return this.username;
    }

    @Override
    public void notifyGameStarting() {
        MainClient.getInstance().notifyGameStarting();
    }

    @Override
    public void notifyStartedGame(){
        MainClient.getInstance().notifyGameStarted();
        serverRemoteInterfaceAdapter.getGridSelectionFromServer();
    }

    @Override
    public void notifyReconnection() {

    }

    @Override
    public void setGrids(Map<String, Grid> playersGrids) {
        Proxy.getInstance().setGridsForEachPlayer(playersGrids);
    }

    @Override
    public void setDicePool(List<Die> dicePool) {
        Proxy.getInstance().setDicePool(dicePool);
    }

    @Override
    public void setSingleGrid(Grid grid) {
        Proxy.getInstance().updateGrid(grid);
    }

    @Override
    public void setRoundTrack(List<Die> roundTrack) {
        Proxy.getInstance().setRoundTrack(roundTrack);
    }

    @Override
    public void setToolCards(List<ToolCard> toolCards) {
        Proxy.getInstance().setToolCards(toolCards);
    }

    @Override
    public void notifyEndGame() {
        Proxy.getInstance().setGameToFinished();
    }

    @Override
    public void setPoints(Map<String, String> playersPoints) {
        Proxy.getInstance().setPoints(playersPoints);
        MainClient.getInstance().notifyEndDataInProxy();
    }

    @Override
    public void notifyEndTurn() {
        endTurn=true;
        MainClient.getInstance().notifyEndTurn();
    }

    @Override
    public void notifyTurnInitialized() {
        if(endTurn) {
            checkDataRetrieve();
            endTurn=false;
            MainClient.getInstance().notifyTurnUpdated();
        }else {
            checkDataRetrieve();
            MainClient.getInstance().notifySomethingChanged();
        }

    }

    private void checkDataRetrieve() {
        lock.lock();
        if(retrievingData){
            retrievingData=false;
            lock.unlock();
            serverRemoteInterfaceAdapter.notifyDataRetrieved();
            lock.lock();
        }
        lock.unlock();
    }


    @Override
    public void notifyTurnOf(String username) {
        Proxy.getInstance().setTurnPlayer(username);
        MainClient.getInstance().notifyTurnUpdated();
    }

    @Override
    public void notifyDisconnection() {
        serverRemoteInterfaceAdapter.setToDisconnected();
    }

    @Override
    public void setTurnPlayerToDisconnected() {
        Proxy.getInstance().setPlayerToDisconnected();
    }

    @Override
    public void notifyGameInitialized(){
        MainClient.getInstance().setGameToInitialized();
    }


    public void setRMICommunication(ServerRMICommunication serverRemoteInterfaceAdapter) {
        this.serverRemoteInterfaceAdapter=serverRemoteInterfaceAdapter;
    }

    public void setUsername(String username) {
        this.username=username;
    }

    public void setRetrieving() {
        lock.lock();
        retrievingData=true;
        lock.unlock();
    }
}
