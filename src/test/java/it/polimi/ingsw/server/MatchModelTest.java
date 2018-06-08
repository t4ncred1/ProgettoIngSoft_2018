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
    void checkAskTurn() throws NotValidConfigPathException, NotValidParameterException {
        Set<String> playerUserNames = new CopyOnWriteArraySet<String>();
        playerUserNames.add("cancaro");
        playerUserNames.add("test");
        MatchModel test = new MatchModel(playerUserNames);
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
        assertThrows(NotValidParameterException.class,()->test.insertdieinRT(temp,0));
        assertThrows(NotValidParameterException.class,()->test.insertdieinRT(temp1,1));
        test.insertdieinRT(temp1,0);
        copy=test.getRoundTrack();
        assertEquals(copy.get(0),temp1);
        assertEquals(copy.get(0),test.getDieFromRoundtrack(0));
        assertThrows(NotInPoolException.class,()->test.getDieFromRoundtrack(-1));
        assertThrows(IndexOutOfBoundsException.class,()->test.getDieFromRoundtrack(1));
        test.removeDieFromRoundTrack(0);;
        copy=test.getRoundTrack();
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
        dpCopy=test.getDicePool();
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
        assertThrows(NotValidParameterException.class,()->test.setPlayerToDisconnect("test1"));
        test.setPlayerToDisconnect("cancaro");
        test.setPlayerToDisconnect("test");
        assertThrows(NotEnoughPlayersException.class,()->test.updateTurn(10));
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
    void checkInsertDieOperation() throws NotValidConfigPathException, NotValidParameterException, InvalidOperationException, NotInPoolException {
     //TODO
    }

    @Test
    void checkGetGridsForPlayer() throws NotValidConfigPathException, NotValidParameterException, InvalidOperationException, InvalidUsernameException {
        Set<String> playerUserNames = new CopyOnWriteArraySet<String>();
        playerUserNames.add("cancaro");
        playerUserNames.add("test");
        MatchModel test = new MatchModel(playerUserNames);
        test.setPlayerToDisconnect("cancaro");
        test.setPlayerToDisconnect("test");
        assertThrows(InvalidUsernameException.class,()->test.getGridsForPlayer("cancaro"));
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


}