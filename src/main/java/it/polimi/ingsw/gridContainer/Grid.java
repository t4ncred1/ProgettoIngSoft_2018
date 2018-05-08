package it.polimi.ingsw.gridContainer;

import com.google.gson.Gson;
import it.polimi.ingsw.Box;
import it.polimi.ingsw.Die;
import it.polimi.ingsw.customException.InvalidOperationException;
import it.polimi.ingsw.customException.NotProperParameterException;

import java.awt.dnd.InvalidDnDOperationException;

public class Grid {
    private final static int rowNumber=4;
    private final static int columnNumber =5;
    private String name;
    private int difficulty;
    private Box[][] gameGrid;

    protected Grid(int difficulty, String name) throws NotProperParameterException {
        final String expectedData= new String("Difficulty should have a value between 3 and 6 (both included)");

        if(name==null) throw new NullPointerException();
        if(difficulty<3||difficulty>6) throw new NotProperParameterException(""+difficulty,expectedData);
        gameGrid= new Box[rowNumber][columnNumber];
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
    protected void createBoxInXY(int x, int y, String constraint) throws NotProperParameterException {
        final String indexOutOfBound = new String("coordinates should be: 0<=x<=3 and 0<=y<=4");
        final String expectedEmptyBox = new String("other coordinates: in this place already exist a Box!");
        //constraint == null isn't accepted
        if(constraint==null) throw new NullPointerException();
        //index out of bound for gameGrid;
        if(x<0||x>rowNumber-1||y<0||y>columnNumber-1) throw new NotProperParameterException("("+x+","+y+")", indexOutOfBound);
        //this function should be used to just once for each box in gameGrid
        if(gameGrid[x][y]!=null) throw new NotProperParameterException("("+x+","+y+")", expectedEmptyBox);


        try{
            int valueConstraint = Integer.parseInt(constraint);
            this.gameGrid[x][y]= new Box(valueConstraint, x, y);
        }catch (NumberFormatException e) {
            String colorConstraint = constraint;
            if (colorConstraint.equals("none")) {
                this.gameGrid[x][y] = new Box(x, y);
                if(this.gameGrid[x][y].getCoordX()==0||this.gameGrid[x][y].getCoordX()==rowNumber-1||this.gameGrid[x][y].getCoordY()==0||this.gameGrid[x][y].getCoordY()==columnNumber-1)
                    this.gameGrid[x][y].setToOpened();
            } else {
                this.gameGrid[x][y] = new Box(colorConstraint, x, y);
                if(this.gameGrid[x][y].getCoordX()==0||this.gameGrid[x][y].getCoordX()==rowNumber-1||this.gameGrid[x][y].getCoordY()==0||this.gameGrid[x][y].getCoordY()==columnNumber-1)
                    this.gameGrid[x][y].setToOpened();
            }
        }
    }


    public void insertDieInXY(int x, int y, boolean colorCheck, boolean valueCheck, Die die) throws NotProperParameterException, InvalidOperationException {
        final String indexOutOfBound = new String("coordinates should be: 0<=x<=3 and 0<=y<=4");

        if(die == null) throw new NullPointerException();

        if(x<0||x>rowNumber-1||y<0||y>columnNumber-1) throw new NotProperParameterException("("+x+","+y+")", indexOutOfBound);

        if(gameGrid[x][y].tryToInsertDie(colorCheck, valueCheck, die)==false)
            throw new InvalidOperationException();
        else
            gameGrid[x][y].insertDie(die);
    }
}
