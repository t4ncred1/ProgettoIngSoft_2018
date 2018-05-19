package it.polimi.ingsw.netPart_container;

import it.polimi.ingsw.ClientInterface;
import it.polimi.ingsw.Client_side_container.ClientRemoteInterface;
import it.polimi.ingsw.MatchHandler;
import it.polimi.ingsw.custom_exception.DisconnectionException;
import it.polimi.ingsw.custom_exception.InvalidOperationException;
import it.polimi.ingsw.custom_exception.InvalidUsernameException;
import it.polimi.ingsw.custom_exception.ReconnectionException;

import java.rmi.RemoteException;
import java.util.logging.Logger;

public class ClientRemoteInterfaceAdapter implements ClientInterface {

    ClientRemoteInterface clientHandled;

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
        return null;
    }

    @Override
    public void arrangeForUsername(int trial) throws InvalidOperationException, DisconnectionException, ReconnectionException, InvalidUsernameException {
        String username =new String();
        try {
            username= clientHandled.requestAUsername(trial);
        } catch (RemoteException e) {
            throw new DisconnectionException();
        }
        MatchHandler.getInstance().requestUsername(username);
    }
}
