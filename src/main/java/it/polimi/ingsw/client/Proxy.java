package it.polimi.ingsw.client;

import it.polimi.ingsw.server.components.Grid;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Proxy {

    ArrayList<Grid> gridsSelection;
    Map<String,Grid> playerGrids;

    public Proxy(List<Grid> gridsSelection){
        this.gridsSelection= (ArrayList<Grid>) gridsSelection;
        playerGrids= new HashMap<>();
    }

}
