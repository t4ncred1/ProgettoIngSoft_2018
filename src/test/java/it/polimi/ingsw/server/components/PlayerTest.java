package it.polimi.ingsw.server.components;

import it.polimi.ingsw.server.custom_exception.InvalidOperationException;
import it.polimi.ingsw.server.custom_exception.NotValidParameterException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PlayerTest {

    @Test
    public void checkCreator() {
        Player test = new Player("test");

        assertEquals(null, test.getGridsSelection());
        assertEquals(null, test.getSelectedGrid());
        assertEquals(null, test.getObjective());
        assertEquals("test", test.getUsername());
    }

    @Test
    public void checkSetGrid() throws NotValidParameterException, InvalidOperationException {
        int i = -1;
        int j=4;
        int x=2;
        Player test = new Player("test");
        assertThrows(InvalidOperationException.class, () -> {
            test.setGrid(i);
        });

        assertThrows(InvalidOperationException.class, ()->{
            test.setGrid(j);
        });
        assertThrows(InvalidOperationException.class, ()->{
            test.setGrid(x);
        });


    }


}