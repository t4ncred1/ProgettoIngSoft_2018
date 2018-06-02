package it.polimi.ingsw.client;

import it.polimi.ingsw.server.components.Grid;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Proxy {

    private static Proxy instance;

    private ArrayList<Grid> gridsSelection;
    private Map<String,Grid> playerGrids;

    private Proxy(){
        gridsSelection= new ArrayList<>();
        playerGrids= new HashMap<>();
    }

    public static Proxy getInstance(){
        if(instance==null) instance= new Proxy();
        return instance;
    }

    public void setGridsSelection(List<Grid> gridsSelection){
        this.gridsSelection=(ArrayList<Grid>)gridsSelection;
    }

    public List<Grid> getGridsSelection() {
        return gridsSelection;
    }

    public int getGridsSelectionDimension() {
        return gridsSelection.size();
    }
}
