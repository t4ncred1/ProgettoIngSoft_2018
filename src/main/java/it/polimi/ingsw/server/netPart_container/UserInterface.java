package it.polimi.ingsw.server.netPart_container;


import it.polimi.ingsw.server.MatchController;
import it.polimi.ingsw.server.custom_exception.*;


public interface UserInterface {
    boolean isConnected();

    void chooseUsername() throws DisconnectionException;

    String getUsername();

    void arrangeForUsername() throws InvalidOperationException, DisconnectionException, ReconnectionException, InvalidUsernameException;
    /*InvalidOperationException--> server full*/

    void notifyStarting() throws DisconnectionException;

    void notifyStart() throws DisconnectionException;

    String getOperation();

    void notifyAlreadyDoneOperation();

    void askForOperation();

    void notifyDisconnection();

    void notifyReconnection() throws DisconnectionException;

    void setController(MatchController matchController);

    void notifyDieInsertion();

    void notifyToolUsed();
}
