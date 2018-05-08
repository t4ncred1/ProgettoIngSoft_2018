package it.polimi.ingsw.cardContainer;
import it.polimi.ingsw.customException.NotProperParameterException;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class PrivateObjectiveTest {
//test
    @Test
    public void checkColor() throws NotProperParameterException{
        assertThrows(NotProperParameterException.class, ()-> {
            PrivateObjective privateObjective_test = new PrivateObjective("black");
        });
    }

}