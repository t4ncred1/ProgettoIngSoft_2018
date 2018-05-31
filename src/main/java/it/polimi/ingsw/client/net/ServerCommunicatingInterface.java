package it.polimi.ingsw.client;

import it.polimi.ingsw.client.custom_exception.*;
import it.polimi.ingsw.server.custom_exception.DisconnectionException;


public interface ServerCommunicatingInterface {

    void setUpConnection() throws ServerIsDownException;
    void login(String username) throws ServerIsFullException, InvalidUsernameException, ServerIsDownException;
    void waitForGame(boolean starting) throws GameStartingException, GameStartedException, TimerRestartedException, ServerIsDownException, GameInProgressException;
    boolean logout() throws ServerIsDownException;


    void getGrids() throws ServerIsDownException, GameInProgressException;
    void setGrid(int gridIndex) throws ServerIsDownException, InvalidMoveException;

    String askTurn() throws ServerIsDownException, ServerNotReadyException, GameFinishedException;

    void listen(String username) throws ServerIsDownException, TurnFinishedException, DisconnectionException;

    void getUpdatedDicePool() throws ServerIsDownException;
}
