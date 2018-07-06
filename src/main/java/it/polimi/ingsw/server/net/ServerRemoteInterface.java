package it.polimi.ingsw.server.net;

import it.polimi.ingsw.client.net.ClientRMI;
import it.polimi.ingsw.client.net.ClientRemoteInterface;
import it.polimi.ingsw.server.MatchController;
import it.polimi.ingsw.server.custom_exception.*;
import it.polimi.ingsw.server.custom_exception.connection_exceptions.IllegalRequestException;
import it.polimi.ingsw.server.model.cards.PrivateObjective;
import it.polimi.ingsw.server.model.components.Die;
import it.polimi.ingsw.server.model.components.Grid;

import java.rmi.*;
import java.util.List;

public interface ServerRemoteInterface extends Remote {
    void login(ClientRemoteInterface client) throws RemoteException, InvalidOperationException, InvalidUsernameException;

    void logout(ClientRemoteInterface client) throws RemoteException, InvalidOperationException;

    void setControllerForClient(ClientRemoteInterface client, MatchController controller) throws RemoteException, InvalidOperationException, NotValidParameterException;

    List<Grid> getGrids(ClientRemoteInterface thisClient) throws RemoteException, InvalidOperationException, NotValidParameterException, IllegalRequestException;

    void setGrid(ClientRemoteInterface thisClient, int gridIndex) throws RemoteException, InvalidOperationException, NotValidParameterException, IllegalRequestException;

    PrivateObjective getPrivateObjective(ClientRemoteInterface thisClient) throws RemoteException, NotValidParameterException, IllegalRequestException;

    List<Die> getUpdatedDicePool(ClientRemoteInterface thisClient) throws NotValidParameterException, RemoteException;

    List<Grid> getGridSelection(ClientRemoteInterface clientCalling) throws RemoteException, InvalidOperationException;

    void selectGrid(ClientRemoteInterface clientCalling,int gridIndex) throws RemoteException,InvalidOperationException;

    void insertDie(ClientRemoteInterface clientCalling,int position, int column, int row)throws RemoteException, OperationAlreadyDoneException, NotInPoolException, InvalidOperationException;

    void endTurn(ClientRemoteInterface thisClient)throws RemoteException;
}
