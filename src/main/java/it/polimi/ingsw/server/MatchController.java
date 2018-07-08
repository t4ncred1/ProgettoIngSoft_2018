package it.polimi.ingsw.server;

import it.polimi.ingsw.server.custom_exception.connection_exceptions.IllegalRequestException;
import it.polimi.ingsw.server.model.cards.PrivateObjective;
import it.polimi.ingsw.server.model.cards.ToolCard;
import it.polimi.ingsw.server.model.cards.effects.Effect;
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
    private List<UserInterface> reconnectingUsers;
    private static final Object reconnectingUsersGuard= new Object();
    private Map<String,Boolean> flagNotifyStatPlayer;
    private static final Object playersInMatchGuard = new Object();
    private boolean gameStarted;
    private boolean gameStartingSoon;
    private MatchModel model;
    private final Object modelGuard = new Object();
    private List<Effect> effectsToDo;
    private int currentEffect;

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
    private Lock endLock;
    private Condition endCondition;
    private Lock turnPlayerGuard;

    private Logger logger;
    private static long matchNumber = 0L;
    private long thisMatchNumber;
    private boolean disconnected;
    private int reconnectingPlayers;
    private boolean turnLogicStarted;

    /**
     * Constructor for MatchController.
     */
    public MatchController(){
        this.lock = new ReentrantLock();
        this.condition= lock.newCondition();
        this.endLock= new ReentrantLock();
        this.endCondition= endLock.newCondition();
        this.playersInMatch= new LinkedHashMap<>();
        this.reconnectingUsers= new ArrayList<>();
        this.reconnectingPlayers=0;
        this.turnLogicStarted =false;
        this.flagNotifyStatPlayer=new LinkedHashMap<>();
        this.turnPlayerGuard= new ReentrantLock();
        logger= Logger.getLogger(this.getClass().getName()+"_"+matchNumber);
        thisMatchNumber=matchNumber++;
    }

    @Override
    public void run(){
        checkForDisconnectionUntilStart();

        logger.log(Level.FINE, "Match {0} starting.", thisMatchNumber);
        new Thread(this::checkForDisconnections).start(); //this will let player to reconnect
        initializeGame();
        handleGame();
        MatchHandler.getInstance().notifyEndGame(this);
    }

    /**
     * Game handler (round-turn logic, final results).
     */
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
        Map<String,String> playersPoints;
        synchronized (modelGuard){
            playersPoints = model.calculatePoints();
        }
        synchronized (playersInMatchGuard){
            playersInMatch.forEach((username,player)->player.notifyEndGame());
            playersInMatch.forEach((username,player)->player.sendPoints(playersPoints));
        }

    }


    /*
    * --------------------------------------------------
    *       Methods to handle initialization
    * --------------------------------------------------
    */

    /**
     * Game initialization.
     */
    private void initializeGame() {
        initializeGrids();
    }

    /**
     * Sends the dice pool to every player.
     */
    private void sendDicePool() {
        List<Die> dicePool;
        synchronized (modelGuard){
            dicePool=model.getDicePool().showDiceInPool();
        }
        synchronized (playersInMatchGuard){
            playersInMatch.forEach((username,player)->player.sendDicePool(dicePool));
        }
    }

    /**
     * Sends a notification of game initialized to every player.
     */
    private void notifyGameInitialized() {
        synchronized (playersInMatchGuard) {
            playersInMatch.forEach((username, player) -> player.notifyGameInitialized());
        }
    }

    /**
     * Sends the grids to every player.
     */
    private void sendGrids() {
        Map<String,Grid> playersGrids;
        List<String> connectedPlayers;
        synchronized (modelGuard) {
            playersGrids= model.getAllGrids();
            connectedPlayers=model.getConnectedPlayers();
        }
        synchronized (playersInMatchGuard) {
            playersInMatch.forEach((username, player) -> player.sendGrids(playersGrids,connectedPlayers));
        }
    }

    /**
     * Waits until all players have chosen a grid (in case someone doesn't choose a grid, he will be disconnected when the timeout event occurs).
     */
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

    /**
     * In case someone doesn't choose a grid and the timeout event occurs, he will be disconnected here.
     *
     * @param timeout True if a timeout event occurred.
     * @return True if all players have chosen a grid.
     */
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

    /**
     * Turn handler (turn update, turn initialization).
     *
     * @throws TooManyRoundsException See updateTurn doc in MatchModel class.
     * @throws NotEnoughPlayersException See updateTurn doc in MatchModel class.
     */
    private void handleTurn() throws TooManyRoundsException, NotEnoughPlayersException {

        lock.lock();
        String username;
        turnLogicStarted=false;
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

    /**
     * Turn executor.
     * @param username Current player's username.
     */
    private void executeTurn(String username){
        boolean updatedDie=false;
        boolean updatedTool=false;
        GameTimer timer=null;
        while (!turnFinished) {
            if(timer==null|| timer.isStopped())timer = new GameTimer(this,OPERATION_TIMER);
            lock.lock();
            try {
                condition.await();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            lock.unlock();
            if (dieInserted&&!updatedDie) {
                updatedDie=true;
                timer.stop();
                synchronized (playersInMatchGuard){
                    playersInMatch.forEach((username1, userInterface) -> sendDataForDieInsertion(userInterface));
                }
            }

            if (toolCardUsed&&!updatedTool) {
                updatedTool=true;
                timer.stop();
                synchronized (playersInMatchGuard){
                    playersInMatch.forEach((username1, userInterface) -> sendDataForToolUse(userInterface));
                }
            }

            lock.lock();
            handleEventualTimeout(timer,username);
            lock.unlock();
        }
        if(timer!= null) timer.stop();
        waitForReConnections();
        synchronized (playersInMatchGuard){
            playersInMatch.forEach(this::updateEndTurn);
        }
        disconnected=false;
        turnFinished=false;
    }

    private void sendDataForToolUse(UserInterface userInterface) {
        List<Die> dicePool;
        Grid grid;
        List<ToolCard> toolCards;
        List<Die> roundTrack;
        String turnPlayerUsername;
        turnPlayerGuard.lock();
        turnPlayerUsername = turnPlayer.getUsername();
        turnPlayerGuard.unlock();
        synchronized (modelGuard){
            dicePool= model.getDicePool().showDiceInPool();
            grid=model.getPlayerCurrentGrid(turnPlayerUsername);
            toolCards=model.getToolCards();
            roundTrack=model.getRoundTrackCopy();
        }
        userInterface.sendGrid(grid);
        userInterface.sendDicePool(dicePool);
        userInterface.sendRoundTrack(roundTrack);
        userInterface.sendToolCards(toolCards);
        userInterface.notifyTurnInitialized();
    }

    private void waitForReConnections() {
        endLock.lock();
        while (reconnectingPlayers>0){
            try {
                endCondition.await();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        endLock.unlock();
    }

    private void updateEndTurn(String username, UserInterface player) {
        List<Die> dicePool;
        Grid grid;
        String turnPlayerUsername;
        turnPlayerGuard.lock();
        turnPlayerUsername = turnPlayer.getUsername();
        turnPlayerGuard.unlock();
        synchronized (modelGuard){
            dicePool= model.getDicePool().showDiceInPool();
            grid=model.getPlayerCurrentGrid(turnPlayerUsername);
            //todo pass toolcards, roundtrack
        }
        player.synchronizeEndTurn(disconnected,grid,dicePool);
    }

    /**
     * Disconnects a player if a timeout event occurs.
     *
     * @param timer Timer.
     * @param username Player to be disconnected.
     */
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
            disconnected= true;
            turnFinished = true;
            timer.stop();
        }
    }


    /**
     * Turn initialization (sends all match elements).
     *
     * @param username Current player's username.
     */
    private void initializeTurn(String username){
        dieInserted= false;
        toolCardUsed=false;
        turnFinished=false;
        synchronized (playersInMatchGuard){
            turnPlayerGuard.lock();
            turnPlayer= playersInMatch.get(username);
            turnPlayerGuard.unlock();
            playersInMatch.forEach((playerName,player)-> player.notifyTurnOf(username));
        }
        sendDicePool();
        sendRoundTrack();
        sendGrids();
        sendToolCards();
        notifyGameInitialized();
        lock.lock();
        turnLogicStarted = true;
        condition.signal();
        lock.unlock();
    }

    private void sendToolCards() {
        List<ToolCard> toolCards;
        synchronized (modelGuard){
            toolCards = model.getToolCards();
        }
        synchronized (playersInMatchGuard){
            playersInMatch.forEach((username,player)->player.sendToolCards(toolCards));
        }
    }

    /**
     * Sends the round track to every player.
     */
    private void sendRoundTrack() {
        List<Die> roundTrack;
        synchronized (modelGuard){
            roundTrack=model.getRoundTrack();
        }
        synchronized (playersInMatchGuard){
            playersInMatch.forEach((username,player)->player.sendRoundTrack(roundTrack));
        }
    }

    /**
     *
     * @return The number of players in queue.
     */
    int playerInGame() {
        int playerInQueue;
        synchronized (playersInMatchGuard) {
            playerInQueue = playersInMatch.size();
        }
        logger.log(Level.INFO,"player in queue: {0}",playerInQueue);
        return playerInQueue;
    }

    /**
     * Checks if someone is disconnecting. Checks if match can start.
     */
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

    /**
     * Insert a client in a match queue.
     *
     * @param client Client logged.
     */
    void insert(UserInterface client) {
        synchronized (playersInMatchGuard) {
            flagNotifyStatPlayer.put(client.getUsername(), false);
            playersInMatch.put(client.getUsername(),client);
        }
    }

    //state modifier

    /**
     * Sets gameStartingSoon.
     *
     * @param timeout True if a timeout event occurred.
     */
    void setGameToStartingSoon(boolean timeout) {
        final boolean startingSoonState =true;
        if(!gameStartingSoon||timeout) {
            this.gameStartingSoon = startingSoonState;
            this.notifyStartingSoonToPlayers();
        }
        else {
            synchronized (playersInMatchGuard) {
                if(!timeout) {
                    notifyAllPlayersNotNotified();
                }
            }
        }
    }

    /**
     * Notify the players not notified in setGameToStartingSoon.
     * (e.g. someone logged between 'match starting' message and 'match started' message)
     */
    private void notifyAllPlayersNotNotified() {
        Iterator<Map.Entry<String,Boolean>> iterator = flagNotifyStatPlayer.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String,Boolean> playerAndStatus= iterator.next();
            String username = playerAndStatus.getKey();
            boolean notified = playerAndStatus.getValue();
            if(!notified){
                try {
                    playersInMatch.get(username).notifyStarting();
                    playerAndStatus.setValue(true);
                } catch (DisconnectionException e) {
                    playersInMatch.remove(username);
                    iterator.remove();
                    MatchHandler.getInstance().notifyAboutDisconnection(username);
                }
            }
        }
    }

    /**
     * Remove a client.
     *
     * @param client Client to remove.
     * @throws InvalidOperationException Thrown when game is starting or it's already started.
     */
    public void remove(UserInterface client) throws InvalidOperationException {
        if(gameStartingSoon||gameStarted) {
            throw new InvalidOperationException();
        }
        else {
            synchronized (playersInMatchGuard) {
                playersInMatch.remove(client.getUsername());
                flagNotifyStatPlayer.remove(client.getUsername());
            }
        }
    }

    /**
     * Notify the players about match starting.
     */
    private void notifyStartingSoonToPlayers() {
        synchronized (playersInMatchGuard){
            Iterator<Map.Entry<String, UserInterface>> iterator=playersInMatch.entrySet().iterator();
            while (iterator.hasNext()){
                Map.Entry<String,UserInterface> player= iterator.next();
                try {
                    player.getValue().notifyStarting();
                    flagNotifyStatPlayer.put(player.getKey(), true);
                } catch (DisconnectionException e) {
                    iterator.remove();
                    MatchHandler.getInstance().notifyAboutDisconnection(player.getKey());
                }
            }
        }
    }

    /**
     * Notify the players about match started.
     */
    private void notifyStartToPlayers() {
        synchronized (playersInMatchGuard){
            playersInMatch.forEach(this::notifyStartToPlayerX);
        }
    }

    /**
     * Notify a player about match started.
     *
     * @param username Player's username.
     * @param player Player to be notified.
     */
    private void notifyStartToPlayerX(String username, UserInterface player){
        player.setController(this);
        MatchHandler.getInstance().setPlayerInGame(username,this);
        try {
            player.notifyStart();
        } catch (DisconnectionException e) {
            MatchHandler.getInstance().notifyAboutDisconnection(username);
        }
    }

    /**
     * Sets game to started.
     */
    void setGameToStarted() {
        this.gameStartingSoon= false; //game is not starting anymore: in fact it's started
        this.gameStarted=true; //game is started
        try {
            synchronized (playersInMatchGuard) {
                flagNotifyStatPlayer.forEach(this::notifyPlayerStillNotNotified);
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

    /**
     * Notify a player still not notified about game starting.
     *
     * @param username Player to be notified.
     * @param notified True if player wasn't notified.
     */
    private void notifyPlayerStillNotNotified(String username, Boolean notified) {
        if(!notified){
            try {
                playersInMatch.get(username).notifyStarting();
            } catch (DisconnectionException e) {
                MatchHandler.getInstance().notifyAboutDisconnection(username);
            }
        }
    }

    /**
     * Wakes up the controller.
     */
    void wakeUpController(){
        lock.lock();
        condition.signal();
        lock.unlock();
    }

    /**
     * Handles a reconnection event.
     *
     * @param player Player trying to reconnect.
     */
    void handleReconnection(UserInterface player) {
        player.setController(this);
        synchronized (reconnectingUsersGuard){
            reconnectingUsers.add(player);
        }
        new Thread(()->setUpReconnectingPlayer(player)).start();
    }

    private void setUpReconnectingPlayer(UserInterface player) {
        player.syncWithReconnectingUserAgent();
        endLock.lock();
        reconnectingPlayers++;
        endLock.unlock();
        waitIfGameLogicIsNotReady();
        boolean gameFinish;
        lock.lock();
        gameFinish=gameFinished;
        lock.unlock();
        if(!gameFinish){
            handleReconnectionDuringGame(player);
        }else {
            handleReconnectionEndGame(player);
        }
        synchronized (reconnectingUsersGuard){
            reconnectingUsers.remove(player);
        }
        endLock.lock();
        reconnectingPlayers--;
        endCondition.signal();
        endLock.unlock();
    }

    private void handleReconnectionEndGame(UserInterface player) {
        player.notifyEndGame();
        Map<String,String> playersPoints;
        synchronized (modelGuard){
            playersPoints = model.calculatePoints();
        }
        player.sendPoints(playersPoints);
    }

    private void handleReconnectionDuringGame(UserInterface player) {
        String turnPlayerUsername;
        turnPlayerGuard.lock();
        turnPlayerUsername = turnPlayer.getUsername();
        turnPlayerGuard.unlock();
        sendTurnDataToPlayer(player,turnPlayerUsername);
        if(player.getUsername().equals(turnPlayerUsername)) {
            reinsertPlayerInReconnected(player);
            turnPlayerGuard.lock();
            turnPlayer=player;
            turnPlayerGuard.unlock();
            player.notifyTurnInitialized();
        }else {
            player.notifyTurnInitialized();
            reinsertPlayerInReconnected(player);
        }
    }

    private void waitIfGameLogicIsNotReady() {
        lock.lock();
        while (!turnLogicStarted){
            try {
                condition.await();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        lock.unlock();
    }


    private void sendTurnDataToPlayer(UserInterface player,String turnPlayerUsername) {
        player.notifyTurnOf(turnPlayerUsername);
        Map<String,Grid> playersAndGrids;
        List<Die> dicePool;
        List<Die> roundTrack;
        List<String> connectedPlayers;
        synchronized (modelGuard){
            playersAndGrids = model.getAllGrids();
            dicePool=model.getDicePool().getDicePoolCopy();
            roundTrack= model.getRoundTrackCopy();
            connectedPlayers=model.getConnectedPlayers();
            player.sendToolCards(model.getToolCards());
        }
        player.sendDicePool(dicePool);
        player.sendRoundTrack(roundTrack);
        player.sendGrids(playersAndGrids, connectedPlayers);
    }

    private void reinsertPlayerInReconnected(UserInterface player) {
        String username=player.getUsername();
        synchronized (playersInMatchGuard) {
            playersInMatch.put(username, player);
        }
        synchronized (modelGuard){
            try {
                model.setPlayerToConnect(username);
            } catch (InvalidUsernameException e) {
                logger.log(Level.SEVERE,"Invalid operation",e);
            }
        }
    }

    /**
     *
     * @param userInterface Player's interface.
     * @return A list containing the 4 grids chosen for a player or, in case of an invalid username, an empty arraylist.
     * @throws InvalidOperationException See setGridsForPlayer doc in MatchModel class.
     * @throws IllegalRequestException See securityControl doc.
     */
    public List<Grid> getPlayerGrids(UserInterface userInterface) throws InvalidOperationException, IllegalRequestException {
        String username = userInterface.getUsername();
        securityControl(userInterface);

        try {
            synchronized (modelGuard) {
                return model.getGridsForPlayer(username);
            }
        } catch (InvalidUsernameException e) {
            logger.log(Level.SEVERE, MESSAGE_FOR_PLAYER_NOT_IN_MODEL, e);
        }
        return new ArrayList<>();

    }

    public boolean wasDieInserted(){
        return dieInserted;
    }

    /**
     * Sets the grid for a player.
     *
     * @param userInterface Player's interface.
     * @param gridChosen Index of the grid chosen by the player.
     * @throws InvalidOperationException See setPlayerGrid doc in MatchModel class.
     * @throws IllegalRequestException See securityControl doc.
     */
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

    /**
     *
     * @return A list representing the dice pool to be shown.
     */
    public List<Die> getDicePool() {
        synchronized (modelGuard) {
            return model.getDicePool().showDiceInPool();
        }
    }

    /**
     *
     * @param clientCalling Player.
     * @return Player's private objective.
     * @throws IllegalRequestException See securityControl doc.
     */
    public PrivateObjective getPrivateObject(UserInterface clientCalling) throws IllegalRequestException {
        securityControl(clientCalling);

        try {
            return model.getPrivateObjective(clientCalling.getUsername());
        } catch (InvalidUsernameException e) {
            logger.log(Level.SEVERE, MESSAGE_FOR_PLAYER_NOT_IN_MODEL, e);
        }
        return null;
    }

    /**
     *
     * @param player Player trying to insert a die.
     * @param position The index of the pool where to get the die.
     * @param x Abscissa of the box.
     * @param y Ordinate of the box.
     * @throws InvalidOperationException See insertDieInXY doc in Grid class.
     * @throws NotInPoolException See getDieFromPool doc in DicePool class.
     * @throws IllegalRequestException See securityControl doc.
     * @throws OperationAlreadyDoneException Thrown when this operation was already done in this turn.
     */
    public void insertDie(UserInterface player, int position, int x, int y) throws InvalidOperationException, NotInPoolException, IllegalRequestException, OperationAlreadyDoneException {
        securityControl(player);
        turnPlayerGuard.lock();
        if(!player.equals(turnPlayer)) throw new IllegalRequestException();
        turnPlayerGuard.unlock();
        if(dieInserted) throw new OperationAlreadyDoneException();
        try {
            model.insertDieOperation(x, y, position);
            this.dieInserted = true;
        }
        catch (NotValidParameterException e){
            //FIXME this exception should go upper
            e.printStackTrace();
        }
        lock.lock();
        dieInserted=true;
        condition.signal();
        lock.unlock();
    }

    /**
     * Sends the elements for a die insertion.
     *
     * @param userInterface Player's interface.
     */

    private void sendDataForDieInsertion(UserInterface userInterface) {
        List<Die> dicePool;
        Grid grid;
        String turnPlayerUsername;
        turnPlayerGuard.lock();
        turnPlayerUsername = turnPlayer.getUsername();
        turnPlayerGuard.unlock();
        synchronized (modelGuard){
            dicePool= model.getDicePool().showDiceInPool();
            grid=model.getPlayerCurrentGrid(turnPlayerUsername);
        }
        userInterface.sendGrid(grid);
        userInterface.sendDicePool(dicePool);
        userInterface.notifyTurnInitialized();
    }

    /**
     *
     * @param player Player.
     * @return Current player's grid.
     * @throws IllegalRequestException See securityControl doc.
     */
    public Grid getPlayerGrid(UserInterface player) throws IllegalRequestException {
        securityControl(player);
        return model.getPlayerCurrentGrid(player.getUsername());
    }

    /**
     * Checks if the player requested exists.
     *
     * @param player Player's interface.
     * @throws IllegalRequestException Thrown when 'player' does not exists.
     */
    private void securityControl(UserInterface player) throws  IllegalRequestException {
        String username= player.getUsername();
        if(!reconnectingUsers.contains(player)){
            synchronized (playersInMatchGuard) {
                if (!playersInMatch.containsKey(username)) throw new IllegalRequestException();
                if (!playersInMatch.get(username).equals(player)) throw new IllegalRequestException();
            }
        }
    }

    /**
     * Sends a notification about turn end.
     *
     * @param player Player to be notified.
     * @throws IllegalRequestException See securityControl doc.
     */
    public void notifyEnd(UserInterface player) throws IllegalRequestException {
        securityControl(player);
        turnPlayerGuard.lock();
        if(!player.equals(turnPlayer)) throw new IllegalRequestException();
        turnPlayerGuard.unlock();
        lock.lock();
        turnFinished=true;
        condition.signal();
        lock.unlock();
    }

    /**
     * Checks for disconnections before match start.
     */
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

    /**
     * Checks if someone is disconnecting.
     */
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

    /**
     *
     * @param player Player trying to use a tool card.
     * @param toolCardIndex Tool card index.
     * @throws OperationAlreadyDoneException Thrown when this operation was already done in the same turn.
     * @throws NotValidParameterException See getToolCard doc in MatchModel class.
     * @throws IllegalRequestException Thrown when 'player' is not valid.
     */
    public void tryToUseToolCard(UserInterface player, int toolCardIndex) throws OperationAlreadyDoneException, NotValidParameterException, IllegalRequestException {
        securityControl(player);
        turnPlayerGuard.lock();
        if(!player.equals(turnPlayer)) throw new IllegalRequestException();
        turnPlayerGuard.unlock();
        if(toolCardUsed) {
            throw new OperationAlreadyDoneException();
        }
        else {
            synchronized (modelGuard){
                effectsToDo=model.getToolCard(toolCardIndex).getEffects();
                currentEffect=0;
            }
        }
    }

    /**
     *
     * @param player Player trying to use a tool card.
     * @param effectName Effect name.
     * @param parameters Effect's parameters.
     * @throws IllegalRequestException Thrown when 'player' is not valid.
     * @throws InvalidOperationException Thrown when 'effectName' is not valid.
     * @throws NotValidParameterException See setToolCardParams doc in Effect class.
     */
    public void setEffectParameters(UserInterface player,String effectName, List<String> parameters) throws IllegalRequestException, InvalidOperationException, NotValidParameterException {
        securityControl(player);
        turnPlayerGuard.lock();
        if(!player.equals(turnPlayer)) throw new IllegalRequestException();
        turnPlayerGuard.unlock();
        synchronized (modelGuard){
            Effect effect= effectsToDo.get(currentEffect);
            if(effectName.equals(effect.getName())){
                effect.setToolCardParams(parameters);
                currentEffect++;
            }else{
                throw new InvalidOperationException();
            }
        }
    }

    /**
     *
     * @param player Player trying to use a tool card.
     * @param index Tool card index.
     * @throws IllegalRequestException Thrown when 'player' is not valid.
     */
    public void executeToolCard(UserInterface player, int index) throws IllegalRequestException, EffectException {
        securityControl(player);
        turnPlayerGuard.lock();
        if(!player.equals(turnPlayer)) throw new IllegalRequestException();
        // TODO: 07/07/2018 control currentEffect==size;
        turnPlayerGuard.unlock();
        synchronized (modelGuard){
            try {
                model.getToolCard(index).useToolCard();
            } catch (NotValidParameterException e) {
                logger.log(Level.SEVERE, "Something went wrong when TryToUseToolCard was executed", e);
            }
        }
        lock.lock();
        toolCardUsed=true;
        condition.signal();
        lock.unlock();
    }
}

