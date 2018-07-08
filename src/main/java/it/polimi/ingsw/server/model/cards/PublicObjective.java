package it.polimi.ingsw.server.model.cards;
import it.polimi.ingsw.server.model.components.*;

import java.util.Arrays;

public class PublicObjective extends Objective{

    private int value;
    private int cardNumber;

    /**
     * Constructor for PublicObjective.
     * Note: this method is not used except in tests.
     * @param tit the public objective's title.
     * @param desc the public objective's description.
     * @param card the public objective's card number.
     * @param points the number of points assigned to player when calculatePoints is called.
     */
    public PublicObjective(String tit, String desc, int card, int points){
        title=tit;
        description=desc;
        cardNumber=card;
        value=points;
    }

    /**
     * Constructor for PublicObjective.
     * @param objective is a notNull existing objective.
     */
    public PublicObjective(PublicObjective objective){
        title=objective.getTitle();
        description=objective.getDescription();
        cardNumber=objective.getCardNumber();
        value=objective.getValue();
    }

    /**
     *
     * @return the card number.
     */
    private int getCardNumber() {
        return this.cardNumber;
    }

    /**
     *
     * @return A string containing the current values of the fields Description, CardNumber and Value, formatted nicely.
     */
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

    /**
     *
     * @return the point a user would score if he accomplished this objective.
     */
    public int getValue(){
        return this.value;
    }

    @Override
    public int calculatePoints(Grid grid) { //please note: on_rows and on_columns, if true, must be sided by either on_value or on_color

        Box [][] actualGrid = grid.getGrid();
        int returnValue=0;
        switch (cardNumber) {

            case 1:
                actualGrid = symmetricGrid(grid);

            case 2:
                returnValue = 0;
                for (Box[] i : actualGrid) {
                    boolean isColumnOk = true;
                    boolean[] colorVector = new boolean[]{false, false, false, false, false};
                    for (Box j : i) {
                        try {
                                if (colorVector[j.getDieConstraint().getColorRestriction()]) {
                                    isColumnOk = false;
                                    break;
                                }
                                colorVector[j.getDieConstraint().getColorRestriction()] = true;
                        } catch (NullPointerException e) {
                            isColumnOk = false;
                        }
                    }
                    if (isColumnOk) returnValue += value;
                }
                break;

            case 3:
                actualGrid = symmetricGrid(grid);

            case 4:
                returnValue = 0;
                for (Box[] i : actualGrid) {
                    boolean isColumnOk = true;
                    boolean[] valueVector = new boolean[]{false, false, false, false, false, false};
                    for (Box j : i) {
                        try {
                                if (valueVector[j.getDieConstraint().getValueRestriction()]) {
                                    isColumnOk = false;
                                    break;
                                }
                                valueVector[j.getDieConstraint().getValueRestriction()] = true;
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
                boolean[][] alreadyUsed = new boolean[grid.getColumnNumber()][grid.getRowNumber()];
                returnValue = cicleThroughDiagonalgrid(alreadyUsed, diagonalGridLTR(grid)) + cicleThroughDiagonalgrid(alreadyUsed, diagonalGridRTL(grid));
                break;
            default: // case 10
                returnValue=findColorArraysInGrid(grid.getGrid(), new int[]{0,1,2,3,4})*value;
                break;
        }
        return returnValue;
    }

    /**
     * Used in CalculatePoints for Public Objective 10.
     * @param actualGrid grid on which the groups are counted.
     * @param colorConstraintsToCheck constraint indexes of the colors that must be grouped.
     * @return The number of groups (not interwoven) of colors specified in colorConstraintToCheck.
     * @see it.polimi.ingsw.server.model.components.DieToConstraintsAdapter to get the association between constraintIndexes and colors.
     */

    private int findColorArraysInGrid( Box[][] actualGrid, int[] colorConstraintsToCheck){
        boolean[][] alreadyUsed = new boolean[actualGrid.length][actualGrid[0].length]; //here we assume the matrix is rectangular
        int returnValue=0;
        for(int i=0; i<actualGrid.length;i++){
            for (int j=0; j<actualGrid[0].length;j++){
                try {
                    if (actualGrid[i][j].getDieConstraint().getColorRestriction() == colorConstraintsToCheck[0] && !alreadyUsed[i][j]) {
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

    /**
     * Method only called by findColorArraysInGrid.
     * @param alreadyUsed matrix representing the boxes already checked to find color groups.
     * @param actualGrid grid from which the results are given.
     * @param colorConstraints constraint indexes of the colors that must be grouped.
     * @return true if, between the boxes not already checked, there's a group of the same colors given by colorConstraints.
     * @see #findSingleColorArrayInGrid(boolean[][], Box[][], int[])
     */
    private boolean findSingleColorArrayInGrid(boolean[][] alreadyUsed, Box[][] actualGrid, int[] colorConstraints){
        for(int i=0; i<actualGrid.length;i++) {
            for (int j = 0; j < actualGrid[0].length; j++) {
                try{
                    if (actualGrid[i][j].getDieConstraint().getColorRestriction() == colorConstraints[0] && !alreadyUsed[i][j]){
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

    /**
     *  Used in CalculatePoints for Public Objective 5,6,7,8.
     * @param actualGrid grid from which the results are given.
     * @param valuesToCheck array of integers that the method will try to find in actualGrid and group.
     * @return number of groups of numbers in valuesToCheck found in actualGrid.
     */
    private int findArraysInGrid( Box[][] actualGrid, int[] valuesToCheck){
        boolean[][] alreadyUsed = new boolean[actualGrid.length][actualGrid[0].length]; //here we assume the matrix is rectangular
        int returnValue=0;
        for(int i=0; i<actualGrid.length;i++){
            for (int j=0; j<actualGrid[0].length;j++){
                try {
                    if (actualGrid[i][j].getDieConstraint().getValueRestriction() + 1 == valuesToCheck[0] && !alreadyUsed[i][j]) {
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

    /**
     * Method only used by findArraysInGrid.
     * @param alreadyUsed matrix representing the boxes already checked to find number groups.
     * @param actualGrid grid from which the results are given.
     * @param valuesToCheck array of integers that the method will try to find in actualGrid and group.
     * @return true if, between the boxes not already checked, there's a group of the same numbers given by valuesToCheck.
     * @see #findArraysInGrid(Box[][], int[])
     */
    private boolean findSingleArrayInGrid(boolean[][] alreadyUsed, Box[][] actualGrid, int[] valuesToCheck){
        for(int i=0; i<actualGrid.length;i++) {
            for (int j = 0; j < actualGrid[0].length; j++) {
                try{
                    if (actualGrid[i][j].getDieConstraint().getValueRestriction() + 1 == valuesToCheck[0] && !alreadyUsed[i][j]){
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

    /**
     * Used in calculatePoints for public objective 9.
     * @param alreadyUsed matrix rapresenting already checked boxes.
     * @param actualGrid original Box matrix on which the method is applied.
     * @return number of same color boxes on the same column
     *
     */
    private int cicleThroughDiagonalgrid(boolean[][] alreadyUsed, Box[][] actualGrid){
        int previousColor;
        int currentColor;
        int returnValue=0;
        boolean second = true;
        for (Box[] i : actualGrid){
            previousColor=-1;
            for (Box j : i){
                try {
                    currentColor = j.getDieConstraint().getColorRestriction();
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

    /**
     * Used to create a copy of the grid reflected on its diagonal.
     * @param grid grid to reflect.
     * @return Array of boxes representing the grid passed reflectd on its upper left to lower right axis.
     */
    private Box[][] symmetricGrid(Grid grid){
        Box[][] rotatedGrid = new Box[grid.getRowNumber()][grid.getColumnNumber()];
        for (int i=0;i<grid.getColumnNumber();i++){
            for(int j=0;j<grid.getRowNumber();j++){
                rotatedGrid[j][i]=grid.getGrid()[i][j];
            }
        }
        return rotatedGrid;
    }

    /**
     * @param grid grid analyzed to get the resulting grid.
     * @return a Box matrix representing the grid passed as parameter but modified to have, on its columns, the left to right diagonals of the original grid.
     */
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

    /**
     * @param grid grid analyzed to get the resulting grid.
     * @return a Box matrix representing the grid passed as parameter but modified to have, on its columns, the right to left diagonals of the original grid.
     */

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
