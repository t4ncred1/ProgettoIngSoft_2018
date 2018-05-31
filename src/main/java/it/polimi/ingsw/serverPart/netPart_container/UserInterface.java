package it.polimi.ingsw.serverPart.netPart_container;


import it.polimi.ingsw.serverPart.MatchController;
import it.polimi.ingsw.serverPart.custom_exception.*;

import java.util.ArrayList;


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
