package it.polimi.ingsw;

import it.polimi.ingsw.customException.NotProperParameterException;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertThrows;

import static org.junit.Assert.*;


public class DieTest{

//tests
    @Test
    public void dieValue(){
        assertThrows(NotProperParameterException.class, () ->{
            Die test_die = new Die("green",10);
        });
    }
    @Test
    public void dieColor() throws NotProperParameterException{
        assertThrows(NotProperParameterException.class, ()-> {
            Die test_die = new Die("gray", 5);
        });
    }

    @Test
    public void bothValidParameters() throws NotProperParameterException {

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
    public void colorNotCapsSensitive() throws NotProperParameterException {

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