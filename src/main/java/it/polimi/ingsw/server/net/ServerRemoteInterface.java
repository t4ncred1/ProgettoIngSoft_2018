package it.polimi.ingsw.server.net;

import it.polimi.ingsw.client.net.ClientRMI;
import it.polimi.ingsw.client.net.ClientRemoteInterface;
import it.polimi.ingsw.server.MatchController;
import it.polimi.ingsw.server.cards.PrivateObjective;
import it.polimi.ingsw.server.components.Grid;
import it.polimi.ingsw.server.custom_exception.InvalidOperationException;
import it.polimi.ingsw.server.custom_exception.InvalidUsernameException;

import java.rmi.*;
import java.util.List;

public interface ServerRemoteInterface extends Remote {
    void login(ClientRemoteInterface client) throws RemoteException, InvalidOperationException, InvalidUsernameException;

    void logout(ClientRemoteInterface client) throws RemoteException, InvalidOperationException;

    void setControllerForClient(ClientRemoteInterface client, MatchController controller) throws RemoteException;

    List<Grid> getGrids(ClientRemoteInterface thisClient) throws RemoteException,InvalidOperationException;

    void setGrid(ClientRemoteInterface thisClient, int gridIndex) throws RemoteException,InvalidOperationException;

    PrivateObjective getPrivateObjective(ClientRMI thisClient) throws RemoteException;
}
