package it.polimi.ingsw;

import it.polimi.ingsw.customException.NotInPoolException;
import it.polimi.ingsw.customException.NotProperParameterException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class DicePoolTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();
//test for generateDiceForPull
    @Test
    public void checkPoolSize() throws NotProperParameterException{
        thrown.expect(NotProperParameterException.class);
        DicePool test_dicepool = new DicePool();
        test_dicepool.generateDiceForPull(1);
    }
//test for getDieFromPool
    @Test
    public void checkDieToRemove()throws NotInPoolException, NotProperParameterException {
        thrown.expect(NotInPoolException.class);
        DicePool test_dicepool = new DicePool();
        test_dicepool.generateDiceForPull(5);
        test_dicepool.getDieFromPool(6);

    }


}