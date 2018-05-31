package it.polimi.ingsw.server.components;
import it.polimi.ingsw.server.custom_exception.NotValidParameterException;

public class Die {

    private int value;
    private String color;

    public Die (String color, int value) throws NotValidParameterException {
        final String expectedColor= "Color: red, yellow, green, blue, purple";
        final String expectedValue= "Value: 1, 2, 3, 4, 5, 6";

        color=color.toLowerCase(); //NB

        if(!(color.equals("red")||color.equals("green")||color.equals("yellow")||color.equals("blue")||color.equals("purple")))
            throw new NotValidParameterException(color,expectedColor);
        else if(!(value>=1&&value<=6))
            throw new NotValidParameterException(""+value,expectedValue);
        else {
            this.color = color;
            this.value = value;
        }
    }


    public String getColor(){
        return color;
    }

    public int getValue(){
        return value;
    }

}

