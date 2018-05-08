package it.polimi.ingsw.cardContainer;

import it.polimi.ingsw.customException.NotProperParameterException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class PrivateObjectiveTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();
//test
    @Test
    public void checkColor() throws NotProperParameterException{
        thrown.expect(NotProperParameterException.class);
        PrivateObjective privateObjective_test = new PrivateObjective("black");
    }

}