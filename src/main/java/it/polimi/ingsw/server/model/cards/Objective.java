package it.polimi.ingsw.server.model.cards;
import it.polimi.ingsw.server.custom_exception.NotValidParameterException;
import it.polimi.ingsw.server.model.components.Grid;
public abstract class Objective {
    String title;
    String description;
    public abstract int calculatePoints(Grid grid) throws NotValidParameterException;
    public String getTitle(){
        return title;
    }
    public String getDescription(){
        return description;
    }

}
