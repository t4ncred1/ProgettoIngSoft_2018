package it.polimi.ingsw.server.net;

import it.polimi.ingsw.client.net.ClientRMI;
import it.polimi.ingsw.client.net.ClientRemoteInterface;
import it.polimi.ingsw.server.MatchController;
import it.polimi.ingsw.server.MatchHandler;
import it.polimi.ingsw.server.cards.PrivateObjective;
import it.polimi.ingsw.server.components.Grid;
import it.polimi.ingsw.server.custom_exception.DisconnectionException;
import it.polimi.ingsw.server.custom_exception.InvalidOperationException;
import it.polimi.ingsw.server.custom_exception.InvalidUsernameException;
import it.polimi.ingsw.server.custom_exception.ReconnectionException;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RmiHandler extends Thread implements ServerRemoteInterface{

    private static RmiHandler instance;
    private static int port=11001;

    private Map<ClientRemoteInterface, RMIUserAgent> clientsHandled;
    private Map<ClientRemoteInterface, MatchController> clientsMatch;
    private static final Object clientsHandledGuard= new Object();

    private RmiHandler(){
        clientsHandled= new HashMap<>();
        clientsMatch = new HashMap<>();
    }
    public RmiHandler(int port){
        this();
        this.port=port;
    }

    public static RmiHandler getInstance(){
        if(instance==null)instance= new RmiHandler();
        return instance;
    }

    public int getPort(){
        return port;
    }

    @Override
    public void run(){
        System.out.println("RMIHandler started");
        while (true){
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                e.printStackTrace();
            }
            synchronized (clientsHandledGuard) {
                for (int i=0; i<clientsHandled.size(); i++){
                    if(!clientsHandled.get(i).isConnected()) clientsHandled.remove(i);
                }
            }
        }
    }

    @Override
    public void login(ClientRemoteInterface client) throws InvalidOperationException, InvalidUsernameException {
        System.out.println("Connection request received on RMI system");
        RMIUserAgent clientInterface= new RMIUserAgent(client);
        try {
           MatchHandler.login(clientInterface);
           synchronized (clientsHandledGuard) {
               clientsHandled.put(client, clientInterface);
           }
        }
        catch (DisconnectionException e){
            System.out.println("Connection protocol ended. Client disconnected");
            e.printStackTrace();
        }
        catch (InvalidOperationException e) {
            System.out.println("Connection protocol ended. Server is full");
            e.printStackTrace();
            throw new InvalidOperationException();
        } catch (ReconnectionException e) {
            System.out.println("Connection protocol ended. Client joined the game left before");
            e.printStackTrace();
        }
    }

    @Override
    public void logout(ClientRemoteInterface client) throws InvalidOperationException {
        synchronized (clientsHandledGuard) {
            if (clientsHandled.containsKey(client)) {
                MatchHandler.getInstance().logOut(clientsHandled.remove(client));
            }
        }
    }

    @Override
    public void setControllerForClient(ClientRemoteInterface client, MatchController controller){
        //fixme controls.
        clientsMatch.put(client,controller);
    }

    @Override
    public List<Grid> getGrids(ClientRemoteInterface thisClient) throws InvalidOperationException {
        RMIUserAgent clientCalling;
        synchronized (clientsHandledGuard) {
            clientCalling = clientsHandled.get(thisClient);
        }
        if (clientCalling==null) ; //fixme what exception should I throw?
        return clientsMatch.get(thisClient).getPlayerGrids(clientCalling);
    }

    @Override
    public void setGrid(ClientRemoteInterface thisClient, int index) throws InvalidOperationException {

        RMIUserAgent clientCalling;
        synchronized (clientsHandledGuard) {
            clientCalling = clientsHandled.get(thisClient);
        }
        //if (clientCalling==null) ;
        clientsMatch.get(thisClient).setGrid(clientCalling,index);
    }

    @Override
    public PrivateObjective getPrivateObjective(ClientRMI thisClient) throws RemoteException {
        RMIUserAgent clientCalling;
        synchronized (clientsHandledGuard) {
            clientCalling = clientsHandled.get(thisClient);
        }
        //if (clientCalling == null) ;
        return clientsMatch.get(thisClient).getPrivateObject(clientCalling);
    }


}
