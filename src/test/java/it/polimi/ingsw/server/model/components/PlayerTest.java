package it.polimi.ingsw.server.model.components;

import it.polimi.ingsw.server.model.cards.PrivateObjective;
import it.polimi.ingsw.server.custom_exception.InvalidOperationException;
import it.polimi.ingsw.server.custom_exception.NotValidParameterException;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

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
        int x=0;
        Grid gridtest = new Grid(3,"test00");
        ArrayList<Grid> grids=new ArrayList<>();
        grids.add(gridtest);
        Player test = new Player("test");
        test.setGridsSelection(grids);
        assertThrows(NotValidParameterException.class, () -> {
            test.setGrid(i);
        });

        assertThrows(NotValidParameterException.class, ()->{
            test.setGrid(j);
        });
        test.setGrid(x);
        assertEquals(test.getSelectedGrid(),gridtest);
    }

    @Test
    public void checkSetObj() throws NotValidParameterException {
        Player test=new Player("test");
        PrivateObjective testing=new PrivateObjective("green");
        test.setObjective(testing);
        assertEquals(test.getObjective(),testing);
    }

    @Test
    public void checkHasSelectedGrid() throws NotValidParameterException {
        Player player_to_test=new Player("test");
        boolean bool;
        bool=player_to_test.hasSelectedAGrid();
        assertNotEquals(bool,null);

    }



}