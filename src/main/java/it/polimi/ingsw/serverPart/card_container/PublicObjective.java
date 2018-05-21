package it.polimi.ingsw.serverPart.card_container;
import it.polimi.ingsw.serverPart.component_container.*;

import java.util.stream.IntStream;

public class PublicObjective extends Objective{
    private boolean on_rows;
    private boolean on_columns;
    private boolean on_values;
    private boolean on_colors;
    private int value;
    private int[] valuesconsidered;

    public PublicObjective(String tit, String desc, boolean rows, boolean columns, boolean valueconst, boolean colors, int points, int[]values){
        title=tit;
        description=desc;
        on_rows=rows;
        on_columns=columns;
        on_values=valueconst;
        on_colors=colors;
        value=points;
        valuesconsidered=values;
    }


    public String toString(){
        StringBuilder build = new StringBuilder("Title = ");
        build.append(this.getTitle());
        build.append("\n");
        build.append("Description = ");
        build.append(this.getDescription());
        build.append("\nrows = " + Boolean.toString(on_rows));
        build.append("\ncolumns = " + Boolean.toString(on_columns));
        build.append("\nvalues = " + Boolean.toString(on_values));
        build.append("\ncolors = " + Boolean.toString(on_colors));
        build.append("\npoints = " + Integer.toString(value));
        build.append("\nvalues considered: ");
        for (int i : valuesconsidered){
            build.append(Integer.toString(i)+",");
        }
        build.append("\n");

        return build.toString();
    }

    @Override
    public int calculatePoints(Grid grid) throws NullPointerException{
        if (grid==null) throw new NullPointerException();
        Box[][] modifiedGrid;
        if (on_columns && !on_rows) modifiedGrid=grid.getGrid();
        else if (on_rows && !on_columns) modifiedGrid=rotateGrid(grid);

        return 0;
    }

    private Box[][] rotateGrid(Grid grid){
        Box[][] rotatedGrid = new Box[grid.getRowNumber()][grid.getColumnNumber()];
        for(int i=0;i<grid.getColumnNumber();i++){
            for(int j=0;j<grid.getRowNumber();j++){
                rotatedGrid[j][i]=grid.getGrid()[i][j];
            }
        }
        return rotatedGrid;
    }

//    private Box[][] diagonalsGrid(Grid grid){
//        //please note: this method only works if the number of columns is greater than the number of rows.
//        //
////        Box[][] diagonalGrid = new Box[(grid.getColumnNumber()+grid.getRowNumber()-1)*2][];
////        for(int i=0; i<grid.getColumnNumber();i++){
////
////            diagonalGrid[i]=new Box[(i>=grid.getRowNumber())?grid.getRowNumber():i]; //used atomic statement here
////            for (int j=0; j<=i && j<grid.getRowNumber();j++){
////                diagonalGrid[i][j]=grid.getGrid()[i-j][j];
////            }
////            if(i<grid.getRowNumber()-1) {
////                diagonalGrid[grid.getColumnNumber() + grid.getRowNumber() - 1 - i] = new Box[grid.getRowNumber() - i + 1];
////                for (int j = 0; j <= i && j < grid.getRowNumber(); j++) {
////                    diagonalGrid[grid.getColumnNumber() + grid.getRowNumber() - 1 - i][j] = grid.getGrid()[grid.getColumnNumber()-1 - i + j][grid.getRowNumber()-1-j];
////                }
////            }
////
////            diagonalGrid[grid.getColumnNumber()+grid.getRowNumber()+i]=new Box[(i>=grid.getRowNumber())?grid.getRowNumber():i]; //used atomic statement here
////            for (int j=((grid.getColumnNumber()-i+1>(grid.getRowNumber()-1))?grid.getRowNumber()-1:grid.getColumnNumber()-i+1); j>=0;j--){
////                diagonalGrid[grid.getColumnNumber()+grid.getRowNumber()+i][j]=grid.getGrid()[i+j][j];
////            }
////            if(i>=grid.getColumnNumber()-grid.getRowNumber()+1) {
////                diagonalGrid[(grid.getColumnNumber() + grid.getRowNumber() - 1)*2-i] = new Box[grid.getRowNumber() - i + 1];
////                for (int j=((grid.getColumnNumber()-i+1>(grid.getRowNumber()-1))?grid.getRowNumber()-1:grid.getColumnNumber()-i+1); j>=0;j--) {
////                    diagonalGrid[(grid.getColumnNumber() + grid.getRowNumber() - 1)*2 - i][j] = grid.getGrid()[j][grid.getColumnNumber()-1-i];
////                }
////            }
////        }
//        return diagonalGrid;
//    }

    public boolean isObjectiveOk(){
        return ((!(on_values && on_colors)) && ((!(on_rows && on_columns )) || (on_colors)) && (on_colors || on_values));
    }

}
