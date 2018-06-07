package it.polimi.ingsw.server;

import it.polimi.ingsw.server.custom_exception.NotValidParameterException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


class MatchModelTest {

    @Test
    void checkCreator(){

        assertThrows(NullPointerException.class,()-> new MatchModel(null));

    }



}