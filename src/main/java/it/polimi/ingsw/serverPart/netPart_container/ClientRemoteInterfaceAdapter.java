package it.polimi.ingsw.serverPart.netPart_container;

import it.polimi.ingsw.clientPart.ClientRemoteInterface;
import it.polimi.ingsw.serverPart.MatchHandler;
import it.polimi.ingsw.serverPart.custom_exception.DisconnectionException;
import it.polimi.ingsw.serverPart.custom_exception.InvalidOperationException;
import it.polimi.ingsw.serverPart.custom_exception.InvalidUsernameException;
import it.polimi.ingsw.serverPart.custom_exception.ReconnectionException;

import java.rmi.RemoteException;

public class ClientRemoteInterfaceAdapter implements ClientInterface {

    ClientRemoteInterface clientHandled;
    String username;

    public ClientRemoteInterfaceAdapter(ClientRemoteInterface clientToHandle){
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
            e.printStackTrace();
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
        MatchHandler.getInstance().requestUsername(username);
    }
}
