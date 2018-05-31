package it.polimi.ingsw.client;

import it.polimi.ingsw.client.custom_exception.*;
import it.polimi.ingsw.client.net.ServerCommunicatingInterface;
import it.polimi.ingsw.client.net.ServerRMICommunication;
import it.polimi.ingsw.client.net.ServerSocketCommunication;
import it.polimi.ingsw.server.custom_exception.DisconnectionException;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.Scanner;

public class ReworkedClientMain {

    private ServerCommunicatingInterface server;
    private static ReworkedClientMain instance;

    private final String USE_SOCKET = "socket";
    private final String USE_RMI= "rmi";
    private final String LOG_IN_REQUEST = "login";
    private final String QUIT_REQUEST = "quit";
    private final String LOG_OUT_REQUEST = "logout";
    private final String INSERT_DIE= "insert_die";
    private String username;

    public static void main(String[] args){
        instance = new ReworkedClientMain();
        Scanner scanner = new Scanner(System.in);
        String written;
        String read;

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
//            if(written.equals("GUI")) {
//                launch(args);
//            }
//        }
//        while(!(written.equals("GUI")||written.equals("CLI")));

        instance.chooseConnectionSystemCLI();
        try {
            instance.handleLoginCLI();

            instance.handleWaitForGameCLI();

            instance.handleGameInitializationCLI();
            
            instance.handleTurnLogicCLI();
        }
        catch (ServerIsDownException e){
            System.err.println("Can't connect to server, something went wrong!");
        }
    }

    private void handleTurnLogicCLI() throws ServerIsDownException {
        boolean gameFinished=false;

        do{
            try {
                String turnPlayer;
                turnPlayer= getPlayerOfThisTurn();
                server.getUpdatedDicePool();
                if (turnPlayer.equals(this.username)) {
                    handleMyTurnLogic();
                } else {
                    handleOtherPlayerTurnLogic(username);
                }
            } catch (GameFinishedException e) {
                gameFinished=true;
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
                /*TODO notify proxy.*/
            }
        }while (!turnFinished);

    }

    private void handleMyTurnLogic() {
        //TODO logic my turn
        Scanner scanner = new Scanner(System.in);
        String operation;
        boolean turnFinished = false;
        do {
            System.out.println("Choose operation:");
            System.out.println("Possible operations are: " + INSERT_DIE+" - ");
            operation= scanner.nextLine();
            turnFinished=doOperation(operation.toLowerCase());
        } while (!turnFinished);
    }

    private boolean doOperation(String operation) {
        switch (operation){
            case INSERT_DIE:
                System.out.println("Insert: Die position in dice pool");
                /*TODO*/
                break;
            default:
                System.err.println("Invalid operation! Retry.");
        }
        return false;
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
        /*TODO get data from server*/
    }

    private void selectGrid() throws ServerIsDownException {
        Scanner scanner= new Scanner(System.in);
        boolean gridCorrectlyChosen= false;
        try {
            server.getGrids();
            System.out.println("Insert a value from 0 to 3 to chose a grid"); //FIXME get this from proxy
            do{
                String read=scanner.nextLine();
                try{
                    int gridIndex = Integer.parseInt(read);
                    if(gridIndex<0||gridIndex>3) throw new InvalidMoveException();//FIXME get this from proxy
                    server.setGrid(gridIndex);

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
        String read;
        Scanner scanner= new Scanner(System.in);
        DataInputStream dataInputStream = new DataInputStream(System.in);
        System.out.println("Enter Logout to logout or wait for a game to start.");
        try {
            boolean starting = false;
            boolean gameStarted = false;

            while (!gameStarted) {
                if (dataInputStream.available() > 0) {
                    read = scanner.nextLine();
                    read= read.toLowerCase();
                    switch (read){
                        case LOG_OUT_REQUEST:
                            gameStarted=instance.server.logout();
                            if(gameStarted==true) {
                                System.out.println("Logged out correctly");
                                System.exit(0);
                            }
                            else
                                System.err.println("You can't log out, a game is starting");
                            break;
                        default:
                            System.err.println("Invalid input. Enter logout or wait for a game.");
                            break;
                    }
                }
                do{
                    if (dataInputStream.available() > 0) {
                        read = scanner.nextLine();
                        read= read.toLowerCase();
                        switch (read){
                            case LOG_OUT_REQUEST:
                                System.err.println("Can't log out while game is starting");
                                break;
                            default:
                                System.err.println("Invalid input");
                        }


                    }
                    try {
                        instance.server.waitForGame(starting);
                    } catch (GameStartingException e) {
                        System.out.println("A Game is starting soon...");
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
                }while (starting);
            }
        } catch (IOException e) {
            e.printStackTrace();
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


}
