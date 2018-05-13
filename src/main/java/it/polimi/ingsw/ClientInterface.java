package it.polimi.ingsw;


import it.polimi.ingsw.custom_exception.DisconnectionException;
import it.polimi.ingsw.custom_exception.InvalidOperationException;

public interface ClientInterface {
    boolean isConnected();

    void chooseUsername() throws DisconnectionException;

    String getUsername();

    void arrangeForUsername() throws InvalidOperationException, DisconnectionException;
}
