package it.polimi.ingsw.server.net;

import it.polimi.ingsw.client.net.ClientRMI;
import it.polimi.ingsw.client.net.ClientRemoteInterface;
import it.polimi.ingsw.server.MatchController;
import it.polimi.ingsw.server.cards.PrivateObjective;
import it.polimi.ingsw.server.components.Grid;
import it.polimi.ingsw.server.custom_exception.InvalidOperationException;
import it.polimi.ingsw.server.custom_exception.InvalidUsernameException;
import it.polimi.ingsw.server.custom_exception.NotValidParameterException;
import it.polimi.ingsw.server.custom_exception.TooManyRoundsException;

import java.rmi.*;
import java.util.List;

public interface ServerRemoteInterface extends Remote {
    void login(ClientRemoteInterface client) throws RemoteException, InvalidOperationException, InvalidUsernameException;

    void logout(ClientRemoteInterface client) throws RemoteException, InvalidOperationException;

    void setControllerForClient(ClientRemoteInterface client, MatchController controller) throws RemoteException;

    List<Grid> getGrids(ClientRemoteInterface thisClient) throws RemoteException, InvalidOperationException, NotValidParameterException;

    void setGrid(ClientRemoteInterface thisClient, int gridIndex) throws RemoteException, InvalidOperationException, NotValidParameterException;

    PrivateObjective getPrivateObjective(ClientRemoteInterface thisClient) throws RemoteException, NotValidParameterException;

    String askTurn(ClientRemoteInterface thisClient) throws RemoteException, NotValidParameterException, InvalidOperationException, TooManyRoundsException;
}
