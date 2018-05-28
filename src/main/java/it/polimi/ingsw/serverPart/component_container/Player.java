package it.polimi.ingsw.serverPart.component_container;

import it.polimi.ingsw.serverPart.card_container.PrivateObjective;
import it.polimi.ingsw.serverPart.custom_exception.InvalidOperationException;
import it.polimi.ingsw.serverPart.custom_exception.NotValidParameterException;

import java.util.ArrayList;
import java.util.List;

public class Player {
        private String username;
        private ArrayList<Grid> gridsSelection;
        private Grid gridSelected;
        private PrivateObjective objective;



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
            if (i<0||i>3) throw new NotValidParameterException("index of the chosen grid: "+i,"Should be 1 or 0");
            if (gridsSelection==null||gridsSelection.size()!=4) throw new InvalidOperationException();
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
}


