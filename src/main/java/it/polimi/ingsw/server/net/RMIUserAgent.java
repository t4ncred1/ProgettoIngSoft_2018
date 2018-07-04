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

public class RMIUserAgent implements UserInterface {

    private ClientRemoteInterface clientHandled;
    private String username;
    private RmiHandler myHandler;
    private int gameCode;


    public RMIUserAgent(ClientRemoteInterface clientToHandle, RmiHandler handler){
        this.myHandler=handler;
        clientHandled=clientToHandle;
    }

    @Override
    public boolean isConnected() {
        boolean ok;
        try{
            clientHandled.isConnected();
            ok=true;
        }
        catch (RemoteException e){
            ok=false;
        }
        return ok;
    }

    @Override
    public void chooseUsername() throws DisconnectionException {
        try {
            clientHandled.chooseUsername();
        } catch (RemoteException e) {
            e.printStackTrace();
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
            e.printStackTrace();
            throw new DisconnectionException();
        }
    }

    @Override
    public void notifyStart() throws DisconnectionException {
        try {
            clientHandled.notifyStartedGame();
        } catch (RemoteException e) {
            e.printStackTrace();
            throw new DisconnectionException();
        }
    }

    @Override
    public void notifyReconnection() throws DisconnectionException {
        try {
            clientHandled.notifyReconnection();
        } catch (RemoteException e) {
            e.printStackTrace();
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
    public void notifyEndTurn() {
        // TODO: 05/06/2018  
    }

    @Override
    public void sendGrids(Map<String, Grid> playersGrids) {
        // TODO: 26/06/2018
    }

    @Override
    public void notifyTurnInitialized() {
        // TODO: 26/06/2018
    }

    @Override
    public void notifyTurnOf(String username) {
        // TODO: 26/06/2018
    }

    @Override
    public void setToReconnecting() {
        // TODO: 27/06/2018  
    }

    @Override
    public void sendDicePool(List<Die> dicePool) {
        // TODO: 27/06/2018  
    }

    @Override
    public void sendGrid(Grid grid) {
        // TODO: 27/06/2018  
    }

    @Override
    public void synchronize(boolean disconnected, Grid grid, List<Die> dicePool) {
        // TODO: 28/06/2018  
    }

    @Override
    public void notifyEnd() {
        // TODO: 28/06/2018
    }

    @Override
    public void sendPoints(Map<String, String> playersPoints) {
        // TODO: 29/06/2018  
    }

    @Override
    public void sendRoundTrack(List<Die> roundTrack) {
        // TODO: 30/06/2018  
    }

    @Override
    public void sendToolCards(List<ToolCard> toolCards) {
        // TODO: 03/07/2018
    }

    //TODO from here.

    @Override
    public void notifyDisconnection() {

    }
}
