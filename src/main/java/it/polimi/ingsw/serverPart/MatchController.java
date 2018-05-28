package it.polimi.ingsw.serverPart;

import it.polimi.ingsw.serverPart.component_container.Grid;
import it.polimi.ingsw.serverPart.custom_exception.*;
import it.polimi.ingsw.serverPart.netPart_container.UserInterface;

import java.util.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class MatchController extends Thread{
    private Map<String,UserInterface> playersInMatch;
    private static final Object playersInMatchGuard = new Object();
    private boolean gameStarted;
    private boolean gameStartingSoon;
    private MatchModel model;
    private final Object modelGuard = new Object();

    private static final int MAX_ROUND =10;
    private boolean dieInserted;
    private boolean toolCardUsed;

    private Lock lock;
    private Condition condition;

    private static int maxReconnectionTime=15000;

    public MatchController(){
        this.lock = new ReentrantLock();
        this.condition= lock.newCondition();
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

        //Initializing game
        new Thread(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            synchronized (playersInMatchGuard) {
                Set set = playersInMatch.keySet();
                Iterator iterator = set.iterator();
                while (iterator.hasNext()) {
                    String username = (String) iterator.next();
                    if (!playersInMatch.get(username).isConnected()) {
                        //in this instruction player is removed only from connectedPlayers
                        MatchHandler.getInstance().notifyAboutDisconnection(username);
                    }
                }
            }
        }).start();
        initializeGame();

        //round-turn logic.
        boolean gameFinished=false;
        do{
            try {
                handleTurn();
            } catch (TooManyRoundsException e) {
                gameFinished=true;
            }
        }
        while(!gameFinished);
    }


    private void initializeGame() {
        for(Map.Entry<String,UserInterface> entry : playersInMatch.entrySet()) {
            ArrayList<Grid> gridsToSend= model.getGridsForPlayer(entry.getKey());
            entry.getValue().sendGrids(); //FIXME
        }
        GameTimer timer = new GameTimer(this, "initialization");
        boolean timeout, ok;
        do {
            lock.lock();
            try {
                condition.await();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            lock.unlock();
            timeout=timer.getTimeoutEvent();
            ok= haveAllPlayersChosenAGrid(timeout);
        }
    while(!(timeout||ok));
        timer.stop();
    }

    private boolean haveAllPlayersChosenAGrid(boolean timeout) {
        //At the end of this part, if all players chose a grid then game could start (ok remains true).
        boolean ok = true;
        synchronized (modelGuard){
            ArrayList<String> disconnectedPlayers = new ArrayList<>();
            for(Map.Entry<String,UserInterface> entry : playersInMatch.entrySet()) {
                String username= entry.getKey();
                Boolean hasPlayerChosenAGrid= model.hasPlayerChosenAGrid(username);
                ok= ok && hasPlayerChosenAGrid;

                //if timeout event occurred and player still has not chosen a grid he can be considered as disconnected.
                if(timeout&&!hasPlayerChosenAGrid){
                    model.setPlayerToDisconnect(username);
                    entry.getValue().notifyDisconnection();
                    MatchHandler.getInstance().notifyAboutDisconnection(entry.getKey());
                    disconnectedPlayers.add(username);
                }
            }

            //Remove UserInterfaces of disconnected players
            synchronized (playersInMatchGuard) {
                for (String player : disconnectedPlayers) {
                    playersInMatch.remove(player);
                }
            }
        }
        return ok;
    }

    private void handleTurn() throws TooManyRoundsException {
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        lock.lock();
        sendConnectedPlayers();
        model.updateTurn(MAX_ROUND);
        String username = model.requestTurnPlayer();
        lock.unlock();
        try {
            executeTurn(username);
        } catch (InvalidOperationException e) {
            e.printStackTrace();
        } catch (InvalidUsernameException e) {
            //FIXME
            System.err.println("this exception now is launched cause players in matchModel are still not created.");
            return;
            //e.printStackTrace();
        }
    }

    private void sendConnectedPlayers() {
        for(Map.Entry<String,UserInterface> player: playersInMatch.entrySet()){
            ArrayList<String> connectedPlayers = new ArrayList<>();
            for(Map.Entry<String,UserInterface> player2: playersInMatch.entrySet()){
                if(!player.getKey().equals(player2.getKey())) connectedPlayers.add(player2.getKey());
            }
            player.getValue().sendConnectedPlayers(connectedPlayers);
        }
    }

    private void executeTurn(String username) throws InvalidUsernameException, InvalidOperationException {

        //FIXME
        final String turnStarted = "start";
        final String turnEnded= "end";
        dieInserted= false;
        toolCardUsed=false;

        if(username==null) throw new InvalidUsernameException();
        UserInterface turnPlayer= playersInMatch.get(username);
        if(turnPlayer==null) throw new InvalidUsernameException();

        //notify other players that turn started
        for(Map.Entry<String,UserInterface> player: playersInMatch.entrySet()){
            if(!player.getKey().equals(username)){
                player.getValue().notifyTurnOf(username, turnStarted);
            }
        }

        //Handle the turn of the current player.
        boolean turnFinished= false;
        do {
            String operation= getTurnPlayerOperation(username);
            turnFinished= handleTurnPlayerOperation(operation, username);
        }while (!turnFinished);

        //notify other players that turn ended
        for(Map.Entry<String,UserInterface> player: playersInMatch.entrySet()){
            if(!player.getKey().equals(username)){
                player.getValue().notifyTurnOf(username, turnEnded);
            }
        }
    }

    private String getTurnPlayerOperation(String username) {
        UserInterface turnPlayer= playersInMatch.get(username);
        turnPlayer.askForOperation();
        String operation;
        do{
            //TODO implements a timeout.
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            if(!turnPlayer.equals(playersInMatch.get(username))){
                turnPlayer=playersInMatch.get(username);
                turnPlayer.askForOperation();
            }
            operation = turnPlayer.getOperation();
            //FIXME if(timeout) operation=timeoutOccurred;
        }while (operation==null);
        return operation;
    }

    private boolean handleTurnPlayerOperation(String operation, String username) throws InvalidOperationException {
        final String insertDie= "put_die";
        final String useToolCard = "tool_card";
        final String finish="finish";
        final String timeout="timeout";

        UserInterface turnPlayer= playersInMatch.get(username);
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
                return true;
            case timeout:
                //TODO disconnect current player.
                return true;
            default:
                throw new InvalidOperationException();
        }
        return false;
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
            Set<String> playerUserNames = playersInMatch.keySet();
            model = new MatchModel(playerUserNames,this);
        } catch (NotValidParameterException e) {
            e.printStackTrace();
        }
        this.notifyStartToPlayers();
    }

    public void wakeUpController(){
        lock.lock();
        condition.signal();
        lock.unlock();
    }

    public void selectGrid(UserInterface client, int grid) throws InvalidOperationException {
        String player = client.getUsername();
        if(!playersInMatch.get(player).equals(client)) throw new InvalidOperationException();
        synchronized (modelGuard){
            model.setPlayerGrid(player,grid);
        }
        lock.lock();
        condition.signal();
        lock.unlock();
    }


    public void handleReconnection(UserInterface player){

        lock.lock();
        String username = player.getUsername();
        synchronized (playersInMatchGuard) {
            playersInMatch.put(username, player);
        }
        if(!model.hasPlayerChosenAGrid(username)){
            ArrayList<Grid> toSent = model.getGridsForPlayer(username);

            player.sendGrids();//FIXME
        }
        try {
            Thread.sleep(maxReconnectionTime);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        if(!model.hasPlayerChosenAGrid(username)){
            model.setPlayerToDisconnect(username);
            player.notifyDisconnection();
            MatchHandler.getInstance().notifyAboutDisconnection(username);
            playersInMatch.remove(username);
        }
        //TODO notify player if is his turn.
        lock.unlock();
    }

}
