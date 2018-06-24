package it.polimi.ingsw.client;

import it.polimi.ingsw.client.configurations.GridInterface;
import it.polimi.ingsw.client.custom_exception.*;
import it.polimi.ingsw.client.net.ServerCommunicatingInterface;
import it.polimi.ingsw.client.net.ServerRMICommunication;
import it.polimi.ingsw.client.net.ServerSocketCommunication;
import it.polimi.ingsw.server.model.components.Grid;
import it.polimi.ingsw.server.custom_exception.DisconnectionException;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class ReworkedClientMain {

    private ServerCommunicatingInterface server;
    private static ReworkedClientMain instance;

    private static final String USE_SOCKET = "socket";
    private static final String USE_RMI= "rmi";
    private static final String LOG_IN_REQUEST = "login";
    private static final String QUIT_REQUEST = "quit";
    private static final String LOG_OUT_REQUEST = "logout";
    private static final String INSERT_DIE= "insert_die";
    private static final String END_TURN="end_turn";
    private String username;

    public static void main(String[] args){
        instance = new ReworkedClientMain();
        //don't use caps. All inputs are reduced to lowercase with .toLowerCase



        //TODO set java 9 to compile this part
//        System.out.println("Sagrada is starting...");
//        System.out.println("Enter CLI if you want to use command line interface, otherwise enter GUI.");
//        trial=0;
//        do {
//            trial++;
//            if(trial>1) System.err.println("Invalid input, please enter GUI or CLI");
//            written=scanner.nextLine();
//            written=written.toUpperCase();
//            if(written.equalAgent("GUI")) {
//                launch(args);
//            }
//        }
//        while(!(written.equalAgent("GUI")||written.equalAgent("CLI")));

        instance.chooseConnectionSystemCLI();
        try {
            instance.handleLoginCLI();

            instance.handleWaitForGameCLI();

            instance.handleGameInitializationCLI();
            
            instance.handleTurnLogicCLI();
        }
        catch (ServerIsDownException e){
            System.err.println("Can't connect to server, something went wrong!");
            e.printStackTrace();    //fixme to be removed. only here to show problems.
        }
    }

    private void handleTurnLogicCLI() throws ServerIsDownException {
        boolean gameFinished=false;

        do{
            try {
                String turnPlayer;
                turnPlayer= getPlayerOfThisTurn();
                // TODO: 05/06/2018  
//                server.getToolCards();
//                server.getRoundTrack();
                server.getUpdatedDicePool();
                if (turnPlayer.equals(this.username)) {
                    handleMyTurnLogic();
                } else {
                    handleOtherPlayerTurnLogic(turnPlayer);
                }
            } catch (GameFinishedException e) {
                gameFinished=true;
                System.out.println("Game finished");
            }
        }while (!gameFinished);
    }

    private void handleOtherPlayerTurnLogic(String username) throws ServerIsDownException {
        boolean turnFinished= false;
        do {
            try {
                server.listen(username);
            } catch (TurnFinishedException e) {
                turnFinished = true;
            } catch (DisconnectionException e) {
                turnFinished=true;
                System.out.println(username + " disconnected.");
            }
        }while (!turnFinished);

    }

    private void handleMyTurnLogic() throws ServerIsDownException {
        //TODO logic my turn
        Scanner scanner = new Scanner(System.in);
        String operation;
        boolean turnFinished = false;
        do {
            System.out.println("Choose operation:");
            System.out.println("Possible operations are: " + INSERT_DIE+" - "+ END_TURN);
            operation= scanner.nextLine();
            turnFinished=doOperation(operation.toLowerCase());
        } while (!turnFinished);
    }

    private boolean doOperation(String operation) throws ServerIsDownException {
        switch (operation){
            case INSERT_DIE:
                try {
                    insertDieOperation();
                }catch (InvalidMoveException e){
                    System.err.println("Invalid parameters!");
                }
                break;
            case END_TURN:
                server.endTurn();
                return true;
            default:
                System.err.println("Invalid operation! Retry.");
        }
        return false;
    }

    private void insertDieOperation() throws InvalidMoveException, ServerIsDownException {
        System.out.println("Insert: Die position in dice pool");
        Scanner scanner= new Scanner(System.in);
        int position = scanner.nextInt();
        System.out.println("Insert row:");
        int x = scanner.nextInt();
        System.out.println("Insert column");
        int y = scanner.nextInt();

        //Proxy.getInstance().tryToInsertDieInXY(position,x,y);
        server.insertDie(position,x,y);
    }

    private String getPlayerOfThisTurn() throws ServerIsDownException, GameFinishedException {
        boolean print=true;
        String turnPlayer=null;
        do {
            try {
                turnPlayer = server.askTurn();
                if (turnPlayer.equals(username))
                    System.out.println("It's your turn");
                else
                    System.out.println("It's " + turnPlayer + " turn");
            } catch (ServerNotReadyException e) {
                if (print) {
                    System.err.println("Waiting for others players");
                    print = false;
                }
            }
        }
        while (turnPlayer == null);
        return turnPlayer;
    }

    private void handleGameInitializationCLI() throws ServerIsDownException {
        selectGrid();
        server.getSelectedGrid();
        System.err.println("here.");
//        server.getPrivateObjective();
//        server.getPublicObjectives();
//        server.getOtherPlayersGrids();
        /*TODO get data from server*/
    }

    private void selectGrid() throws ServerIsDownException {
        Scanner scanner= new Scanner(System.in);
        boolean gridCorrectlyChosen= false;
        final int INDEX_SHIFT=1;
        try {
            server.getGrids();
            ArrayList<GridInterface> toPrint = (ArrayList<GridInterface>) Proxy.getInstance().getGridsSelection();
            for(GridInterface grid: toPrint){
                System.out.println(grid.getGridInterface());
            }
            System.out.println("Insert a value from 1 to "+Proxy.getInstance().getGridsSelectionDimension() +" to chose a grid");
            do{
                String read=scanner.nextLine();
                try{
                    int gridIndex = Integer.parseInt(read);
                    if(gridIndex<INDEX_SHIFT||gridIndex>Proxy.getInstance().getGridsSelectionDimension()) throw new InvalidMoveException();
                    server.setGrid(gridIndex-INDEX_SHIFT);

                    System.out.println("Grid correctly chosen.");
                    gridCorrectlyChosen=true;
                }
                catch (InvalidMoveException e){
                    System.err.println("Invalid index. Please insert a valid one.");
                }
                catch (NumberFormatException e){
                    System.err.println("Please insert a number");
                }
            }
            while (!gridCorrectlyChosen);
        } catch (GameInProgressException e) {
            System.out.println("You already selected a grid.");
        }

    }

    private void handleWaitForGameCLI() throws ServerIsDownException {

        System.out.println("Enter Logout to logout or wait for a game to start.");
        boolean starting = false;
        boolean gameStarted = false;


        while (!gameStarted) {
            handleEventualLogoutRequest(starting);
            try {
                instance.server.waitForGame(starting);
            } catch (GameStartingException e) {
                System.out.println("A Game will start soon...");
                starting=true;
            } catch (GameStartedException e) {
                System.out.println("Game is started.");
                starting=false;
                gameStarted=true;
            } catch (TimerRestartedException e) {
                System.err.println("Someone disconnected. Timer has been restarted.");
            } catch (GameInProgressException e) {
                System.out.println("Reconnected successfully.");
                starting=false;
                gameStarted=true;
            }
        }

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

    private void chooseConnectionSystemCLI() {
        Scanner scanner = new Scanner(System.in);
        String written;
        System.out.println("Choose between Socket or RMI to connect to server: ");
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
                    System.err.println("Invalid input: please enter Socket or RMI");
            }
        }
        while (!(written.equals(USE_SOCKET) || written.equals(USE_RMI)));
    }

    private void handleEventualLogoutRequest(boolean starting) throws ServerIsDownException {
        String read;
        Scanner scanner= new Scanner(System.in);
        DataInputStream dataInputStream = new DataInputStream(System.in);
        try {
            if (dataInputStream.available() > 0) {
                read = scanner.nextLine();
                read= read.toLowerCase();
                handleEventualInput(read, starting);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleEventualInput(String read, boolean starting) throws ServerIsDownException {
        try {
            if (LOG_OUT_REQUEST.equals(read) && !starting) {
                instance.server.logout();
                System.out.println("Logged out correctly");
                System.exit(0);
            } else if (LOG_OUT_REQUEST.equals(read)) {
                throw new GameStartingException();
            } else {
                System.err.println("Invalid input.");

            }
        } catch (GameStartingException e){
            System.err.println("You can't logout while the game is starting");
        }
    }


}
