package it.polimi.ingsw;
import it.polimi.ingsw.customException.NotProperParameterException;

public interface BoxObserver {
    void update(boolean remove, DieCostraints die, int x, int y) throws NotProperParameterException;
}
