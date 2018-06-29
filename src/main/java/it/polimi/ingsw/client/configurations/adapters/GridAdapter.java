package it.polimi.ingsw.client.configurations.adapters;

import it.polimi.ingsw.server.custom_exception.InvalidOperationException;
import it.polimi.ingsw.server.custom_exception.NotValidParameterException;
import it.polimi.ingsw.server.model.components.Die;
import it.polimi.ingsw.server.model.components.Grid;

public abstract class GridAdapter implements GridInterface {
    private Grid grid;
    private String[][] constraints;
    private Die[][] diceInGrid;

    protected GridAdapter(Grid grid){
        final int indexForDimension= 0;

        this.grid=grid;
        this.grid.initializeAllObservers();
        String[][] temp = grid.getStructure();
        constraints= new String[temp[indexForDimension].length][temp.length];
        for(int i=0; i<temp.length;i++){
            for(int j=0; j<temp[i].length;j++){
                constraints[j][i]=temp[i][j];
            }
        }
        Die[][] structure = grid.getDice();
        diceInGrid = new Die[structure[indexForDimension].length][structure.length];
        for(int i=0; i<structure.length;i++){
            for(int j=0; j<structure[i].length;j++){
                diceInGrid[j][i]=structure[i][j];
            }
        }
    }

    public String[][] getConstraints(){
        return constraints;
    }

    public Die[][] getDiceInGrid(){
        return diceInGrid;
    }

    public String getName(){
        return grid.getName();
    }

    public int getDifficulty(){
        return grid.getDifficulty();
    }

    public void insertDieInXY(int row, int column, boolean colorCheck, boolean valueCheck, Die die) throws InvalidOperationException, NotValidParameterException {
        grid.insertDieInXY(column, row, colorCheck, valueCheck, die);
    }


}
