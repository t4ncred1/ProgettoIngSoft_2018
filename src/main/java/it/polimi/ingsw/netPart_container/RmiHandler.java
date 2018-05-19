package it.polimi.ingsw.netPart_container;

import it.polimi.ingsw.Client_side_container.ClientRemoteInterface;
import it.polimi.ingsw.MatchHandler;
import it.polimi.ingsw.custom_exception.DisconnectionException;
import it.polimi.ingsw.custom_exception.InvalidOperationException;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class RmiHandler extends UnicastRemoteObject implements ServerRemoteInterface{

    private static final int port=11001;

    public RmiHandler() throws RemoteException {
    }

    public int getPort(){
        return port;
    }

    @Override
    public void login(ClientRemoteInterface client) {
        try {
            MatchHandler.login(new ClientRemoteInterfaceAdapter(client));
        }
        catch (DisconnectionException e){

        }
        catch (InvalidOperationException e) {
            e.printStackTrace();
        }
    }
}
