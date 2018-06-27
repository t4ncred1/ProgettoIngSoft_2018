package it.polimi.ingsw.server;

import it.polimi.ingsw.server.model.MatchModel;
import it.polimi.ingsw.server.model.cards.*;
import it.polimi.ingsw.server.model.components.*;
import it.polimi.ingsw.server.custom_exception.*;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.concurrent.CopyOnWriteArraySet;

import static org.junit.jupiter.api.Assertions.*;


class MatchModelTest {

    @Test
    void checkCreator() throws NotValidConfigPathException, NotValidParameterException {

        assertThrows(NullPointerException.class,()-> new MatchModel(null));

        Set<String> playerUserNames = new CopyOnWriteArraySet<String>();
        playerUserNames.add("cancaro");
        playerUserNames.add("test");
        MatchModel test= new MatchModel(playerUserNames);
    }

    @Test
    void checkAskTurn() throws NotValidConfigPathException, NotValidParameterException, TooManyRoundsException, NotEnoughPlayersException {
        Set<String> playerUserNames = new CopyOnWriteArraySet<String>();
        playerUserNames.add("cancaro");
        playerUserNames.add("test");
        MatchModel test = new MatchModel(playerUserNames);
        test.updateTurn(10);
        assertEquals("cancaro",test.askTurn());
    }

    @Test
    void checkSetPGridandCheckEnd() throws NotValidConfigPathException, NotValidParameterException, InvalidOperationException, NotInPoolException, TooManyRoundsException, NotEnoughPlayersException {
        Set<String> playerUserNames = new CopyOnWriteArraySet<String>();
        Grid gridtest = new Grid(3,"test00");
        Grid gridtest1=new Grid(4,"test1");
        ArrayList<Grid> grids=new ArrayList<>();
        grids.add(gridtest);
        grids.add(gridtest1);
        playerUserNames.add("cancaro");
        playerUserNames.add("test");
        MatchModel test = new MatchModel(playerUserNames);
        assertThrows(InvalidOperationException.class,()->test.setPlayerGrid("cancaro",5));
        test.setPlayerGrid("cancaro",0);
        assertEquals(test.checkEndInitialization(),false);
        test.setPlayerGrid("test",1);
        assertEquals(test.checkEndInitialization(),true);
    }

    @Test
    void checkPrepareForNextRound() throws NotInPoolException, NotValidParameterException, TooManyRoundsException, NotValidConfigPathException, NotEnoughPlayersException {
        Set<String> playerUserNames = new CopyOnWriteArraySet<String>();
        playerUserNames.add("cancaro");
        playerUserNames.add("test");
        MatchModel test = new MatchModel(playerUserNames);
        test.updateTurn(0);
        test.updateTurn(0);
        test.updateTurn(0);
        test.updateTurn(0);
        assertThrows(TooManyRoundsException.class,()->test.updateTurn(0));
    }

    @Test
    void checkRoundTrack() throws NotValidParameterException, NotValidConfigPathException, NotInPoolException {
        ArrayList<Die> copy=new ArrayList<>();
        boolean bool;
        Die temp=null;
        Die temp1=new Die("green",1);
        Set<String> playerUserNames = new CopyOnWriteArraySet<String>();
        playerUserNames.add("cancaro");
        playerUserNames.add("test");
        MatchModel test = new MatchModel(playerUserNames);
        assertThrows(NotValidParameterException.class,()->test.insertDieInRT(temp,0));
        assertThrows(NotValidParameterException.class,()->test.insertDieInRT(temp1,1));
        test.insertDieInRT(temp1,0);
        copy=new ArrayList<>(test.getRoundTrack());
        assertEquals(copy.get(0),temp1);
        assertEquals(copy.get(0),test.getDieFromRoundTrack(0));
        assertThrows(NotInPoolException.class,()->test.getDieFromRoundTrack(-1));
        assertThrows(IndexOutOfBoundsException.class,()->test.getDieFromRoundTrack(1));
        test.removeDieFromRoundTrack(0);
        copy=new ArrayList<>(test.getRoundTrack());
        bool=copy.isEmpty();
        assertEquals(bool,true);
    }

    @Test
    void checkMatchDicepool() throws NotInPoolException,NotValidParameterException,NotValidConfigPathException {
        Set<String> playerUserNames = new CopyOnWriteArraySet<String>();
        Die temp=new Die("green",1);
        List<Die> dpCopy;
        playerUserNames.add("cancaro");
        playerUserNames.add("test");
        MatchModel test = new MatchModel(playerUserNames);
        test.insertDieInPool(temp,0);
        dpCopy=test.getDicePool().showDiceInPool();
        assertEquals(dpCopy.get(0),temp);
        test.removeDiePool(0);
    }

    @Test
    void checkValidUpdateTurn() throws TooManyRoundsException, NotEnoughPlayersException, NotValidConfigPathException, NotValidParameterException {
        Set<String> playerUserNames = new CopyOnWriteArraySet<String>();
        playerUserNames.add("cancaro");
        playerUserNames.add("test");
        MatchModel test = new MatchModel(playerUserNames);
        test.updateTurn(10);
        test.updateTurn(10);
        test.updateTurn(10);
        test.updateTurn(10);
        test.updateTurn(10);
    }

    @Test
    void checkSetPlayerToDisconnect() throws NotValidParameterException, NotValidConfigPathException, InvalidUsernameException {
        Set<String> playerUserNames = new CopyOnWriteArraySet<String>();
        playerUserNames.add("cancaro");
        playerUserNames.add("test");
        MatchModel test = new MatchModel(playerUserNames);
        assertThrows(InvalidUsernameException.class,()->test.setPlayerToDisconnect("test1"));
    }

    @Test
    void checkHasPlayerChosenAGrid() throws NotValidConfigPathException, NotValidParameterException {
        Set<String> playerUserNames = new CopyOnWriteArraySet<String>();
        playerUserNames.add("cancaro");
        playerUserNames.add("test");
        MatchModel test = new MatchModel(playerUserNames);
        assertThrows(NotValidParameterException.class,()->test.hasPlayerChosenAGrid("fake"));
        test.hasPlayerChosenAGrid("cancaro");
    }

    @Test
    void checkInsertDieOpGetGridsForPlayersGetPlCurrentGrid() throws NotValidConfigPathException, NotValidParameterException, InvalidOperationException, NotInPoolException, TooManyRoundsException, NotEnoughPlayersException, InvalidUsernameException {
        List<Grid> testgrids = new ArrayList<>();
        int i,j;
        Grid grid1 = null;
        Grid grid2;
        try{
            grid1= new Grid(4,"toTest");
            for(i=0;i<grid1.getColumnNumber();i++){
                for(j=0;j<grid1.getRowNumber();j++){
                    grid1.createBoxInXY(i,j,"none");
                }
            }
            grid1.initializeAllObservers();
        }catch (NotValidParameterException e){
            fail("test failed");
        }
        testgrids.add(grid1);
        Set<String> playerUserNames = new CopyOnWriteArraySet<String>();
        playerUserNames.add("cancaro");
        playerUserNames.add("test");
        MatchModel test = new MatchModel(playerUserNames);
        test.updateTurn(10);
        test.getCurrentPlayer().setGridsSelection(testgrids);
        assertEquals(testgrids,test.getGridsForPlayer("cancaro"));
        test.getCurrentPlayer().setGrid(0);
        test.insertDieOperation(0,0,0);
        grid2=test.getPlayerCurrentGrid("cancaro");
        assertEquals(grid2,grid1);
        assertThrows(InvalidOperationException.class,()->test.getGridsForPlayer("cancaro"));
    }

    @Test
    void checkPrivateObjective() throws NotValidConfigPathException, NotValidParameterException, InvalidUsernameException {
        PrivateObjective priv1,priv2,priv3,priv4;
        Set<String> playerUserNames = new CopyOnWriteArraySet<String>();
        playerUserNames.add("cancaro");
        playerUserNames.add("test");
        playerUserNames.add("3");
        playerUserNames.add("4");
        MatchModel test = new MatchModel(playerUserNames);
        priv1=test.getPrivateObjective("cancaro");
        priv2=test.getPrivateObjective("test");
        priv3=test.getPrivateObjective("3");
        priv4=test.getPrivateObjective("4");
        assertThrows(InvalidUsernameException.class,()->test.getPrivateObjective("invalid"));
        assertNotEquals(priv1,priv2);
        assertNotEquals(priv1,priv3);
        assertNotEquals(priv1,priv4);
        assertNotEquals(priv2,priv3);
        assertNotEquals(priv2,priv4);
        assertNotEquals(priv3,priv4);
    }

    @Test
    void checkMatchModelCreator() throws NotValidConfigPathException, NotValidParameterException {
        Set<String> playerUserNames = new CopyOnWriteArraySet<String>();
        playerUserNames.add("cancaro");
        playerUserNames.add("test");
        MatchController matchController=new MatchController();
        MatchModel matchModel=new MatchModel(playerUserNames,matchController);
    }
}