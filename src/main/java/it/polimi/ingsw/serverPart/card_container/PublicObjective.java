package it.polimi.ingsw.serverPart.card_container;
import it.polimi.ingsw.serverPart.custom_exception.NotValidParameterException;
import it.polimi.ingsw.serverPart.component_container.*;

import java.util.Arrays;

public class PublicObjective extends Objective{

    private int value;
    private int cardNumber;

    public PublicObjective(String tit, String desc, int card, int points){
        title=tit;
        description=desc;
        cardNumber=card;
        value=points;
    }


    public String toString(){
        StringBuilder build = new StringBuilder("Title = ");
        build.append(this.getTitle());
        build.append("\n");
        build.append("Description = ");
        build.append(this.getDescription());
        build.append(Integer.toString(cardNumber));
        build.append("\n");

        return build.toString();
    }

    @Override
    public int calculatePoints(Grid grid) throws NotValidParameterException { //please note: on_rows and on_columns, if true, must be sided by either on_value or on_color
//        Box[][] actual_grid=grid.getGrid();
//        boolean[] controlArray;
//        boolean valid;
//        int return_value=0;
//
//        if (on_rows&&on_columns){
//            actual_grid=diagonalGridLTR(grid);
//        }
//
//        else if (on_rows) actual_grid = rotatedGrid(grid);
//
//        if (on_rows^on_columns) {
//            if (!(on_colors||on_values)) throw new NotValidParameterException("Public Objective card: "+title, "on_colors or on_values to be true if on_rows or on_columns are active");
//            for (Box[] i : actual_grid) {
//                if (on_colors) controlArray = new boolean[]{false,false,false,false,false};
//                else if (on_values) controlArray = new boolean[]{false,false,false,false,false,false};
//                else throw new NotValidParameterException("Public Objective card: "+title, "on_colors or on_values to be true if on_rows or on_columns are active");
//                valid = true;
//                for (Box j : i) {
//                    try {
//                        if (on_colors) {
//                            if (j.getDie().getColorRestriction() != -1) {
//                                if (controlArray[j.getDie().getColorRestriction()]) valid=false;
//                                controlArray[j.getDie().getColorRestriction()] = true;
//                                System.out.println("ok");
//                            }
//                        }
//                        if (on_values) {
//                            if (j.getDie().getValueRestriction() != -1) {
//                                if (controlArray[j.getDie().getValueRestriction()]) valid=false;
//                                controlArray[j.getDie().getValueRestriction()] = true;
//                            }
//                        }
//                    } catch (NullPointerException e){
//                        valid=false;
//                        System.out.println("what.");
//                    }
//                }
//                if (valid) return_value+=value;
//            }
//        }
//
//        if (on_rows&&on_columns){
//            if (!on_colors) throw new NotValidParameterException("Public Objective card: "+title, "on_colors should be true when both on_rows and on_columns are active");
//            for (Box[] i : actual_grid){
//                //TODO Do the diagonal calulation case. this will be difficult.
//            }
//        }
//        return return_value;
        Box [][] actualGrid = grid.getGrid();
        int returnValue=0;
        switch (cardNumber){
            case 1 :
                actualGrid=rotatedGrid(grid);
            case 2 :
                returnValue=0;
                for(Box[] i : actualGrid) {
                    boolean isColumnOk = true;
                    boolean[] colorVector = new boolean[]{false, false, false, false, false};
                    for (Box j : i) {
                        try {
                            if (j.getDie().getColorRestriction() != -1) {
                                if (colorVector[j.getDie().getColorRestriction()]) {
                                    isColumnOk = false;
                                    break;
                                }
                                colorVector[j.getDie().getColorRestriction()] = true;
                            }
                        } catch (NullPointerException e) {
                            isColumnOk = false;
                        }
                    }
                    if (isColumnOk) returnValue += value;
                }
                break;
            case 9 :
                actualGrid=diagonalGridRTL(grid);
                boolean [][]alreadyUsed = new boolean[grid.getColumnNumber()][grid.getRowNumber()];
                int previousColor;
                int currentColor;
                for (Box[] i : actualGrid){
                    previousColor=-1;
                    for (Box j : i){
                        try {
                            currentColor = j.getDie().getColorRestriction();
                        } catch (NullPointerException e){
                            currentColor=-1;
                        }
                        if (currentColor==previousColor && currentColor!=-1 && !alreadyUsed[j.getCoordX()][j.getCoordY()]){
                            returnValue++;
                        }
                        alreadyUsed[j.getCoordX()][j.getCoordY()] = true;
                        previousColor=currentColor;
                    }
                }

        }
        return returnValue;
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

}
