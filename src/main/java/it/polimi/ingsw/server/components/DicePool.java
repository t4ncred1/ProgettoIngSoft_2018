package it.polimi.ingsw.server.component_container;

import java.util.ArrayList;
import java.util.List;

import it.polimi.ingsw.server.custom_exception.*;

public class DicePool {
    private ArrayList<Die> pool;
    private ArrayList<String> availableColors;
    private final int diceForEachColor= 18;



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
                randomAvailableColor = availableColors.remove((int) (Math.random() * availableColors.size()));
                randomDieValue = (int) (Math.random() * 6 + 1);
                pool.add(new Die(randomAvailableColor, randomDieValue));
            }
        } else {
                 strValue=((Integer)number).toString();
                 throw new NotValidParameterException(strValue,expectedValueType);
               }
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
    public void removeDieFromPool(int index) throws NotValidParameterException, NotInPoolException {
        this.getDieFromPool(index);
        this.pool.remove(index);
    }
}
