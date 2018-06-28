package it.polimi.ingsw.client.configurations.adapters;

import it.polimi.ingsw.server.model.components.Die;
import it.polimi.ingsw.server.model.components.Grid;

import java.util.HashMap;
import java.util.Map;

public class GridAdapterCLI extends GridAdapter {

    private Map<String,String> colors;
    private static final String ANSI_RESET = "\u001B[0m";
    private static final String[] faces = {
            "\u2680",
            "\u2681",
            "\u2682",
            "\u2683",
            "\u2684",
            "\u2685"
    };

    public GridAdapterCLI(Grid grid){
        super(grid);
        colors = new HashMap<>();
        colors.put("red", "\033[0;31m");
        colors.put("yellow", "\033[0;33m");
        colors.put("green","\033[0;92m" );
        colors.put("blue", "\033[0;34m");
        colors.put("purple", "\033[0;35m");
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
                Die die=diceInGrid[i][j];
                if(die!=null) {
                    structure.append(colors.get(die.getColor()));
                    structure.append(faces[die.getValue()-1]);
                    structure.append(ANSI_RESET);
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
