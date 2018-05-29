package it.polimi.ingsw.server;

import it.polimi.ingsw.server.component_container.Die;
import it.polimi.ingsw.server.custom_exception.InvalidOperationException;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;


class MatchModelTest {

    private class MockMatchController extends MatchController {
        void MockMatchController(){

        }

        @Override
        public int askForDieIndex(String username) throws InvalidOperationException {
            return 1;
        }

        @Override
        public int[] askForDieCoordinates(String username) throws InvalidOperationException {
            return new int[]{1,2};
        }

        @Override
        public void sendDicePool(List<Die> dicePool, String username) {
            
        }
    }


    //Constructor tests
    @Test
    void nullControllerConstructorTest(){
        Set<String> strings = new HashSet<>(Arrays.asList("Test String 1", "Test String 2"));

        assertThrows(NullPointerException.class,()-> new MatchModel(strings, null));
    }

    @Test
    void nullPlayerSetConstructorTest(){
        MockMatchController controller = new MockMatchController();

        assertThrows(NullPointerException.class,()-> new MatchModel(null, controller));
    }



}