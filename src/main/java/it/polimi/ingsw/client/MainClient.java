package it.polimi.ingsw.client;

import it.polimi.ingsw.client.configurations.adapters.EffectAdapter;
import it.polimi.ingsw.client.configurations.adapters.GridInterface;
import it.polimi.ingsw.client.configurations.adapters.ToolCardAdapter;
import it.polimi.ingsw.client.configurations.Display;
import it.polimi.ingsw.client.custom_exception.*;
import it.polimi.ingsw.client.custom_exception.invalid_operations.*;
import it.polimi.ingsw.client.net.ServerCommunicatingInterface;
import it.polimi.ingsw.client.net.ServerRMICommunication;
import it.polimi.ingsw.client.net.ServerSocketCommunication;
import it.polimi.ingsw.server.custom_exception.DisconnectionException;
import it.polimi.ingsw.server.custom_exception.ReconnectionException;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MainClient {

    private static MainClient instance;

    private ServerCommunicatingInterface server;
    private boolean gameStarting;
    private boolean gameStarted;
    private boolean gridsInProxy;
    private boolean gridsAlreadySelected;
    private boolean gameInitialized;
    private Boolean turnUpdated;
    private boolean somethingChanged;
    private boolean gameFinished;
    private boolean turnEnded;
    private boolean gameEndDataInProxy;

    private ArrayList<String> toPrint;

    private Lock lock;
    private Condition condition;
    private Logger logger;

    private static final long SLEEP_TIME = 1000;

    private static final String USE_SOCKET = "socket";
    private static final String USE_RMI= "rmi";
    private static final String LOG_IN_REQUEST = "login";
    private static final String QUIT_REQUEST = "chiudi";
    private static final String LOGOUT_REQUEST = "logout";
    private static final String INSERT_DIE="inserisci dado";
    private static final String USE_TOOL_CARD= "usa carta strumento";
    private static final String END_TURN="finisci turno";
    private static final String ANSI_RED="\033[0;31m";
    private static final String ANSI_RESET="\u001B[0m";
    private boolean dataRetrieved;

    /**
     * Constructor for MainClient.
     */
    private MainClient(){
        lock= new ReentrantLock();
        condition= lock.newCondition();
        gameStarting =false;
        gameStarted=false;
        gridsInProxy=false;
        gridsAlreadySelected=false;
        turnUpdated=false;
        turnEnded=false;
        gameEndDataInProxy =false;
        toPrint=new ArrayList<>();
        logger= Logger.getLogger(MainClient.class.getName());
        dataRetrieved=false;
    }


    public static void main(String[] args){
        //initializing instance
        instance= new MainClient();
        //choosing connection system
        instance.chooseConnectionSystemCLI();
        //handling whole game logic
        try {
            instance.handleLoginCLI();
            instance.handleWaitForGameCLI();
            instance.handleGameInitializationLogic();
            instance.handleGameLogic();
        }
        catch (ServerIsDownException e){
            System.err.println("Non è possibile connettersi al server. Qualcosa è andato storto");
        } catch (LoggedOutException e) {
            System.out.println("Logged Out");
        } catch (DisconnectionException e) {
            System.err.println("Sei stato disconnesso per inattività");
        }
        System.exit(0);
    }

    /**
     * Handles game logic.
     *
     * @throws ServerIsDownException Thrown if the server is down.
     * @throws DisconnectionException Thrown when someone is trying to disconnect.
     */
    private void handleGameLogic() throws ServerIsDownException, DisconnectionException {
        do{
            try {
                boolean isMyTurn= askProxyIfItsMyTurn();
                if(isMyTurn)
                    handleMyTurn();
                else
                    waitEndOfOtherPlayerTurn();
            } catch (GameFinishedException e) {
                gameFinished=true;
            }
        }while (!gameFinished);
        System.out.println("La partita è finita");
        handleGameEnd();
    }

    /**
     * Handles the end a match.
     */
    private void handleGameEnd() {
        while(!gameEndDataInProxy){
            try {
                lock.lock();
                condition.await();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }finally {
                lock.unlock();
            }
        }
        Map<String,String> ranks= Proxy.getInstance().getPlayerRanking();
        ranks.forEach(this::printRanks);
        if(ranks.entrySet().iterator().hasNext()){
           String winner= ranks.entrySet().iterator().next().getKey();
           System.out.println("Il vincitore è "+winner);
        }
    }

    /**
     * Prints the final results.
     *
     * @param s Player's username.
     * @param s1 Player's points.
     */
    private void printRanks(String s, String s1) {
        try{
            int points=Integer.parseInt(s1);
            System.out.println(s+" ha totalizzato "+points+" punti");
        }catch (NumberFormatException e){
            System.out.println(s+" non era connesso al termine della partita");
        }
    }

    /**
     * Waits until his turn. Player is able to see all other players' stuff.
     */
    private void waitEndOfOtherPlayerTurn() {
        String turnPlayer=Proxy.getInstance().getTurnPlayer();
        System.out.println("E' il turno di " + turnPlayer);
        while(!turnEnded){
            printOtherPlayerTurnThings(turnPlayer);
            while (!somethingChanged){
                waitForServer();
            }
            lock.lock();
            somethingChanged=false;
            lock.unlock();
        }
        turnEnded=false;
        printOtherPlayerTurnThings(turnPlayer);
        try {
            System.out.println(ANSI_RED+Proxy.getInstance().getDisconnection()+ " si è disconnesso"+ANSI_RESET);
        } catch (NoDisconnectionException e) {
            logger.log(Level.FINE,"No one disconnected");
        }
    }

    /**
     * Sends to a player (who is waiting for his turn) all current player' stuff.
     * @param turnPlayer Current player.
     */
    private void printOtherPlayerTurnThings(String turnPlayer) {
        try {
            System.out.println("Le tool cards:");
            Proxy.getInstance().getToolCards().stream().map(ToolCardAdapter::getAdapterInterface).forEach(Display::display);
            Proxy.getInstance().getDicePool().getAdapterInterface().display();
            Proxy.getInstance().getRoundTrack().getAdapterInterface().display();
            System.out.println("La mappa di " + turnPlayer);
            Proxy.getInstance().getGridsOf(turnPlayer).getAdapterInterface().display();
        } catch (InvalidUsernameException e) {
            e.printStackTrace();
        }
    }

    /**
     * Handles current player turn (operations and other stuff).
     *
     * @throws ServerIsDownException Thrown if the server is down.
     * @throws DisconnectionException Thrown if someone is trying to disconnect.
     */
    private void handleMyTurn() throws ServerIsDownException, DisconnectionException {
        Scanner scanner = new Scanner(System.in);
        boolean myTurnFinished=false;
        boolean doneSomething=false;
        do {
            printThingsOnMyTurn();
            String command = scanner.nextLine().toLowerCase();
            switch (command){
                case INSERT_DIE:
                    doneSomething=handleDieInsertion();
                    break;
                case USE_TOOL_CARD:
                    doneSomething=handleToolCard();
                    break;
                case END_TURN:
                    myTurnFinished=true;
                    server.endTurn();
                    doneSomething=true;
                    break;
                default:
                    System.err.println("Nessun comando corrisponde a quello inserito ("+command+"). Reinserire un comando valido");
            }
            if(doneSomething){
                waitDataRetrieving();
                doneSomething=false;
            }
        }while (!myTurnFinished);
    }

    private void waitDataRetrieving() {
        lock.lock();
        while(!dataRetrieved) {
            try {
                condition.await();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        dataRetrieved=false;
        lock.unlock();
    }

    /**
     * Shows current player' stuff.
     */
    private void printThingsOnMyTurn() {
        System.out.println("Le tool cards:");
        Proxy.getInstance().getToolCards().stream().map(ToolCardAdapter::getAdapterInterface).forEach(Display::display);
        Proxy.getInstance().getRoundTrack().getAdapterInterface().display();
        Proxy.getInstance().getDicePool().getAdapterInterface().display();
        System.out.println("La tua mappa:");
        Proxy.getInstance().getGridSelected().getAdapterInterface().display();
        System.out.println("E' il tuo turno");
        System.out.println("Ricorda, puoi eseguire solo una volta nel turno");
        System.out.println("l'inserimento dei dati o l'uso della carta strumento");
        System.out.println("Puoi usare i comandi "+INSERT_DIE+", "+USE_TOOL_CARD+" e "+END_TURN);
    }
    /**
     * Handles tool card operation.
     *
     * @throws ServerIsDownException Thrown if the server is down.
     * @throws DisconnectionException Thrown if someone is trying to disconnect.
     */
    private boolean handleToolCard() throws ServerIsDownException, DisconnectionException {
        System.out.println("Scegliere l'indice della tool card:");
        Scanner scanner= new Scanner(System.in);
        int value;
        ToolCardAdapter toolCard;
        do{
            try{
                value = Integer.parseInt(scanner.nextLine());
                toolCard=Proxy.getInstance().getToolCard(value-1);
                server.useToolCard(value-1);
                handleToolCardEffects(toolCard);
                return true;
            }catch (NumberFormatException e){
                System.err.println("Il parametro inserito è invalido, inserire un numero:");
            } catch (ToolCardNotExistException e) {
                System.err.println("Non esite alcuna carta strumento nella posizione indicata, riprovare:");
            } catch (AlreadyDoneOperationException e) {
                System.err.println("L'operazione è già stata eseguita, non può essere eseguita nuovamente");
                return false;
            } catch (InvalidMoveException e) {
                System.out.println(ANSI_RED + "Il server notifica che non è possibile giocare la carta strumento coi dati inseriti" + ANSI_RESET);
                return false;
            }
        }while (true);
    }

    /**
     *
     * @param toolCard Tool card to use.
     *
     * @throws ServerIsDownException Thrown if the server is down.
     * @throws DisconnectionException Thrown if someone is trying to disconnect.
     */
    private void handleToolCardEffects(ToolCardAdapter toolCard) throws ServerIsDownException, DisconnectionException, InvalidMoveException {
        List<EffectAdapter> effects= toolCard.getEffects();
        for(EffectAdapter effect: effects) {
            boolean ok = false;
            while (!ok) {
                try {
                    List<String> params = effect.computeEffect();
                    server.doEffect(effect.getName(), params);
                    ok = true;
                } catch (InvalidMoveException e) {
                    System.out.println(ANSI_RED + "Il server notifica che almeno un parametro inserito non è corretto, reinserirli" + ANSI_RESET);
                }
            }
        }
        server.launchToolCards();

    }


    /**
     *
     * @return True if the die is correctly inserted.
     *
     * @throws ServerIsDownException Thrown if the server is down.
     * @throws DisconnectionException Thrown if someone is trying to disconnect.
     */
    private boolean handleDieInsertion() throws ServerIsDownException, DisconnectionException {
        Scanner scanner= new Scanner(System.in);
        System.out.println("Inserisci la posizione del dado nella dice pool");
        int position= scanner.nextInt()-1;
        System.out.println("Inserisci la riga della mappa in cui inserirlo");
        int row= scanner.nextInt()-1;
        System.out.println("Inserisci la colonna della mappa in cui inserirlo");
        int column= scanner.nextInt()-1;
        try {
            Proxy.getInstance().tryToInsertDieInXY(position, row, column);
            server.insertDie(position,column,row);
            return true;
        } catch (InvalidMoveException e) {
            System.err.println("Non è possibile inserire un dado alle coordinate indicate");
        } catch (DieNotExistException e) {
            System.err.println("Non esite alcun dado nella posizione indicata");
        } catch (AlreadyDoneOperationException e) {
            System.err.println("L'operazione è già stata eseguita, non può essere eseguita nuovamente");
        }
        return false;
    }

    /**
     *
     * @return True if this is player's turn.
     * @throws GameFinishedException Thrown if the match is already finished.
     */
    private boolean askProxyIfItsMyTurn() throws GameFinishedException {
        while (!turnUpdated){
            waitForServer();
        }
        lock.lock();
        turnUpdated=false;
        lock.unlock();
        return Proxy.getInstance().askIfItsMyTurn();
    }

    private void waitForServer() {
        lock.lock();
        try {
            condition.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        lock.unlock();
    }

    /**
     * Handles game initialization (receive grids, choose a grid).
     *
     * @throws ServerIsDownException Thrown if the server is down.
     * @throws DisconnectionException Thrown if someone is trying to disconnect.
     */
    private void handleGameInitializationLogic() throws ServerIsDownException, DisconnectionException {
        try {
            waitForGridsFromServer();
            selectAGrid();
            System.out.println("Aspettando gli altri giocatori...");
        } catch (GameInProgressException e) {
            System.out.println("La tua partita è gia in corso, sei stato reinserito correttamente.");
            System.out.println("In attesa dei dati da parte del server...");
        }
        waitForServerToUpdateProxy();
        if(!gameFinished) printGridsDicePoolAndObjectives();
    }

    /**
     * Show grids, dice pool and objectives card.
     */
    private void printGridsDicePoolAndObjectives() {
        System.out.println("La tua mappa:");
        Proxy.getInstance().getGridSelected().getAdapterInterface().display();
    }

    private void waitForServerToUpdateProxy() {
        lock.lock();
        while (!gameInitialized&&!gameFinished) {
            try {
                condition.await();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            gameFinished = Proxy.getInstance().isGameFinished();
        }
        lock.unlock();
    }

    /**
     * Gets player's grid choice.
     *
     * @throws ServerIsDownException Thrown if the server is down.
     * @throws DisconnectionException Thrown if someone is trying to disconnect.
     */
    private void selectAGrid() throws ServerIsDownException, DisconnectionException {
        Scanner scanner= new Scanner(System.in);
        String request;
        int value;

        List<GridInterface> gridSelection= Proxy.getInstance().getGridsSelection();
        gridSelection.stream().map(GridInterface::getAdapterInterface).forEach(Display::display);
        System.out.println("Scegli una mappa tra le seguenti (scegli un numero da 1 a "+Proxy.getInstance().getGridsSelectionDimension()+"):");
        do{
            request= scanner.nextLine();
            try {
                value = Integer.parseInt(request);
                if(value<1||value>Proxy.getInstance().getGridsSelectionDimension()) System.err.println("Inserire un numero valido. "+value+" non è in range");
                server.selectGrid(value-1);
            }catch (NumberFormatException e){
                value=-1;
                System.err.println(request+"non è un numero. Inserire un numero valido");
            } catch (InvalidIndexException e) {
                value=-1;
                logger.log(Level.INFO,"Il server notifica che l{0}indice non è disponibile: {1} (è stato rimosso il controllo locale?)",new Object[]{'\'',request});
            }
        }while(value<1||value>Proxy.getInstance().getGridsSelectionDimension());

    }

    /**
     *
     * @throws GameInProgressException Thrown if the game is already started.
     */
    private void waitForGridsFromServer() throws GameInProgressException {
        lock.lock();
        while (!gridsInProxy) {
            if (gridsAlreadySelected) throw new GameInProgressException();
            try {
                condition.await();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        lock.unlock();
    }

    /**
     * Handles "waitForGame" phase.
     * @throws ServerIsDownException
     * @throws LoggedOutException
     */
    private void handleWaitForGameCLI() throws ServerIsDownException, LoggedOutException {
        System.out.println("Usa 'Logout' per uscire dalla coda. Non puoi uscire una volta che la partita sta incominciando");
        waitForGameStartingSoonMessage();
        System.out.println("Una partita inizierà presto");
        waitForGameStartedMessage();
        System.out.println("Inizia la partita");
    }

    private void waitForGameStartedMessage() {
        lock.lock();
        while(!gameStarted){
            try {
                condition.await();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            if(!gameStarted) System.out.println("Someone disconnected, game countdown restarted");
        }
        lock.unlock();
    }

    /**
     *
     * @throws ServerIsDownException Thrown if the server is down.
     * @throws LoggedOutException Thrown if the player logged out.
     */
    private void waitForGameStartingSoonMessage() throws ServerIsDownException, LoggedOutException {
        lock.lock();
        while (!gameStarting) {
            lock.unlock();
            DataInputStream dataInputStream = new DataInputStream(System.in);
            Scanner scanner = new Scanner(System.in);
            String read;
            try {
                if (dataInputStream.available() > 0) {
                    read = scanner.nextLine();
                    read = read.toLowerCase();
                    if (read.equals(LOGOUT_REQUEST)) {
                        tryLogout();
                    } else {
                        System.out.println("Invalid request");
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                Thread.sleep(SLEEP_TIME);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            lock.lock();
        }
        lock.unlock();
    }

    /**
     *
     * @throws ServerIsDownException Thrown if the server is down.
     * @throws LoggedOutException Thrown if the player just logged out.
     */
    private void tryLogout() throws ServerIsDownException, LoggedOutException {
        try {
            server.askForLogout();
        } catch (GameStartingException e) {
            gameStarting =true;
        }
    }


    public static MainClient getInstance(){
        if(instance==null) instance=new MainClient();
        return instance;
    }

    /**
     * Choice between Socket and Rmi.
     */
    private void chooseConnectionSystemCLI() {
        Scanner scanner = new Scanner(System.in);
        String written;
        System.out.println("Scegli tra '"+USE_SOCKET+"' e '"+USE_RMI+"' come modalità di connessione: ");
        do {
            written = scanner.nextLine();
            written = written.toLowerCase();
            switch (written){
                case USE_SOCKET:
                    instance.server = new ServerSocketCommunication();
                    break;
                case USE_RMI:
                    instance.server = new ServerRMICommunication();
                    break;
                default:
                    System.err.println("Input invalido: scegli tra Socket e RMI");
            }
        }
        while (!(written.equals(USE_SOCKET) || written.equals(USE_RMI)));
    }

    /**
     * Handles game login (username choice, username check and other stuff).
     * @throws ServerIsDownException Thrown if the server is down.
     */
    private void handleLoginCLI() throws ServerIsDownException {
        String written;
        Scanner scanner= new Scanner(System.in);
        System.out.println("Per loggare usa il comando "+LOG_IN_REQUEST+", altrimenti usa "+QUIT_REQUEST+" per chiudere");
        do {
            written = scanner.nextLine();
            written = written.toLowerCase();
            switch (written){
                case LOG_IN_REQUEST:
                    boolean ok=false;
                    instance.server.setUpConnection();
                    System.out.println("Benvenuto nel server di Sagrada. Per favore scegli uno username:");

                    do {
                        try {
                            String usernameChosen=scanner.nextLine();
                            instance.server.login(usernameChosen);
                            ok=true;
                            System.out.println("Ti sei connesso. Sei stato inserito nella lobby.");
                        } catch (ServerIsFullException e) {
                            logger.log(Level.INFO,"Il server è pieno, riprova più tardi");
                            System.exit(0);
                        } catch (InvalidUsernameException e) {
                            logger.log(Level.INFO,"Questo username non è valido o è gia esistente. Per favore scegline un altro:");
                        } catch (ReconnectionException e) {
                            System.out.println("Ti sei riconnesso a una partita precedente.");
                            ok=true;
                        }
                    }
                    while (!ok);
                    break;
                case QUIT_REQUEST:
                    System.out.println("Chiudendo Sagrada...");
                    System.exit(0);
                    break;
                default:
                    logger.log(Level.INFO,"Input invalido: Per favore usa {0} o {1}", new Object[]{LOG_IN_REQUEST,QUIT_REQUEST});
            }

        }
        while (!(written.equals(LOG_IN_REQUEST) || written.equals(QUIT_REQUEST)));
    }

    public void notifyGameStarting() {
        lock.lock();
        gameStarting =true;
        condition.signal();
        lock.unlock();
    }

    public void notifyGameStarted() {
        lock.lock();
        gameStarted =true;
        condition.signal();
        lock.unlock();
    }

    public void notifyGridsAreInProxy() {
        lock.lock();
        gridsInProxy =true;
        condition.signal();
        lock.unlock();
    }

    public void setGameToInitialized() {
        lock.lock();
        gameInitialized=true;
        condition.signal();
        lock.unlock();
    }

    public void setGridsAlreadySelected(boolean state){
        lock.lock();
        this.gridsAlreadySelected=state;
        condition.signal();
        lock.unlock();
    }

    public void notifyTurnUpdated() {
        lock.lock();
        turnUpdated=true;
        condition.signal();
        lock.unlock();
    }

    public void notifySomethingChanged() {
        lock.lock();
        somethingChanged=true;
        condition.signal();
        lock.unlock();
    }

    public void notifyEndTurn() {
        lock.lock();
        somethingChanged=true;
        turnEnded=true;
        condition.signal();
        lock.unlock();
    }

    public void setPrint(List<String> strings) {
        toPrint.addAll(strings);
    }

    public void notifyEndDataInProxy() {
        lock.lock();
        gameEndDataInProxy =true;
        condition.signal();
        lock.unlock();
    }

    public void notifyDataRetrieved(){
        lock.lock();
        dataRetrieved =true;
        condition.signal();
        lock.unlock();
    }
}
