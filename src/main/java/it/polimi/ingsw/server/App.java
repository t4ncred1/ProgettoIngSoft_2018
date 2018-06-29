package it.polimi.ingsw.server;


import it.polimi.ingsw.server.configurations.ConfigurationHandler;
import it.polimi.ingsw.server.custom_exception.NotValidConfigPathException;
import it.polimi.ingsw.server.net.SocketHandler;
import it.polimi.ingsw.server.net.RmiHandler;
import it.polimi.ingsw.server.net.ServerRemoteInterface;

import java.rmi.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.logging.Level;
import java.util.logging.Logger;


public class App
{

    private static Logger logger;
    public static void main( String[] args )
    {
        logger= Logger.getLogger(App.class.getName());
        try {
            ConfigurationHandler.getInstance();
            logger.log(Level.CONFIG, "Configurations loaded");
        } catch (NotValidConfigPathException e) {
            logger.log(Level.WARNING, "Can't load configurations properly", e);
        }
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
