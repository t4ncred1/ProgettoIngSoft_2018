package it.polimi.ingsw.component_container;
import it.polimi.ingsw.custom_exception.NotValidParameterException;

public interface BoxObserver {
    void update(boolean remove, DieConstraints die, int x, int y) throws NotValidParameterException;
}
