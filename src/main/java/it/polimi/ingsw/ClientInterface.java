package it.polimi.ingsw;


import it.polimi.ingsw.custom_exception.DisconnectionException;
import it.polimi.ingsw.custom_exception.InvalidOperationException;
import it.polimi.ingsw.custom_exception.InvalidUsernameException;
import it.polimi.ingsw.custom_exception.ReconnectionException;

public interface ClientInterface {
    boolean isConnected();

    void chooseUsername() throws DisconnectionException;

    String getUsername();

    void arrangeForUsername(int trial) throws InvalidOperationException, DisconnectionException, ReconnectionException, InvalidUsernameException;
    /*InvalidOperationException--> server full*/
}
