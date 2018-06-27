package it.polimi.ingsw.server;


import it.polimi.ingsw.server.net.SocketHandler;
import it.polimi.ingsw.server.net.RmiHandler;
import it.polimi.ingsw.server.net.ServerRemoteInterface;

import java.rmi.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;




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
        } catch (RemoteException | AlreadyBoundException e) {
            e.printStackTrace();
        }
        Thread.yield();
    }
}
