package it.polimi.ingsw.server.net;

import it.polimi.ingsw.client.net.ClientRemoteInterface;
import it.polimi.ingsw.server.custom_exception.InvalidOperationException;
import it.polimi.ingsw.server.custom_exception.InvalidUsernameException;

import java.rmi.*;

public interface ServerRemoteInterface extends Remote {
    void login(ClientRemoteInterface client) throws RemoteException, InvalidOperationException, InvalidUsernameException;

    void logout(ClientRemoteInterface client) throws RemoteException, InvalidOperationException;
}
