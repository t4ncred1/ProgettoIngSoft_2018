package it.polimi.ingsw.server.component_container;

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
        assertThrows(NotValidParameterException.class, () -> {
            test.setGrid(i);
        });

        assertThrows(NotValidParameterException.class, ()->{
            test.setGrid(j);
        });
        assertThrows(InvalidOperationException.class, ()->{
            test.setGrid(x);
        });


    }


}