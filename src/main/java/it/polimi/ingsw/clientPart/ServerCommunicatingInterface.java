package it.polimi.ingsw.clientPart;

import it.polimi.ingsw.clientPart.custom_exception.*;
import it.polimi.ingsw.serverPart.custom_exception.DisconnectionException;

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
