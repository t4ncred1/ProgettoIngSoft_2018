package it.polimi.ingsw.server.netPart_container;

import it.polimi.ingsw.client.ClientRemoteInterface;
import it.polimi.ingsw.server.MatchController;
import it.polimi.ingsw.server.MatchHandler;
import it.polimi.ingsw.server.custom_exception.DisconnectionException;
import it.polimi.ingsw.server.custom_exception.InvalidOperationException;
import it.polimi.ingsw.server.custom_exception.InvalidUsernameException;
import it.polimi.ingsw.server.custom_exception.ReconnectionException;

import java.rmi.RemoteException;
import java.util.ArrayList;

public class RMIUserAgent implements UserInterface {

    private ClientRemoteInterface clientHandled;
    private String username;
    private int gameCode;
    private MatchController gameHandling;

    public RMIUserAgent(ClientRemoteInterface clientToHandle){
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
        this.gameHandling=matchController;
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

    @Override
    public boolean equals(Object o){
        RMIUserAgent UA;
        if(o==null) return false;
        if(this.getClass()==o.getClass())
            UA= (RMIUserAgent) o;
        else
            return false;
        if(UA.clientHandled.equals(this.clientHandled))
            return true;
        else
            return false;
    }
}
