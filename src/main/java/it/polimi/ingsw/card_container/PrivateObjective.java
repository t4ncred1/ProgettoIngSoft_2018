package it.polimi.ingsw.card_container;
import it.polimi.ingsw.custom_exception.NotValidParameterException;

public class PrivateObjective implements Objective {
    private String color;
    private final static String type= "private";

    public PrivateObjective(String color)throws NotValidParameterException {
        final String expectedColorType= new String("Color: red, yellow, green, blue, purple");
        if(color.equals("red")||color.equals("green")||color.equals("yellow")||color.equals("blue")||color.equals("purple"))
        this.color=color.toLowerCase();
        else throw new NotValidParameterException(color,expectedColorType);
    }

    public String getType(){
        return this.type;
    }


    public String showPrivateObjective(){
        return this.color;
    }

//    @Override
//    public int calculatePoints(Player player){
//      return player.checkPrivateObjPoints(this.color);
//    }


}
