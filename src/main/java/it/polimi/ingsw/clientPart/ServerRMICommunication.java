package it.polimi.ingsw.clientPart;

import it.polimi.ingsw.clientPart.custom_exception.*;
import it.polimi.ingsw.serverPart.custom_exception.InvalidOperationException;
import it.polimi.ingsw.serverPart.netPart_container.ServerRemoteInterface;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class ServerRMICommunication implements ServerCommunicatingInterface {

    private transient ServerRemoteInterface stub;
    private transient ClientRMI thisClient;


    private static String registerName = "MatchHandler";
    private static int serverPort = 11001;
    private static String serverAddress="127.0.0.1";

    private transient boolean startingGame; //this parameter should be set to false once used.
    private transient boolean gameStarted;



    @Override
    public void setUpConnection() throws ServerIsDownException{
        try {
            Registry registry = LocateRegistry.getRegistry(serverAddress,serverPort);
            stub = (ServerRemoteInterface) registry.lookup(registerName);
            thisClient=new ClientRMI();
            thisClient.setRMICommunication(this);
        } catch (Exception e) {
            throw new ServerIsDownException();
        }
    }

    @Override
    public void login(String username) throws ServerIsFullException, InvalidUsernameException, ServerIsDownException {
        try {
            thisClient.setUsername(username);
            stub.login(thisClient);
        } catch (RemoteException e) {
            throw new ServerIsDownException();
        } catch (InvalidOperationException e) {
            throw new ServerIsFullException();
        } catch (it.polimi.ingsw.serverPart.custom_exception.InvalidUsernameException e) {
            throw new InvalidUsernameException();
        }

    }

    @Override
    public void waitForGame(boolean starting) throws GameStartingException, GameStartedException, TimerRestartedException {

        if(!starting) {
            if (startingGame) {
                startingGame = false;
                throw new GameStartingException();
            }
            if (gameStarted) throw new GameStartedException();
        }
        else{
            if(startingGame){
                startingGame=false;
                throw new TimerRestartedException();
            }
            if(gameStarted) throw new GameStartedException();
        }



//        if(!startingGame&&!gameStarted){
//            return false;
//        }
//        else{
//            while (!gameStarted){
//                try {
//                    Thread.sleep(1000);
//                } catch (InterruptedException e) {
//                    Thread.currentThread().interrupt();
//                    e.printStackTrace();
//                }
//            }
//        }
//        return true;
    }

    @Override
    public boolean logout() {
        Boolean ok=false;
        try{
            stub.logout(thisClient);
            ok=true;
        } catch (InvalidOperationException e) {
            e.printStackTrace();
        } catch (RemoteException e) {
            //TODO
            e.printStackTrace();
        }
        return ok;

    }

    public void notifyStarting() {
        this.startingGame=true;
    }

    public void notifyStarted() {
        this.gameStarted=true;
        this.startingGame=false;
    }
}
