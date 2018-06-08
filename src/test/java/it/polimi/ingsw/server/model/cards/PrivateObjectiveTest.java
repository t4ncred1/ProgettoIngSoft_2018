package it.polimi.ingsw.server.model.cards;
import it.polimi.ingsw.server.model.components.Die;
import it.polimi.ingsw.server.model.components.Grid;
import it.polimi.ingsw.server.custom_exception.InvalidOperationException;
import it.polimi.ingsw.server.custom_exception.NotValidParameterException;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

public class PrivateObjectiveTest {
//test
    @Test
    public void checkColor() throws NotValidParameterException {
        assertThrows(NotValidParameterException.class, ()-> {
            PrivateObjective privateObjective_test = new PrivateObjective("black");
        });
    }

    @Test
    public void checkType() throws NotValidParameterException {
        PrivateObjective priv_obj = new PrivateObjective("green");
        assertEquals("private", priv_obj.getType());
    }

    @Test
    public void checkShowPrivate() throws NotValidParameterException {
        PrivateObjective private_test = new PrivateObjective("blue");
        assertEquals("blue",private_test.showPrivateObjective());
    }

    @Test
    public void checkCalculate()throws NotValidParameterException, InvalidOperationException {
        Grid toTest=null;
        int i,j;
        PrivateObjective test_card= new PrivateObjective("green");
        try{
            toTest= new Grid(4,"toTest");
            for(i=0;i<toTest.getColumnNumber();i++){
                for(j=0;j<toTest.getRowNumber();j++){
                    toTest.createBoxInXY(i,j,"none");
                }
            }
            toTest.initializeAllObservers();
            toTest.insertDieInXY(1,0,true,true, new Die("green",2));
            toTest.insertDieInXY(2,1,true,true, new Die("green",5));

        }catch (NotValidParameterException | InvalidOperationException e){
            fail("test failed");
        }
        assertEquals(7,test_card.calculatePoints(toTest));

    }

}