package it.polimi.ingsw.server.model.components;
import it.polimi.ingsw.server.custom_exception.NotValidParameterException;

public interface BoxObserver {
    void update(boolean remove, DieConstraints die, int x, int y) throws NotValidParameterException;
}
