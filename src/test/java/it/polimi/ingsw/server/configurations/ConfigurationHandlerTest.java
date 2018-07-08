package it.polimi.ingsw.server.configurations;

import com.google.gson.Gson;
import it.polimi.ingsw.server.custom_exception.NotValidConfigPathException;
import it.polimi.ingsw.server.model.cards.PublicObjective;
import it.polimi.ingsw.server.model.cards.ToolCard;
import it.polimi.ingsw.server.model.components.Grid;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ConfigurationHandlerTest {
    @Test
    void getInstanceTest(){
        ConfigurationHandler instance1=null;
        ConfigurationHandler instance2=null;
        try {
            instance1 = ConfigurationHandler.getInstance();
            instance2 = ConfigurationHandler.getInstance();
        } catch (NotValidConfigPathException e) {
            fail("Test Failed.");
        }
        assertEquals(instance1,instance2);
    }

    @Test
    void getGridsTest(){
        List<Grid> gridList=null;
        try {
            gridList=ConfigurationHandler.getInstance().getGrids();
        } catch (NotValidConfigPathException e) {
            fail("Test Failed.");
        }
        if(gridList.isEmpty()) fail("Test Failed.");

    }

    @Test
    void getPublicObjectiveTest(){
        List<PublicObjective> pubs=null;
        try {
            pubs=ConfigurationHandler.getInstance().getPublicObjectives();
        } catch (NotValidConfigPathException e) {
            fail("Test Failed.");
        }
        if(pubs.isEmpty()) fail("Test Failed.");
    }

    @Test
    void getToolCardsTest(){
        List<ToolCard> tcs=null;
        try {
            tcs=ConfigurationHandler.getInstance().getToolCards();
        } catch (NotValidConfigPathException e) {
            fail("Test Failed.");
        }
        if(tcs.isEmpty()) fail("Test Failed.");
    }

    @Test
    void getGsonForToolCardsTest(){
        Gson gson=null;
        try {
            gson=ConfigurationHandler.getInstance().getGsonForToolCards();
        } catch (NotValidConfigPathException e) {
            fail("Test Failed.");
        }
        assertEquals(
                "{\"effects\":[],\"title\":\"Title\",\"description\":\"Description\",\"used\":false,\"removeAllDiceFromDicePool\":false,\"colourInRoundtrack\":false,\"colorCheck\":false,\"valueCheck\":false,\"openCheck\":false,\"jumpNextTurn\":false,\"mustBeSecondTurn\":false,\"diceMustNotBeInserted\":false}"
                                ,gson.toJson(new ToolCard("Title","Description",new ArrayList<>(),null)));
    }
}