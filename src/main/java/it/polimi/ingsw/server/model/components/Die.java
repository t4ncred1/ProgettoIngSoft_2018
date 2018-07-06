package it.polimi.ingsw.server.model.components;
import it.polimi.ingsw.server.custom_exception.NotValidParameterException;

import java.io.Serializable;

public class Die implements Serializable {

    private int value;
    private String color;
    /**
     *
     * @param die The die to clone.
     */
    public Die(Die die){
        this.value = die.value;
        this.color=die.color;
    }

    /**
     * Constructor for Die.
     * @param color The color of the die to create.
     * @param value The value of the die to create.
     * @throws NotValidParameterException Thrown when the color is not one of the 5 colors admitted or the number is not between 1 and 6.
     */
    public Die (String color, int value) throws NotValidParameterException {
        final String expectedColor= "Color: red, yellow, green, blue, purple";
        final String expectedValue= "Value: 1, 2, 3, 4, 5, 6";

        color=color.toLowerCase(); //NB

        if(!DieToConstraintsAdapter.getColorMap().containsKey(color))
            throw new NotValidParameterException(color,expectedColor);
        else if(!(value>=1&&value<=6))
            throw new NotValidParameterException(""+value,expectedValue);
        else {
            this.color = color;
            this.value = value;
        }
    }

    /**
     *
     * @return The color of the die.
     */
    public String getColor(){
        return color;
    }

    /**
     *
     * @return The value of the die.
     */
    public int getValue(){
        return value;
    }

}

