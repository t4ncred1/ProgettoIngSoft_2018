package it.polimi.ingsw;
import it.polimi.ingsw.CustomException.NotProperParameterException;

public class Die {

    private int value;
    private String color;

    public Die (String color, int value) throws NotProperParameterException{
        final String expectedColorType= new String("Color: red, yellow, green, blue, purple");
        final String expectedValueType= new String("Value: 1, 2, 3, 4, 5, 6");
        final String strValue;
        if(color.equals("red")||color.equals("green")||color.equals("yellow")||color.equals("blue")||color.equals("purple"))
           this.color=color.toLowerCase();
        else throw new NotProperParameterException(color,expectedColorType);
        if(value>=1&&value<=6)
            this.value=value;
        else {
            strValue=((Integer)value).toString();
            throw new NotProperParameterException(strValue, expectedValueType);
             }
    }


    public String getColor(){
        return color;
    }

    public int getValue(){
        return value;
    }

    public void modifyDie(){

    }
}

