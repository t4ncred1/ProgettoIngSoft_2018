package it.polimi.ingsw.client.net;

import it.polimi.ingsw.server.MatchController;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class ClientRMI extends UnicastRemoteObject implements ClientRemoteInterface {

    private ServerRMICommunication serverRemoteInterfaceAdapter;

    private String username;

    protected ClientRMI()  throws RemoteException{
    }

    @Override
    public void isConnected() throws RemoteException {
        return;
    }

    @Override
    public void chooseUsername() throws RemoteException {
    }

    @Override
    public String requestAUsername() throws RemoteException {
        return this.username;
    }

    @Override
    public void notifyGameStarting() {
        serverRemoteInterfaceAdapter.notifyStarting();
    }

    @Override
    public void notifyStartedGame() {
        serverRemoteInterfaceAdapter.notifyStarted();
    }

    @Override
    public void notifyReconnection() throws RemoteException {
        serverRemoteInterfaceAdapter.notifyReconnection();
    }

    @Override
    public void setController(MatchController matchController) {
        serverRemoteInterfaceAdapter.setController(matchController);
    }

    public void setRMICommunication(ServerRMICommunication serverRemoteInterfaceAdapter) {
        this.serverRemoteInterfaceAdapter=serverRemoteInterfaceAdapter;
    }

    public void setUsername(String username) {
        this.username=username;
    }
}
