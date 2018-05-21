package it.polimi.ingsw.serverPart;


import com.google.gson.Gson;
import it.polimi.ingsw.serverPart.netPart_container.SocketHandler;
import it.polimi.ingsw.serverPart.netPart_container.RmiHandler;
import it.polimi.ingsw.serverPart.netPart_container.ServerRemoteInterface;

import java.io.FileReader;
import java.rmi.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;



/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        MatchHandler.getInstance().start();
        SocketHandler.getInstance().start();
        try {
            RmiHandler handler = new RmiHandler();
            ServerRemoteInterface stub = (ServerRemoteInterface) UnicastRemoteObject.exportObject(handler, handler.getPort());
            Registry registry = LocateRegistry.createRegistry(handler.getPort());
            registry.bind("MatchHandler", stub);
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (AlreadyBoundException e) {
            e.printStackTrace();
        }
        Thread.yield();
    }
}
