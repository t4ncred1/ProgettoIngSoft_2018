package it.polimi.ingsw.client.configurations.adapters.cli;

import it.polimi.ingsw.client.configurations.adapters.DieInterface;
import it.polimi.ingsw.client.configurations.adapters.GridInterface;
import it.polimi.ingsw.client.custom_exception.invalid_operations.DieNotExistException;
import it.polimi.ingsw.server.model.components.Die;
import it.polimi.ingsw.server.model.components.Grid;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;



public class GridAdapterCLI extends GridInterface {

    public GridAdapterCLI(Grid grid){
        super(grid);
    }

    private static final Map<String,String> colors;
    static {
        HashMap<String,String> temp = new HashMap<>();
        temp.put("R", "\u001B[5m\u001B[41m\u001B[30mR\u001B[0m");
        temp.put("Y", "\u001B[5m\u001B[43m\u001B[30mY\u001B[0m");
        temp.put("G","\u001B[5m\u001B[42m\u001B[30mG\u001B[0m" );
        temp.put("B", "\u001B[5m\u001B[44m\u001B[30mB\u001B[0m");
        temp.put("P", "\u001B[5m\u001B[45m\u001B[30mP\u001B[0m");
        temp.put("1","1");
        temp.put("2","2");
        temp.put("3","3");
        temp.put("4","4");
        temp.put("5","5");
        temp.put("6", "6");
        temp.put(" ", " ");
        colors = Collections.unmodifiableMap(temp);
    }

    @Override
    public void displayInterface(){
        StringBuilder structure= new StringBuilder();
        System.out.print("Nome: ");
        System.out.print(super.getName());
        System.out.print("\t Difficoltà: ");
        System.out.println(super.getDifficulty());
        String[][] constraints= super.getConstraints();
        Die[][] diceInGrid=super.getDiceInGrid();
        for(int i =0;i<constraints.length&&i<diceInGrid.length;i++){
            System.out.print("|");
            appendConstraintRow(i,constraints,diceInGrid);
            System.out.print("\t\t|");
            appendDiceRow(i,constraints,diceInGrid);
            System.out.println();
            System.out.print("|");
            appendRowDividers(i,constraints,diceInGrid);
            System.out.print("\n");
        }
        System.out.println(structure.toString());
    }


    private void appendDiceRow( int i, String[][] constraints, Die[][] diceInGrid) {
        for(int j=0;j<constraints[i].length&&j<diceInGrid[i].length;j++){
            System.out.print("\t");
            try {
                DieInterface die = new DieAdapterCLI(diceInGrid[i][j]);
                die.displayInterface();
            } catch (DieNotExistException e) {
                System.out.print(" ");
            }
            System.out.print("\t|");
        }
    }

    private void appendConstraintRow(int i, String[][] constraints, Die[][] diceInGrid) {
        for(int j=0;j<constraints[i].length&&j<diceInGrid[i].length;j++){
            System.out.print("\t");
            System.out.print(colors.get(constraints[i][j]));
            System.out.print("\t|");
        }
    }

    private void appendRowDividers(int i, String[][] constraints, Die[][] diceInGrid) {
        for(int j=0;j<constraints[i].length&&j<diceInGrid[i].length;j++){
            System.out.print("\t");
            System.out.print("-");
            System.out.print("\t|");
        }
        System.out.print("\t\t|");
        for(int j=0;j<constraints[i].length&&j<diceInGrid[i].length;j++){
            System.out.print("\t");
            System.out.print("-");
            System.out.print("\t|");
        }
    }
}
