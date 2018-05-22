package it.polimi.ingsw.serverPart.netPart_container;


import it.polimi.ingsw.serverPart.custom_exception.*;


public interface UserInterface {
    boolean isConnected();

    void chooseUsername() throws DisconnectionException;

    String getUsername();

    void arrangeForUsername(int trial) throws InvalidOperationException, DisconnectionException, ReconnectionException, InvalidUsernameException;
    /*InvalidOperationException--> server full*/

    void setGameCode(int i);

    int getGameCode();

    void notifyStarting() throws DisconnectionException;

    void notifyStart() throws DisconnectionException;
}
