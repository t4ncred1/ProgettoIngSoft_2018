package it.polimi.ingsw.server.components;

import it.polimi.ingsw.server.custom_exception.InvalidOperationException;
import it.polimi.ingsw.server.custom_exception.LimitValueException;
import it.polimi.ingsw.server.custom_exception.NotValidParameterException;

import java.io.Serializable;

public class Grid implements Serializable {
    private final static int COLUMN_NUMBER =5;
    private final static int ROW_NUMBER =4;
    private String name;
    private int difficulty;
    private Box[][] gameGrid;

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
        if(gameGrid[x][y].tryToInsertDie(colorCheck, valueCheck, die)==false)
            throw new InvalidOperationException();
        else
            gameGrid[x][y].insertDie(die);
    }
    public Box[][] getGrid(){
        return gameGrid.clone();
    }

    public int getColumnNumber(){
        return COLUMN_NUMBER;
    }

    public int getRowNumber(){
        return ROW_NUMBER;
    }

    public void associateBoxes() {
        for(int i=0; i<this.getColumnNumber(); i++){
            for(int j=0; j<this.getRowNumber(); j++){
                if (gameGrid[i][j] == null) throw new NullPointerException();
                if(i>0) {
                    gameGrid[i][j].register(gameGrid[i-1][j]);
                }
                if(j>0){
                    gameGrid[i][j].register(gameGrid[i][j-1]);
                }
                if(i<COLUMN_NUMBER-1){
                    gameGrid[i][j].register(gameGrid[i+1][j]);
                }
                if(j<ROW_NUMBER-1){
                    gameGrid[i][j].register(gameGrid[i][j+1]);
                }
                if(j<ROW_NUMBER-1 && i<COLUMN_NUMBER-1){
                    gameGrid[i][j].register(gameGrid[i+1][j+1]);
                }
                if(j>0 && i!=0){
                    gameGrid[i][j].register(gameGrid[i-1][j-1]);
                }
                if(j>0 && i<COLUMN_NUMBER-1){
                    gameGrid[i][j].register(gameGrid[i+1][j-1]);
                }
                if(i>0 && j<ROW_NUMBER-1){
                    gameGrid[i][j].register(gameGrid[i-1][j+1]);
                }
            }
        }
    }

    public String getStructure() {
        StringBuilder structure= new StringBuilder("");
        for(Box[] i : gameGrid){
            structure.append("{");
            for(Box j : i){
                structure.append("\t");
                structure.append(j.getConstraint());
                structure.append(",");
            }
            structure.append("}\n");
        }
        return structure.toString();
    }

    public Die removeDieFromXY(int x, int y) throws NotValidParameterException, InvalidOperationException {
        final String indexOutOfBound = "coordinates should be: 0<=x<=3 and 0<=y<=4";
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
}
