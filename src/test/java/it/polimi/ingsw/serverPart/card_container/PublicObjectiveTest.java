package it.polimi.ingsw.serverPart.card_container;

import it.polimi.ingsw.serverPart.component_container.Die;
import it.polimi.ingsw.serverPart.component_container.Grid;
import it.polimi.ingsw.serverPart.custom_exception.InvalidOperationException;
import it.polimi.ingsw.serverPart.custom_exception.NotValidParameterException;
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
            toTest.associateBoxes();
            toTest.insertDieInXY(0,0,true,true, new Die("green",1));
            toTest.insertDieInXY(0,1,true,true, new Die("blue",2));
            toTest.insertDieInXY(0,2,true,true, new Die("yellow",3));
            toTest.insertDieInXY(0,3,true,true, new Die("purple",4));
            //toTest.insertDieInXY(0,4,true,true, new Die("red",5));

        } catch (NotValidParameterException | InvalidOperationException e){
            fail("test failed. Invalid initialization Operation.");
        }
        try {
            assertEquals(4,pubObj2.calculatePoints(toTest));
        } catch (NotValidParameterException e) {
            fail("test failed. Invalid calculate points method.");
        }
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
            toTest.associateBoxes();
            toTest.insertDieInXY(0,0,true,true, new Die("green",1));
            toTest.insertDieInXY(1,0,true,true, new Die("blue",2));
            toTest.insertDieInXY(2,0,true,true, new Die("yellow",3));
            toTest.insertDieInXY(3,0,true,true, new Die("purple",4));
            toTest.insertDieInXY(4,0,true,true, new Die("red",5));

        } catch (NotValidParameterException | InvalidOperationException e){
            fail("test failed. Invalid initialization Operation.");
        }
        try {
            assertEquals(4,pubObj1.calculatePoints(toTest));
        } catch (NotValidParameterException e) {
            fail("test failed. Invalid calculate points method.");
        }
    }

//    @Test
//    void publicObjective9Test() {
//        Grid toTest=null;
//        PublicObjective pubObj9 = new PublicObjective("Test Objective 9", "",9,0);
//        try {
//            toTest = new Grid(3,"testGrid");
//            for(int i=0;i<toTest.getColumnNumber();i++){
//                for (int j=0;j<toTest.getRowNumber();j++){
//                    toTest.createBoxInXY(i,j,"none");
//                }
//            }
//            toTest.associateBoxes();
//            toTest.insertDieInXY(4,0,true,true, new Die("green",1));
//            toTest.insertDieInXY(3,1,true,true, new Die("green",2));
//            toTest.insertDieInXY(2,2,true,true, new Die("green",3));
//            toTest.insertDieInXY(1,3,true,true, new Die("green",4));
//
//        } catch (NotValidParameterException | InvalidOperationException e){
//            fail("test failed. Invalid initialization Operation.");
//        }
//        try {
//            assertEquals(4,pubObj9.calculatePoints(toTest));
//        } catch (NotValidParameterException e) {
//            fail("test failed. Invalid calculate points method.");
//        }
//    }

}