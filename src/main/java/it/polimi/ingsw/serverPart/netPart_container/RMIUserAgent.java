package it.polimi.ingsw.serverPart.netPart_container;

import it.polimi.ingsw.clientPart.ClientRemoteInterface;
import it.polimi.ingsw.serverPart.MatchHandler;
import it.polimi.ingsw.serverPart.custom_exception.DisconnectionException;
import it.polimi.ingsw.serverPart.custom_exception.InvalidOperationException;
import it.polimi.ingsw.serverPart.custom_exception.InvalidUsernameException;
import it.polimi.ingsw.serverPart.custom_exception.ReconnectionException;

import java.rmi.RemoteException;

public class RMIUserAgent implements UserInterface {

    private ClientRemoteInterface clientHandled;
    private String username;
    private int gameCode;

    public RMIUserAgent(ClientRemoteInterface clientToHandle){
        clientHandled=clientToHandle;
    }

    @Override
    public boolean isConnected() {
        boolean ok=false;
        try{
            clientHandled.isConnected();
            ok=true;
        }
        catch (RemoteException e){
            System.err.println("A player disconnected");
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
    public void arrangeForUsername(int trial) throws InvalidOperationException, DisconnectionException, ReconnectionException, InvalidUsernameException {
        try {
            username= clientHandled.requestAUsername(trial);
            System.out.println("Selected username: " + username);
        } catch (RemoteException e) {
            throw new DisconnectionException();
        }
        MatchHandler.getInstance().requestUsername(username,this);
    }

    @Override
    public void setGameCode(int i) {
        this.gameCode=i;
    }

    @Override
    public int getGameCode() {
        return this.gameCode;
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
    public boolean equals(Object o){
        RMIUserAgent UA;
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
