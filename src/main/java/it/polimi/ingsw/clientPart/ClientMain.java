package it.polimi.ingsw.clientPart;


//import javafx.application.Application;

//import javafx.scene.Scene;
//import javafx.scene.layout.GridPane;
//import javafx.stage.Stage;

import java.util.Scanner;

public class ClientMain /*extends Application*/ {

    private static ClientMain instance;
    private ServerInterface server;

    public static void main(String[] args){
        instance = new ClientMain();
        Scanner scanner = new Scanner(System.in);
        String written;
        String read=new String();
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
        trial=0;
        do {
            trial++;
            if(trial>1) System.err.println("Invalid input: please enter Socket or RMI");
            written=scanner.nextLine();
            written=written.toLowerCase();
            if(written.equals("socket")) {
                instance.server = new ServerSocketCommunication();
            }
            else if(written.equals("rmi")){
                instance.server= new ServerRemoteInterfaceAdapter();
            }
        }
        while(!(written.equals("socket")||written.equals("rmi")));

        System.out.println("To log in enter Login, otherwise enter Quit to exit");
        trial=0;
        do {
            trial++;
            if(trial>1) System.err.println("Invalid input: please enter Login or Quit");
            written=scanner.nextLine();
            written=written.toLowerCase();
            if(written.equals("login")) {
                instance.server.login();
            }
            else if(written.equals("quit")){
                System.exit(0);
            }
        }
        while(!(written.equals("login")||written.equals("quit")));
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
