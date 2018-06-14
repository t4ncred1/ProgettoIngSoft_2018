package it.polimi.ingsw.server.model.components;

import it.polimi.ingsw.server.model.cards.PrivateObjective;
import it.polimi.ingsw.server.custom_exception.InvalidOperationException;
import it.polimi.ingsw.server.custom_exception.NotValidParameterException;

import java.util.ArrayList;
import java.util.List;

public class Player {
        private String username;
        private ArrayList<Grid> gridsSelection;
        private Grid gridSelected;
        private PrivateObjective objective;
        private boolean firstTurn = true;
        private boolean jumpSecondTurn = false;


        public Player(String username){
            gridsSelection=null;
            gridSelected=null;
            objective=null;
            this.username=username;
        }

        public String getUsername(){
            return username;
        }

    public void setGridsSelection(List<Grid> grids) {
            this.gridsSelection=new ArrayList<>(grids);
    }

    public void setGrid(int i) throws NotValidParameterException, InvalidOperationException {
            if (gridsSelection==null) throw new InvalidOperationException();
            if (i<0||i>=gridsSelection.size()) throw new NotValidParameterException("index of the chosen grid: "+i,"Should be a value between 0 and " + (gridsSelection.size()-1));
            gridSelected=gridsSelection.get(i);
    }

    public ArrayList<Grid> getGridsSelection(){
            return this.gridsSelection;
    }

    public Grid getSelectedGrid() {
            return this.gridSelected;
    }

    public PrivateObjective getObjective() {
        return objective;
    }

    public void setObjective(PrivateObjective objective) {
        this.objective = objective;
    }

    public boolean hasSelectedAGrid() {
        return this.gridSelected!= null;
    }

    public boolean isFirstTurn() {
        return firstTurn;
    }

    public void setFirstTurn(boolean firstTurn) {
        this.firstTurn = firstTurn;
    }

    public boolean isJumpSecondTurn() {
        return jumpSecondTurn;
    }

    public void setJumpSecondTurn(boolean jumpSecondTurn) {
        this.jumpSecondTurn = jumpSecondTurn;
    }
}


