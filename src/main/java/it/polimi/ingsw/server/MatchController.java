package it.polimi.ingsw.server;

import it.polimi.ingsw.server.component_container.Die;
import it.polimi.ingsw.server.component_container.Grid;
import it.polimi.ingsw.server.custom_exception.*;
import it.polimi.ingsw.server.netPart_container.UserInterface;

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
            //this arrayList is used to avoid continuous print of "playerX disconnected"
            ArrayList<String> stopCheck= new ArrayList<>();
            while (true){
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
                        if (!stopCheck.contains(username)&&!playersInMatch.get(username).isConnected()) {
                            //in this instruction player is removed only from connectedPlayers
                            System.err.println(username+" disconnected.");
                            stopCheck.add(username);
                            MatchHandler.getInstance().notifyAboutDisconnection(username);
                        }
                        else if(stopCheck.contains(username)&&playersInMatch.get(username).isConnected()){
                            stopCheck.remove(username);
                        }
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
            } catch (NotValidParameterException e) {
                e.printStackTrace();
            } catch (NotInPoolException e) {
                e.printStackTrace();
            }
        }
        while(!gameFinished);
    }


    /*
    * --------------------------------------------------
    *       Methods to handle initialization
    * --------------------------------------------------
    */
    private void initializeGame() {
        initializeGrids();
    }

    private void initializeGrids() {
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
                Boolean hasPlayerChosenAGrid= null;
                try {
                    hasPlayerChosenAGrid = model.hasPlayerChosenAGrid(username);
                } catch (NotValidParameterException e) {
                    e.printStackTrace(); //FIXME caused by "hasPlayerChosenAGrid" when username passed is not in game. Should this exception be thrown?
                }
                ok= ok && hasPlayerChosenAGrid;

                //if timeout event occurred and player still has not chosen a grid he can be considered as disconnected.
                if(timeout&&!hasPlayerChosenAGrid){
                    try {
                        model.setPlayerToDisconnect(username);
                    } catch (NotValidParameterException e) {
                        e.printStackTrace();    //fixme only thrown if username passe is not in current match.
                    }
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

    /*
    --------------------------------------------------
                    Methods to handle turn.
    --------------------------------------------------
     */

    private void handleTurn() throws TooManyRoundsException, NotValidParameterException, NotInPoolException {
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        lock.lock();
        sendConnectedPlayers();
        model.updateTurn(MAX_ROUND);
        String username = model.askTurn();
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

    private void executeTurn(String username) throws InvalidUsernameException, InvalidOperationException, NotValidParameterException, NotInPoolException {

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

    private void sendConnectedPlayers() {
        for(Map.Entry<String,UserInterface> player: playersInMatch.entrySet()){
            ArrayList<String> connectedPlayers = new ArrayList<>();
            for(Map.Entry<String,UserInterface> player2: playersInMatch.entrySet()){
                if(!player.getKey().equals(player2.getKey())) connectedPlayers.add(player2.getKey());
            }
            player.getValue().sendConnectedPlayers(connectedPlayers);
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

            //This if is executed if a the user interface of a certain player has changed.
            //This can be caused by the fact that tha player disconnected and reconnected immediately after before running in timeout event
            if(!turnPlayer.equals(playersInMatch.get(username))){
                turnPlayer=playersInMatch.get(username);
                turnPlayer.askForOperation();
            }
            operation = turnPlayer.getOperation();
            //FIXME if(timeout) operation=timeoutOccurred;
        }while (operation==null);
        return operation;
    }

    private boolean handleTurnPlayerOperation(String operation, String username) throws InvalidOperationException, NotValidParameterException, NotInPoolException {
        final String insertDie= "put_die";
        final String useToolCard = "tool_card";
        final String finish="finish";
        final String timeout="timeout";

        UserInterface turnPlayer= playersInMatch.get(username);
        switch (operation){
            case insertDie:
                if(!dieInserted){
                    model.insertDieOperation(0,0,0); //FIXME
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
            ArrayList<String> toRemove = new ArrayList<>();
            while (iterator.hasNext()) {
                String username = (String) iterator.next();
                if(!playersInMatch.get(username).isConnected()){
                    System.err.println(username+" disconnected.");
                    toRemove.add(username);
                    MatchHandler.getInstance().notifyAboutDisconnection(username);
                }
                if(playersInMatch.size()>1&&!this.gameStartingSoon) MatchHandler.notifyMatchCanStart();
            }
            for(String username: toRemove){
                playersInMatch.remove(username);
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
                client.setController(this);
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
        } catch (NotValidConfigPathException e) {
            e.printStackTrace();    //FIXME this exception is thrown if the configuration file is wrong.
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
            try {
                model.setPlayerGrid(player,grid);
            } catch (NotValidParameterException e) {
                e.printStackTrace();    //FIXME caused by "setPlayerGrid" when player is not in game. Should this be thrown?
            }
        }
        lock.lock();
        condition.signal();
        lock.unlock();
    }


    public void handleReconnection(UserInterface player) throws InvalidOperationException {

        lock.lock();
        String username = player.getUsername();
        synchronized (playersInMatchGuard) {
            playersInMatch.put(username, player);
        }
        try {
            player.notifyReconnection();
        } catch (DisconnectionException e) {
            e.printStackTrace();
        }
//        try {
//            if(!model.hasPlayerChosenAGrid(username)){
//                ArrayList<Grid> toSent = (ArrayList<Grid>)model.getGridsForPlayer(username);
//
//                player.sendGrids();//FIXME
//            }
//        } catch (NotValidParameterException e) {
//            e.printStackTrace();    //FIXME caused by "hasPlayerChosenAGrid" when username passed is not in game. Should this exception be thrown?
//        }
//        try {
//            Thread.sleep(maxReconnectionTime);
//        } catch (InterruptedException e) {
//            Thread.currentThread().interrupt();
//        }
//        try {
//            if(!model.hasPlayerChosenAGrid(username)){
//                model.setPlayerToDisconnect(username);
//                player.notifyDisconnection();
//                MatchHandler.getInstance().notifyAboutDisconnection(username);
//                synchronized (playersInMatchGuard) {
//                    playersInMatch.remove(username);
//                }
//            }
//        } catch (NotValidParameterException e) {
//            e.printStackTrace();    //FIXME caused by "hasPlayerChosenAGrid" when username passed is not in game. Should this exception be thrown?
//        }
//        //TODO notify player if is his turn.
        lock.unlock();
    }

    public int askForDieIndex(String username) throws InvalidOperationException{
        return 0;   //todo should ask to player about the index in the dicepool of the die he wants to put.
        //todo should throw InvalidOperationException if user decides to abort the move.
    }

    public int[] askForDieCoordinates(String username) throws InvalidOperationException{
        return new int[0];//todo should return a value array containing two coordinates for the die to be put in player's grid.
        //todo should throw InvalidOperationException if user decides to abort the move.
    }

    public void sendDicePool(List<Die> dicePool, String username) {
        //todo should send dicepool to player.
        //should I remove this? Will you ask the model to give you the dicepool?
    }

    public List<Grid> getPlayerGrids(UserInterface userInterface) {
        String username = userInterface.getUsername();
        synchronized (playersInMatchGuard){
            if(!playersInMatch.containsKey(username)) /*TODO throw an exception*/;
            if(!playersInMatch.get(username).equals(userInterface)) /*TODO throw an exception*/;
        }

        try {
            return model.getGridsForPlayer(username);
        } catch (InvalidOperationException e) {
            e.printStackTrace();
        }
        return null;

    }

    public void setGrid(UserInterface userInterface, int gridChosen) throws InvalidOperationException {
        String username = userInterface.getUsername();
        synchronized (playersInMatchGuard){
            if(!playersInMatch.containsKey(username)) /*TODO throw an exception*/;
            if(!playersInMatch.get(username).equals(userInterface)) /*TODO throw an exception*/;
        }

        try {
            model.setPlayerGrid(username, gridChosen);
            System.out.println(username + " chose the grid number: "+ gridChosen);
        } catch (NotValidParameterException e) {
            System.err.println("ERROR!");
            e.printStackTrace();
        }
    }
}
