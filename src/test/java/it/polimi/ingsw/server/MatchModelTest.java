package it.polimi.ingsw.server;

import it.polimi.ingsw.server.component_container.Die;
import it.polimi.ingsw.server.custom_exception.InvalidOperationException;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;


class MatchModelTest {

    @Test
    void nullPlayerSetConstructorTest(){
        assertThrows(NullPointerException.class,()-> new MatchModel(null));
    }



}