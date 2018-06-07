package it.polimi.ingsw.server.net;

import it.polimi.ingsw.client.configurations.ConfigHandler;
import it.polimi.ingsw.client.net.ClientRemoteInterface;
import it.polimi.ingsw.server.MatchController;
import it.polimi.ingsw.server.MatchHandler;
import it.polimi.ingsw.server.configurations.ConfigurationHandler;
import it.polimi.ingsw.server.model.cards.PrivateObjective;
import it.polimi.ingsw.server.model.components.Die;
import it.polimi.ingsw.server.model.components.Grid;
import it.polimi.ingsw.server.custom_exception.*;

import java.rmi.RemoteException;
import java.util.*;

public class RmiHandler extends Thread implements ServerRemoteInterface{

    private static RmiHandler instance;
    private int port=11001;

    private Map<ClientRemoteInterface, RMIUserAgent> clientsHandled;
    private Map<ClientRemoteInterface, MatchController> clientsMatch;
    private static final Object clientsHandledGuard= new Object();

    private RmiHandler(){
        try {
            port=ConfigurationHandler.getRmiPort();
        } catch (NotValidConfigPathException e) {
            System.err.println("Configuration file is corrupted. Using defaults.");
        }
        clientsHandled= new HashMap<>();
        clientsMatch = new HashMap<>();
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
                Iterator<Map.Entry<ClientRemoteInterface,RMIUserAgent>> iterator =clientsHandled.entrySet().iterator();
                while (iterator.hasNext()) {
                    RMIUserAgent player = iterator.next().getValue();
                    if (!player.isConnected()) {
                        iterator.remove();
                        System.err.println(player.getUsername() + " removed from interfaces handled in RMI Handler"); // FIXME: 04/06/2018
                    }

                }
            }
        }
    }

    @Override
    public void login(ClientRemoteInterface client) throws InvalidOperationException, InvalidUsernameException {
        System.out.println("Connection request received on RMI system");
        RMIUserAgent clientInterface= new RMIUserAgent(client,this);
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
    public void setControllerForClient(ClientRemoteInterface client, MatchController controller) throws InvalidOperationException, NotValidParameterException {
        if (client ==null || controller == null) throw new NotValidParameterException("Client passed or controller are null","should be a valid link to client to associate to the match.");
        if (clientsMatch.containsKey(client))throw new InvalidOperationException();
        clientsMatch.put(client,controller);
    }

    @Override
    public List<Grid> getGrids(ClientRemoteInterface thisClient) throws InvalidOperationException, NotValidParameterException {
        RMIUserAgent clientCalling;
        synchronized (clientsHandledGuard) {
            if (!clientsHandled.containsKey(thisClient)) throw new NotValidParameterException("this Client is not registered to RMI","Client calling should be registered to RMI");
            clientCalling = clientsHandled.get(thisClient);
        }
        return clientsMatch.get(thisClient).getPlayerGrids(clientCalling);
    }

    @Override
    public void setGrid(ClientRemoteInterface thisClient, int index) throws InvalidOperationException, NotValidParameterException {
        //if it throws a InvalidOperationException, Index Parameter is wrong.
        RMIUserAgent clientCalling;
        synchronized (clientsHandledGuard) {
            if (!clientsHandled.containsKey(thisClient)) throw new NotValidParameterException("client thisClient, passed as parameter, is not handled by RMI","should be a cliend handled by RMI");
            clientCalling = clientsHandled.get(thisClient);
        }
        clientsMatch.get(thisClient).setGrid(clientCalling,index);
    }

    @Override
    public PrivateObjective getPrivateObjective(ClientRemoteInterface thisClient) throws RemoteException, NotValidParameterException {
        RMIUserAgent clientCalling;
        synchronized (clientsHandledGuard) {
            if (!(clientsHandled.containsKey(thisClient))) throw new NotValidParameterException("Client thisClient is not in any Match.","Should be in a match to ask for a private objective.");
            clientCalling = clientsHandled.get(thisClient);
        }
        try {
            return clientsMatch.get(thisClient).getPrivateObject(clientCalling);
        } catch (NotValidParameterException e) {
            e.printStackTrace(); //should not be thrown if RMIhandler is correct
        }
        return null;
    }

    @Override
    public String askTurn(ClientRemoteInterface thisClient) throws NotValidParameterException, InvalidOperationException, TooManyRoundsException {
        
        synchronized (clientsHandledGuard) {
            if (!(clientsHandled.containsKey(thisClient)))
                throw new NotValidParameterException("Client thisClient is not in any Match.", "Should be in a match to ask for a private objective.");
        }
        return clientsMatch.get(thisClient).requestTurnPlayer();
    }

    @Override
    public List<Die> getUpdatedDicepool(ClientRemoteInterface thisClient) throws NotValidParameterException {
        synchronized (clientsHandledGuard){
            if(!(clientsHandled.containsKey(thisClient)))
                throw new NotValidParameterException("Client thisClient is not in any Match.", "Should be in a match to ask for dicepool to be shown.");
        }
        return clientsMatch.get(thisClient).getDicePool();
    }
}
