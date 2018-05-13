package it.polimi.ingsw;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.*;
import java.net.Socket;

import static org.junit.jupiter.api.Assertions.assertThrows;


public class SocketHandlerTest {

    @Test
    public void connectionClosed() throws IOException {

        //Given
        SocketHandler handler= SocketHandler.getInstance();

        SocketHandler.closeConnection();
    }
}
