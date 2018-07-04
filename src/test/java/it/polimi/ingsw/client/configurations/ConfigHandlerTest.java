package it.polimi.ingsw.client.configurations;

import it.polimi.ingsw.server.custom_exception.NotValidConfigPathException;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ConfigHandlerTest {

    @Test
    void getInstanceTest(){
        ConfigHandler instance1=null;
        ConfigHandler instance2=null;
        try {
            instance1 = ConfigHandler.getInstance();
            instance2 = ConfigHandler.getInstance();
        } catch (NotValidConfigPathException e) {
            fail("Test Failed.");
        }
        assertEquals(instance1,instance2);
    }
    @Test
    void getServerIpTest(){
        String ip;
        try {
            ip= ConfigHandler.getInstance().getServerIp();
        } catch (NotValidConfigPathException e) {
            fail("Test failed.");
        }
    }

    @Test
    void getSocketPortTest(){
        try {
            ConfigHandler.getInstance().getSocketPort();
        } catch (NotValidConfigPathException e) {
            fail("Test failed.");
        }
    }

    @Test
    void getRmiPortTest(){
        try {
            ConfigHandler.getInstance().getRmiPort();
        } catch (NotValidConfigPathException e) {
            fail("Test failed.");
        }
    }

    @Test
    void getRegisterNameTest(){
        try {
            ConfigHandler.getInstance().getRegisterName();
        } catch (NotValidConfigPathException e) {
            fail("Test failed.");
        }
    }

}