package it.polimi.ingsw;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {

        MatchHandler.getInstance().start();
        SocketHandler.getInstance().start();
        Thread.yield();
    }
}
