package it.polimi.ingsw;

import org.junit.jupiter.api.Test;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

public class MatchHandlerTest {

    MatchHandler matchHandler;
    SocketHandler socketHandler;
    Socket socket;
    DataOutputStream outputStream;
    DataInputStream inputStream;

    public MatchHandlerTest() throws IOException {
        matchHandler= MatchHandler.getInstance();
        socketHandler= SocketHandler.getInstance();
    }


    @Test
    public void connectionAccepted() throws IOException, InterruptedException {
        //Given
        new MatchHandlerTest();
        matchHandler.start();
        socketHandler.start();
        Thread.sleep(2000);
        socket= new Socket(InetAddress.getLocalHost(), 11000);
        this.inputStream = new DataInputStream(socket.getInputStream());
        this.outputStream= new DataOutputStream(socket.getOutputStream());

        //When
        String username = new String("PlayerName1");
        System.out.println(inputStream.readUTF());
        outputStream.writeUTF(username);
        System.out.println("Sent: " + username);
        String result = inputStream.readUTF();
        System.out.println(result);

        //Assert
        assertEquals("Connected", result);
        assertEquals(1, matchHandler.connectedPlayers());

    }

//    @Test
//    public void usernameAlreadyExist() throws IOException, InterruptedException {
//
//        //Given
//        new MatchHandlerTest();
//        matchHandler.start();
//        socketHandler.start();
//        Thread.sleep(2000);
//        //Connection from client 1 with username1= PlayerName1
//        socket= new Socket(InetAddress.getLocalHost(), 11000);
//        this.inputStream = new DataInputStream(socket.getInputStream());
//        this.outputStream= new DataOutputStream(socket.getOutputStream());
//        String username1 = new String("PlayerName1");
//        outputStream.writeUTF(username1);
//        inputStream.readUTF();
//        //Connection from client 2
//        Socket socket2= new Socket(InetAddress.getLocalHost(), 11000);
//        this.inputStream = new DataInputStream(socket2.getInputStream());
//        this.outputStream= new DataOutputStream(socket2.getOutputStream());
//
//
//        //When username2= username1
//        String username2 = username1;
//        System.out.println(inputStream.readUTF());
//        outputStream.writeUTF(username1);
//        System.out.println("Sent: " + username2);
//        String result = inputStream.readUTF();
//        System.out.println(result);
//
//        //Assert
//        assertEquals("Not available, choose another username:", result);
//        assertEquals(1, matchHandler.notSincronizedConnectedPlayers());
//
//
//    }
//
//    @Test
//    public void disconnectionInLoginPhase() throws InterruptedException, IOException {
//        //Given
//        new MatchHandlerTest();
//        matchHandler.start();
//        socketHandler.start();
//        Thread.sleep(2000);
//        socket= new Socket(InetAddress.getLocalHost(), 11000);
//        this.inputStream = new DataInputStream(socket.getInputStream());
//        this.outputStream= new DataOutputStream(socket.getOutputStream());
//        System.out.println(inputStream.readUTF());
//
//        //When
//        socket.close();
//
//        //Assert
//        assertEquals(0, matchHandler.connectedPlayers());
//    }
//
//    @Test
//    public void serverFullAndUsernameIsNotInDisconnectedPlayers() throws IOException, InterruptedException {
//        //Given
//        new MatchHandlerTest();
//        matchHandler.start();
//        socketHandler.start();
//        Thread.sleep(2000);
//        ArrayList<Socket> connections = new ArrayList<Socket>();
//        final int maximumMatchNumber = matchHandler.getMaximumMatchNumber();
//        final int playerForMatch = 4;
//        //Connection from client 1 to N-1 with username1= PlayerName1,
//        for(int i=1; i<=maximumMatchNumber*playerForMatch; i++){
//            socket= new Socket(InetAddress.getLocalHost(), 11000);
//            this.inputStream = new DataInputStream(socket.getInputStream());
//            this.outputStream= new DataOutputStream(socket.getOutputStream());
//            String usernameX = new String("PlayerName"+i);
//            outputStream.writeUTF(usernameX);
//            inputStream.readUTF();
//            connections.add(socket);
//        }
//        //Connection number N: server is full and connected username isn't in disconnected players list
//        Socket socketN= new Socket(InetAddress.getLocalHost(), 11000);
//        this.inputStream = new DataInputStream(socketN.getInputStream());
//        this.outputStream= new DataOutputStream(socketN.getOutputStream());
//
//        //When
//        String usernameN = new String("PlayerName"+(maximumMatchNumber*playerForMatch+1)); //different from username 1,2,3,4
//        System.out.println(inputStream.readUTF());
//        outputStream.writeUTF(usernameN);
//        System.out.println("Sent: " + usernameN);
//        String result = inputStream.readUTF();
//        System.out.println(result);
//
//
//        assertEquals("Server is full, can't connect!", result);
//        assertEquals(maximumMatchNumber*playerForMatch, matchHandler.connectedPlayers());
//    }
}
