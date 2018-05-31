package it.polimi.ingsw.serverPart.card_container;
import it.polimi.ingsw.serverPart.custom_exception.NotValidParameterException;
import it.polimi.ingsw.serverPart.component_container.Grid;
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
