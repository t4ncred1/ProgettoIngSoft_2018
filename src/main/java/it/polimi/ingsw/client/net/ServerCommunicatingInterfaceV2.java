package it.polimi.ingsw.client.net;

import it.polimi.ingsw.client.custom_exception.*;

public interface ServerCommunicatingInterfaceV2 {
    void selectGrid(int i) throws ServerIsDownException;

    void askForLogout() throws ServerIsDownException, GameStartingException, LoggedOutException;

    void setUpConnection() throws ServerIsDownException;

    void login(String usernameChosen) throws ServerIsFullException, InvalidUsernameException, ServerIsDownException;
}
