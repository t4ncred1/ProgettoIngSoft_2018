package it.polimi.ingsw;
import it.polimi.ingsw.customException.NotValidParameterException;

public interface BoxObserver {
    void update(boolean remove, DieConstraints die, int x, int y) throws NotValidParameterException;
}
