package it.polimi.ingsw.client.net;

import it.polimi.ingsw.client.custom_exception.*;
import it.polimi.ingsw.client.custom_exception.invalid_operations.InvalidMoveException;
import it.polimi.ingsw.server.custom_exception.DisconnectionException;


public interface ServerCommunicatingInterface {

    void setUpConnection() throws ServerIsDownException;
    void login(String username) throws ServerIsFullException, InvalidUsernameException, ServerIsDownException;
    void waitForGame(boolean starting) throws GameStartingException, GameStartedException, TimerRestartedException, ServerIsDownException, GameInProgressException;
    void logout() throws ServerIsDownException, GameStartingException;


    void getGrids() throws ServerIsDownException, GameInProgressException;
    void setGrid(int gridIndex) throws ServerIsDownException, InvalidMoveException;
    void getPrivateObjective() throws ServerIsDownException;

    String askTurn() throws ServerIsDownException, ServerNotReadyException, GameFinishedException;

    void listen(String username) throws ServerIsDownException, TurnFinishedException, DisconnectionException;

    void getUpdatedDicePool() throws ServerIsDownException;

    void insertDie(int position, int x, int y) throws ServerIsDownException, InvalidMoveException;

    void endTurn() throws ServerIsDownException;

    void getSelectedGrid()throws ServerIsDownException;
}
