package it.polimi.ingsw;

import it.polimi.ingsw.customException.InvalidOperationException;

public interface ClientInterface {
    boolean isConnected();

    void chooseUsername();

    String getUsername();

    void arrangeForUsername() throws InvalidOperationException;
}
