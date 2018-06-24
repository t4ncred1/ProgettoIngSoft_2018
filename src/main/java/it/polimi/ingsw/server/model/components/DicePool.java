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

    public DicePool(){
        this.pool= new ArrayList<>();
        this.availableColors = new ArrayList<>();
        for(int i=0;i<diceForEachColor; i++) this.availableColors.add("green");
        for(int i=0;i<diceForEachColor; i++) this.availableColors.add("red");
        for(int i=0;i<diceForEachColor; i++) this.availableColors.add("blue");
        for(int i=0;i<diceForEachColor; i++) this.availableColors.add("yellow");
        for(int i=0;i<diceForEachColor; i++) this.availableColors.add("purple");
    }

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

    public List<String> getAvailableColors(){
        return new ArrayList<>(availableColors);
    }

    public void swapColor(String color, int index) throws NotValidParameterException {
        if (!color.equals("red") && !color.equals("green") && !color.equals("yellow") && !color.equals("purple") && !color.equals("blue"))
            throw new NotValidParameterException("Invalid color string passed", "Must be either red, blue, purple, yellow or green");
        availableColors.add(color);
        availableColors.remove(index);
    }


    public List<Die> showDiceInPool(){
        return pool;
    }

    public Die getDieFromPool(int index) throws NotInPoolException {
        if(index>=0&&index<this.pool.size())
            return this.pool.get(index);
        else
            throw new NotInPoolException();
    }
    public void removeDieFromPool(int index) throws NotInPoolException {
        this.getDieFromPool(index);
        this.pool.remove(index);
    }

    public void insertDieInPool(Die die, int dpIndex) throws NotValidParameterException {
        if (die==null) throw new NotValidParameterException("die: null","a valid die");
        if (dpIndex>pool.size()) throw new NotValidParameterException("IndexOutOfBounds: "+dpIndex,"A value betweeen");
        pool.add(dpIndex,die);

        /*Die die_to_change,die_changed;
        String color;
        int value_to_change;
        die_to_change=this.getDieFromPool(dpIndex);
        this.RemoveDieFromPoolEffect(dpIndex);
        value_to_change=die_to_change.getValue();
        value_to_change++;
        color=die_to_change.getColor();
        die_changed=new Die(color,value_to_change);
        pool.add(dpIndex,die_changed);*/    /*USEFUL FOR MATCHMODEL*/
    }

    public void insertDieInPool(Die die) throws NotValidParameterException {
        if (die==null) throw new NotValidParameterException("die: null","a valid die");
        pool.add(die);
    }
}
