package it.polimi.ingsw.server.net;


import it.polimi.ingsw.server.MatchController;
import it.polimi.ingsw.server.custom_exception.*;
import it.polimi.ingsw.server.model.cards.ToolCard;
import it.polimi.ingsw.server.model.components.Die;
import it.polimi.ingsw.server.model.components.Grid;

import java.util.List;
import java.util.Map;


public interface UserInterface {
    /**
     *
     * @return True if player is connected.
     */
    boolean isConnected();

    /**
     * Asks client to choose a username.
     *
     * @throws DisconnectionException Thrown when the client is disconnecting.
     */
    void chooseUsername() throws DisconnectionException;

    /**
     * Getter for username.
     *
     * @return Client's username.
     */
    String getUsername();

    /**
     * Checks the username chosen by the client.
     *
     * @throws InvalidOperationException Thrown when the server is full.
     * @throws DisconnectionException Thrown when the client is disconnecting.
     * @throws ReconnectionException Thrown when the client is trying to reconnect after a disconnection.
     * @throws InvalidUsernameException Thrown when the username is not available.
     */
    void arrangeForUsername() throws InvalidOperationException, DisconnectionException, ReconnectionException, InvalidUsernameException;
    /*InvalidOperationException--> server full*/

    /**
     * Sends a notification about a starting match.
     *
     * @throws DisconnectionException Thrown when the client is disconnecting.
     */
    void notifyStarting() throws DisconnectionException;

    /**
     * Sends a notification about match started.
     *
     * @throws DisconnectionException Thrown when the client is disconnecting.
     */
    void notifyStart() throws DisconnectionException;

    /**
     * Sends a notification about disconnection(timeout).
     */
    void notifyDisconnection();

    /**
     * Sends a notification about reconnection happening.
     *
     * @throws DisconnectionException Thrown when the client is disconnecting.
     */
    void notifyReconnection() throws DisconnectionException;

    /**
     * Sets the controller to the interfaces.
     *
     * @param matchController Match Controller.
     */
    void setController(MatchController matchController);

    void notifyDieInsertion();

    void notifyToolUsed();

    /**
     * Sends all players' grids to a player.
     *
     * @param playersGrids Players' grids.
     */
    void sendGrids(Map<String,Grid> playersGrids);

    /**
     * Sends a notify about initialization end.
     */
    void notifyTurnInitialized();

    /**
     * Sends a notification about current turn.
     *
     * @param username Player's username.
     */
    void notifyTurnOf(String username);

    void syncWithReconnectingUserAgent();

    /**
     * Sends the dice pool.
     *
     * @param dicePool Match dice pool.
     */
    void sendDicePool(List<Die> dicePool);

    /**
     * Sends the grid.
     *
     * @param grid Player's grid.
     */
    void sendGrid(Grid grid);

    void synchronizeEndTurn(boolean disconnected, Grid grid, List<Die> dicePool);

    void notifyEndGame();

    void sendPoints(Map<String, String> playersPoints);

    void sendRoundTrack(List<Die> roundTrack);

    void sendToolCards(List<ToolCard> toolCards);

    void notifyGameInitialized();
}
