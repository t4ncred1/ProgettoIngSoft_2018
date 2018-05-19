package it.polimi.ingsw.Client_side_container;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ClientRemoteInterface extends Remote {
    void isConnected() throws RemoteException;

    void chooseUsername() throws RemoteException;

    String requestAUsername(int trial) throws RemoteException;
}
