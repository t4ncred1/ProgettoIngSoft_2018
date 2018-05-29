package it.polimi.ingsw.server.component_container;

public interface BoxSubject {
    void register (BoxObserver o);
    void notifyAllObservers(boolean remove);
}
