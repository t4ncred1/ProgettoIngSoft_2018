package it.polimi.ingsw.client.net;

import it.polimi.ingsw.client.custom_exception.*;
import it.polimi.ingsw.client.custom_exception.invalid_operations.*;
import it.polimi.ingsw.server.custom_exception.DisconnectionException;
import it.polimi.ingsw.server.custom_exception.ReconnectionException;

import java.util.ArrayList;
import java.util.List;

public interface ServerCommunicatingInterface {
    /**
     * Handles the grid choice.
     *
     * @param i Grid's index.
     * @throws ServerIsDownException Thrown if the server is down.
     * @throws DisconnectionException Thrown if the user is trying to disconnect.
     * @throws InvalidIndexException Thrown if 'i' is not valid.
     */
    void selectGrid(int i) throws ServerIsDownException, DisconnectionException, InvalidIndexException;

    /**
     * Handles a logout.
     *
     * @throws ServerIsDownException Thrown if the server is down.
     * @throws GameStartingException Thrown if the game is starting.
     * @throws LoggedOutException Thrown if the user has correctly logged out.
     */
    void askForLogout() throws ServerIsDownException, GameStartingException, LoggedOutException;

    /**
     * Sets the connection.
     *
     * @throws ServerIsDownException Thrown if the server is down.
     */
    void setUpConnection() throws ServerIsDownException;

    /**
     * Handles the login.
     *
     * @param usernameChosen Username chosen.
     * @throws ServerIsFullException Thrown if the server is full.
     * @throws InvalidUsernameException Thrown if the username is not valid.
     * @throws ServerIsDownException Thrown if the server is down.
     * @throws ReconnectionException Thrown if the user successfully reconnected.
     */
    void login(String usernameChosen) throws ServerIsFullException, InvalidUsernameException, ServerIsDownException, ReconnectionException;

    /**
     * Sends out the parameters for a die insertion operation. Handles the server response.
     *
     * @param position Die chosen's position in dicePool.
     * @param column Column of the box.
     * @param row Row of the box.
     * @throws ServerIsDownException Thrown if the server is down.
     * @throws InvalidMoveException Thrown if the move is not valid.
     * @throws DieNotExistException Thrown if the die does not exist.
     * @throws AlreadyDoneOperationException Thrown if this operation was already done in this turn.
     * @throws DisconnectionException Thrown if the user is trying to disconnect.
     */
    void insertDie(int position, int column, int row) throws ServerIsDownException, InvalidMoveException, DieNotExistException, AlreadyDoneOperationException, DisconnectionException;

    /**
     * Handles the end-turn logic.
     *
     * @throws ServerIsDownException Thrown if the server is down.
     * @throws DisconnectionException Thrown if the user is trying to disconnect.
     */
    void endTurn() throws ServerIsDownException, DisconnectionException;

    /**
     * Execute an effect.
     *
     * @param effectName Name of the effect.
     * @param params List of parameters.
     * @throws ServerIsDownException Thrown if the server is down.
     * @throws DisconnectionException Thrown if the user is trying to disconnect.
     * @throws InvalidMoveException Thrown if the move is not valid.
     */
    void doEffect(String effectName, List<String> params) throws ServerIsDownException, DisconnectionException, InvalidMoveException;

    /**
     * Tool card choice.
     *
     * @param i Tool card's index.
     * @throws ServerIsDownException Thrown if the server is down.
     * @throws DisconnectionException Thrown if the user is trying to disconnect.
     * @throws ToolCardNotExistException Thrown if the index 'i' is not valid.
     * @throws AlreadyDoneOperationException Thrown if this operation was already done in this turn.
     */
    void useToolCard(int i) throws ServerIsDownException, DisconnectionException, ToolCardNotExistException, AlreadyDoneOperationException;

    /**
     * Tool cards execution.
     *
     * @throws ServerIsDownException Thrown if the server is down.
     * @throws DisconnectionException Thrown if the user is trying to disconnect.
     * @throws InvalidMoveException Thrown if the move is not valid.
     */
    void launchToolCards() throws ServerIsDownException, DisconnectionException, InvalidMoveException;
}
