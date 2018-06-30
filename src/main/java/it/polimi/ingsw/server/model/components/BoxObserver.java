package it.polimi.ingsw.server.model.components;
import it.polimi.ingsw.server.custom_exception.NotValidParameterException;

public interface BoxObserver {
    /**
     * Update an observer.
     *
     * @param remove True if a die has been removed, false if a die has been added.
     * @param die Constraints of the inserted/removed die.
     * @param x Abscissa of the box.
     * @param y Ordinate of the box.
     * @throws NotValidParameterException Thrown when the coordinates of the passed die don't match the ones of a near die.
     */
    void update(boolean remove, DieConstraints die, int x, int y) throws NotValidParameterException;
}
