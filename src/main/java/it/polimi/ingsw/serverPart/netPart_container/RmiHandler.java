package it.polimi.ingsw.serverPart.netPart_container;

import it.polimi.ingsw.clientPart.ClientRemoteInterface;
import it.polimi.ingsw.serverPart.MatchController;
import it.polimi.ingsw.serverPart.MatchHandler;
import it.polimi.ingsw.serverPart.custom_exception.DisconnectionException;
import it.polimi.ingsw.serverPart.custom_exception.InvalidOperationException;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class RmiHandler extends Thread implements ServerRemoteInterface{

    private static int port=11001;

    private static ArrayList<RMIUserAgent> clientsHandled;

    public RmiHandler(){
        clientsHandled= new ArrayList<>();
    }
    public RmiHandler(int port){
        this.port=port;
        clientsHandled= new ArrayList<>();
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
    public void login(ClientRemoteInterface client) throws InvalidOperationException {
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
