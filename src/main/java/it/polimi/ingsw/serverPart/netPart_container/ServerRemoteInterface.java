package it.polimi.ingsw.serverPart.netPart_container;

import it.polimi.ingsw.clientPart.ClientRemoteInterface;
import it.polimi.ingsw.serverPart.custom_exception.InvalidOperationException;
import it.polimi.ingsw.serverPart.custom_exception.InvalidUsernameException;

import java.rmi.*;

public interface ServerRemoteInterface extends Remote {
    void login(ClientRemoteInterface client) throws RemoteException, InvalidOperationException, InvalidUsernameException;

    void logout(ClientRemoteInterface client) throws RemoteException, InvalidOperationException;
}
