package it.polimi.ingsw.clientPart;

import it.polimi.ingsw.serverPart.custom_exception.InvalidOperationException;
import it.polimi.ingsw.serverPart.netPart_container.ServerRemoteInterface;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class ServerRemoteInterfaceAdapter implements ServerInterface{

    private ServerRemoteInterface stub;
    private ClientRemoteInterface thisClient;


    private static String registerName = "MatchHandler";
    private static int serverPort = 11001;
    private static String serverAddress="127.0.0.1";

    public ServerRemoteInterfaceAdapter(){
        try {
            Registry registry = LocateRegistry.getRegistry(serverAddress,serverPort);
            stub = (ServerRemoteInterface) registry.lookup(registerName);
            thisClient=new ClientRMI();

        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (NotBoundException e) {
            e.printStackTrace();
        }

    }


    @Override
    public void login() {
        try {
            stub.login(thisClient);
            System.out.println("You successfully logged.");
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (InvalidOperationException e) {
            System.err.println("Server is now full, retry later.");
        }

    }
}
