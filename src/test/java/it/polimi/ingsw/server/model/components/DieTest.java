package it.polimi.ingsw.server.model.components;

import it.polimi.ingsw.server.custom_exception.NotValidParameterException;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertThrows;

import static org.junit.Assert.*;


public class DieTest{

//constructor tests
    @Test
    void dieCopy(){
        Die test1 = null, test2 = null;
        try {
            test1 = new Die("yellow", 5);
            test2 = new Die(test1);
        } catch (NotValidParameterException e) {
            fail("Failed initialization.");
        }
        assertEquals(test1.getColor(),test2.getColor());
        assertEquals(test1.getValue(),test2.getValue());
    }

//tests
    @Test
    void dieValue(){
        assertThrows(NotValidParameterException.class, () ->{
            Die test_die = new Die("green",10);
        });
    }
    @Test
    void dieColor() throws NotValidParameterException {
        assertThrows(NotValidParameterException.class, ()-> {
            Die test_die = new Die("gray", 5);
        });
    }

    @Test
    void bothValidParameters() throws NotValidParameterException {

        //Given
        String passedColor= "red";
        int passedValue= 6;

        //When
        Die testDie = new Die(passedColor,passedValue);

        //Assert
        assertEquals(passedValue, testDie.getValue());
        assertEquals(passedColor, testDie.getColor());
    }

    @Test
    void colorNotCapsSensitive() throws NotValidParameterException {

        //Given
        String passedColor= "RED";
        int passedValue= 6;

        //When
        Die testDie = new Die(passedColor,passedValue);

        //Assert
        assertEquals(passedValue, testDie.getValue());
        assertEquals(passedColor.toLowerCase(), testDie.getColor());
        assertNotEquals(passedColor, testDie.getColor());
    }

}