package it.polimi.ingsw.server.components;

import it.polimi.ingsw.server.custom_exception.NotValidParameterException;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertThrows;

import static org.junit.Assert.*;

class DieToConstraintsAdapterTest {
    //tests
    @Test
    public void right_value()throws NotValidParameterException{
        //green case
        Die green_die=new Die("green",5);
        DieToConstraintsAdapter green_adapter = new DieToConstraintsAdapter(green_die);
        //assert
        assertEquals(0,green_adapter.getColorRestriction());

        //blue case
        Die blue_die=new Die("blue",5);
        DieToConstraintsAdapter blue_adapter = new DieToConstraintsAdapter(blue_die);
        //assert
        assertEquals(2,blue_adapter.getColorRestriction());

        //purple case
        Die purple_die=new Die("purple",5);
        DieToConstraintsAdapter purple_adapter = new DieToConstraintsAdapter(purple_die);
        //assert
        assertEquals(4,purple_adapter.getColorRestriction());
        }
    @Test
    public void checkDie() throws NotValidParameterException {

        Die test_die=new Die("green",5);
        DieToConstraintsAdapter test_adapter = new DieToConstraintsAdapter(test_die);
        //assert
        assertEquals(test_die,test_adapter.getDie());

    }

    @Test
    public void modify_test() throws NotValidParameterException {
        new DieToConstraintsAdapter(new Die("green",5)).modifyDie();
    }

}