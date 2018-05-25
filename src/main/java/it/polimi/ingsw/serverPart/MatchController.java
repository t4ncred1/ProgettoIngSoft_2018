package it.polimi.ingsw.serverPart;

import it.polimi.ingsw.serverPart.custom_exception.DisconnectionException;
import it.polimi.ingsw.serverPart.custom_exception.InvalidOperationException;
import it.polimi.ingsw.serverPart.custom_exception.InvalidUsernameException;
import it.polimi.ingsw.serverPart.netPart_container.UserInterface;

import java.io.FileNotFoundException;
import java.util.ArrayList;

public class MatchController extends Thread{
    private ArrayList<UserInterface> playersInMatch;
    private boolean gameStarted;
    private boolean gameStartingSoon;
    private MatchModel model;

    private final int maxRound =10;
    private boolean dieInserted;
    private boolean toolCardUsed;
    private UserInterface turnPlayer;

    public MatchController(){
        this.playersInMatch= new ArrayList<UserInterface>();
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
        while(round <= maxRound);
    }

    private void executeTurn(String username) throws InvalidUsernameException, InvalidOperationException {
        final String insertDie= "put_die";
        final String useToolCard = "tool_card";
        final String finish="finish";
        dieInserted= false;
        toolCardUsed=false;

        turnPlayer= getTurnPlayer(username);
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

    private UserInterface getTurnPlayer(String username) throws InvalidUsernameException{
        for(UserInterface player: playersInMatch){
            if (player.getUsername().equals(username))
                return player;
        }
        throw new InvalidUsernameException();
    }


    public int playerInGame() {

            System.out.println("player in queue: "+playersInMatch.size());
            return playersInMatch.size();

    }

    public void updateQueue(){
        synchronized (playersInMatch){
            for (int i=0; i<playersInMatch.size();i++) {
                if(!playersInMatch.get(i).isConnected()){
                    //in this instruction player is removed both from playersInMatch and connectedPlayers
                    MatchHandler.getInstance().notifyAboutDisconnection(playersInMatch.remove(i), this.gameStarted);
                }
                if(playersInMatch.size()>1&&!this.gameStartingSoon) MatchHandler.notifyMatchCanStart();
            }

        }
    }

    public void insert(UserInterface client) {
        synchronized (playersInMatch) {
            playersInMatch.add(client);
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
            synchronized (playersInMatch) {
                if(!timeout) {
                    try {
                        playersInMatch.get(playersInMatch.size() - 1).notifyStarting();
                    } catch (DisconnectionException e) {
                        MatchHandler.getInstance().notifyAboutDisconnection(playersInMatch.remove(playersInMatch.size() - 1), this.gameStarted);
                    }
                }
            }
        }
    }

    public void remove(UserInterface client) throws InvalidOperationException {
        if(gameStartingSoon) throw new InvalidOperationException();
        else {
            synchronized (playersInMatch) {
                playersInMatch.remove(client);
                MatchHandler.getInstance().notifyAboutDisconnection(client, this.gameStarted);
            }
        }
    }


    private void notifyStartingSoonToPlayers() {
        synchronized (playersInMatch){
            for(int i=0; i<playersInMatch.size();i++){
                try {
                    playersInMatch.get(i).notifyStarting();
                } catch (DisconnectionException e) {
                    MatchHandler.getInstance().notifyAboutDisconnection(playersInMatch.remove(i), this.gameStarted);
                    e.printStackTrace();
                }
            }
        }
    }

    private void notifyStartToPlayers() {
        synchronized (playersInMatch){
            for(UserInterface cl: playersInMatch){
                try {
                    cl.notifyStart();
                } catch (DisconnectionException e) {
                    //FIXME if necessary
                    MatchHandler.getInstance().notifyAboutDisconnection(cl, this.gameStarted);
                    e.printStackTrace();
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
            model = new MatchModel();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        this.notifyStartToPlayers();
    }
}
