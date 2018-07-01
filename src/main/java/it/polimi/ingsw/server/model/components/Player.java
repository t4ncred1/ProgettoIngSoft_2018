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
        private int favorTokens;
        private boolean firstTurn = true;
        private boolean jumpSecondTurn = false;

    /**
     * Constructor for player.
     *
     * @param username Username for the player to create.
     */
    public Player(String username){
            gridsSelection=null;
            gridSelected=null;
            objective=null;
            favorTokens = 0;
            this.username=username;
        }

    /**
     *
     * @return Username of the player.
     */
    public String getUsername(){
            return username;
        }

    /**
     *
     * Setter for gridsSelection.
     *
     * @param grids A list of 4 grids.
     */
    public void setGridsSelection(List<Grid> grids) {
            this.gridsSelection=new ArrayList<>(grids);
    }

    /**
     * Method that sets gridSelected as the grid chosen by the player.
     *
     * @param i Number of the grid selected by the player.
     * @throws NotValidParameterException Thrown if 'i' is not between 0 and gridSelection' size.
     * @throws InvalidOperationException Thrown if gridsSelection is null.
     */
    public void setGrid(int i) throws NotValidParameterException, InvalidOperationException {
            if (gridsSelection==null) throw new InvalidOperationException();
            if (i<0||i>=gridsSelection.size()) throw new NotValidParameterException("index of the chosen grid: "+i,"Should be a value between 0 and " + (gridsSelection.size()-1));
            gridSelected=gridsSelection.get(i);
            setFavorTokens(gridSelected.getDifficulty());
    }

    /**
     * Getter for gridsSelection.
     *
     * @return A list of 4 grids unique for each player: he will choose one of them.
     */
    public ArrayList<Grid> getGridsSelection(){
            return this.gridsSelection;
    }

    /**
     * Getter for gridSelected.
     *
     * @return The selected grid.
     */
    public Grid getSelectedGrid() {
            return this.gridSelected;
    }

    /**
     * Getter for player's private objective.
     *
     * @return Player's private objective.
     */
    public PrivateObjective getObjective() {
        return objective;
    }

    /**
     * Setter for player's private objective.
     *
     * @param objective A private objective.
     */
    public void setObjective(PrivateObjective objective) {
        this.objective = objective;
    }

    /**
     *
     * @return True if a grid has been selected by the player, false if not.
     */
    public boolean hasSelectedAGrid() {
        return this.gridSelected!= null;
    }

    /**
     *
     * @return True if player's turn is his first one, false if not.
     */
    public boolean isFirstTurn() {
        return firstTurn;
    }

    /**
     * Setter for firstTurn.
     *
     * @param firstTurn True if player's turn is his first one, false if not.
     */
    public void setFirstTurn(boolean firstTurn) {
        this.firstTurn = firstTurn;
    }

    /**
     *
     * @return True if player has to skip his second turn (tool card effect), false if not.
     */
    public boolean isJumpSecondTurn() {
        return jumpSecondTurn;
    }

    /**
     * Setter for jumpSecondTurn.
     *
     * @param jumpSecondTurn True if player has to skip his second turn (tool card effect), false if not.
     */
    public void setJumpSecondTurn(boolean jumpSecondTurn) {
        this.jumpSecondTurn = jumpSecondTurn;
    }

    /**
     * Setter for player's favor tokens.
     *
     * @param favorTokens Difficulty of the grid selected.
     */
    public void setFavorTokens(int favorTokens) {
        this.favorTokens = favorTokens;
    }

    /**
     * Getter for player's favor tokens.
     *
     * @return Player's favor tokens.
     */
    public int getFavorTokens() {
        return favorTokens;
    }
}


