package it.polimi.ingsw.server.net;


import it.polimi.ingsw.server.MatchController;
import it.polimi.ingsw.server.custom_exception.*;
import it.polimi.ingsw.server.model.components.Die;
import it.polimi.ingsw.server.model.components.Grid;

import java.util.List;
import java.util.Map;


public interface UserInterface {
    boolean isConnected();

    void chooseUsername() throws DisconnectionException;

    String getUsername();

    void arrangeForUsername() throws InvalidOperationException, DisconnectionException, ReconnectionException, InvalidUsernameException;
    /*InvalidOperationException--> server full*/

    void notifyStarting() throws DisconnectionException;

    void notifyStart() throws DisconnectionException;

    void notifyDisconnection();

    void notifyReconnection() throws DisconnectionException;

    void setController(MatchController matchController);

    void notifyDieInsertion();

    void notifyToolUsed();

    void notifyEndTurn();

    void sendGrids(Map<String,Grid> playersGrids);

    void notifyTurnInitialized();

    void notifyTurnOf(String username);

    void setToReconnecting();

    void sendDicePool(List<Die> dicePool);

    void sendGrid(Grid grid);

    void synchronize(boolean disconnected, Grid grid, List<Die> dicePool);

    void notifyEnd();

    void sendPoints(Map<String, String> playersPoints);
}
