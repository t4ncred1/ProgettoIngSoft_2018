package it.polimi.ingsw.clientPart;


//import javafx.application.Application;

//import javafx.scene.Scene;
//import javafx.scene.layout.GridPane;
//import javafx.stage.Stage;


import java.io.DataInputStream;
import java.io.IOException;
import java.util.Scanner;



public class ClientMain /*extends Application*/ {

    private static ClientMain instance;
    private ServerCommunicatingInterface server;

    public static void main(String[] args) {
        instance = new ClientMain();
        Scanner scanner = new Scanner(System.in);
        String written;
        String read = new String();
        int trial;
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
        System.out.println("Choose between Socket or RMI to connect to server: ");
        trial = 0;
        do {
            trial++;
            if (trial > 1) System.err.println("Invalid input: please enter Socket or RMI");
            written = scanner.nextLine();
            written = written.toLowerCase();
            if (written.equals("socket")) {
                instance.server = new ServerSocketCommunication();
            } else if (written.equals("rmi")) {
                instance.server = new ServerRMICommunication();
            }
        }
        while (!(written.equals("socket") || written.equals("rmi")));

        System.out.println("To log in enter Login, otherwise enter Quit to exit");
        trial = 0;
        do {
            trial++;
            if (trial > 1) System.err.println("Invalid input: please enter Login or Quit");
            written = scanner.nextLine();
            written = written.toLowerCase();
            if (written.equals("login")) {
                instance.server.login();
            } else if (written.equals("quit")) {
                System.exit(0);
            }
        }
        while (!(written.equals("login") || written.equals("quit")));

        boolean ok = false;
        DataInputStream dataInputStream = new DataInputStream(System.in);
        System.out.println("Enter Logout to logout or wait for a game to start.");
        try {
            while (!ok) {
                read = new String("");
                if (dataInputStream.available() > 0) {
                    read = scanner.nextLine();
                    read= read.toLowerCase();
                }
                if (read.equals("logout")) {
                    ok=instance.server.logout();
                    if(ok==true) {
                        System.out.println("Logged out correctly");
                        System.exit(0);
                    }
                    else
                        System.err.println("You can't log out, a game is starting");
                }
                else if(read.equals("")) {
                    ok = instance.server.waitForGame();
                }
                else
                    System.err.println("Invalid input. Enter logout or wait for a game.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


//    @Override
//    public void start(Stage primaryStage) throws Exception {
//        Stage window= primaryStage;
//        GridPane gridPane = new GridPane();
//        Scene toShow = new Scene(gridPane,400,300);
//        window.setTitle("Sagrada");
//        window.setScene(toShow);
//        window.show();
//    }
}
