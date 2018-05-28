package it.polimi.ingsw.serverPart.component_container;

import java.util.ArrayList;

public class Player {
        private String username;
        private ArrayList<Grid> gridsSelection;
        private Grid gridSelected;

        public Player(String username){
            this.username=username;
        }
        public String getUsername(){
            return username;
        }


    public void setGridsSelection(ArrayList<Grid> grids) {
            this.gridsSelection=grids;
    }

    public ArrayList<Grid> getGridsSelection(){
            return this.gridsSelection;
    }

    public Grid getSelectedGrid() {
            return this.gridSelected;
    }
}


