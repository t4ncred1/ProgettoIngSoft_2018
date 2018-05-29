package it.polimi.ingsw.clientPart;

import it.polimi.ingsw.clientPart.custom_exception.*;

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

        instance.CLIChooseConnectionSystem();
        try {
            instance.CLIHandleLogin();

            instance.CLIHandleWaitForGame();

            instance.CLIHandleGameInitialization();
        }
        catch (ServerIsDownException e){
            System.err.println("Can't connect to server, something went wrong!");
        }
    }

    private void CLIHandleGameInitialization() throws ServerIsDownException {
        server.getGrids();
    }

    private void CLIHandleWaitForGame() throws ServerIsDownException {
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

    private void CLIHandleLogin() throws ServerIsDownException {
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
                            String username=scanner.nextLine();
                            instance.server.login(username);
                            ok=true;
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

    private void CLIChooseConnectionSystem() {
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
