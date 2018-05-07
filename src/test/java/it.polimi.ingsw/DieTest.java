package it.polimi.ingsw;

import it.polimi.ingsw.CustomException.NotProperParameterException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


public class DieTest{

    @Rule
    public ExpectedException thrown = ExpectedException.none();
//tests
    @Test
    public void dieValue() throws NotProperParameterException{
        thrown.expect(NotProperParameterException.class);
        Die test_die = new Die("green",10);
    }
    @Test
    public void dieColor() throws NotProperParameterException{
        thrown.expect(NotProperParameterException.class);
        Die test_die = new Die("gray",5);
    }

}