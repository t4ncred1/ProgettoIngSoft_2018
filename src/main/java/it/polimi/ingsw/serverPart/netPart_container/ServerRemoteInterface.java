package it.polimi.ingsw.serverPart.netPart_container;

import it.polimi.ingsw.clientPart.ClientRemoteInterface;

import java.rmi.*;

public interface ServerRemoteInterface extends Remote {
     void login(ClientRemoteInterface client) throws RemoteException;
}
