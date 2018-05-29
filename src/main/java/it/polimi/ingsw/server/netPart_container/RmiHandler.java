package it.polimi.ingsw.server.netPart_container;

import it.polimi.ingsw.client.ClientRemoteInterface;
import it.polimi.ingsw.server.MatchHandler;
import it.polimi.ingsw.server.custom_exception.DisconnectionException;
import it.polimi.ingsw.server.custom_exception.InvalidOperationException;
import it.polimi.ingsw.server.custom_exception.InvalidUsernameException;
import it.polimi.ingsw.server.custom_exception.ReconnectionException;

import java.util.ArrayList;

public class RmiHandler extends Thread implements ServerRemoteInterface{

    private static RmiHandler instance;
    private static int port=11001;

    private ArrayList<RMIUserAgent> clientsHandled;

    private RmiHandler(){
        clientsHandled= new ArrayList<>();
    }
    public RmiHandler(int port){
        this.port=port;
        clientsHandled= new ArrayList<>();
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
            synchronized (clientsHandled) {
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
           synchronized (clientsHandled) {
               for (RMIUserAgent UA : clientsHandled) {
                   if (UA.equals(clientInterface)) clientsHandled.remove(UA);
               }
               clientsHandled.add(clientInterface);
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
        RMIUserAgent clientInterface = new RMIUserAgent(client);
        synchronized (clientsHandled) {
            for (int i=0; i<clientsHandled.size();i++) {
                if (clientsHandled.get(i).equals(clientInterface)) {
                    MatchHandler.getInstance().logOut(clientsHandled.remove(i));
                }
            }
        }
    }
}
