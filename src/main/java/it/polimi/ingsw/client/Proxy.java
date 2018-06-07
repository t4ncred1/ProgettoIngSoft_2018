package it.polimi.ingsw.client;

import it.polimi.ingsw.client.custom_exception.InvalidMoveException;
import it.polimi.ingsw.server.model.components.Die;
import it.polimi.ingsw.server.model.components.Grid;
import it.polimi.ingsw.server.custom_exception.InvalidOperationException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Proxy {

    private static Proxy instance;

    private ArrayList<Grid> gridsSelection;
    private Grid gridSelected;
    private Map<String,Grid> playerGrids;
    private ArrayList<Die> dicePool;

    private Proxy(){
        gridsSelection= new ArrayList<>();
        playerGrids= new HashMap<>();
    }

    public static Proxy getInstance(){
        if(instance==null) instance= new Proxy();
        return instance;
    }

    public void setGridsSelection(List<Grid> gridsSelection) throws InvalidOperationException {
        if (!this.gridsSelection.isEmpty()) throw new InvalidOperationException();   //thrown in case grids have already been chosen
        this.gridsSelection=(ArrayList<Grid>)gridsSelection;
    }

    public List<Grid> getGridsSelection() {
        return gridsSelection;
    }

    public int getGridsSelectionDimension() {
        return gridsSelection.size();
    }

    public void tryToInsertDieInXY(int position, int x, int y) throws InvalidMoveException {
        final boolean checkColorConstraint= true;
        final boolean checkValueConstraint= true;
        Die die=null;
        //FIXME here should be launched two different exceptions for dicePool and insertDie
        try {
            dicePool.remove(position);
        }catch (IndexOutOfBoundsException e){
            throw new InvalidMoveException();
        }
        try {
            gridSelected.insertDieInXY(x, y, checkColorConstraint, checkValueConstraint, die);
        } catch (Exception e) {
            throw new InvalidMoveException();
        }
    }

    public void updateGridSelected(Grid grid) {
        this.gridSelected=grid;
    }

    public Grid getGridSelected() {
        return this.gridSelected;
    }

    public void setDicePool(List<Die> dicePool) {
        this.dicePool=(ArrayList<Die>) dicePool;
    }
}
