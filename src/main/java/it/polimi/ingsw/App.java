package it.polimi.ingsw;

import com.sun.security.ntlm.Server;
import it.polimi.ingsw.netPart_container.RmiHandler;
import it.polimi.ingsw.netPart_container.ServerRemoteInterface;

import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
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
