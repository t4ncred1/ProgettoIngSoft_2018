package it.polimi.ingsw.server.model.cards;

import it.polimi.ingsw.server.custom_exception.EffectException;
import it.polimi.ingsw.server.custom_exception.InvalidOperationException;
import it.polimi.ingsw.server.custom_exception.NotValidParameterException;
import it.polimi.ingsw.server.model.MatchModel;
import it.polimi.ingsw.server.model.cards.effects.Effect;
import it.polimi.ingsw.server.model.components.Die;
import it.polimi.ingsw.server.model.components.Grid;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;


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
        this.dieCoordinatesX= new ArrayList<>();
        this.dieCoordinatesY= new ArrayList<>();
        this.dieDestinationCoordinatesX=new ArrayList<>();
        this.dieDestinationCoordinatesY=new ArrayList<>();
        this.setupData(model);
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

    private void use() {
        this.used = true;
    }

    public void setupData(MatchModel model){
        effects.forEach(effect -> effect.setParameters(model, this));
        this.dieCoordinatesX= new ArrayList<>();
        this.dieCoordinatesY= new ArrayList<>();
        this.dieDestinationCoordinatesX=new ArrayList<>();
        this.dieDestinationCoordinatesY=new ArrayList<>();
        this.dieRemovedFromDicePool=new ArrayList<>();
    }

    public List<Effect> getEffects(){
        return this.effects;
    }

    public void useToolCard() throws EffectException {

        // save all parameters in local variables
        List<Die> dRemovedFromDicePool = dieRemovedFromDicePool.stream().map(Die::new).collect(Collectors.toList());
        List<Integer> dCoordinatesX = new ArrayList<>(this.dieCoordinatesX);
        List<Integer> dCoordinatesY = new ArrayList<>(this.dieCoordinatesY);
        //Grid pGrid = new Grid(this.playerGrid);   this is commented out because it needs to be a link to original player grid.
        List<Integer>dDestinationCoordinatesX = new ArrayList<>(this.dieDestinationCoordinatesX);
        List<Integer>dDestinationCoordinatesY = new ArrayList<>(this.dieDestinationCoordinatesX);


        //EXECUTING EFFECTS
        for (Effect e : effects) {
            e.executeTest();
        }

    //SETTING PARAMETERS TO ORIGINAL VALUE

        this.dieRemovedFromDicePool = dRemovedFromDicePool.stream().map(Die::new).collect(Collectors.toList());
        this.dieCoordinatesX = new ArrayList<>(dCoordinatesX);
        this.dieCoordinatesY = new ArrayList<>(dCoordinatesY);
        //Grid pGrid = new Grid(this.playerGrid);   this is commented out because it needs to be a link to original player grid.
        this.dieDestinationCoordinatesX = new ArrayList<>(dDestinationCoordinatesX);
        this.dieDestinationCoordinatesY = new ArrayList<>(dDestinationCoordinatesY);
        this.roundTrack = new ArrayList<>();
        this.removedDieFromRoundTrack = null;
        effects.forEach(Effect::execute);

    //should set parameters back to original values again.
        this.indexOfDieToBeRemoved = 0;
        this.dieRemovedFromDicePool = null;
        this.increment = false;
        this.dieCoordinatesX = null;
        this.dieCoordinatesY = null;
        this.playerGrid=null;
        this.roundTrackColor = null;
        this.dieDestinationCoordinatesX = null;
        this.dieDestinationCoordinatesY = null;
        this.roundTrack = null;
        this.indexOfRoundTrackDie = 0;
        this.removedDieFromRoundTrack = null;


        this.use();
    }


////////////////////////SETTERS & GETTERS
    public void setIndexToBeRemoved(int index){
        indexOfDieToBeRemoved=index;
    }

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
    }

    public List<Integer> getDieCoordinatesX() {
        return dieCoordinatesX;
    }

    public void addDieCoordinatesX(int dieCoordinatesX) {
        this.dieCoordinatesX.add(dieCoordinatesX);
    }

    public  List<Integer> getDieCoordinatesY() {
        return dieCoordinatesY;
    }

    public void addDieCoordinatesY(int dieCoordinatesY) {
        this.dieCoordinatesY.add(dieCoordinatesY);
    }

    public Grid getPlayerGrid() {
        return playerGrid;
    }

    public void setPlayerGrid(Grid playerGrid) {
        this.playerGrid = playerGrid;
    }

    public List<Integer> getDieDestinationCoordinatesX() {
        return dieDestinationCoordinatesX;
    }

    public void addDieDestinationCoordinatesX(int dieDestinationCoordinatesX) {
        this.dieDestinationCoordinatesX.add(dieDestinationCoordinatesX);
    }

    public List<Integer> getDieDestinationCoordinatesY() {
        return this.dieDestinationCoordinatesY;
    }

    public void addDieDestinationCoordinatesY(int dieDestinationCoordinatesY) {
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
    }

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