package it.polimi.ingsw.server.model.components;

public interface BoxSubject {
    void register (BoxObserver o);
    void notifyAllObservers(boolean remove);
}
