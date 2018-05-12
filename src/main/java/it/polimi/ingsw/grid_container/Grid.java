package it.polimi.ingsw.grid_container;

import it.polimi.ingsw.Box;
import it.polimi.ingsw.Die;
import it.polimi.ingsw.custom_exception.InvalidOperationException;
import it.polimi.ingsw.custom_exception.NotValidParameterException;

public class Grid {
    private final static int ROW_NUMBER =4;
    private final static int COLUMN_NUMBER =5;
    private String name;
    private int difficulty;
    private Box[][] gameGrid;

    protected Grid(int difficulty, String name) throws NotValidParameterException {
        final String expectedData= "Difficulty should have a value between 3 and 6 (both included)";

        if(name==null) throw new NullPointerException();
        if(difficulty<3||difficulty>6) throw new NotValidParameterException(""+difficulty,expectedData);
        gameGrid= new Box[ROW_NUMBER][COLUMN_NUMBER];
        this.difficulty= difficulty;
        this.name=name;
    }

    //Observer
    public String getName() {
        return name;
    }

    public int getDifficulty() {
        return difficulty;
    }

    //Modifier
    protected void createBoxInXY(int x, int y, String constraint) throws NotValidParameterException {
        final String indexOutOfBound = "coordinates should be: 0<=x<=3 and 0<=y<=4";
        final String expectedEmptyBox = "other coordinates: in this place already exist a Box!";
        //constraint == null isn't accepted
        if(constraint==null) throw new NullPointerException();
        //index out of bound for gameGrid
        if(x<0||x> ROW_NUMBER -1||y<0||y> COLUMN_NUMBER -1) throw new NotValidParameterException("("+x+","+y+")", indexOutOfBound);
        //this function should be used to just once for each box in gameGrid
        if(gameGrid[x][y]!=null) throw new NotValidParameterException("("+x+","+y+")", expectedEmptyBox);


        try{
            int valueConstraint = Integer.parseInt(constraint);
            this.gameGrid[x][y]= new Box(valueConstraint, x, y);
        }catch (NumberFormatException e) {
            String colorConstraint = constraint;
            if (colorConstraint.equals("none")) {
                this.gameGrid[x][y] = new Box(x, y);
                if(this.gameGrid[x][y].getCoordX()==0||this.gameGrid[x][y].getCoordX()== ROW_NUMBER -1||this.gameGrid[x][y].getCoordY()==0||this.gameGrid[x][y].getCoordY()== COLUMN_NUMBER -1)
                    this.gameGrid[x][y].setToOpened();
            } else {
                this.gameGrid[x][y] = new Box(colorConstraint, x, y);
                if(this.gameGrid[x][y].getCoordX()==0||this.gameGrid[x][y].getCoordX()== ROW_NUMBER -1||this.gameGrid[x][y].getCoordY()==0||this.gameGrid[x][y].getCoordY()== COLUMN_NUMBER -1)
                    this.gameGrid[x][y].setToOpened();
            }
        }
    }


    public void insertDieInXY(int x, int y, boolean colorCheck, boolean valueCheck, Die die) throws NotValidParameterException, InvalidOperationException {
        final String indexOutOfBound = "coordinates should be: 0<=x<=3 and 0<=y<=4";

        if(die == null) throw new NullPointerException();

        if(x<0||x> ROW_NUMBER -1||y<0||y> COLUMN_NUMBER -1) throw new NotValidParameterException("("+x+","+y+")", indexOutOfBound);

        if(gameGrid[x][y].tryToInsertDie(colorCheck, valueCheck, die)==false)
            throw new InvalidOperationException();
        else
            gameGrid[x][y].insertDie(die);
    }
}
