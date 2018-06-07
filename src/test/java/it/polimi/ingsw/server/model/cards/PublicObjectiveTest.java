package it.polimi.ingsw.server.model.cards;

import it.polimi.ingsw.server.model.components.Die;
import it.polimi.ingsw.server.model.components.Grid;
import it.polimi.ingsw.server.custom_exception.InvalidOperationException;
import it.polimi.ingsw.server.custom_exception.NotValidParameterException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PublicObjectiveTest {

    @Test
    void publicObjective2Test() {
        Grid toTest=null;
        PublicObjective pubObj2 = new PublicObjective("Test Objective 2", "",2,4);
        try {
            toTest = new Grid(3,"testGrid");
            for(int i=0;i<toTest.getColumnNumber();i++){
                for (int j=0;j<toTest.getRowNumber();j++){
                    toTest.createBoxInXY(i,j,"none");
                }
            }
            toTest.initializeAllObservers();
            toTest.insertDieInXY(0,0,true,true, new Die("green",1));
            toTest.insertDieInXY(0,1,true,true, new Die("blue",2));
            toTest.insertDieInXY(0,2,true,true, new Die("yellow",3));
            toTest.insertDieInXY(0,3,true,true, new Die("purple",4));
            toTest.insertDieInXY(2,0,true,true, new Die("yellow",1));
            toTest.insertDieInXY(2,1,true,true, new Die("blue",2));
            toTest.insertDieInXY(2,2,true,true, new Die("yellow",3));
            toTest.insertDieInXY(2,3,true,true, new Die("purple",4));

        } catch (NotValidParameterException | InvalidOperationException e){
            fail("test failed. Invalid initialization Operation.");
        }
        assertEquals(4,pubObj2.calculatePoints(toTest));
    }

    @Test
    void publicObjective1Test() {
        Grid toTest=null;
        PublicObjective pubObj1 = new PublicObjective("Test Objective 1", "",1,4);
        try {
            toTest = new Grid(3,"testGrid");
            for(int i=0;i<toTest.getColumnNumber();i++){
                for (int j=0;j<toTest.getRowNumber();j++){
                    toTest.createBoxInXY(i,j,"none");
                }
            }
            toTest.initializeAllObservers();
            toTest.insertDieInXY(0,0,true,true, new Die("green",1));
            toTest.insertDieInXY(1,0,true,true, new Die("blue",2));
            toTest.insertDieInXY(2,0,true,true, new Die("yellow",3));
            toTest.insertDieInXY(3,0,true,true, new Die("purple",4));
            toTest.insertDieInXY(4,0,true,true, new Die("red",5));

        } catch (NotValidParameterException | InvalidOperationException e){
            fail("test failed. Invalid initialization Operation.");
        }
        assertEquals(4,pubObj1.calculatePoints(toTest));
    }

    @Test
    void publicObjective3Test() {
        Grid toTest=null;
        PublicObjective pubObj3 = new PublicObjective("Test Objective 3", "",3,4);
        try {
            toTest = new Grid(3,"testGrid");
            for(int i=0;i<toTest.getColumnNumber();i++){
                for (int j=0;j<toTest.getRowNumber();j++){
                    toTest.createBoxInXY(i,j,"none");
                }
            }
            toTest.initializeAllObservers();
            toTest.insertDieInXY(0,0,true,true, new Die("green",1));
            toTest.insertDieInXY(1,0,true,true, new Die("blue",2));
            toTest.insertDieInXY(2,0,true,true, new Die("yellow",3));
            toTest.insertDieInXY(3,0,true,true, new Die("purple",4));
            toTest.insertDieInXY(4,0,true,true, new Die("red",5));


        } catch (NotValidParameterException | InvalidOperationException e){
            fail("test failed. Invalid initialization Operation.");
        }
        assertEquals(4,pubObj3.calculatePoints(toTest));
    }

    @Test
    void publicObjective4Test() {
        Grid toTest=null;
        PublicObjective pubObj4 = new PublicObjective("Test Objective 4", "",4,4);
        try {
            toTest = new Grid(3,"testGrid");
            for(int i=0;i<toTest.getColumnNumber();i++){
                for (int j=0;j<toTest.getRowNumber();j++){
                    toTest.createBoxInXY(i,j,"none");
                }
            }
            toTest.initializeAllObservers();
            toTest.insertDieInXY(1,0,true,true, new Die("green",1));
            toTest.insertDieInXY(1,1,true,true, new Die("blue",2));
            toTest.insertDieInXY(1,2,true,true, new Die("yellow",3));
            toTest.insertDieInXY(1,3,true,true, new Die("purple",4));
            toTest.insertDieInXY(3,0,true,true, new Die("green",1));
            toTest.insertDieInXY(3,1,true,true, new Die("blue",2));
            toTest.insertDieInXY(3,2,true,true, new Die("yellow",1));
            toTest.insertDieInXY(3,3,true,true, new Die("purple",4));

        } catch (NotValidParameterException | InvalidOperationException e){
            fail("test failed. Invalid initialization Operation.");
        }
        assertEquals(4,pubObj4.calculatePoints(toTest));
    }

    @Test
    void publicObjective5Test() {
        Grid toTest=null;
        PublicObjective pubObj5 = new PublicObjective("Test Objective 5", "",5,2);
        try {
            toTest = new Grid(3,"testGrid");
            for(int i=0;i<toTest.getColumnNumber();i++){
                for (int j=0;j<toTest.getRowNumber();j++){
                    toTest.createBoxInXY(i,j,"none");
                }
            }
            toTest.initializeAllObservers();
            toTest.insertDieInXY(1,0,true,true, new Die("green",1));
            toTest.insertDieInXY(1,1,true,true, new Die("blue",2));
            toTest.insertDieInXY(1,2,true,true, new Die("yellow",1));
            toTest.insertDieInXY(1,3,true,true, new Die("purple",2));
            toTest.insertDieInXY(4,2,true,true, new Die("yellow",1));
            toTest.insertDieInXY(4,1,true,true, new Die("purple",6));

        } catch (NotValidParameterException | InvalidOperationException e){
            fail("test failed. Invalid initialization Operation.");
        }
        assertEquals(4,pubObj5.calculatePoints(toTest));
    }

    @Test
    void publicObjective6Test() {
        Grid toTest=null;
        PublicObjective pubObj6 = new PublicObjective("Test Objective 6", "",6,2);
        try {
            toTest = new Grid(3,"testGrid");
            for(int i=0;i<toTest.getColumnNumber();i++){
                for (int j=0;j<toTest.getRowNumber();j++){
                    toTest.createBoxInXY(i,j,"none");
                }
            }
            toTest.initializeAllObservers();
            toTest.insertDieInXY(1,0,true,true, new Die("green",5));
            toTest.insertDieInXY(1,1,true,true, new Die("blue",6));
            toTest.insertDieInXY(1,2,true,true, new Die("yellow",3));
            toTest.insertDieInXY(1,3,true,true, new Die("red",4));
            toTest.insertDieInXY(4,2,true,true, new Die("yellow",5));
            toTest.insertDieInXY(4,1,true,true, new Die("purple",6));
            toTest.insertDieInXY(3,3,true,true, new Die("purple",6));

        } catch (NotValidParameterException | InvalidOperationException e){
            fail("test failed. Invalid initialization Operation.");
        }
        assertEquals(2,pubObj6.calculatePoints(toTest));
    }

    @Test
    void publicObjective7Test() {
        Grid toTest=null;
        PublicObjective pubObj7 = new PublicObjective("Test Objective 7", "",7,2);
        try {
            toTest = new Grid(3,"testGrid");
            for(int i=0;i<toTest.getColumnNumber();i++){
                for (int j=0;j<toTest.getRowNumber();j++){
                    toTest.createBoxInXY(i,j,"none");
                }
            }
            toTest.initializeAllObservers();
            toTest.insertDieInXY(1,0,true,true, new Die("green",5));
            toTest.insertDieInXY(1,1,true,true, new Die("blue",6));
            toTest.insertDieInXY(1,2,true,true, new Die("yellow",3));
            toTest.insertDieInXY(1,3,true,true, new Die("red",4));
            toTest.insertDieInXY(4,2,true,true, new Die("yellow",5));
            toTest.insertDieInXY(4,1,true,true, new Die("purple",6));
            toTest.insertDieInXY(3,3,true,true, new Die("purple",6));

        } catch (NotValidParameterException | InvalidOperationException e){
            fail("test failed. Invalid initialization Operation.");
        }
        assertEquals(4,pubObj7.calculatePoints(toTest));
    }

    @Test
    void publicObjective8Test() {
        Grid toTest=null;
        PublicObjective pubObj8 = new PublicObjective("Test Objective 8", "",8,6);
        try {
            toTest = new Grid(3,"testGrid");
            for(int i=0;i<toTest.getColumnNumber();i++){
                for (int j=0;j<toTest.getRowNumber();j++){
                    toTest.createBoxInXY(i,j,"none");
                }
            }
            toTest.initializeAllObservers();
            toTest.insertDieInXY(1,0,true,true, new Die("green",1));
            toTest.insertDieInXY(1,1,true,true, new Die("blue",2));
            toTest.insertDieInXY(1,2,true,true, new Die("yellow",3));
            toTest.insertDieInXY(1,3,true,true, new Die("red",4));
            toTest.insertDieInXY(4,2,true,true, new Die("yellow",5));
            toTest.insertDieInXY(4,1,true,true, new Die("purple",6));
            toTest.insertDieInXY(3,3,true,true, new Die("purple",6));

        } catch (NotValidParameterException | InvalidOperationException e){
            fail("test failed. Invalid initialization Operation.");
        }
        assertEquals(6,pubObj8.calculatePoints(toTest));
    }

    @Test
    void publicObjective9Test() {
        Grid toTest=null;
        PublicObjective pubObj9 = new PublicObjective("Test Objective 9", "",9,0);
        try {
            toTest = new Grid(3,"testGrid");
            for(int i=0;i<toTest.getColumnNumber();i++){
                for (int j=0;j<toTest.getRowNumber();j++){
                    toTest.createBoxInXY(i,j,"none");
                }
            }
            toTest.initializeAllObservers();
            toTest.insertDieInXY(4,0,true,true, new Die("green",1));
            toTest.insertDieInXY(3,1,true,true, new Die("green",2));
            toTest.insertDieInXY(2,2,true,true, new Die("green",3));
            toTest.insertDieInXY(1,3,true,true, new Die("green",4));

        } catch (NotValidParameterException | InvalidOperationException e){
            fail("test failed. Invalid initialization Operation.");
        }
        assertEquals(4,pubObj9.calculatePoints(toTest));
    }

    @Test
    void publicObjective9Test_ForTwoDiagonals() {
        Grid toTest=null;
        PublicObjective pubObj9 = new PublicObjective("Test Objective 9", "",9,0);
        try {
            toTest = new Grid(3,"testGrid");
            for(int i=0;i<toTest.getColumnNumber();i++){
                for (int j=0;j<toTest.getRowNumber();j++){
                    toTest.createBoxInXY(i,j,"none");
                }
            }
            toTest.initializeAllObservers();
            toTest.insertDieInXY(4,0,true,true, new Die("green",1));
            toTest.insertDieInXY(3,1,true,true, new Die("green",2));
            toTest.insertDieInXY(2,2,true,true, new Die("green",3));
            toTest.insertDieInXY(1,1,true,true, new Die("green",5));
            toTest.insertDieInXY(1,3,true,true, new Die("green",4));

        } catch (NotValidParameterException | InvalidOperationException e){
            fail("test failed. Invalid initialization Operation.");
        }
        assertEquals(5,pubObj9.calculatePoints(toTest));
    }

    @Test
    void publicObjective9Test_ForNotConsequentDiagonals() {
        Grid toTest=null;
        PublicObjective pubObj9 = new PublicObjective("Test Objective 9", "",9,0);
        try {
            toTest = new Grid(3,"testGrid");
            for(int i=0;i<toTest.getColumnNumber();i++){
                for (int j=0;j<toTest.getRowNumber();j++){
                    toTest.createBoxInXY(i,j,"none");
                }
            }
            toTest.initializeAllObservers();
            toTest.insertDieInXY(4,0,true,true, new Die("green",1));
            toTest.insertDieInXY(3,1,true,true, new Die("green",3));
            toTest.insertDieInXY(1,3,true,true, new Die("green",4));

        } catch (NotValidParameterException | InvalidOperationException e){
            fail("test failed. Invalid initialization Operation.");
        }
        assertEquals(2,pubObj9.calculatePoints(toTest));
    }

    @Test
    void publicObjective10Test() {
        Grid toTest=null;
        PublicObjective pubObj10 = new PublicObjective("Test Objective 10", "",10,4);
        try {
            toTest = new Grid(3,"testGrid");
            for(int i=0;i<toTest.getColumnNumber();i++){
                for (int j=0;j<toTest.getRowNumber();j++){
                    toTest.createBoxInXY(i,j,"none");
                }
            }
            toTest.initializeAllObservers();
            toTest.insertDieInXY(1,0,true,true, new Die("green",1));
            toTest.insertDieInXY(1,1,true,true, new Die("blue",2));
            toTest.insertDieInXY(1,2,true,true, new Die("yellow",1));
            toTest.insertDieInXY(1,3,true,true, new Die("purple",2));
            toTest.insertDieInXY(4,2,true,true, new Die("red",1));
            toTest.insertDieInXY(4,1,true,true, new Die("purple",2));

        } catch (NotValidParameterException | InvalidOperationException e){
            fail("test failed. Invalid initialization Operation.");
        }
        assertEquals(4,pubObj10.calculatePoints(toTest));
    }

    @Test
    void publicObjective10Test_Alternative() {
        Grid toTest=null;
        PublicObjective pubObj10 = new PublicObjective("Test Objective 10", "",10,4);
        try {
            toTest = new Grid(3,"testGrid");
            for(int i=0;i<toTest.getColumnNumber();i++){
                for (int j=0;j<toTest.getRowNumber();j++){
                    toTest.createBoxInXY(i,j,"none");
                }
            }
            toTest.initializeAllObservers();
            toTest.insertDieInXY(1,0,true,true, new Die("green",1));
            toTest.insertDieInXY(2,3,true,true, new Die("green",1));
            toTest.insertDieInXY(1,1,true,true, new Die("blue",2));
            toTest.insertDieInXY(1,2,true,true, new Die("yellow",1));
            toTest.insertDieInXY(1,3,true,true, new Die("purple",2));
            toTest.insertDieInXY(4,2,true,true, new Die("red",1));
            toTest.insertDieInXY(4,1,true,true, new Die("purple",2));

        } catch (NotValidParameterException | InvalidOperationException e){
            fail("test failed. Invalid initialization Operation.");
        }
        assertEquals(4,pubObj10.calculatePoints(toTest));
    }

    @Test
    void toStringTest(){
        PublicObjective pubObjTest = new PublicObjective("Test Objective for toString", "sample description",0, 0);
        assertEquals("Title = Test Objective for toString\nDescription = sample description\nCard = 0\nPoints = 0\n", pubObjTest.toString());
    }


}