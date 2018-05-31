package it.polimi.ingsw.serverPart.component_container;
import it.polimi.ingsw.serverPart.custom_exception.NotValidParameterException;

public interface BoxObserver {
    void update(boolean remove, DieConstraints die, int x, int y) throws NotValidParameterException;
}
