package it.polimi.ingsw.client.configurations.adapters;

import it.polimi.ingsw.client.custom_exception.invalid_operations.DieNotExistException;
import it.polimi.ingsw.server.model.components.Die;
import it.polimi.ingsw.server.model.components.Grid;

import java.util.HashMap;
import java.util.Map;

public class GridAdapterCLI extends GridAdapter {

    public GridAdapterCLI(Grid grid){
        super(grid);
    }

    @Override
    public String getGridInterface(){
        StringBuilder structure= new StringBuilder();
        structure.append("Nome: ");
        structure.append(super.getName());
        structure.append("\t Difficoltà: ");
        structure.append(super.getDifficulty());
        structure.append("\n");
        String[][] constraints= super.getConstraints();
        Die[][] diceInGrid=super.getDiceInGrid();
        for(int i =0;i<constraints.length&&i<diceInGrid.length;i++){
            structure.append("|");
            for(int j=0;j<constraints[i].length&&j<diceInGrid[i].length;j++){
                structure.append("\t");
                structure.append(constraints[i][j]);
                structure.append("\t|");
            }
            structure.append("\t\t|");
            for(int j=0;j<constraints[i].length&&j<diceInGrid[i].length;j++){
                structure.append("\t");
                try {
                    DieInterface die = new DieAdapterCLI(diceInGrid[i][j]);
                    structure.append(die.getDieInterface());
                } catch (DieNotExistException e) {
                    structure.append(" ");
                }
                structure.append("\t|");
            }
            structure.append("\n");
            structure.append("|");
            for(int j=0;j<constraints[i].length&&j<diceInGrid[i].length;j++){
                structure.append("\t");
                structure.append("-");
                structure.append("\t|");
            }
            structure.append("\t\t|");
            for(int j=0;j<constraints[i].length&&j<diceInGrid[i].length;j++){
                structure.append("\t");
                structure.append("-");
                structure.append("\t|");
            }
            structure.append("\n");
        }
        return structure.toString();
    }
}
