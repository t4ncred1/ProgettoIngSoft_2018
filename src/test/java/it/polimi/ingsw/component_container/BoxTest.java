package it.polimi.ingsw.component_container;

import it.polimi.ingsw.custom_exception.NotValidParameterException;
import org.junit.jupiter.api.Test;

import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class BoxTest {

//Tests sui costruttori
    @Test
    public void boxPositions(){
        assertThrows(NotValidParameterException.class,
                () ->{
                Box test_Box = new Box(9, 10);
                });
    }
    @Test
    public void boxValue(){
        assertThrows(NotValidParameterException.class,
                () -> {
                    Box test_box = new Box(9, 0, 0);
                });
    }
    @Test
    public void boxColor(){
        assertThrows(NotValidParameterException.class,
                () -> {
                    Box test_box = new Box("ciao",0,0);
                });
    }

//Test sulla funzione update
    @Test
    public void update_Test(){

        assertThrows(NotValidParameterException.class,
                () -> {
                    Box test_box = new Box(0,0);
                    test_box.update(false, new DieToConstraintsAdapter(new Die("Red",4)), 9, 6);
                });

    }

    /*----------------------------------------------------------------------------------------------------*/
    //                                       METODO tryToInsertDie
    /*----------------------------------------------------------------------------------------------------*/
    @Test
    public void boxFilledByADie() throws NotValidParameterException {

        //Given
        Box box= new Box(0,0);
        Die dieInBox= new Die("red",2);
        Die dieToInsert =new Die("red",1);

        //When
        box.insertDie(dieInBox); //inserisco un dado a caso

        //Assert
        assertFalse(box.tryToInsertDie(true,true, dieToInsert));
    }

    @Test
    public void boxNotOpened() throws NotValidParameterException {

        //Given
        Box box= new Box(0,0);
        Die dieToInsert =new Die("red",1);

        //When
        //Box is not opened after construction;
        boolean checkColorConstraints=true;
        boolean checkValueConstraints=true;

        //Assert
        assertFalse(box.tryToInsertDie(checkColorConstraints,checkValueConstraints, dieToInsert));
    }

    @Test
    public void boxOpened() throws NotValidParameterException {

        //Given
        Box box= new Box(0,0);
        Box box2= new Box(1,1);

        //When
        Die dieInBox= new Die("red",2);
        Die dieToInsert =new Die("red",1);
        box.register(box2);
        box.insertDie(dieInBox); //box2 viene notificato, quindi diventa opened. Constraints NON AGGIORNATI perchè siamo in DIAGONALE
        boolean checkColorConstraints=true;
        boolean checkValueConstraints=true;


        //Assert
        assertTrue(box2.tryToInsertDie(checkColorConstraints,checkValueConstraints, dieToInsert));
    }

    @Test
    public void boxOpenedButColorConstraintsBlock() throws NotValidParameterException {

        //Given
        Box box= new Box(0,0);
        Box box2= new Box(0,1);


        //When
        Die dieInBox= new Die("red",2);
        Die dieToInsert =new Die("red",1);
        box.register(box2);
        box.insertDie(dieInBox); //box2 viene notificato, quindi diventa opened. Constraints AGGIORNATI perchè NON siamo in DIAGONALE
        boolean checkColorConstraints=true;
        boolean checkValueConstraints=true;


        //Assert
        assertFalse(box2.tryToInsertDie(checkColorConstraints,checkValueConstraints, dieToInsert));
    }

    @Test
    public void boxOpenedButColorConstraintsDontBlock() throws NotValidParameterException {

        //Same test as before, but unchecked color constraints

        //Given
        Box box= new Box(0,0);
        Box box2= new Box(0,1);


        //When
        Die dieInBox= new Die("red",2);
        Die dieToInsert =new Die("red",1);
        box.register(box2);
        box.insertDie(dieInBox); //box2 viene notificato, quindi diventa opened. Constraints AGGIORNATI perchè NON siamo in DIAGONALE
        boolean checkColorConstraints=false;
        boolean checkValueConstraints=true;


        //Assert
        assertTrue(box2.tryToInsertDie(checkColorConstraints,checkValueConstraints, dieToInsert));
    }

    @Test
    public void boxOpenedButValueConstraintsBlock() throws NotValidParameterException {

        //Given
        Box box= new Box(0,0);
        Box box2= new Box(0,1);


        //When
        Die dieInBox= new Die("red",2);
        Die dieToInsert =new Die("yellow",2);
        box.register(box2);
        box.insertDie(dieInBox); //box2 viene notificato, quindi diventa opened. Constraints AGGIORNATI perchè NON siamo in DIAGONALE
        boolean checkColorConstraints=true;
        boolean checkValueConstraints=true;


        //Assert
        assertFalse(box2.tryToInsertDie(checkColorConstraints,checkValueConstraints, dieToInsert));
    }

    @Test
    public void boxOpenedButValueConstraintsDontBlock() throws NotValidParameterException {

        //Same test as before, but unchecked value constraints

        //Given
        Box box= new Box(0,0);
        Box box2= new Box(0,1);


        //When
        Die dieInBox= new Die("red",2);
        Die dieToInsert =new Die("yellow",2);
        box.register(box2);
        box.insertDie(dieInBox); //box2 viene notificato, quindi diventa opened. Constraints AGGIORNATI perchè NON siamo in DIAGONALE
        boolean checkColorConstraints=true;
        boolean checkValueConstraints=false;


        //Assert
        assertTrue(box2.tryToInsertDie(checkColorConstraints,checkValueConstraints, dieToInsert));
    }

    /*----------------------------------------------------------------------------------------------------*/
    //                                       METODO checkPrivatePoints
    /*----------------------------------------------------------------------------------------------------*/

    @Test
    public void equalsColor() throws NotValidParameterException {

        //Given
        String dieColor = "red";
        int dieValue =3;
        Box box= new Box(0,0);
        Die dieToInsert =new Die(dieColor,dieValue);

        //When
        box.insertDie(dieToInsert);
        String colorToCheck = "red";

        //Assert
        assertEquals(dieValue, box.checkPrivatePoints(colorToCheck));
    }

    @Test
    public void differentColor() throws NotValidParameterException {

        //Given
        String dieColor = "red";
        int dieValue =3;
        Box box= new Box(0,0);
        Die dieToInsert =new Die(dieColor,dieValue);

        //When
        box.insertDie(dieToInsert);
        String colorToCheck = "yellow";

        //Assert
        assertNotEquals(dieValue, box.checkPrivatePoints(colorToCheck));
        assertEquals(0, box.checkPrivatePoints(colorToCheck));
    }

}