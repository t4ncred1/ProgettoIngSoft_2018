package it.polimi.ingsw;

import it.polimi.ingsw.customException.NotProperParameterException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class BoxTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();
//Tests sui costruttori
    @Test
    public void boxPositions() throws NotProperParameterException{
        thrown.expect(NotProperParameterException.class);
        Box test_Box = new Box(9, 10);
    }
    @Test
    public void boxValue() throws NotProperParameterException{
        thrown.expect(NotProperParameterException.class);
        Box test_box = new Box(9,0,0);
    }
    @Test
    public void boxColor() throws NotProperParameterException{
        thrown.expect(NotProperParameterException.class);
        Box test_box = new Box("ciao",0,0);
    }

//Test sulla funzione update
    @Test
    public void update_Test() throws NotProperParameterException{
        thrown.expect(NotProperParameterException.class);
        Box test_box = new Box(0,0);
        test_box.update(false, new DieToCostraintsAdapter(new Die("Red",4)), 9, 6);
    }
}