package it.polimi.ingsw.clientPart;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Scanner;

public class ClientRMI extends UnicastRemoteObject implements ClientRemoteInterface{

    ServerRMICommunication serverRemoteInterfaceAdapter;

    protected ClientRMI() throws RemoteException {
    }

    @Override
    public void isConnected() throws RemoteException {
        return;
    }

    @Override
    public void chooseUsername() throws RemoteException {
        System.out.println("Select your username");
    }

    @Override
    public String requestAUsername(int trial) throws RemoteException {
        Scanner scanner= new Scanner(System.in);
        if(trial>1) System.err.println("This username already exist or it's invalid. Please choose another one:");
        String username = scanner.nextLine();
        return username;
    }

    @Override
    public void notifyGameStarting() {
        serverRemoteInterfaceAdapter.notifyStarting();
    }

    @Override
    public void notifyStartedGame() {
        serverRemoteInterfaceAdapter.notifyStarted();
    }

    public void setRMICommunication(ServerRMICommunication serverRemoteInterfaceAdapter) {
        this.serverRemoteInterfaceAdapter=serverRemoteInterfaceAdapter;
    }
}
