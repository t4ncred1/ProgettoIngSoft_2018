package it.polimi.ingsw.server.cards;
import it.polimi.ingsw.server.custom_exception.NotValidParameterException;
import it.polimi.ingsw.server.components.Grid;
public abstract class Objective {
    String title;
    String description;
    public int calculatePoints(Grid grid) throws NotValidParameterException {
        return 0;
    }

    public String getTitle(){
        return title;
    }
    public String getDescription(){
        return description;
    }

}
