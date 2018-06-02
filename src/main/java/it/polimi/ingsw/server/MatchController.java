package it.polimi.ingsw.server;

import it.polimi.ingsw.server.components.Die;
import it.polimi.ingsw.server.components.Grid;
import it.polimi.ingsw.server.custom_exception.*;
import it.polimi.ingsw.server.net.UserInterface;

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
    private UserInterface turnPlayer;
    private boolean ready;
    private boolean turnFinished;
    private boolean gameFinished;
    private boolean dieInserted;
    private boolean toolCardUsed;

    private static final String OPERATION_TIMER = "operation";
    private static final String GRID_CHOOSE_TIMER="initialization";
    private static final int SLEEP_TIME = 1000;

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
                Thread.sleep(SLEEP_TIME);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                e.printStackTrace();
            }
        }

        //Initializing game
        new Thread(() -> {
            //this arrayList is used to avoid continuous print of "playerX disconnected"
            ArrayList<String> stopCheck= new ArrayList<>();
            while (!gameFinished){
                try {
                    Thread.sleep(SLEEP_TIME);
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

        do{
            try {
                handleTurn();
            } catch (TooManyRoundsException e) {
                gameFinished = true;
            } catch (NotEnoughPlayersException e) {
                System.out.println("Game finished.");
                gameFinished=true;
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
        GameTimer timer = new GameTimer(this, GRID_CHOOSE_TIMER);
        boolean timeout;
        boolean ok;
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
        System.out.println("Ready to start rounds logic.");
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

    private void handleTurn() throws TooManyRoundsException, NotEnoughPlayersException {

        //Let player reconnect
        try {
            Thread.sleep(SLEEP_TIME);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        lock.lock();
        String username;
        synchronized (modelGuard) {
            ready=true; //now i can handle clients requests.
            model.updateTurn(MAX_ROUND);
            username = model.askTurn();
            initializeTurn(username);
        }
        lock.unlock();
        executeTurn(username);
    }

    private void executeTurn(String username){
        boolean updatedDie=false;
        boolean updatedTool=false;
        GameTimer timer=null;
        do {
            if(timer==null|| timer.isStopped())timer = new GameTimer(this,OPERATION_TIMER);
            lock.lock();
            try {
                condition.await();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            lock.unlock();
            if (dieInserted&&!updatedDie) {
                notifyDieInsertionBy(username);
                updatedDie=true;
                timer.stop();
            }

            if (toolCardUsed&&!updatedTool) {
                notifyToolCardUsedBy(username);
                updatedTool=true;
                timer.stop();
            }

            lock.lock();
            handleEventualTimeout(timer,username);
            lock.unlock();
            try {
                Thread.sleep(SLEEP_TIME);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }while (!turnFinished);
    }

    private void handleEventualTimeout(GameTimer timer, String username) {
        if (timer.getTimeoutEvent()) {
            ready=false;
            synchronized (modelGuard){
                try {
                    model.setPlayerToDisconnect(username);
                } catch (NotValidParameterException e) {
                    e.printStackTrace();
                }
            }
            for (Map.Entry<String, UserInterface> players : playersInMatch.entrySet())
                players.getValue().notifyDisconnection(); //FIXME
            turnFinished = true;
            timer.stop();
        }
    }


    private void notifyToolCardUsedBy(String username) {
        synchronized (playersInMatchGuard) {
            for (Map.Entry<String, UserInterface> players : playersInMatch.entrySet())
                if (!players.getKey().equals(username))
                    players.getValue().notifyToolUsed(); //FIXME
        }
    }

    private void notifyDieInsertionBy(String username) {
        synchronized (playersInMatchGuard) {
            for (Map.Entry<String, UserInterface> players : playersInMatch.entrySet())
                if (!players.getKey().equals(username))
                    players.getValue().notifyDieInsertion();
        }
    }

    private void initializeTurn(String username){
        dieInserted= false;
        toolCardUsed=false;
        turnFinished=false;
        turnPlayer= playersInMatch.get(username);
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
                if(playersInMatch.size()>1&&!this.gameStartingSoon) MatchHandler.getInstance().notifyMatchCanStart();
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
                MatchHandler.getInstance().setPlayerInGame(username,this);
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
            model = new MatchModel(playerUserNames);
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

    public void handleReconnection(UserInterface player) throws InvalidOperationException {
        lock.lock();
        String username = player.getUsername();
        synchronized (playersInMatchGuard) {
            playersInMatch.put(username, player);
        }
        synchronized (modelGuard){
            try {
                model.setPlayerToConnect(username);
            } catch (NotValidParameterException e) {
                e.printStackTrace();
            }
        }
        player.setController(this);
        try {
            player.notifyReconnection();
        } catch (DisconnectionException e) {
            e.printStackTrace();
        }
        lock.unlock();
    }

    public List<Grid> getPlayerGrids(UserInterface userInterface) throws InvalidOperationException {
        String username = userInterface.getUsername();
        synchronized (playersInMatchGuard){
            if(!playersInMatch.containsKey(username)) /*TODO throw an exception*/;
            if(!playersInMatch.get(username).equals(userInterface)) /*TODO throw an exception*/;
        }

        try {
            return model.getGridsForPlayer(username);
        } catch (InvalidUsernameException e) {
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
            synchronized (modelGuard) {
                model.setPlayerGrid(username, gridChosen);
            }
            System.out.println(username + " chose the grid number: "+ gridChosen);
        } catch (NotValidParameterException e) {
            System.err.println("ERROR!");
            e.printStackTrace();
        }
        lock.lock();
        condition.signal();
        lock.unlock();
    }

    public String requestTurnPlayer() throws InvalidOperationException, TooManyRoundsException {
        synchronized (modelGuard) {
            if (!ready) throw new InvalidOperationException();
            if (gameFinished) throw new TooManyRoundsException();
            return model.askTurn();
        }
    }

    public List<Die> getDicePool() {
        synchronized (modelGuard) {
            return model.getDicePool();
        }
    }
}
