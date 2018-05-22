package it.polimi.ingsw.serverPart.card_container;
import it.polimi.ingsw.serverPart.custom_exception.NotValidParameterException;
import it.polimi.ingsw.serverPart.component_container.*;
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
    public int calculatePoints(Grid grid) throws NotValidParameterException { //please note: on_rows and on_columns, if true, must be sided by either on_value or on_color
        Box[][] actual_grid=grid.getGrid();
        boolean[] controlArray;
        int return_value=0;

        if (on_rows&&on_columns){
            actual_grid=diagonalGridLTR(grid);
        }

        else if (on_rows) actual_grid = rotatedGrid(grid);

        if (on_rows^on_columns) {
            if (!(on_colors||on_values)) throw new NotValidParameterException("Public Objective card: "+title, "on_colors or on_values to be true if on_rows or on_columns are active");
            for (Box[] i : actual_grid) {
                if (on_colors) controlArray = new boolean[5];
                else if (on_values) controlArray = new boolean[6];
                else throw new NotValidParameterException("Public Objective card: "+title, "on_colors or on_values to be true if on_rows or on_columns are active");
                for (Box j : i) {
                    if (on_colors){
                        if(j.getDie().getColorRestriction()!=-1){
                            if(controlArray[j.getDie().getColorRestriction()]) return 0;
                            controlArray[j.getDie().getColorRestriction()]=true;
                        }
                    }
                    if (on_values){
                        if(j.getDie().getValueRestriction()!=-1){
                            if(controlArray[j.getDie().getValueRestriction()]) return 0;
                            controlArray[j.getDie().getValueRestriction()]=true;
                        }
                    }
                }
                return_value+=value;
            }
        }

        if (on_rows&&on_columns){
            if (!on_colors) throw new NotValidParameterException("Public Objective card: "+title, "on_colors should be true when both on_rows and on_columns are active");
            for (Box[] i : actual_grid){
                //TODO Do the diagonal calulation case. this will be difficult.
            }
        }
        return return_value;
    }

    private Box[][] rotatedGrid(Grid grid){
        Box[][] rotatedGrid = new Box[grid.getRowNumber()][grid.getColumnNumber()];
        for (int i=0;i<grid.getColumnNumber();i++){
            for(int j=0;j<grid.getRowNumber();j++){
                rotatedGrid[j][i]=grid.getGrid()[i][j];
            }
        }
        return rotatedGrid;
    }

    private Box[][] diagonalGridLTR(Grid grid){
        Box[][] actualGrid = grid.getGrid();
        Box[][] diagonalGrid = new Box[grid.getColumnNumber()+grid.getRowNumber()-1][];
        for (int i=0; i<grid.getColumnNumber();i++){

            if ((grid.getColumnNumber()-i)>grid.getRowNumber()-1) diagonalGrid[i+grid.getRowNumber()-1]=new Box[grid.getRowNumber()];
            else {
                diagonalGrid[i + grid.getRowNumber() - 1] = new Box[grid.getColumnNumber() - i];
                diagonalGrid[-i+grid.getRowNumber()]=new Box[grid.getColumnNumber()-i];
            }

            for(int j=0;j<(grid.getColumnNumber()-i)&&(j<grid.getRowNumber());j++){
                   diagonalGrid[i+grid.getRowNumber()-1][j]=actualGrid[i+j][j];
                   if (i>(grid.getColumnNumber()-grid.getRowNumber())) diagonalGrid[-i+grid.getRowNumber()][j]=actualGrid[j][i-grid.getColumnNumber()+grid.getRowNumber()+j];
            }
        }
        return diagonalGrid;
    }

    private Box[][] diagonalGridRTL(Grid grid){
        Box[][] actualGrid = grid.getGrid();
        Box[][] diagonalGrid = new Box[grid.getColumnNumber()+grid.getRowNumber()-1][];
        for (int i=0; i<grid.getColumnNumber();i++){

            if (i>(grid.getColumnNumber()-grid.getRowNumber()+1)) diagonalGrid[i]=new Box[grid.getRowNumber()];
            else {
                diagonalGrid[i] = new Box[i+1];
                diagonalGrid[grid.getRowNumber()+grid.getColumnNumber()-i-2]=new Box[i+1];
            }

            for(int j=i;j>=0&&(j>(i-grid.getRowNumber()));j--){
                diagonalGrid[i][i-j]=actualGrid[j][i-j];
                if (i<=(grid.getColumnNumber()-grid.getRowNumber()+1)) diagonalGrid[-i+grid.getRowNumber()+grid.getColumnNumber()-2][i-j]=actualGrid[grid.getColumnNumber()-i+j-1][grid.getRowNumber()-j-1];
            }
        }
        return diagonalGrid;
    }

    public boolean isObjectiveOk(){
        return ((!(on_values && on_colors)) && ((!(on_rows && on_columns )) || (on_colors)) && (on_colors || on_values));
    }

}
