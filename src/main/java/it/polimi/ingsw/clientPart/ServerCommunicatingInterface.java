package it.polimi.ingsw.clientPart;

import it.polimi.ingsw.clientPart.custom_exception.ServerIsDownException;
import it.polimi.ingsw.clientPart.custom_exception.ServerIsFullException;

public interface ServerCommunicatingInterface {
    void login() throws ServerIsDownException, ServerIsFullException;
    boolean waitForGame();
    boolean logout();
}
