package it.polimi.ingsw.clientPart;

import it.polimi.ingsw.clientPart.custom_exception.*;

public interface ServerCommunicatingInterface {

    void setUpConnection() throws ServerIsDownException;
    void login(String username) throws ServerIsFullException, InvalidUsernameException, ServerIsDownException;
    void waitForGame(boolean starting) throws GameStartingException, GameStartedException, TimerRestartedException, ServerIsDownException;
    boolean logout() throws ServerIsDownException;


}
