package it.polimi.ingsw.client.net;

import it.polimi.ingsw.server.model.cards.ToolCard;
import it.polimi.ingsw.server.model.components.Die;
import it.polimi.ingsw.server.model.components.Grid;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;

public interface ClientRemoteInterface extends Remote {
    void isConnected() throws RemoteException;

    void chooseUsername() throws RemoteException;

    String requestAUsername() throws RemoteException;

    void notifyGameStarting() throws RemoteException;

    void notifyStartedGame() throws RemoteException;

    void notifyReconnection() throws RemoteException;

    void setGrids(Map<String, Grid> playersGrids, List<String> connectedPlayers)throws RemoteException;

    void setDicePool(List<Die> dicePool) throws RemoteException;

    void setSingleGrid(Grid grid) throws RemoteException;

    void setRoundTrack(List<Die> roundTrack) throws RemoteException;

    void setToolCards(List<ToolCard> toolCards) throws RemoteException;

    void notifyEndGame() throws RemoteException;

    void setPoints(Map<String, String> playersPoints) throws RemoteException;

    void notifyEndTurn() throws RemoteException;

    void notifyTurnInitialized() throws RemoteException;

    void notifyTurnOf(String username) throws RemoteException;

    void notifyDisconnection() throws RemoteException;

    void setTurnPlayerToDisconnected() throws RemoteException;

    void notifyGameInitialized() throws RemoteException;
}
