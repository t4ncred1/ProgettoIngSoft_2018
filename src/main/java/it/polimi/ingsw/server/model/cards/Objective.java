package it.polimi.ingsw.server.model.cards;
import it.polimi.ingsw.server.custom_exception.NotValidParameterException;
import it.polimi.ingsw.server.model.components.Grid;
public abstract class Objective {
    String title;
    String description;

    /**
     *
     * @param grid Player's grid.
     * @return Points earned.
     * @throws NotValidParameterException Thrown when 'grid' is not valid.
     */
    public abstract int calculatePoints(Grid grid) throws NotValidParameterException;

    /**
     *
     * @return Objective card title.
     */
    public String getTitle(){
        return title;
    }

    /**
     *
     * @return Objective card description.
     */
    public String getDescription(){
        return description;
    }

}
