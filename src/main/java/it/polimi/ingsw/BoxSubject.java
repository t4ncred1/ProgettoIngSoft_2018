package it.polimi.ingsw;

public interface BoxSubject {
    void register (BoxObserver o);
    void notifyAllObservers(boolean remove);
}
