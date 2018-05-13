package it.polimi.ingsw;

import java.io.*;
import java.net.*;
import java.util.Scanner;

public class MainClient {
    private static Socket socket;
    private static DataInputStream inputStream;
    private static DataOutputStream outputStream;
    private static Scanner scanner;

    public static void main(String[] args){
        try{
            System.out.println("Client Starting");
            String letto;
            String scritto;
            SocketHandler.closeConnection();
            socket= new Socket(InetAddress.getLocalHost(), 11000);
            scanner= new Scanner(System.in);
            inputStream = new DataInputStream(socket.getInputStream());
            outputStream= new DataOutputStream(socket.getOutputStream());
            Scanner scanner = new Scanner(System.in);
            BufferedReader tastiera;
            String message= inputStream.readUTF();
            System.out.println(message);
            int trials = 0;
            String result = new String();
            do
            {
                trials++;
                try {
                    if(trials>1) {
                        System.out.println(result);
                    }
                    scritto= scanner.nextLine();
                    outputStream.writeUTF(scritto);
                    System.out.println("inviato: "+ scritto);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                result = inputStream.readUTF();

            }
            while (result.equals("not available"));

            System.out.println(result);
        }
        catch (IOException e){
            e.printStackTrace();
        }

        try {
            String temp =new String("");
            do {
                temp =inputStream.readUTF();
            }
            while(temp.equals(""));
            System.out.println(temp);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

}
