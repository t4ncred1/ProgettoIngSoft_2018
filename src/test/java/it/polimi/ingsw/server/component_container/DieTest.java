package it.polimi.ingsw.server.component_container;

import it.polimi.ingsw.server.custom_exception.NotValidParameterException;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertThrows;

import static org.junit.Assert.*;


public class DieTest{

//tests
    @Test
    public void dieValue(){
        assertThrows(NotValidParameterException.class, () ->{
            Die test_die = new Die("green",10);
        });
    }
    @Test
    public void dieColor() throws NotValidParameterException {
        assertThrows(NotValidParameterException.class, ()-> {
            Die test_die = new Die("gray", 5);
        });
    }

    @Test
    public void bothValidParameters() throws NotValidParameterException {

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
    public void colorNotCapsSensitive() throws NotValidParameterException {

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