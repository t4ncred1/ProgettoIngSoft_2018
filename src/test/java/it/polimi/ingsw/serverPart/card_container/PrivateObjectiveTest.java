package it.polimi.ingsw.serverPart.card_container;
import it.polimi.ingsw.serverPart.custom_exception.NotValidParameterException;

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