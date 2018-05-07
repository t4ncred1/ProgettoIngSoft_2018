package it.polimi.ingsw;
import it.polimi.ingsw.CustomException.NotProperParameterException;

public interface BoxObserver {
    void update(boolean remove, DieCostraints die, int x, int y) throws NotProperParameterException;
}
