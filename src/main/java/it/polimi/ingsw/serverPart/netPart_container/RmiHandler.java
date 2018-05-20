package it.polimi.ingsw.serverPart.netPart_container;

import it.polimi.ingsw.clientPart.ClientRemoteInterface;
import it.polimi.ingsw.serverPart.MatchHandler;
import it.polimi.ingsw.serverPart.custom_exception.DisconnectionException;
import it.polimi.ingsw.serverPart.custom_exception.InvalidOperationException;

public class RmiHandler implements ServerRemoteInterface{

    private static final int port=11001;

    public RmiHandler(){
        System.out.println("RMIHandler started");
    }

    public int getPort(){
        return port;
    }

    @Override
    public void login(ClientRemoteInterface client) throws InvalidOperationException {
        System.out.println("Connection request received on RMI system");
        try {
            MatchHandler.login(new ClientRemoteInterfaceAdapter(client));
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
}
