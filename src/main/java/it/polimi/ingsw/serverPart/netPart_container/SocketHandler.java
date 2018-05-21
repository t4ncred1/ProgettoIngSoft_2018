package it.polimi.ingsw.serverPart.netPart_container;

import java.io.IOException;
import java.net.*;

public class SocketHandler extends Thread{


    private static SocketHandler instance;

    private static boolean connectionOpened;
    private ServerSocket serverSock;
    private static final int port =11000;

    private SocketHandler(){
        try {
            serverSock=new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
        }
        connectionOpened=true;
    }

    public static SocketHandler getInstance(){
        if(instance==null){
            instance= new SocketHandler();
        }
        return instance;
    }

    @Override
    public void run(){
        System.out.println("SocketHandlerStarted");
        while(true){
            synchronized (this){
            if(connectionOpened){
                try {
                    Socket client=serverSock.accept();
                    new SocketUserAgent(client).start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            }
        }
    }

    public static synchronized void openConnection(){
        connectionOpened=true;
    }

    public static synchronized void closeConnection(){
        connectionOpened=false;
    }
}
