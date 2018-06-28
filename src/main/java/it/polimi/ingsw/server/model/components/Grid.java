package it.polimi.ingsw.server.model.components;

import it.polimi.ingsw.server.custom_exception.EmptyBoxException;
import it.polimi.ingsw.server.custom_exception.InvalidOperationException;
import it.polimi.ingsw.server.custom_exception.LimitValueException;
import it.polimi.ingsw.server.custom_exception.NotValidParameterException;

import java.io.Serializable;

public class Grid implements Serializable {
    public Box[][] getGameGrid() {
        return gameGrid;
    }

    private static final int COLUMN_NUMBER =5;

    public boolean isFirstInsertion() {
        return firstInsertion;
    }

    private static final int ROW_NUMBER =4;
    private String name;
    private int difficulty;
    private Box[][] gameGrid;
    private transient boolean firstInsertion=true;

    public Grid (Grid aGrid){
        this.name = aGrid.name;
        this.difficulty = aGrid.difficulty;
        Box[][] gGrid = new Box[COLUMN_NUMBER][ROW_NUMBER];
        for(int i=0; i<COLUMN_NUMBER; i++){
            for (int j=0; j<ROW_NUMBER; j++){
                gGrid[i][j]=new Box(aGrid.getGrid()[i][j]);
            }
        }
        this.gameGrid=gGrid;
        firstInsertion=aGrid.firstInsertion;
    }

    public Grid(int difficulty, String name) throws NotValidParameterException {
        final String expectedData= "Difficulty should have a value between 3 and 6 (both included)";

        if(name==null) throw new NullPointerException();
        if(difficulty<3||difficulty>6) throw new NotValidParameterException(""+difficulty,expectedData);
        gameGrid= new Box[COLUMN_NUMBER][ROW_NUMBER];
        this.difficulty= difficulty;
        this.name=name;
    }

    @Override
    public String toString(){
        StringBuilder build = new StringBuilder("nome: ");
        build.append(this.getName());
        build.append("\tDifficoltà: ");
        build.append(this.getDifficulty());
        build.append("\nFirstInsertion: "); build.append(Boolean.toString(firstInsertion));
        build.append("\nBoxes di " +this.getName()+ ":\n");
        int k=0;
        int n;
        for(Box[] i : gameGrid){
            k++;
            n=0;
            build.append(" colonna ");
            build.append(Integer.toString(k));
            build.append(":\n");
            for(Box j : i){
                n++;
                build.append("\t riga ");
                build.append(Integer.toString(n));
                build.append(": \n");
                build.append("\t\t");
                build.append(j.toString());
                build.append("\n");
            }
        }
        return build.toString();
    }
    //Observer
    public String getName() {
        return name;
    }

    public int getDifficulty() {
        return difficulty;
    }

    //Modifier
    public void createBoxInXY(int x, int y, String constraint) throws NotValidParameterException {
        final String indexOutOfBound = "coordinates should be: 0<=x<=3 and 0<=y<=4";
        final String expectedEmptyBox = "other coordinates: in this place already exist a Box!";
        //constraint == null isn't accepted
        if(constraint==null) throw new NullPointerException();
        //index out of bound for gameGrid
        if(x<0||x> COLUMN_NUMBER -1||y<0||y> ROW_NUMBER -1) throw new NotValidParameterException("("+x+","+y+")", indexOutOfBound);
        //this function should be used to just once for each box in gameGrid
        if(gameGrid[x][y]!=null) throw new NotValidParameterException("("+x+","+y+")", expectedEmptyBox);


        try{
            int valueConstraint = Integer.parseInt(constraint);
            this.gameGrid[x][y]= new Box(valueConstraint, x, y);
        }catch (NumberFormatException e) {
            String colorConstraint = constraint;
            if (colorConstraint.equals("none")) {
                this.gameGrid[x][y] = new Box(x, y);
                if(this.gameGrid[x][y].getCoordX()==0||this.gameGrid[x][y].getCoordX()== COLUMN_NUMBER -1||this.gameGrid[x][y].getCoordY()==0||this.gameGrid[x][y].getCoordY()== ROW_NUMBER -1)
                    this.gameGrid[x][y].setToOpened();
            } else {
                this.gameGrid[x][y] = new Box(colorConstraint, x, y);
                if(this.gameGrid[x][y].getCoordX()==0||this.gameGrid[x][y].getCoordX()== COLUMN_NUMBER -1||this.gameGrid[x][y].getCoordY()==0||this.gameGrid[x][y].getCoordY()== ROW_NUMBER -1)
                    this.gameGrid[x][y].setToOpened();
            }
        }
    }



    public void insertDieInXY(int x, int y, boolean colorCheck, boolean valueCheck, Die die) throws NotValidParameterException, InvalidOperationException {
        final String indexOutOfBound = "coordinates should be: 0<=x<=3 and 0<=y<=4";

        if(die == null) throw new NullPointerException();

        if(x<0||x> COLUMN_NUMBER -1||y<0||y> ROW_NUMBER -1) throw new NotValidParameterException("("+x+","+y+")", indexOutOfBound);

        if(gameGrid[x][y]==null) throw new NotValidParameterException("("+x+","+y+")","the box in this position should be initialized. ");
        if(!gameGrid[x][y].tryToInsertDie(colorCheck, valueCheck, true, die))
            throw new InvalidOperationException();
        else
            gameGrid[x][y].insertDie(die);

        if (firstInsertion) {
            setBoxesClosed(x,y);
            firstInsertion=false;
        }
    }

    public void insertDieInXY(int x, int y, boolean colorCheck, boolean valueCheck, boolean openCheck, Die die) throws NotValidParameterException, InvalidOperationException {
        final String indexOutOfBound = "coordinates should be: 0<=x<=3 and 0<=y<=4";

        if(die == null) throw new NullPointerException();

        if(x<0||x> COLUMN_NUMBER -1||y<0||y> ROW_NUMBER -1) throw new NotValidParameterException("("+x+","+y+")", indexOutOfBound);

        if(gameGrid[x][y]==null) throw new NotValidParameterException("("+x+","+y+")","the box in this position should be initialized. ");
        if(!gameGrid[x][y].tryToInsertDie(colorCheck, valueCheck, openCheck, die))
            throw new InvalidOperationException();
        else
            gameGrid[x][y].insertDie(die);

        if (firstInsertion) {
            setBoxesClosed(x,y);
            firstInsertion=false;
        }
    }

    private void setBoxesClosed(int x, int y) {
        for(Box[] column : gameGrid){
            for (Box box : column){
                if (!(box.getCoordY()==y&&box.getCoordX()==x)) box.setToClosed();
                if (box.getCoordX()==x && (box.getCoordY()==y-1 || box.getCoordY()==y+1)) box.setToOpened();
                if ((box.getCoordX()==x+1||box.getCoordX()==x-1) && box.getCoordY()==y) box.setToOpened();
                if ((box.getCoordX()==x+1||box.getCoordX()==x-1) && (box.getCoordY()==y+1||box.getCoordY()==y-1)) box.setToOpened();
            }
        }

    }


    public Box[][] getGrid(){
        return gameGrid.clone();
    }

    public int getColumnNumber(){ return COLUMN_NUMBER;}


    public int getRowNumber(){
        return ROW_NUMBER;
    }


    public String[][] getStructure() {
        String[][] constraints= new String[COLUMN_NUMBER][ROW_NUMBER];
        for(Box[] i : gameGrid){
            for(Box j : i){
                constraints[j.getCoordX()][j.getCoordY()]= j.getConstraint();
            }
        }
        return constraints;
    }

    public void initializeAllObservers() {
        initializeObserverListForEachBox();
        registerObserversForEachBox();
    }

    private void registerObserversForEachBox() {
        for (int column = 0; column < COLUMN_NUMBER; column++) {
            for (int row = 0; row < ROW_NUMBER; row++) {
                if (gameGrid[column][row] == null) throw new NullPointerException();
                if (column > 0) {
                    int underColumn=column-1;
                    gameGrid[column][row].register(gameGrid[underColumn][row]);
                    if (row > 0) gameGrid[column][row].register(gameGrid[underColumn][row - 1]);
                    if (row < ROW_NUMBER-1) gameGrid[column][row].register(gameGrid[underColumn][row + 1]);
                }
                if (column < COLUMN_NUMBER-1) {
                    int overColumn=column+1;
                    gameGrid[column][row].register(gameGrid[overColumn][row]);
                    if (row > 0) gameGrid[column][row].register(gameGrid[overColumn][row - 1]);
                    if (row < ROW_NUMBER-1) gameGrid[column][row].register(gameGrid[overColumn][row + 1]);
                }
                if (row > 0) gameGrid[column][row].register(gameGrid[column][row - 1]);
                if (row < ROW_NUMBER-1) gameGrid[column][row].register(gameGrid[column][row + 1]);

            }
        }
    }

    private void initializeObserverListForEachBox() {
        for (Box[] column : gameGrid) {
            for (Box box : column) {
                if (box == null) throw new NullPointerException();
                box.initializeObserverList();
            }
        }
    }


    public Die removeDieFromXY(int x, int y) throws NotValidParameterException, InvalidOperationException {
        final String indexOutOfBound = "coordinates should be: 0<=x<"+COLUMN_NUMBER +" and 0<=y<"+ROW_NUMBER;
        Die temp;

        if(x<0||x> COLUMN_NUMBER -1||y<0||y> ROW_NUMBER -1) throw new NotValidParameterException("("+x+","+y+")", indexOutOfBound);

        if(gameGrid[x][y]==null) throw new NotValidParameterException("("+x+","+y+")","the box in this position should be initialized. ");

        try {
            temp=gameGrid[x][y].removeDie();
        } catch (LimitValueException e) {
            throw new InvalidOperationException();
        }
        return temp;
    }

    public Die[][] getDice(){
        Die[][] temp= new Die[COLUMN_NUMBER][ROW_NUMBER];
        for(int column=0; column<COLUMN_NUMBER;column++){
            for (int row=0; row<ROW_NUMBER; row++){
                try {
                    temp[column][row]=gameGrid[column][row].getDie();
                } catch (EmptyBoxException e) {
                    temp[column][row]=null;
                }
            }
        }
        return temp;
    }
}
