package it.polimi.ingsw.client.net;

import it.polimi.ingsw.client.custom_exception.*;
import it.polimi.ingsw.client.custom_exception.invalid_operations.AlreadyDoneOperationException;
import it.polimi.ingsw.client.custom_exception.invalid_operations.DieNotExistException;
import it.polimi.ingsw.client.custom_exception.invalid_operations.InvalidMoveException;
import it.polimi.ingsw.server.custom_exception.DisconnectionException;

public interface ServerCommunicatingInterfaceV2 {
    void selectGrid(int i) throws ServerIsDownException, DisconnectionException;

    void askForLogout() throws ServerIsDownException, GameStartingException, LoggedOutException;

    void setUpConnection() throws ServerIsDownException;

    void login(String usernameChosen) throws ServerIsFullException, InvalidUsernameException, ServerIsDownException;

    void insertDie(int position, int column, int row) throws ServerIsDownException, InvalidMoveException, DieNotExistException, AlreadyDoneOperationException, DisconnectionException;

    void endTurn() throws ServerIsDownException, DisconnectionException;
}
