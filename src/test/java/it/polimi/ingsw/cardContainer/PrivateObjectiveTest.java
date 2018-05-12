package it.polimi.ingsw.cardContainer;
import it.polimi.ingsw.customException.NotValidParameterException;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class PrivateObjectiveTest {
//test
    @Test
    public void checkColor() throws NotValidParameterException {
        assertThrows(NotValidParameterException.class, ()-> {
            PrivateObjective privateObjective_test = new PrivateObjective("black");
        });
    }

}