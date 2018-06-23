package it.polimi.ingsw.server.model.cards;

import it.polimi.ingsw.server.model.MatchModel;
import it.polimi.ingsw.server.model.cards.effects.Effect;
import it.polimi.ingsw.server.model.components.Die;
import it.polimi.ingsw.server.model.components.Grid;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


public class ToolCard implements Serializable {

    private ArrayList<Effect> effects;

    //Note: Parameters set at runtime should be transient. We don't want to get them from a file.

    //Generic Parameters
    private String title;
    private String description;
    private transient boolean used = false;

    //parameters for RemoveDieFromPoolEffect && InsertDieInPoolEffect
    private boolean removeAllDiceFromDicePool;
    private transient int indexOfDieToBeRemoved; //this isn't used if removeAllDiceFromDicePool is true.
    private transient List<Die> dieRemovedFromDicePool;

    //parameters for IncrementDiceEffect
    private transient boolean increment;

    //parameters for RemoveDieFromGridEffect
    private transient List<Integer> dieCoordinatesX;
    private transient List<Integer> dieCoordinatesY;
    private transient Grid playerGrid=null; //we need to put here the whole grid (and not just the removed die) to make sure the test works
    private transient String roundTrackColor = null;
    private boolean colourInRoundtrack;

    //parameters for InsertDieInGridEffect
    private transient List<Integer> dieDestinationCoordinatesX;
    private transient List<Integer> dieDestinationCoordinatesY;
    private boolean colorCheck;     //note: these two are meant to be read from json.
    private boolean valueCheck;     //
    private boolean openCheck;      //toolCard9

    //parameters for InsertDieInGridEffect, toolcard 8
    private boolean jumpNextTurn; //to be read from file.

    //parameters for ChangeValueDiceEffect
    private boolean mustBeSecondTurn; //to be read from file

    //parameters for RemoveDieFromRoundTrack & InsertDieInRoundTrack
    private transient List<Die> roundTrack;
    private transient int indexOfRoundTrackDie;
    private transient Die removedDieFromRoundTrack;


    ///////////////////////CONSTRUCTOR
    public ToolCard(String title, String description, List<Effect> effects,MatchModel model) {
        this.title = title;
        this.description=description;
        this.effects=new ArrayList<>(effects);
        this.setModel(model);
    }


    ////////////////////////STANDARD METHODS
    public String getTitle() {
        return title;
    }

    public String getDescription(){
        return description;
    }

    public boolean isUsed() {
        return used;
    }

    private void used(boolean used) {
        this.used = used;
    }

    public void setModel(MatchModel model){
        for(Effect effect: effects){
            effect.setParameters(model, this);
        }
    }

    public List<Effect> getEffects(){
        return this.effects;
    }

    public void useToolCard() throws Exception{
        //TODO remember, before doing the executeTest on all effects in cascade I need to copy the original values of all parameters.
        // save all parameters in local variables
        List<Die> dRemovedFromDicePool = new ArrayList<>();
            for(Die d : this.dieRemovedFromDicePool){
                dRemovedFromDicePool.add(new Die(d));
            }
        List<Integer> dCoordinatesX = new ArrayList<>(this.dieCoordinatesX);
        List<Integer> dCoordinatesY = new ArrayList<>(this.dieCoordinatesY);
        //Grid pGrid = new Grid(this.playerGrid);   this is commented out because it needs to be a link to original player grid.
        String rTrackColor = this.roundTrackColor;
        List<Integer>dDestinationCoordinatesX = new ArrayList<>(this.dieDestinationCoordinatesX);
        List<Integer>dDestinationCoordinatesY = new ArrayList<>(this.dieDestinationCoordinatesX);
        int iOfRoundTrackDie = this.indexOfRoundTrackDie;
        Die rDieFromRoundTrack = new Die(this.removedDieFromRoundTrack);


    //EXECUTING EFFECTS
        try {
            for (Effect e : effects) {
                e.executeTest();
            }
        } catch (Exception e){
            Logger logger = Logger.getLogger(getClass().getName());
            logger.log(Level.WARNING, " ", e);
            throw e;
        }

    //SETTING PARAMETERS TO ORIGINAL VALUE

        this.dieRemovedFromDicePool = new ArrayList<>();
        for(Die d :dRemovedFromDicePool){
            dieRemovedFromDicePool.add(new Die(d));
        }
        this.dieCoordinatesX = new ArrayList<>(dCoordinatesX);
        this.dieCoordinatesY = new ArrayList<>(dCoordinatesY);
        //Grid pGrid = new Grid(this.playerGrid);   this is commented out because it needs to be a link to original player grid.
        this.roundTrackColor = rTrackColor;
        this.dieDestinationCoordinatesX = new ArrayList<>(dDestinationCoordinatesX);
        this.dieDestinationCoordinatesY = new ArrayList<>(dDestinationCoordinatesY);
        this.roundTrack = new ArrayList<>();
        this.indexOfRoundTrackDie = iOfRoundTrackDie;
        this.removedDieFromRoundTrack = new Die(rDieFromRoundTrack);

        for (Effect e : effects){
            e.execute();
        }

    //should set parameters back to original values again.
        this.indexOfDieToBeRemoved = 0;
        this.dieRemovedFromDicePool = null;
        this.increment = false;
        this.dieCoordinatesX = null;
        this.dieCoordinatesY = null;
        //Grid pGrid = new Grid(this.playerGrid);   this is commented out because it needs to be a link to original player grid.
        this.roundTrackColor = null;
        this.dieDestinationCoordinatesX = null;
        this.dieDestinationCoordinatesY = null;
        this.roundTrack = null;
        this.indexOfRoundTrackDie = 0;
        this.removedDieFromRoundTrack = null;


        this.used(true);
    }


////////////////////////SETTERS & GETTERS
    public void setIndexToBeRemoved(int index){
        indexOfDieToBeRemoved=index;
    }                           //todo va fatta chiamare al client

    public boolean getRemoveAllDiceFromDicePool() {
        return this.removeAllDiceFromDicePool;
    }

    public int getIndexOfDieToBeRemoved() {
        return this.indexOfDieToBeRemoved;
    }

    public List<Die> getDiceRemoved() { return this.dieRemovedFromDicePool;}

    public void saveDiceRemoved(List<Die> diceRemoved) {
        if (dieRemovedFromDicePool == null) dieRemovedFromDicePool = new ArrayList<>();
        dieRemovedFromDicePool.addAll(diceRemoved);
    }

    public void setDiceRemoved(List<Die> dieList){
        dieRemovedFromDicePool=dieList;
    }

    public boolean isIncrement() {
        return increment;
    }

    public void setIncrement(boolean increment) {
        this.increment = increment;
    }                         //todo va fatta chiamare al client

    public List<Integer> getDieCoordinatesX() {
        return dieCoordinatesX;
    }

    public void addDieCoordinatesX(int dieCoordinatesX) {                                               //todo va fatta chiamare al client
        this.dieCoordinatesX.add(dieCoordinatesX);
    }

    public  List<Integer> getDieCoordinatesY() {
        return dieCoordinatesY;
    }

    public void addDieCoordinatesY(int dieCoordinatesY) {
        this.dieCoordinatesY.add(dieCoordinatesY);
    }       //todo va fatta chiamare al client

    public Grid getPlayerGrid() {
        return playerGrid;
    }

    public void setPlayerGrid(Grid playerGrid) {
        this.playerGrid = playerGrid;
    }

    public List<Integer> getDieDestinationCoordinatesX() {
        return dieDestinationCoordinatesX;
    }

    public void addDieDestinationCoordinatesX(int dieDestinationCoordinatesX) {                         //todo va fatta chiamare al client
        this.dieDestinationCoordinatesX.add(dieDestinationCoordinatesX);
    }

    public List<Integer> getDieDestinationCoordinatesY() {
        return this.dieDestinationCoordinatesY;
    }

    public void addDieDestinationCoordinatesY(int dieDestinationCoordinatesY) {                 //todo va fatta chiamare al client
        this.dieDestinationCoordinatesY.add(dieDestinationCoordinatesY);
    }

    public boolean isColorCheck() {
        return colorCheck;
    }

    public boolean isValueCheck() {
        return valueCheck;
    }

    public boolean isColourInRoundtrack() {
        return colourInRoundtrack;
    }

    public String getRoundTrackColor() {
        return roundTrackColor;
    }

    public void setRoundTrackColor(String roundTrackColor) {
        this.roundTrackColor = roundTrackColor;
    }

    public void setValueCheck(boolean valueCheck) {
        this.valueCheck = valueCheck;
    }

    public void setColorCheck(boolean colorCheck) {
        this.colorCheck = colorCheck;
    }

    public void setColourInRoundtrack(boolean colourInRoundtrack) {
        this.colourInRoundtrack = colourInRoundtrack;
    }

    public void setRemoveAllDiceFromDicePool(boolean removeAllDiceFromDicePool) {
        this.removeAllDiceFromDicePool = removeAllDiceFromDicePool;
    }

    public boolean isJumpNextTurn() {
        return jumpNextTurn;
    }

    public void setJumpNextTurn(boolean jumpNextTurn) {
        this.jumpNextTurn = jumpNextTurn;
    }

    public boolean isMustBeSecondTurn() {
        return mustBeSecondTurn;
    }

    public void setMustBeSecondTurn(boolean mustBeSecondTurn) {
        this.mustBeSecondTurn = mustBeSecondTurn;
    }

    public boolean isOpenCheck() {
        return openCheck;
    }

    public void setOpenCheck(boolean openCheck) {
        this.openCheck = openCheck;
    }

    public Die getRemovedDieFromRoundTrack() {
        return removedDieFromRoundTrack;
    }

    public void setRemovedDieFromRoundTrack(Die removedDieFromRoundTrack) {
        this.removedDieFromRoundTrack = removedDieFromRoundTrack;
    }

    public int getIndexOfRoundTrackDie() {
        return indexOfRoundTrackDie;
    }

    public void setIndexOfRoundTrackDie(int indexOfRoundTrackDie) {
        this.indexOfRoundTrackDie = indexOfRoundTrackDie;
    }   //todo used by client

    public List<Die> getRoundTrack() {
        return roundTrack;
    }

    public void setRoundTrack(List<Die> roundTrack) {
        this.roundTrack = roundTrack;
    }

    public void setDieCoordinatesX(List<Integer> dieCoordinatesX) {
        this.dieCoordinatesX = dieCoordinatesX;
    }

    public void setDieCoordinatesY(List<Integer> dieCoordinatesY) {
        this.dieCoordinatesY = dieCoordinatesY;
    }
}