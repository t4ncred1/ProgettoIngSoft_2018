package it.polimi.ingsw.server;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


class MatchModelTest {

    @Test
    void nullPlayerSetConstructorTest(){
        assertThrows(NullPointerException.class,()-> new MatchModel(null));
    }



}