package it.polimi.ingsw.client;

import it.polimi.ingsw.client.configurations.GridInterface;
import it.polimi.ingsw.client.custom_exception.*;
import it.polimi.ingsw.client.net.ServerCommunicatingInterface;
import it.polimi.ingsw.client.net.ServerCommunicatingInterfaceV2;
import it.polimi.ingsw.client.net.ServerSocketCommunicationV2;

import java.io.DataInputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class MainClient {

    private static MainClient instance;

    private String username;
    private ServerCommunicatingInterfaceV2 server;
    private boolean gameStarting;
    private boolean gameStarted;
    private boolean gridsInProxy;
    private boolean gridsAlreadySelected;

    private Lock lock;
    private Condition condition;

    private static final long SLEEP_TIME = 1000;

    private static final String USE_SOCKET = "socket";
    private static final String USE_RMI= "rmi";
    private static final String LOG_IN_REQUEST = "login";
    private static final String QUIT_REQUEST = "quit";
    private static final String LOGOUT_REQUEST = "logout";


    private MainClient(){
        lock= new ReentrantLock();
        condition= lock.newCondition();
        gameStarting =false;
        gameStarted=false;
        gridsInProxy=false;
        gridsAlreadySelected=false;
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
        }
        catch (ServerIsDownException e){
            System.err.println("Can't connect to server, something went wrong!");
            System.exit(0);
        } catch (LoggedOutException e) {
            System.out.println("Logged Out");
            System.exit(0);
        }

    }

    private void handleGameInitializationLogic() throws ServerIsDownException {
        try {
            waitForGridsFromServer();
            selectAGrid();
        } catch (GameInProgressException e) {
            System.out.println("Game already in progress, you have been reinserted correctly");
        }
    }

    private void selectAGrid() throws ServerIsDownException {
        Scanner scanner= new Scanner(System.in);
        String request;
        int value;

        ArrayList<GridInterface> gridSelection= (ArrayList<GridInterface>) Proxy.getInstance().getGridsSelection();
        for(GridInterface grid:gridSelection){
            System.out.println(grid.getGridInterface());
        }
        System.out.println("Choose a grid between those (select a number from 1 to "+Proxy.getInstance().getGridsSelectionDimension()+"):");
        do{
            request= scanner.nextLine();
            try {
                value = Integer.parseInt(request);
                if(value<1||value>Proxy.getInstance().getGridsSelectionDimension()) System.err.println("Insert a valid number. "+value+" isn't in range.");
            }catch (NumberFormatException e){
                value=-1;
                System.err.println(request+" is not a number. Please insert a valid number");
            }
        }while(value<1||value>Proxy.getInstance().getGridsSelectionDimension());
        server.selectGrid(value-1);
    }

    private void waitForGridsFromServer() throws GameInProgressException {
        do{
            lock.lock();
            try {
                condition.await();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            lock.unlock();
            if(gridsAlreadySelected) throw new GameInProgressException();
        }while (!gridsInProxy);
    }

    private void handleWaitForGameCLI() throws ServerIsDownException, LoggedOutException {
        System.out.println("Use 'Logout' command to logout. You can't logout while game is starting");
        waitForGameStartingSoonMessage();
        System.out.println("A game will start soon");
        waitForGameStartedMessage();
        System.out.println("Game started");
    }

    private void waitForGameStartedMessage() {
        lock.lock();
        do {
            try {
                condition.await();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            if(!gameStarted) System.out.println("Someone disconnected, game countdown restarted");
        }while (!gameStarted);
        lock.unlock();
    }

    private void waitForGameStartingSoonMessage() throws ServerIsDownException, LoggedOutException {
        lock.lock();
        do {
            lock.unlock();
            DataInputStream dataInputStream = new DataInputStream(System.in);
            Scanner scanner= new Scanner(System.in);
            String read;
            try {
                if (dataInputStream.available() > 0) {
                    read = scanner.nextLine();
                    read= read.toLowerCase();
                    if(read.equals(LOGOUT_REQUEST)){
                        tryLogout();
                    }else{
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
        }while (!gameStarting);
        lock.unlock();
    }

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

    private void chooseConnectionSystemCLI() {
        Scanner scanner = new Scanner(System.in);
        String written;
        System.out.println("Choose between Socket or RMI to connect to server: ");
        do {
            written = scanner.nextLine();
            written = written.toLowerCase();
            switch (written){
                case USE_SOCKET:
                    instance.server = new ServerSocketCommunicationV2();
                    break;
                case USE_RMI:
                    //instance.server = new ServerRMICommunication(); //fixme
                    break;
                default:
                    System.err.println("Invalid input: please enter Socket or RMI");
            }
        }
        while (!(written.equals(USE_SOCKET) || written.equals(USE_RMI)));
    }

    private void handleLoginCLI() throws ServerIsDownException {
        String written;
        Scanner scanner= new Scanner(System.in);
        System.out.println("To log in enter Login, otherwise enter Quit to exit");
        do {
            written = scanner.nextLine();
            written = written.toLowerCase();
            switch (written){
                case LOG_IN_REQUEST:
                    boolean ok=false;
                    instance.server.setUpConnection();
                    System.out.println("Welcome to Sagrada server. Please choose a username:");

                    do {
                        try {
                            String usernameChosen=scanner.nextLine();
                            instance.server.login(usernameChosen);
                            ok=true;
                            this.username=usernameChosen;
                            System.out.println("You successfully logged. You have been inserted in game queue.");
                        } catch (ServerIsFullException e) {
                            System.err.println("Server is now full, retry later.");
                            System.exit(0);
                        } catch (InvalidUsernameException e) {
                            System.err.println("This username already exist or it's invalid. Please choose another one: ");
                        }
                    }
                    while (!ok);
                    break;
                case QUIT_REQUEST:
                    System.out.println("Closing Sagrada...");
                    System.exit(0);
                    break;
                default:
                    System.err.println("Invalid input: please enter Login or Quit");
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
}
