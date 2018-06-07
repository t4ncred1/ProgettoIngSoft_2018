package it.polimi.ingsw.server.model.components;

import it.polimi.ingsw.server.custom_exception.NotInPoolException;
import it.polimi.ingsw.server.custom_exception.NotValidParameterException;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class DicePoolTest {

//test for generateDiceForPull
    @Test
    public void checkPoolSize(){
        DicePool test_dicepool = new DicePool();
        assertThrows(NotValidParameterException.class,
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
    @Test
    public void checkGetDieFromPool() throws NotValidParameterException, NotInPoolException {
        DicePool test=new DicePool();
        test.generateDiceForPull(3);
        test.getDieFromPool(1);
        assertThrows(NotInPoolException.class,()->test.getDieFromPool(-1));
        assertThrows(NotInPoolException.class,()->test.getDieFromPool(4));
    }

    @Test
    public void checkRemoveDieFromPool() throws NotInPoolException, NotValidParameterException {
        DicePool test=new DicePool();
        test.generateDiceForPull(3);
        test.showDiceInPool();
        test.removeDieFromPool(2);
        test.removeDieFromPool(1);
        test.removeDieFromPool(0);
        assertThrows(NotInPoolException.class,()-> test.removeDieFromPool(0));

    }

    @Test
    public void checkInsertDieInPool() throws NotValidParameterException, NotInPoolException {
        DicePool test=new DicePool();
        Die testdie=null;
        Die test1=new Die("green",1);
        int index=0;
        assertThrows(NotValidParameterException.class,()->test.insertDieInPool(testdie,index));
        int indx=2;
        assertThrows(NotValidParameterException.class,()->test.insertDieInPool(test1,indx));
        test.insertDieInPool(test1,index);
        assertEquals(test1,test.getDieFromPool(index));
    }

}