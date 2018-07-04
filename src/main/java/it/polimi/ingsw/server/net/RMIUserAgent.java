package it.polimi.ingsw.server.net;

import it.polimi.ingsw.client.net.ClientRemoteInterface;
import it.polimi.ingsw.server.MatchController;
import it.polimi.ingsw.server.MatchHandler;
import it.polimi.ingsw.server.custom_exception.*;
import it.polimi.ingsw.server.model.cards.ToolCard;
import it.polimi.ingsw.server.model.components.Die;
import it.polimi.ingsw.server.model.components.Grid;

import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Logger;

public class RMIUserAgent implements UserInterface {

    private ClientRemoteInterface clientHandled;
    private String username;
    private RmiHandler myHandler;
    private int gameCode;
    private Lock lock;
    private Logger logger;

    private static final String DISCONNECTED_LOG= "Disconnected";

    public RMIUserAgent(ClientRemoteInterface clientToHandle, RmiHandler handler){
        this.myHandler=handler;
        clientHandled=clientToHandle;
        lock= new ReentrantLock();
        logger=Logger.getLogger(RMIUserAgent.class.getName()+"_%u");
    }

    @Override
    public boolean isConnected() {
        try{
            clientHandled.isConnected();
            return true;
        }
        catch (RemoteException e){
            return false;
        }
    }

    @Override
    public void chooseUsername() throws DisconnectionException {
        try {
            clientHandled.chooseUsername();
        } catch (RemoteException e) {
            throw new DisconnectionException();
        }

    }

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public void arrangeForUsername() throws InvalidOperationException, DisconnectionException, ReconnectionException, InvalidUsernameException {
        try {
            username= clientHandled.requestAUsername();
            System.out.println("Selected username: " + username);
        } catch (RemoteException e) {
            throw new DisconnectionException();
        }
        MatchHandler.getInstance().requestUsername(username);
    }


    @Override
    public void notifyStarting() throws DisconnectionException {
        try {
            clientHandled.notifyGameStarting();
        } catch (RemoteException e) {
            throw new DisconnectionException();
        }
    }

    @Override
    public void notifyStart() throws DisconnectionException {
        try {
            clientHandled.notifyStartedGame();
        } catch (RemoteException e) {
            throw new DisconnectionException();
        }
    }

    @Override
    public void notifyReconnection() throws DisconnectionException {
        try {
            clientHandled.notifyReconnection();
        } catch (RemoteException e) {
            throw new DisconnectionException();
        }
    }

    @Override
    public void setController(MatchController matchController) {
        try {
            myHandler.setControllerForClient(clientHandled,matchController);
        } catch (NotValidParameterException e) {
            e.printStackTrace();    //should not happen if handler is initialized correctly
        } catch (InvalidOperationException e) {
            e.printStackTrace();    //should not happen if handler is initialized correctly
        }
    }

    @Override
    public void notifyDieInsertion() {

    }

    @Override
    public void notifyToolUsed() {

    }

    @Override
    public void sendGrids(Map<String, Grid> playersGrids) {
        try {
            clientHandled.setGrids(playersGrids);
        } catch (RemoteException e) {
            logger.fine(DISCONNECTED_LOG);
        }
    }

    @Override
    public void notifyTurnInitialized() {
        try{
            clientHandled.notifyTurnInitialized();
        } catch (RemoteException e) {
            logger.fine(DISCONNECTED_LOG);
        }
    }

    @Override
    public void notifyTurnOf(String username) {
        try{
            clientHandled.notifyTurnOf(username);
        } catch (RemoteException e) {
            logger.fine(DISCONNECTED_LOG);
        }
    }

    @Override
    public void setToReconnecting() {
        // TODO: 27/06/2018  
    }

    @Override
    public void sendDicePool(List<Die> dicePool) {
        try {
            clientHandled.setDicePool(dicePool);
        } catch (RemoteException e) {
            logger.fine(DISCONNECTED_LOG);
        }
    }

    @Override
    public void sendGrid(Grid grid) {
        try {
            clientHandled.setSingleGrid(grid);
        } catch (RemoteException e) {
            logger.fine(DISCONNECTED_LOG);
        }
    }

    @Override
    public void synchronizeEndTurn(boolean disconnected, Grid grid, List<Die> dicePool) {
        try {
            clientHandled.setTurnPlayerToDisconnected();
            sendDicePool(dicePool);
            sendGrid(grid);
            clientHandled.notifyEndTurn();
        } catch (RemoteException e) {
            logger.fine(DISCONNECTED_LOG);
        }
//        try {
//            syncLock.lock();
//            waitDataRetrieve();
//        } catch (IOException e) {
//            logger.fine("Disconnected");
//        } finally {
//            syncLock.unlock();
//        }
    }

    @Override
    public void notifyEndGame() {
        try {
            clientHandled.notifyEndGame();
        } catch (RemoteException e) {
            logger.fine(DISCONNECTED_LOG);
        }
    }

    @Override
    public void sendPoints(Map<String, String> playersPoints) {
        try {
            clientHandled.setPoints(playersPoints);
        } catch (RemoteException e) {
            logger.fine(DISCONNECTED_LOG);
        }
    }

    @Override
    public void sendRoundTrack(List<Die> roundTrack) {
        try {
            clientHandled.setRoundTrack(roundTrack);
        } catch (RemoteException e) {
            logger.fine(DISCONNECTED_LOG);
        }
    }

    @Override
    public void sendToolCards(List<ToolCard> toolCards) {
        try{
            clientHandled.setToolCards(toolCards);
        } catch (RemoteException e) {
            logger.fine(DISCONNECTED_LOG);
        }
    }

    @Override
    public void notifyGameInitialized() {
        try {
            clientHandled.notifyGameInitialized();
        } catch (RemoteException e) {
            logger.fine(DISCONNECTED_LOG);
        }
    }

    @Override
    public void notifyDisconnection() {
        try{
            clientHandled.notifyDisconnection();
        } catch (RemoteException e) {
            logger.fine(DISCONNECTED_LOG);
        }
    }
}
