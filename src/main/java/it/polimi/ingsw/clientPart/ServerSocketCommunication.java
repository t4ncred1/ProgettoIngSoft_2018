package it.polimi.ingsw.clientPart;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;

public class ServerSocketCommunication implements ServerInterface{

    private Socket socket;
    private DataInputStream inputStream;
    private DataOutputStream outputStream;

    private static int serverPort = 11000;
    private static String serverAddress="127.0.0.1";

    @Override
    public void login() {
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
            outputStream.writeUTF("Hello");
            do {
                read = inputStream.readUTF();
            }
            while (!read.equals("login"));
            System.out.println("Welcome to Sagrada server. Please choose a username");
            do{
                written=scanner.nextLine();
                outputStream.writeUTF(written);
                do{
                    read=inputStream.readUTF();
                }
                while (!(read.equals("logged")||read.equals("notLogged_server_full")||read.equals("notLogged_username_not_available")));
                if(read.equals("notLogged_server_full")){
                    System.err.println("Server is now full, retry later.");
                    System.exit(0);
                }
                if(read.equals("notLogged_username_not_available")){
                    System.err.println("This username already exist or it's invalid. Please choose another one:");
                }
            }
            while(!read.equals("logged"));
            System.out.println("You successfully logged.");
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
