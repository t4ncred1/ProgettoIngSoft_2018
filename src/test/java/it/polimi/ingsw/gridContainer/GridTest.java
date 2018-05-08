package it.polimi.ingsw.gridContainer;

import it.polimi.ingsw.Die;
import it.polimi.ingsw.customException.InvalidOperationException;
import it.polimi.ingsw.customException.NotProperParameterException;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.*;

public class GridTest {

    /*----------------------------------------------------------------------------------------------------*/

    //                                       Test sui costruttori

    /*----------------------------------------------------------------------------------------------------*/

    /*-**************************************************************************************************-*/
    //                                  Grid (int Difficulty, String Name)
    /*-**************************************************************************************************-*/


    @Test
    public void nullStringPassed() {
        //Given
        String passedName=null;
        int passedDifficulty=3;

        //Assert
        assertThrows(NullPointerException.class, () -> {
            Grid testGrid = new Grid(passedDifficulty, passedName);
        });

    }

    @Test
    public void invalidDifficultyPassed() {

        //Given
        String passedName="nomeGrid";
        int passedDifficulty=1;

        //Assert
        Throwable exception = assertThrows(NotProperParameterException.class, () -> {
            Grid testGrid = new Grid(passedDifficulty, passedName);
        });
        assertEquals("Parameter: 1. Expected: Difficulty should have a value between 3 and 6 (both included)",exception.getMessage());
    }

    @Test
    public void validParameterPassed() throws NotProperParameterException{
        //Given
        String passedName="nomeGrid";
        int passedDifficulty=3;

        //When
        Grid testGrid = new Grid(passedDifficulty, passedName);

        //Assert
        assertEquals(passedDifficulty,testGrid.getDifficulty());
        assertEquals(passedName,testGrid.getName());
    }



    /*----------------------------------------------------------------------------------------------------*/

    //                                       Test sui metodi

    /*----------------------------------------------------------------------------------------------------*/

    /*-**************************************************************************************************-*/
    //                         createBoxInXY(int x, int y, String constraint)
    /*-**************************************************************************************************-*/

    @Test
    public void nullConstraintPassed() throws NotProperParameterException {

        //Given
        String passedConstraint= null;
        int passedCoordinateX= 0;
        int passedCoordinateY= 0;
        Grid toTest = new Grid(3, "name");

        //Assert
        assertThrows(NullPointerException.class, () -> toTest.createBoxInXY(passedCoordinateX,passedCoordinateY,passedConstraint));
    }

    @Test
    public void createIndexesOutOfBound() throws NotProperParameterException {

        //Given
        String passedConstraint = "red";
        int passedCoordinateX= 10;
        int passedCoordinateY= 10;
        Grid toTest=new Grid(3, "name");

        //Assert
        Throwable exception = assertThrows(NotProperParameterException.class, () -> toTest.createBoxInXY(passedCoordinateX,passedCoordinateY,passedConstraint));
        assertEquals("Parameter: (10,10). Expected: coordinates should be: 0<=x<=3 and 0<=y<=4", exception.getMessage());

    }

    @Test
    public void boxAlreadyCreated() throws NotProperParameterException {

        //Given
        String passedConstraint = "red";
        int passedCoordinateX= 0;
        int passedCoordinateY= 0;
        Grid toTest= new Grid(3, "name");
        toTest.createBoxInXY(passedCoordinateX,passedCoordinateY,passedConstraint); //box filled, can't create another one here

        //Assert
        Throwable exception = assertThrows(NotProperParameterException.class, ()-> toTest.createBoxInXY(passedCoordinateX,passedCoordinateY,passedConstraint));
        assertEquals("Parameter: (0,0). Expected: other coordinates: in this place already exist a Box!", exception.getMessage());
    }

    @Test
    public void invalidStringPassed() throws NotProperParameterException { //invalid color and invalid string are the same case


        //Given
        String passedConstraint = "invalidString";
        int passedCoordinateX= 0;
        int passedCoordinateY= 0;
        Grid toTest = new Grid(3, "name");

        //Assert
        Throwable exception = assertThrows(NotProperParameterException.class, () -> toTest.createBoxInXY(passedCoordinateX,passedCoordinateY,passedConstraint));
        assertEquals("Parameter: invalidString. Expected: Color: red, yellow, green, blue, purple", exception.getMessage());
    }

    @Test
    public void invalidValueConstraintPassed() throws NotProperParameterException {

        //Given
        String passedConstraint = "10";
        int passedCoordinateX= 0;
        int passedCoordinateY= 0;
        Grid toTest=new Grid(3, "name");

        //Assert
        Throwable exception = assertThrows(NotProperParameterException.class, ()->toTest.createBoxInXY(passedCoordinateX,passedCoordinateY,passedConstraint));
        assertEquals("Parameter: 10. Expected: Value: 1, 2, 3, 4, 5, 6", exception.getMessage());
    }

    @Test
    public void validValueConstraintPassed() throws NotProperParameterException{
        //Given
        String passedConstraint = "5";
        int passedCoordinateX= 0;
        int passedCoordinateY= 0;
        Grid toTest=new Grid(3, "name");
        toTest.createBoxInXY(passedCoordinateX,passedCoordinateY,passedConstraint);

        //Assert
        Throwable exception = assertThrows(NotProperParameterException.class, () -> toTest.createBoxInXY(passedCoordinateX,passedCoordinateY,passedConstraint));
        assertEquals("Parameter: (0,0). Expected: other coordinates: in this place already exist a Box!", exception.getMessage());
    }

    @Test
    public void validColorConstraintPassed() throws NotProperParameterException{
        //Given
        String passedConstraint = "red";
        int passedCoordinateX= 0;
        int passedCoordinateY= 0;
        Grid toTest= new Grid(3, "name");
        toTest.createBoxInXY(passedCoordinateX,passedCoordinateY,passedConstraint);

        //Assert
        Throwable exception = assertThrows(NotProperParameterException.class, () -> toTest.createBoxInXY(passedCoordinateX,passedCoordinateY,passedConstraint));
        assertEquals("Parameter: (0,0). Expected: other coordinates: in this place already exist a Box!", exception.getMessage());
    }

    @Test
    public void validNoneConstraintPassed() throws NotProperParameterException{
        //Given
        String passedConstraint = "none";
        int passedCoordinateX= 0;
        int passedCoordinateY= 0;
        Grid toTest=new Grid(3, "name");

        //When
        toTest.createBoxInXY(passedCoordinateX,passedCoordinateY,passedConstraint);

        //Assert
        Throwable exception = assertThrows(NotProperParameterException.class, () -> toTest.createBoxInXY(passedCoordinateX,passedCoordinateY,passedConstraint));
        assertEquals("Parameter: (0,0). Expected: other coordinates: in this place already exist a Box!", exception.getMessage());
    }

    @Test
    public void checkBoxOpened() throws NotProperParameterException, InvalidOperationException {
        //Given
        String passedConstraint = "none";
        int passedCoordinateX1= 0;
        int passedCoordinateY1= 0;
        int passedCoordinateX2= 1;
        int passedCoordinateY2= 1;
        Grid toTest1=new Grid(3, "name");


        //When
        toTest1.createBoxInXY(passedCoordinateX1, passedCoordinateY1, passedConstraint);
        toTest1.createBoxInXY(passedCoordinateX2, passedCoordinateY2, passedConstraint);

        //Assert
        //this box is opened, methods ends without throwing exceptions
        toTest1.insertDieInXY(passedCoordinateX1, passedCoordinateY1, true, true, new Die("red",1));
        assertThrows(InvalidOperationException.class, () -> toTest1.insertDieInXY(passedCoordinateX2,passedCoordinateY2,true, true, new Die("red",1)));
    }


    /*-**************************************************************************************************-*/
    //                         insertDieInXY(int x, int y, String constraint)
    /*-**************************************************************************************************-*/
    public void nullDiePassed() throws NotProperParameterException {

        //Given
        int passedCoordinateX= 0;
        int passedCoordinateY= 0;
        boolean colorCheck=true, valueCheck=true;
        Die passedDie= null;

        Grid toTest= new Grid(3, "name");
        //Assert
        assertThrows(NullPointerException.class, () -> toTest.insertDieInXY(passedCoordinateX, passedCoordinateY,colorCheck,valueCheck, passedDie));
    }

    public void insertIndexesOutOfBound() throws NotProperParameterException {

        //Given
        int passedCoordinateX= 10;
        int passedCoordinateY= 10;
        boolean colorCheck=true, valueCheck=true;
        Die passedDie= new Die("red", 1);

        Grid toTest= new Grid(3, "name");

        //Assert
        Throwable exception = assertThrows(NotProperParameterException.class, () -> toTest.insertDieInXY(passedCoordinateX, passedCoordinateY, colorCheck, valueCheck, passedDie));
        assertEquals("Parameter: (10,10). Expected: coordinates should be: 0<=x<=3 and 0<=y<=4", exception.getMessage());
    }

    public void gridNotInitialized() throws NotProperParameterException {


        //Given
        int passedCoordinateX= 0;
        int passedCoordinateY= 0;
        boolean colorCheck=true, valueCheck=true;
        Die passedDie= new Die("red", 1);

        Grid toTest= new Grid(3, "name");

        //Assert

        assertThrows(NotProperParameterException.class, () -> toTest.insertDieInXY(passedCoordinateX, passedCoordinateY,colorCheck,valueCheck, passedDie));
    }

    public void tryToInsertDieReturnFalse() throws NotProperParameterException {

        //Given
        int passedCoordinateX= 1;
        int passedCoordinateY= 1;
        boolean colorCheck=true, valueCheck=true;
        String passedConstraint = "none";
        Die passedDie= new Die("red", 1);

        Grid toTest= new Grid(3, "name");

        //Assert
        assertThrows(InvalidOperationException.class, () -> toTest.insertDieInXY(passedCoordinateX, passedCoordinateY, colorCheck, valueCheck, passedDie));

    }

    public void tryToInsertDieReturnTrue() throws NotProperParameterException, InvalidOperationException {

        //Given
        int passedCoordinateX= 0;
        int passedCoordinateY= 0;
        boolean colorCheck=true, valueCheck=true;
        String passedConstraint = "none";
        Die passedDie= new Die("red", 1);

        Grid toTest= new Grid(3, "name");

        //When
        toTest.insertDieInXY(passedCoordinateX, passedCoordinateY, colorCheck, valueCheck, passedDie); //insert in a opened box

        //Assert
        assertThrows(InvalidOperationException.class, () -> toTest.insertDieInXY(passedCoordinateX, passedCoordinateY, colorCheck, valueCheck, passedDie)); //reinsert should throw an exception.
    }


}
