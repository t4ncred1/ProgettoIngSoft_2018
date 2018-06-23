package it.polimi.ingsw.server;

import it.polimi.ingsw.server.custom_exception.connection_exceptions.IllegalRequestException;
import it.polimi.ingsw.server.model.cards.PrivateObjective;
import it.polimi.ingsw.server.model.components.Die;
import it.polimi.ingsw.server.model.components.Grid;
import it.polimi.ingsw.server.custom_exception.*;
import it.polimi.ingsw.server.model.MatchModel;
import it.polimi.ingsw.server.net.UserInterface;

import java.util.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

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

    private static final String MESSAGE_FOR_PLAYER_NOT_IN_MODEL="Something went wrong during initialization: player is not in model";

    private static final String OPERATION_TIMER = "operation";
    private static final String GRID_CHOOSE_TIMER="initialization";
    private static final int SLEEP_TIME = 1000;

    private Lock lock;
    private Condition condition;

    private Logger logger;
    private static long matchNumber = 0L;
    private long thisMatchNumber;


    public MatchController(){
        this.lock = new ReentrantLock();
        this.condition= lock.newCondition();
        this.playersInMatch= new LinkedHashMap<>();
        logger= Logger.getLogger(this.getClass().getName()+"_"+matchNumber);
        thisMatchNumber=matchNumber++;
    }

    @Override
    public void run(){
        checkForDisconnectionUntilStart();
        //Initializing game
        new Thread(this::checkForDisconnections).start();

        logger.log(Level.FINE, "Match {0} started.", thisMatchNumber);
        initializeGame();
        handleGame();
    }


    private void handleGame() {
        //round-turn logic.
        do{
            try {
                handleTurn();
            } catch (TooManyRoundsException e) {
                gameFinished = true;
            } catch (NotEnoughPlayersException e) {
                logger.log(Level.INFO, "Game finished");
                gameFinished=true;
                ready=true;
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
        logger.log(Level.INFO, "Ready to handle game logic");
    }

    private boolean haveAllPlayersChosenAGrid(boolean timeout) {
        //At the end of this part, if all players chose a grid then game could start (ok remains true).
        boolean ok = true;
        synchronized (modelGuard){
            for (Iterator<Map.Entry<String, UserInterface>> iterator = playersInMatch.entrySet().iterator(); iterator.hasNext(); ) {
                Map.Entry<String, UserInterface> entry = iterator.next();
                String username = entry.getKey();
                boolean hasPlayerChosenAGrid = true;
                try {
                    hasPlayerChosenAGrid = model.hasPlayerChosenAGrid(username);
                } catch (NotValidParameterException e) {
                    logger.log(Level.SEVERE, MESSAGE_FOR_PLAYER_NOT_IN_MODEL, e);
                }
                ok = ok && hasPlayerChosenAGrid;

                //if timeout event occurred and player still has not chosen a grid he can be considered as disconnected.
                if (timeout && !hasPlayerChosenAGrid) {
                    try {
                        model.setPlayerToDisconnect(username);
                    } catch (InvalidUsernameException e) {
                        logger.log(Level.SEVERE, MESSAGE_FOR_PLAYER_NOT_IN_MODEL, e);
                    }
                    entry.getValue().notifyDisconnection();
                    MatchHandler.getInstance().notifyAboutDisconnection(entry.getKey());
                    iterator.remove();
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
            model.updateTurn(MAX_ROUND);
            username = model.askTurn();
            logger.log(Level.INFO,"It is {0} turn.", username);
            initializeTurn(username);
            ready=true; //now i can handle clients requests.
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
        timer.stop();
    }

    private void handleEventualTimeout(GameTimer timer, String username) {
        if (timer.getTimeoutEvent()) {
            ready=false;
            synchronized (modelGuard){
                try {
                    model.setPlayerToDisconnect(username);
                } catch (InvalidUsernameException e) {
                    logger.log(Level.SEVERE, MESSAGE_FOR_PLAYER_NOT_IN_MODEL, e);
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

    int playerInGame() {
        int playerInQueue;
        synchronized (playersInMatchGuard) {
            playerInQueue = playersInMatch.size();
        }
        logger.log(Level.INFO,"player in queue: {0}",playerInQueue);
        return playerInQueue;
    }

    void updateQueue(){
        boolean notify=false;
        synchronized (playersInMatchGuard){
            playersInMatch.forEach((username,player)->{
                if(!player.isConnected()){
                    logger.log(Level.WARNING, "{0} disconnected",username);
                    MatchHandler.getInstance().notifyAboutDisconnection(username);
                }
            });
            playersInMatch.entrySet().removeIf(player-> !player.getValue().isConnected());
            if(playersInMatch.size()>1&&!this.gameStartingSoon) notify=true;
        }
        if(notify)MatchHandler.getInstance().notifyMatchCanStart();
    }

    void insert(UserInterface client) {
        synchronized (playersInMatchGuard) {
            playersInMatch.put(client.getUsername(),client);
        }
    }

    //state modifier
    void setGameStartingSoon(boolean timeout) {
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
                    while (iterator.hasNext()) lastConnection =playersInMatch.get(iterator.next().toString()); //Fixme is this the cause why "a game will start soon is sent twice?
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
        if(gameStartingSoon) {
            throw new InvalidOperationException();
        }
        else {
            synchronized (playersInMatchGuard) {
                playersInMatch.remove(client.getUsername());
            }
        }
    }


    private void notifyStartingSoonToPlayers() {
        synchronized (playersInMatchGuard){
            Iterator<Map.Entry<String, UserInterface>> iterator=playersInMatch.entrySet().iterator();
            while (iterator.hasNext()){
                Map.Entry<String,UserInterface> player= iterator.next();
                try {
                    player.getValue().notifyStarting();
                } catch (DisconnectionException e) {
                    iterator.remove();
                    MatchHandler.getInstance().notifyAboutDisconnection(player.getKey());
                }
            }
        }
    }

    private void notifyStartToPlayers() {
        synchronized (playersInMatchGuard){
            playersInMatch.forEach(this::notifyStartToPlayerX);
        }
    }

    private void notifyStartToPlayerX(String username, UserInterface player){
        player.setController(this);
        MatchHandler.getInstance().setPlayerInGame(username,this);
        try {
            player.notifyStart();
        } catch (DisconnectionException e) {
            MatchHandler.getInstance().notifyAboutDisconnection(username);
        }
    }

    void setGameToStarted() {
        final boolean gameStartedStatus =true;
        final boolean gameIsNotStartingAnymore = false;

        this.gameStartingSoon=gameIsNotStartingAnymore;
        this.gameStarted=gameStartedStatus;
        try {
            synchronized (playersInMatchGuard) {
                Set<String> playerUserNames = playersInMatch.keySet();
                model = new MatchModel(playerUserNames,this);
            }
        } catch (NotValidParameterException e) {
            e.printStackTrace();
        } catch (NotValidConfigPathException e) {
            e.printStackTrace();    //FIXME this exception is thrown if the configuration file is wrong.
        }
        this.notifyStartToPlayers();
    }

    void wakeUpController(){
        lock.lock();
        condition.signal();
        lock.unlock();
    }

    void handleReconnection(UserInterface player) {
        lock.lock();
        String username = player.getUsername();
        synchronized (playersInMatchGuard) {
            playersInMatch.put(username, player);
        }
        synchronized (modelGuard){
            try {
                model.setPlayerToConnect(username);
            } catch (NotValidParameterException e) {
                logger.log(Level.SEVERE,"Invalid operation",e);
            }
        }
        player.setController(this);
        try {
            player.notifyReconnection();
        } catch (DisconnectionException e) {
            logger.log(Level.INFO, "{0} disconnected during reconnection", player.getUsername());
        }
        lock.unlock();
    }

    public List<Grid> getPlayerGrids(UserInterface userInterface) throws InvalidOperationException, IllegalRequestException {
        String username = userInterface.getUsername();
        securityControl(userInterface);

        try {
            return model.getGridsForPlayer(username);
        } catch (InvalidUsernameException e) {
            logger.log(Level.SEVERE, MESSAGE_FOR_PLAYER_NOT_IN_MODEL, e);
        }
        return new ArrayList<>();

    }

    public void setGrid(UserInterface userInterface, int gridChosen) throws InvalidOperationException, IllegalRequestException {
        String username = userInterface.getUsername();
        securityControl(userInterface);
        try {
            synchronized (modelGuard) {
                model.setPlayerGrid(username, gridChosen);
            }
            System.out.println(username + " chose the grid number: "+ gridChosen);
        } catch (NotValidParameterException e) {
            // FIXME: 08/06/2018
            logger.log(Level.SEVERE, "Error!", e);
        }
        lock.lock();
        condition.signal();
        lock.unlock();
    }

    public String requestTurnPlayer() throws InvalidOperationException, TooManyRoundsException {
        String username;
        synchronized (modelGuard) {
            if (!ready) throw new InvalidOperationException();
            if (gameFinished) throw new TooManyRoundsException();
            username= model.askTurn();
        }
        return username;
    }

    public List<Die> getDicePool() {
        synchronized (modelGuard) {
            return model.getDicePool().showDiceInPool();
        }
    }

    public PrivateObjective getPrivateObject(UserInterface clientCalling) throws IllegalRequestException {
        securityControl(clientCalling);

        try {
            return model.getPrivateObjective(clientCalling.getUsername());
        } catch (InvalidUsernameException e) {
            logger.log(Level.SEVERE, MESSAGE_FOR_PLAYER_NOT_IN_MODEL, e);
        }
        return null;
    }

    public void insertDie(UserInterface player, int position, int x, int y) throws InvalidOperationException, NotInPoolException, IllegalRequestException {
        securityControl(player);
        if(dieInserted) /*TODO throw an exception*/;
        try {
            model.insertDieOperation(x, y, position);
            this.dieInserted = true;
        }
        catch (NotValidParameterException e){
            //FIXME this exception should go upper
            e.printStackTrace();
        }


    }


    public Grid getPlayerGrid(UserInterface player) throws IllegalRequestException {
        securityControl(player);
        return model.getPlayerCurrentGrid(player.getUsername());
    }

    private void securityControl(UserInterface player) throws  IllegalRequestException {
        String username= player.getUsername();
        //fixme create 2 different exceptions?
        synchronized (playersInMatchGuard) {
            if (!playersInMatch.containsKey(username)) throw new IllegalRequestException();
            if (!playersInMatch.get(username).equals(player)) throw new IllegalRequestException();
        }
    }

    public void notifyEnd() {
        lock.lock();
        turnFinished=true;
        ready=false;
        condition.signal();
        lock.unlock();
        synchronized (playersInMatchGuard) {
            playersInMatch.keySet().forEach(player -> playersInMatch.get(player).notifyEndTurn());
        }
    }

    private void checkForDisconnectionUntilStart() {
        while(!gameStarted){
            updateQueue();
            try {
                Thread.sleep(SLEEP_TIME);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                logger.log(Level.SEVERE, "Match interrupted on wait", e);
            }
        }
    }

    private void checkForDisconnections() {
        ArrayList<String> stopCheck = new ArrayList<>();
        //this arrayList is used to avoid continuous print of "playerX disconnected"

        while (!gameFinished) {
            try {
                Thread.sleep(SLEEP_TIME);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            synchronized (playersInMatchGuard) {
                Set<String> set = playersInMatch.keySet();
                for (String username : set) {
                    if (!stopCheck.contains(username) && !playersInMatch.get(username).isConnected()) {
                        //in this instruction player is removed only from connectedPlayers
                        logger.log(Level.WARNING, "{0} disconnected",username);
                        stopCheck.add(username);
                        MatchHandler.getInstance().notifyAboutDisconnection(username);
                    } else if (stopCheck.contains(username) && playersInMatch.get(username).isConnected()) {
                        stopCheck.remove(username);
                    }
                }
            }
        }
    }

    public int toolCardLetPlayerChoose(String color){
        //todo this will be the method called by toolcard 11 to get the value for the newly extracted die from client.
        return 6;
    }
}

