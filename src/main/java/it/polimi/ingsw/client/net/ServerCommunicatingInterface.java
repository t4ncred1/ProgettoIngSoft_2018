package it.polimi.ingsw.client.net;

import it.polimi.ingsw.client.custom_exception.*;
import it.polimi.ingsw.server.components.Grid;
import it.polimi.ingsw.server.custom_exception.DisconnectionException;

import java.util.List;


public interface ServerCommunicatingInterface {

    void setUpConnection() throws ServerIsDownException;
    void login(String username) throws ServerIsFullException, InvalidUsernameException, ServerIsDownException;
    void waitForGame(boolean starting) throws GameStartingException, GameStartedException, TimerRestartedException, ServerIsDownException, GameInProgressException;
    boolean logout() throws ServerIsDownException;


    List<Grid> getGrids() throws ServerIsDownException, GameInProgressException;
    void setGrid(int gridIndex) throws ServerIsDownException, InvalidMoveException;
    void getPrivateObjective() throws ServerIsDownException;

    String askTurn() throws ServerIsDownException, ServerNotReadyException, GameFinishedException;

    void listen(String username) throws ServerIsDownException, TurnFinishedException, DisconnectionException;

    void getUpdatedDicePool() throws ServerIsDownException;
}
