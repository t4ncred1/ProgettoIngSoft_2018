package it.polimi.ingsw.clientPart;

import it.polimi.ingsw.serverPart.netPart_container.ServerRemoteInterface;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class ClientMain {

    private static ClientMain instance;

    public static void main(String[] args){
        instance = new ClientMain();
        instance.runRMIconnection();
    }

    private void runRMIconnection(){

        try {
            ClientRemoteInterface thisClient= new ClientRMI();
            Registry registry = LocateRegistry.getRegistry("127.0.0.1",11001);
            ServerRemoteInterface stub = (ServerRemoteInterface) registry.lookup("MatchHandler");
            stub.login(thisClient);
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (NotBoundException e) {
            e.printStackTrace();
        }
    }
}
