package it.polimi.ingsw.serverPart;

import it.polimi.ingsw.serverPart.custom_exception.DisconnectionException;
import it.polimi.ingsw.serverPart.custom_exception.InvalidOperationException;
import it.polimi.ingsw.serverPart.custom_exception.InvalidUsernameException;
import it.polimi.ingsw.serverPart.netPart_container.UserInterface;

import java.io.FileNotFoundException;
import java.util.*;

public class MatchController extends Thread{
    private Map<String,UserInterface> playersInMatch;
    private static final Object playersInMatchGuard = new Object();
    private boolean gameStarted;
    private boolean gameStartingSoon;
    private MatchModel model;

    private final int MAX_ROUND =10;
    private boolean dieInserted;
    private boolean toolCardUsed;

    public MatchController(){
        this.playersInMatch= new LinkedHashMap<>();
    }

    @Override
    public void run(){
        while(!gameStarted){
            updateQueue();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                e.printStackTrace();
            }
        }
        int round;
        do{
            round= model.updateTurn();
            String username = model.requestTurnPlayer();
            try {
                executeTurn(username);
            } catch (InvalidOperationException e) {
                e.printStackTrace();
            } catch (InvalidUsernameException e) {
                //FIXME
                System.err.println("this exception now is launched cause players in matchmodel are still not created.");
                return;
                //e.printStackTrace();
            }

        }
        while(round <= MAX_ROUND);
    }

    private void executeTurn(String username) throws InvalidUsernameException, InvalidOperationException {
        final String insertDie= "put_die";
        final String useToolCard = "tool_card";
        final String finish="finish";
        dieInserted= false;
        toolCardUsed=false;

        if(username==null) throw new InvalidUsernameException(); //FIXME remove me when player creation will be implemented
        UserInterface turnPlayer= playersInMatch.get(username);
        if(turnPlayer==null) throw new InvalidUsernameException();
        boolean turnFinished= false;
        do {
            turnPlayer.askForOperation();
            String operation;
            do{
                //FIXME insert a timeout.
                operation = turnPlayer.getOperation();
                //if(timeout) operation=timeoutOccurred;
            }while (operation==null);

            switch (operation){
                case insertDie:
                    if(!dieInserted){
                        dieInserted= model.insertDieOperation();
                    }
                    else{
                        turnPlayer.notifyAlreadyDoneOperation();
                    }
                    break;
                case useToolCard:
                    if(!toolCardUsed){
                        toolCardUsed=model.useToolCardOperation();
                    }
                    else{
                        turnPlayer.notifyAlreadyDoneOperation();
                    }
                    break;
                case finish:
                    turnFinished=true;
                    break;
                default:
                    throw new InvalidOperationException();
            }
        }while (!turnFinished);
    }




    public int playerInGame() {

            System.out.println("player in queue: "+playersInMatch.size());
            return playersInMatch.size();

    }

    public void updateQueue(){
        synchronized (playersInMatchGuard){
            Set set= playersInMatch.keySet();
            Iterator iterator=set.iterator();
            while (iterator.hasNext()) {
                String username = (String) iterator.next();
                if(!playersInMatch.get(username).isConnected()){
                    //in this instruction player is removed both from playersInMatch and connectedPlayers
                    playersInMatch.remove(username);
                    MatchHandler.getInstance().notifyAboutDisconnection(username);
                }
                if(playersInMatch.size()>1&&!this.gameStartingSoon) MatchHandler.notifyMatchCanStart();
            }

        }
    }

    public void insert(UserInterface client) {
        synchronized (playersInMatchGuard) {
            playersInMatch.put(client.getUsername(),client);
        }
    }

    //state Observer
    public boolean isGameStartingSoon() {
        return gameStarted;
    }

    //state modifier
    public void setGameStartingSoon(boolean timeout) {
        final boolean startingSoonState =true;
        if(!gameStartingSoon||timeout) {
            this.gameStartingSoon = startingSoonState;
            this.notifyStartingSoonToPlayers();
        }
        else {
            synchronized (playersInMatchGuard) {
                if(!timeout) {
                    Set list = playersInMatch.keySet();
                    Iterator iterator = list.iterator();
                    UserInterface lastConnection= null;
                    while (iterator.hasNext())lastConnection =playersInMatch.get(iterator.next().toString());
                    try {
                        if(lastConnection!=null) lastConnection.notifyStarting();
                    } catch (DisconnectionException e) {
                        String username = lastConnection.getUsername();
                        playersInMatch.remove(username);
                        MatchHandler.getInstance().notifyAboutDisconnection(username);
                    }
                }
            }
        }
    }

    public void remove(UserInterface client) throws InvalidOperationException {
        if(gameStartingSoon) throw new InvalidOperationException();
        else {
            synchronized (playersInMatchGuard) {
                playersInMatch.remove(client.getUsername());
            }
        }
    }


    private void notifyStartingSoonToPlayers() {
        synchronized (playersInMatchGuard){
            Set set= playersInMatch.keySet();
            Iterator iterator=set.iterator();
            while (iterator.hasNext()){
                String username = (String) iterator.next();
                try {
                    playersInMatch.get(username).notifyStarting();
                } catch (DisconnectionException e) {
                    playersInMatch.remove(username);
                    MatchHandler.getInstance().notifyAboutDisconnection(username);
                }
            }
        }
    }

    private void notifyStartToPlayers() {
        synchronized (playersInMatchGuard){
            Set set= playersInMatch.keySet();
            Iterator iterator=set.iterator();
            while (iterator.hasNext()){
                String username = (String) iterator.next();
                UserInterface client = playersInMatch.get(username);
                MatchHandler.setPlayerInGame(username,this);
                try {
                    client.notifyStart();
                } catch (DisconnectionException e) {
                    //FIXME if necessary
                    MatchHandler.getInstance().notifyAboutDisconnection(client.getUsername());
                }
            }
        }
    }

    public void setGameToStarted() {
        final boolean gameStartedStatus =true;
        final boolean gameIsNotStartingAnymore = false;

        this.gameStartingSoon=gameIsNotStartingAnymore;
        this.gameStarted=gameStartedStatus;
        try {
            model = new MatchModel(this);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        this.notifyStartToPlayers();
    }
}
