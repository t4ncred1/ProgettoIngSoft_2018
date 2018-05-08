package it.polimi.ingsw.gridContainer;

import it.polimi.ingsw.Die;
import it.polimi.ingsw.DieCostraints;
import it.polimi.ingsw.DieToCostraintsAdapter;
import it.polimi.ingsw.customException.InvalidOperationException;
import it.polimi.ingsw.customException.NotProperParameterException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.jupiter.api.Assertions.*;

public class GridTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    /*----------------------------------------------------------------------------------------------------*/

    //                                       Test sui costruttori

    /*----------------------------------------------------------------------------------------------------*/

    /*-**************************************************************************************************-*/
    //                                  Grid (int Difficulty, String Name)
    /*-**************************************************************************************************-*/


    @Test
    public void nullStringPassed() throws NotProperParameterException {
        //Assert
        thrown.expect(NullPointerException.class);

        //Given
        String passedName=null;
        int passedDifficulty=3;

        //When
        Grid testGrid = new Grid(passedDifficulty, passedName);

    }

    @Test
    public void invalidDifficultyPassed() throws NotProperParameterException {
        //Assert
        thrown.expect(NotProperParameterException.class);
        thrown.expectMessage("Parameter: 1. Expected: Difficulty should have a value between 3 and 6 (both included)");

        //Given
        String passedName="nomeGrid";
        int passedDifficulty=1;

        //When
        Grid testGrid = new Grid(passedDifficulty, passedName);
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
        //Assert
        thrown.expect(NullPointerException.class);

        //Given
        String passedConstraint= null;
        int passedCoordinateX= 0;
        int passedCoordinateY= 0;
        Grid toTest=null;
        try {
            toTest = new Grid(3, "name");
        }
        catch (NotProperParameterException e){
            System.err.println("Create a valid Grid!");
        }

        //When
        toTest.createBoxInXY(passedCoordinateX,passedCoordinateY,passedConstraint);
    }

    @Test
    public void createIndexesOutOfBound() throws NotProperParameterException {
        //Assert
        thrown.expect(NotProperParameterException.class);
        thrown.expectMessage("Parameter: (10,10). Expected: coordinates should be: 0<=x<=3 and 0<=y<=4");

        //Given
        String passedConstraint = "red";
        int passedCoordinateX= 10;
        int passedCoordinateY= 10;
        Grid toTest=null;
        try {
            toTest = new Grid(3, "name");
        }
        catch (NotProperParameterException e){
            System.err.println("Create a valid Grid!");
        }

        //When
        toTest.createBoxInXY(passedCoordinateX,passedCoordinateY,passedConstraint);

    }

    @Test
    public void boxAlreadyCreated() throws NotProperParameterException {
        //Assert
        thrown.expect(NotProperParameterException.class);
        thrown.expectMessage("Parameter: (0,0). Expected: other coordinates: in this place already exist a Box!");

        //Given
        String passedConstraint = "red";
        int passedCoordinateX= 0;
        int passedCoordinateY= 0;
        Grid toTest=null;
        try {
            toTest = new Grid(3, "name");
            toTest.createBoxInXY(passedCoordinateX,passedCoordinateY,passedConstraint); //box filled, can't create another one here
        }
        catch (NotProperParameterException e){
            System.err.println("Create a valid Grid!");
        }

        //When
        toTest.createBoxInXY(passedCoordinateX,passedCoordinateY,passedConstraint);
    }

    @Test
    public void invalidStringPassed() throws NotProperParameterException { //invalid color and invalid string are the same case
        //Assert
        thrown.expect(NotProperParameterException.class);
        thrown.expectMessage("Parameter: invalidString. Expected: Color: red, yellow, green, blue, purple");

        //Given
        String passedConstraint = "invalidString";
        int passedCoordinateX= 0;
        int passedCoordinateY= 0;
        Grid toTest=null;
        try {
            toTest = new Grid(3, "name");
        }
        catch (NotProperParameterException e){
            System.err.println("Create a valid Grid!");
        }

        //When
        toTest.createBoxInXY(passedCoordinateX,passedCoordinateY,passedConstraint);
    }

    @Test
    public void invalidValueConstraintPassed() throws NotProperParameterException {
        //Assert
        thrown.expect(NotProperParameterException.class);
        thrown.expectMessage("Parameter: 10. Expected: Value: 1, 2, 3, 4, 5, 6");

        //Given
        String passedConstraint = "10";
        int passedCoordinateX= 0;
        int passedCoordinateY= 0;
        Grid toTest=null;
        try {
            toTest = new Grid(3, "name");
        }
        catch (NotProperParameterException e){
            System.err.println("Create a valid Grid!");
        }

        //When
        toTest.createBoxInXY(passedCoordinateX,passedCoordinateY,passedConstraint);
    }

    @Test
    public void validValueConstraintPassed() throws NotProperParameterException{
        //Given
        String passedConstraint = "5";
        int passedCoordinateX= 0;
        int passedCoordinateY= 0;
        Grid toTest=null;
        try {
            toTest = new Grid(3, "name");
        }
        catch (NotProperParameterException e){
            System.err.println("Create a valid Grid!");
        }

        //When
        toTest.createBoxInXY(passedCoordinateX,passedCoordinateY,passedConstraint);

        //Assert
        thrown.expect(NotProperParameterException.class); //Box creation should go well, so if i try to recreate it the methods launch an exception
        thrown.expectMessage("Parameter: (0,0). Expected: other coordinates: in this place already exist a Box!");
        toTest.createBoxInXY(passedCoordinateX,passedCoordinateY,passedConstraint);

    }

    @Test
    public void validColorConstraintPassed() throws NotProperParameterException{
        //Given
        String passedConstraint = "red";
        int passedCoordinateX= 0;
        int passedCoordinateY= 0;
        Grid toTest=null;
        try {
            toTest = new Grid(3, "name");
        }
        catch (NotProperParameterException e){
            System.err.println("Create a valid Grid!");
        }

        //When
        toTest.createBoxInXY(passedCoordinateX,passedCoordinateY,passedConstraint);

        //Assert
        thrown.expect(NotProperParameterException.class); //Box creation should go well, so if i try to recreate it the methods launch an exception
        thrown.expectMessage("Parameter: (0,0). Expected: other coordinates: in this place already exist a Box!");
        toTest.createBoxInXY(passedCoordinateX,passedCoordinateY,passedConstraint);
    }

    @Test
    public void validNoneConstraintPassed() throws NotProperParameterException{
        //Given
        String passedConstraint = "none";
        int passedCoordinateX= 0;
        int passedCoordinateY= 0;

        Grid toTest=null;
        try {
            toTest = new Grid(3, "name");
        }
        catch (NotProperParameterException e){
            System.err.println("Create a valid Grid!");
        }

        //When
        toTest.createBoxInXY(passedCoordinateX,passedCoordinateY,passedConstraint);

        //Assert
        thrown.expect(NotProperParameterException.class); //Box creation should go well, so if i try to recreate it the methods launch an exception
        thrown.expectMessage("Parameter: (0,0). Expected: other coordinates: in this place already exist a Box!");
        toTest.createBoxInXY(passedCoordinateX,passedCoordinateY,passedConstraint);
    }

    @Test
    public void checkBoxOpened() throws NotProperParameterException, InvalidOperationException {
        //Given
        String passedConstraint = "none";
        int passedCoordinateX1= 0;
        int passedCoordinateY1= 0;
        int passedCoordinateX2= 1;
        int passedCoordinateY2= 1;

        Grid toTest1=null;
        try {
            toTest1 = new Grid(3, "name");
        }
        catch (NotProperParameterException e){
            System.err.println("Create a valid Grid!");
        }


        //When
        toTest1.createBoxInXY(passedCoordinateX1, passedCoordinateY1, passedConstraint);
        toTest1.createBoxInXY(passedCoordinateX2, passedCoordinateY2, passedConstraint);

        //Assert
        //this box is opened, methods ends without throwing exceptions
        toTest1.insertDieInXY(passedCoordinateX1, passedCoordinateY1, true, true, new Die("red",1));
        thrown.expect(InvalidOperationException.class);
        toTest1.insertDieInXY(passedCoordinateX2,passedCoordinateY2,true, true, new Die("red",1));
    }


    /*-**************************************************************************************************-*/
    //                         insertDieInXY(int x, int y, String constraint)
    /*-**************************************************************************************************-*/
    public void nullDiePassed() throws NotProperParameterException,InvalidOperationException {
        //Assert
        thrown.expect(NullPointerException.class);

        //Given
        int passedCoordinateX= 0;
        int passedCoordinateY= 0;
        boolean colorCheck=true, valueCheck=true;
        Die passedDie= null;

        Grid toTest= null;
        try {
            toTest = new Grid(3, "name");
        }
        catch (NotProperParameterException e){
            System.err.println("Create a valid Grid!");
        }

        //When
        toTest.insertDieInXY(passedCoordinateX, passedCoordinateY,colorCheck,valueCheck, passedDie);
    }

    public void insertIndexesOutOfBound() throws NotProperParameterException, InvalidOperationException {
        //Assert
        thrown.expect(NullPointerException.class);
        thrown.expectMessage("Parameter: (10,10). Expected: coordinates should be: 0<=x<=3 and 0<=y<=4");

        //Given
        int passedCoordinateX= 10;
        int passedCoordinateY= 10;
        boolean colorCheck=true, valueCheck=true;
        Die passedDie= new Die("red", 1);

        Grid toTest= null;
        try {
            toTest = new Grid(3, "name");
        }
        catch (NotProperParameterException e){
            System.err.println("Create a valid Grid!");
        }

        //When
        toTest.insertDieInXY(passedCoordinateX, passedCoordinateY, colorCheck, valueCheck, passedDie);
    }

    public void gridNotInitialized() throws NotProperParameterException,InvalidOperationException {
        //Assert
        thrown.expect(NullPointerException.class);

        //Given
        int passedCoordinateX= 0;
        int passedCoordinateY= 0;
        boolean colorCheck=true, valueCheck=true;
        Die passedDie= new Die("red", 1);

        Grid toTest= null;
        try {
            toTest = new Grid(3, "name");
        }
        catch (NotProperParameterException e){
            System.err.println("Create a valid Grid!");
        }

        //When
        toTest.insertDieInXY(passedCoordinateX, passedCoordinateY,colorCheck,valueCheck, passedDie);
    }

    public void tryToInsertDieReturnFalse() throws NotProperParameterException, InvalidOperationException {

        //Assert
        thrown.expect(InvalidOperationException.class);

        //Given
        int passedCoordinateX= 1;
        int passedCoordinateY= 1;
        boolean colorCheck=true, valueCheck=true;
        String passedConstraint = "none";
        Die passedDie= new Die("red", 1);

        Grid toTest= null;
        try {
            toTest = new Grid(3, "name");
            toTest.createBoxInXY(passedCoordinateX, passedCoordinateY, passedConstraint); //insert in a closed box
        }
        catch (NotProperParameterException e){
            System.err.println("Create a valid Grid!");
        }

        //When
        toTest.insertDieInXY(passedCoordinateX, passedCoordinateY, colorCheck, valueCheck, passedDie);

    }

    public void tryToInsertDieReturnTrue() throws NotProperParameterException, InvalidOperationException {

        //Given
        int passedCoordinateX= 0;
        int passedCoordinateY= 0;
        boolean colorCheck=true, valueCheck=true;
        String passedConstraint = "none";
        Die passedDie= new Die("red", 1);

        Grid toTest= null;
        try {
            toTest = new Grid(3, "name");
            toTest.createBoxInXY(passedCoordinateX, passedCoordinateY, passedConstraint);
        }
        catch (NotProperParameterException e){
            System.err.println("Create a valid Grid!");
        }

        //When
        toTest.insertDieInXY(passedCoordinateX, passedCoordinateY, colorCheck, valueCheck, passedDie); //insert in a opened box

        //Assert
        thrown.expect(InvalidOperationException.class);
        toTest.insertDieInXY(passedCoordinateX, passedCoordinateY, colorCheck, valueCheck, passedDie); //reinsert should throw an exception.
    }


}
