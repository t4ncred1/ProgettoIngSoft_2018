package it.polimi.ingsw.clientPart;

public interface ServerCommunicatingInterface {
    void login();
    boolean waitForGame();
    boolean logout();
}
