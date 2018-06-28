package it.polimi.ingsw.client.configurations;

import it.polimi.ingsw.server.custom_exception.InvalidOperationException;
import it.polimi.ingsw.server.custom_exception.NotValidParameterException;
import it.polimi.ingsw.server.model.components.Die;
import it.polimi.ingsw.server.model.components.Grid;

public class GridAdapter implements GridInterface {
    private Grid grid;
    private String[][] constraints;

    public GridAdapter(Grid grid){
        final int indexForDimension= 0;

        this.grid=grid;
        String[][] temp = grid.getStructure();
        constraints= new String[temp[indexForDimension].length][temp.length];
        for(int i=0; i<temp.length;i++){
            for(int j=0; j<temp[i].length;j++){
                constraints[j][i]=temp[i][j];
                //do the same for dice.
            }
        }
    }


    public String getGridInterface(){
        StringBuilder structure= new StringBuilder();
        structure.append("Nome: ");
        structure.append(grid.getName());
        structure.append("\t Difficoltà: ");
        structure.append(grid.getDifficulty());
        structure.append("\n");
        for(String[] i : constraints){
            structure.append("|");
            for(String j : i){
                structure.append("\t");
                structure.append(j);
                structure.append("\t|");
            }
            structure.append("\n");
            structure.append("|");
            for(String j : i){
                structure.append("\t");
                structure.append("-");
                structure.append("\t|");
            }
            structure.append("\n");
        }
        return structure.toString();
    }

    public void insertDieInXY(int row, int column, boolean colorCheck, boolean valueCheck, Die die) throws InvalidOperationException, NotValidParameterException {
        grid.insertDieInXY(column, row, colorCheck, valueCheck, die);
    }


}
