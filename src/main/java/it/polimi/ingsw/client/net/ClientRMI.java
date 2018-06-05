package it.polimi.ingsw.client.net;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class ClientRMI extends UnicastRemoteObject implements ClientRemoteInterface {

    private ServerRMICommunication serverRemoteInterfaceAdapter;

    private String username;

    protected ClientRMI()  throws RemoteException{
    }

    @Override
    public void isConnected() {
        //Is not doing anything here, it's here only for socket.
    }

    @Override
    public void chooseUsername() {
        //Is not doing anything here, it's here only for socket.
    }

    @Override
    public String requestAUsername() {
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


    public void setRMICommunication(ServerRMICommunication serverRemoteInterfaceAdapter) {
        this.serverRemoteInterfaceAdapter=serverRemoteInterfaceAdapter;
    }

    public void setUsername(String username) {
        this.username=username;
    }
}
