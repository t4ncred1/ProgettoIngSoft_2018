package it.polimi.ingsw.card_container;
import it.polimi.ingsw.Player;
import it.polimi.ingsw.custom_exception.NotValidParameterException;

public class PrivateObjective extends Objective {
    private String color;
    private final static String TYPE = "private";

    public PrivateObjective(String color)throws NotValidParameterException {
        final String expectedColorType= "Color: red, yellow, green, blue, purple";
        if(color.equals("red")||color.equals("green")||color.equals("yellow")||color.equals("blue")||color.equals("purple"))
        this.color=color.toLowerCase();
        else throw new NotValidParameterException(color,expectedColorType);
    }

    public String getType(){
        return TYPE;
    }


    public String showPrivateObjective(){
        return this.color;
    }


    public void calculatePoints(Player player){
    //todo should check player's points (should be int)
   }


}
