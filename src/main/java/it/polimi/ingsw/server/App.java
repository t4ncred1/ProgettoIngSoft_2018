package it.polimi.ingsw.server;


import it.polimi.ingsw.server.netPart_container.SocketHandler;
import it.polimi.ingsw.server.netPart_container.RmiHandler;
import it.polimi.ingsw.server.netPart_container.ServerRemoteInterface;

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
            RmiHandler.getInstance().start();
            ServerRemoteInterface stub = (ServerRemoteInterface) UnicastRemoteObject.exportObject(RmiHandler.getInstance(), RmiHandler.getInstance().getPort());
            Registry registry = LocateRegistry.createRegistry(RmiHandler.getInstance().getPort());
            registry.bind("MatchHandler", stub);
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (AlreadyBoundException e) {
            e.printStackTrace();
        }
        Thread.yield();
    }
}
