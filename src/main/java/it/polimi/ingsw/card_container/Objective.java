package it.polimi.ingsw.card_container;

import it.polimi.ingsw.component_container.Grid;
import it.polimi.ingsw.custom_exception.NotValidParameterException;

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
