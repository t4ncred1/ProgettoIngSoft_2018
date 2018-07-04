package it.polimi.ingsw.client.net;

import it.polimi.ingsw.client.custom_exception.*;
import it.polimi.ingsw.client.custom_exception.invalid_operations.AlreadyDoneOperationException;
import it.polimi.ingsw.client.custom_exception.invalid_operations.DieNotExistException;
import it.polimi.ingsw.client.custom_exception.invalid_operations.InvalidMoveException;
import it.polimi.ingsw.client.custom_exception.invalid_operations.ToolCardNotExistException;
import it.polimi.ingsw.server.custom_exception.DisconnectionException;
import it.polimi.ingsw.server.custom_exception.ReconnectionException;

import java.util.ArrayList;
import java.util.List;

public interface ServerCommunicatingInterfaceV2 {
    void selectGrid(int i) throws ServerIsDownException, DisconnectionException;

    void askForLogout() throws ServerIsDownException, GameStartingException, LoggedOutException;

    void setUpConnection() throws ServerIsDownException;

    void login(String usernameChosen) throws ServerIsFullException, InvalidUsernameException, ServerIsDownException, ReconnectionException;

    void insertDie(int position, int column, int row) throws ServerIsDownException, InvalidMoveException, DieNotExistException, AlreadyDoneOperationException, DisconnectionException;

    void endTurn() throws ServerIsDownException, DisconnectionException;

    void doEffect(String effectName, List<String> params) throws ServerIsDownException, DisconnectionException, InvalidMoveException;

    void useToolCard(int i) throws ServerIsDownException, DisconnectionException, ToolCardNotExistException, AlreadyDoneOperationException;

    void launchToolCards() throws ServerIsDownException, DisconnectionException;
}
