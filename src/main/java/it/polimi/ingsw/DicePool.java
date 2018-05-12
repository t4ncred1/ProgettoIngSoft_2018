package it.polimi.ingsw;

import java.util.ArrayList;
import it.polimi.ingsw.custom_exception.*;

public class DicePool {
    private ArrayList<Die> pool;
    private ArrayList<String> availableColors;
    private final int diceForEachColor= 18;



    public DicePool(){
        this.pool= new ArrayList<Die>();
        this.availableColors = new ArrayList<String>();
        for(int i=0;i<diceForEachColor; i++) this.availableColors.add(new String("green"));
        for(int i=0;i<diceForEachColor; i++) this.availableColors.add(new String("red"));
        for(int i=0;i<diceForEachColor; i++) this.availableColors.add(new String("blue"));
        for(int i=0;i<diceForEachColor; i++) this.availableColors.add(new String("yellow"));
        for(int i=0;i<diceForEachColor; i++) this.availableColors.add(new String("purple"));
    }

    public void generateDiceForPull(int number) throws NotValidParameterException {
        String randomAvaiableColor;
        int randomDieValue;
        final String expectedValueType= new String("Value: 3, 4, 5, 6, 7, 8, 9");
        final String strValue;
        if(number>=3&&number<=9){
            for(int i=0; i<number;i++) {
                randomAvaiableColor = availableColors.remove((int) (Math.random() * availableColors.size()));
                randomDieValue = (int) (Math.random() * 6 + 1);
                pool.add(new Die(randomAvaiableColor, randomDieValue));
            }
        } else {
                 strValue=((Integer)number).toString();
                 throw new NotValidParameterException(strValue,expectedValueType);
               }
        }


    public void showDiceInPool(){

    }

    public Die getDieFromPool(int index) throws NotInPoolException {
        if(index>=0&&index<this.pool.size())
            return this.pool.remove(index);
        else
            throw new NotInPoolException();
    }
}
