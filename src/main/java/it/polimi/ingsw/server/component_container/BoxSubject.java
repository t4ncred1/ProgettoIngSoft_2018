package it.polimi.ingsw.serverPart.component_container;

public interface BoxSubject {
    void register (BoxObserver o);
    void notifyAllObservers(boolean remove);
}
