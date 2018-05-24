package it.polimi.ingsw.clientPart;

import it.polimi.ingsw.clientPart.custom_exception.ServerIsDownException;
import it.polimi.ingsw.clientPart.custom_exception.ServerIsFullException;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class ServerSocketCommunication implements ServerCommunicatingInterface {

    private transient Socket socket;
    private transient DataInputStream inputStream;
    private transient DataOutputStream outputStream;

    private static int serverPort = 11000;
    private static String serverAddress="127.0.0.1";

    @Override
    public void login() throws ServerIsFullException, ServerIsDownException {
        Scanner scanner= new Scanner(System.in);
        String read;
        String written;
        try {
            socket= new Socket(serverAddress, serverPort);
            inputStream = new DataInputStream(socket.getInputStream());
            outputStream= new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            outputStream.writeUTF("hello");
            do {
                read = inputStream.readUTF();
            }
            while (!read.equals("login"));
            System.out.println("Welcome to Sagrada server. Please choose a username:");
            do{
                written=scanner.nextLine();
                outputStream.writeUTF(written);
                do{
                    read=inputStream.readUTF();
                }
                while (!(read.equals("logged")||read.equals("notLogged_server_full")||read.equals("notLogged_username_not_available")));
                if(read.equals("notLogged_server_full")){
                    throw new ServerIsFullException();
                }
                if(read.equals("notLogged_username_not_available")){
                    System.err.println("This username already exist or it's invalid. Please choose another one: ");
                }
            }
            while(!read.equals("logged"));
        } catch (IOException e) {
            throw new ServerIsDownException();
        }

    }

    public boolean waitForGame(){
        boolean starting=false;
        String read;
        try {
            if(inputStream.available()>0){
                read= inputStream.readUTF();
                if(read.equals("launching_game")) {
                    System.out.println("A game will start soon...");
                    starting=true;
                }
                if(read.equals("game_started")){
                    System.out.println("Game started");
                    return true;
                }
            }
            if(starting){

                do {
                    read = inputStream.readUTF();
                    if (read.equals("game_started")) {
                        System.out.println("Game started");
                    }
                    else if(read.equals("launching_game")){
                        System.out.println("Someone disconnected. Game timer has been restarted");
                    }
                }while (!read.equals("game_started"));
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean logout(){
        try{
            outputStream.writeUTF("try_logout");
            String response= inputStream.readUTF();
            switch (response) {
                case "logged_out":
                    return true;
                case "launching_game":
                    return false;
                default:
                    System.err.println(response);
            }

        } catch (IOException e) {
            e.printStackTrace();

        }
        return false;
    }
}



