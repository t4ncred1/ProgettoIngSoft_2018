package it.polimi.ingsw.server.net;

import it.polimi.ingsw.client.configurations.ConfigHandler;
import it.polimi.ingsw.server.configurations.ConfigurationHandler;
import it.polimi.ingsw.server.custom_exception.NotValidConfigPathException;

import javax.security.auth.login.Configuration;
import java.io.IOException;
import java.net.*;

public class SocketHandler extends Thread{


    private static SocketHandler instance;

    private static boolean shutdown;
    private ServerSocket serverSock;
    private int port =11000;

    private SocketHandler(){
        try {
            port=ConfigurationHandler.getInstance().getSocketPort();
        } catch (NotValidConfigPathException e) {
            System.err.println("Configuration file is corrupted. Using defaults.");
        }
        try {
            serverSock=new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
        }
        shutdown=false;
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
        while(!shutdown){
            synchronized (this){
                try {
                    Socket client=serverSock.accept();
                    new SocketUserAgent(client).start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        try {
            serverSock.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("SocketHandlerShutDown");
    }

    public void shutdown(){
        shutdown=true;
        instance= null;
    }
}
