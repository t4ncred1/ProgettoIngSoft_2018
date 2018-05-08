package it.polimi.ingsw;

import it.polimi.ingsw.customException.NotInPoolException;
import it.polimi.ingsw.customException.NotProperParameterException;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class DicePoolTest {

//test for generateDiceForPull
    @Test
    public void checkPoolSize(){
        DicePool test_dicepool = new DicePool();
        assertThrows(NotProperParameterException.class,
                () -> test_dicepool.generateDiceForPull(1));
    }
//test for getDieFromPool
    @Test
    public void checkDieToRemove(){
        DicePool test_dicepool = new DicePool();
        assertThrows(NotInPoolException.class,
                () -> {
                    test_dicepool.generateDiceForPull(5);
                    test_dicepool.getDieFromPool(6);
                });


    }


}