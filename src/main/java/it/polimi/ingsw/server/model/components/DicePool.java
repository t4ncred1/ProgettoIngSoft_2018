package it.polimi.ingsw.server.model.components;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import it.polimi.ingsw.server.custom_exception.*;

public class DicePool {
    private ArrayList<Die> pool;
    private ArrayList<String> availableColors;
    private final int diceForEachColor= 18;


    public DicePool(DicePool dicePool){
        this.pool = new ArrayList<>();

        for(Die d : dicePool.pool){
            this.pool.add(new Die(d));
        }

        this.availableColors = new  ArrayList<>(dicePool.availableColors);

    }

    /**
     * Constructor for DicePool.
     */
    public DicePool(){
        this.pool= new ArrayList<>();
        this.availableColors = new ArrayList<>();
        for(int i=0;i<diceForEachColor; i++) this.availableColors.add("green");
        for(int i=0;i<diceForEachColor; i++) this.availableColors.add("red");
        for(int i=0;i<diceForEachColor; i++) this.availableColors.add("blue");
        for(int i=0;i<diceForEachColor; i++) this.availableColors.add("yellow");
        for(int i=0;i<diceForEachColor; i++) this.availableColors.add("purple");
    }

    /**
     *
     * @param number Number of dice to be randomly generated.
     * @throws NotValidParameterException Thrown if number is not between 3 and 9 (minimum and maximum number of dice to be generated in a single round).
     */
    public void generateDiceForPull(int number) throws NotValidParameterException {
        String randomAvailableColor;
        int randomDieValue;
        final String expectedValueType= "Value: 3, 4, 5, 6, 7, 8, 9";
        final String strValue;
        if(number>=3&&number<=9){
            for(int i=0; i<number;i++) {
                randomAvailableColor = availableColors.remove((new Random().nextInt(availableColors.size())));
                randomDieValue = (new Random().nextInt(6)+1);
                pool.add(new Die(randomAvailableColor, randomDieValue));
            }
        } else {
                 strValue=((Integer)number).toString();
                 throw new NotValidParameterException(strValue,expectedValueType);
        }
    }

    /**
     *
     * @return A list of strings containing the available colors.
     */
    public List<String> getAvailableColors(){
        return new ArrayList<>(availableColors);
    }

    /**
     *
     * @param color The color to be added to 'AvailableColors'.
     * @param index A randomly generated index of 'AvailableColors' where to remove a color .
     * @throws NotValidParameterException Thrown when 'color' is not of the 5 admitted ones.
     */
    public void swapColor(String color, int index) throws NotValidParameterException {
        if (!DieToConstraintsAdapter.getColorMap().containsKey(color))
            throw new NotValidParameterException("Invalid color string passed", "Must be either red, blue, purple, yellow or green");
        availableColors.add(color);
        availableColors.remove(index);
    }

    /**
     *
     * @return The pool to be shown.
     */
    public List<Die> showDiceInPool(){
        return pool;
    }

    /**
     *
     * @return A copy of the pool to be shown.
     */
    public List<Die> getDicePoolCopy(){
        List<Die> dicePoolCopy= new ArrayList<>();
        pool.forEach(die -> dicePoolCopy.add(new Die(die)));
        return pool;
    }

    /**
     *
     * @param index The index of the pool where to get the die.
     * @return The die chosen.
     * @throws NotInPoolException Thrown when 'index' is out of bounds.
     */
    public Die getDieFromPool(int index) throws NotInPoolException {
        if(index>=0&&index<this.pool.size())
            return this.pool.get(index);
        else
            throw new NotInPoolException();
    }

    /**
     *
     * @param index The index of the pool where to get the die to remove.
     * @throws NotInPoolException Thrown if 'index' position in pool doesn't contain a die.
     */
    public void removeDieFromPool(int index) throws NotInPoolException {
        this.getDieFromPool(index);
        this.pool.remove(index);
    }

    /**
     *
     * @param die The die to be inserted in the pool.
     * @param dpIndex The index of the pool where to place 'die'.
     * @throws NotInPoolException Thrown when 'index' is out of bounds.
     * @throws NotValidParameterException Thrown when 'die' is null.
     */
    public void insertDieInPool(Die die, int dpIndex) throws NotValidParameterException {
        if (die==null) throw new NotValidParameterException("die: null","a valid die");
        if (dpIndex>pool.size()||dpIndex<0) throw new NotValidParameterException("IndexOutOfBounds: "+dpIndex,"A value betweeen");
        pool.add(dpIndex,die);
    }

    /*public void insertDieInPool(Die die) throws NotValidParameterException {
        if (die==null) throw new NotValidParameterException("die: null","a valid die");
        pool.add(die);
    }*/
}
