package it.polimi.ingsw.server.model.cards;
import it.polimi.ingsw.server.model.components.*;

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
        build.append("\n");
        build.append("Card = ");
        build.append(Integer.toString(cardNumber));
        build.append("\n");
        build.append("Points = ");
        build.append(Integer.toString(value));
        build.append("\n");

        return build.toString();
    }

    @Override
    public int calculatePoints(Grid grid) { //please note: on_rows and on_columns, if true, must be sided by either on_value or on_color

        Box [][] actualGrid = grid.getGrid();
        int returnValue=0;
        switch (cardNumber) {

            case 1:
                actualGrid = rotatedGrid(grid);

            case 2:
                returnValue = 0;
                for (Box[] i : actualGrid) {
                    boolean isColumnOk = true;
                    boolean[] colorVector = new boolean[]{false, false, false, false, false};
                    for (Box j : i) {
                        try {
                                if (colorVector[j.getDie().getColorRestriction()]) {
                                    isColumnOk = false;
                                    break;
                                }
                                colorVector[j.getDie().getColorRestriction()] = true;
                        } catch (NullPointerException e) {
                            isColumnOk = false;
                        }
                    }
                    if (isColumnOk) returnValue += value;
                }
                break;

            case 3:
                actualGrid = rotatedGrid(grid);

            case 4:
                returnValue = 0;
                for (Box[] i : actualGrid) {
                    boolean isColumnOk = true;
                    boolean[] valueVector = new boolean[]{false, false, false, false, false, false};
                    for (Box j : i) {
                        try {
                                if (valueVector[j.getDie().getValueRestriction()]) {
                                    isColumnOk = false;
                                    break;
                                }
                                valueVector[j.getDie().getValueRestriction()] = true;
                        } catch (NullPointerException e) {
                            isColumnOk = false;
                        }
                    }
                    if (isColumnOk) returnValue += value;
                }
                break;
            case 5:
                returnValue=findArraysInGrid(grid.getGrid(), new int[]{1,2})*value;
                break;
            case 6:
                returnValue=findArraysInGrid(grid.getGrid(), new int[]{3,4})*value;
                break;
            case 7:
                returnValue=findArraysInGrid(grid.getGrid(), new int[]{5,6})*value;
                break;
            case 8:
                returnValue=findArraysInGrid(grid.getGrid(), new int[]{1,2,3,4,5,6})*value;
                break;
            case 9:
                actualGrid = diagonalGridRTL(grid);
                boolean[][] alreadyUsed = new boolean[grid.getColumnNumber()][grid.getRowNumber()];
                returnValue = cicleThroughDiagonalgrid(alreadyUsed, returnValue, actualGrid);
                actualGrid = diagonalGridLTR(grid);
                returnValue = cicleThroughDiagonalgrid(alreadyUsed, returnValue, actualGrid);
                break;
            default: // case 10
                returnValue=findColorArraysInGrid(grid.getGrid(), new int[]{0,1,2,3,4})*value;
                break;
        }
        return returnValue;
    }

    private int findColorArraysInGrid( Box[][] actualGrid, int[] colorConstraintsToCheck){
        boolean[][] alreadyUsed = new boolean[actualGrid.length][actualGrid[0].length]; //here we assume the matrix is rectangular
        int returnValue=0;
        for(int i=0; i<actualGrid.length;i++){
            for (int j=0; j<actualGrid[0].length;j++){
                try {
                    if (actualGrid[i][j].getDie().getColorRestriction() == colorConstraintsToCheck[0] && !alreadyUsed[i][j]) {
                        alreadyUsed[i][j]=true;
                        if (colorConstraintsToCheck.length==1) returnValue++;
                        else {
                            if (findSingleColorArrayInGrid(alreadyUsed, actualGrid, Arrays.copyOfRange(colorConstraintsToCheck, 1, colorConstraintsToCheck.length))) {
                                returnValue++;
                            } else return returnValue;
                        }
                    }
                } catch (NullPointerException e){
                    continue;
                }
            }
        }
        return returnValue;
    }

    private boolean findSingleColorArrayInGrid(boolean[][] alreadyUsed, Box[][] actualGrid, int[] colorConstraints){
        for(int i=0; i<actualGrid.length;i++) {
            for (int j = 0; j < actualGrid[0].length; j++) {
                try{
                    if (actualGrid[i][j].getDie().getColorRestriction() == colorConstraints[0] && !alreadyUsed[i][j]){
                        alreadyUsed[i][j]=true;
                        if(colorConstraints.length==1){
                            return true;
                        }
                        return findSingleColorArrayInGrid(alreadyUsed,actualGrid,Arrays.copyOfRange(colorConstraints, 1, colorConstraints.length));
                    }
                } catch (NullPointerException e){
                    continue;
                }
            }
        }
        return false;
    }

    private int findArraysInGrid( Box[][] actualGrid, int[] valuesToCheck){
        boolean[][] alreadyUsed = new boolean[actualGrid.length][actualGrid[0].length]; //here we assume the matrix is rectangular
        int returnValue=0;
        for(int i=0; i<actualGrid.length;i++){
            for (int j=0; j<actualGrid[0].length;j++){
                try {
                    if (actualGrid[i][j].getDie().getValueRestriction() + 1 == valuesToCheck[0] && !alreadyUsed[i][j]) {
                        alreadyUsed[i][j]=true;
                        if (valuesToCheck.length==1) returnValue++;
                        else {
                            if (findSingleArrayInGrid(alreadyUsed, actualGrid, Arrays.copyOfRange(valuesToCheck, 1, valuesToCheck.length))) {
                                returnValue++;
                            } else return returnValue;
                        }
                    }
                } catch (NullPointerException e){
                    continue;
                }
            }
        }
        return returnValue;

    }

    private boolean findSingleArrayInGrid(boolean[][] alreadyUsed, Box[][] actualGrid, int[] valuesToCheck){
        for(int i=0; i<actualGrid.length;i++) {
            for (int j = 0; j < actualGrid[0].length; j++) {
                try{
                    if (actualGrid[i][j].getDie().getValueRestriction() + 1 == valuesToCheck[0] && !alreadyUsed[i][j]){
                        alreadyUsed[i][j]=true;
                        if(valuesToCheck.length==1){
                            return true;
                        }
                        return findSingleArrayInGrid(alreadyUsed,actualGrid,Arrays.copyOfRange(valuesToCheck, 1, valuesToCheck.length));
                    }
                } catch (NullPointerException e){
                    continue;
                }
            }
        }
        return false;
    }

    private int cicleThroughDiagonalgrid(boolean[][] alreadyUsed, int returnValue, Box[][] actualGrid){
        int previousColor;
        int currentColor;
        boolean second = true;
        for (Box[] i : actualGrid){
            previousColor=-1;
            for (Box j : i){
                try {
                    currentColor = j.getDie().getColorRestriction();
                } catch (NullPointerException e){
                    currentColor=-1;
                }
                if (currentColor==previousColor && currentColor!=-1){
                    if(!alreadyUsed[j.getCoordX()][j.getCoordY()]){
                        returnValue++;
                        alreadyUsed[j.getCoordX()][j.getCoordY()] = true;
                    }
                    if (second) {
                        returnValue++;
                        second=false;
                    }
                }
                else second=true;
                previousColor=currentColor;
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
