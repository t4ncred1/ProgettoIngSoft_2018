package it.polimi.ingsw.component_container;

import it.polimi.ingsw.component_container.BoxObserver;

public interface BoxSubject {
    void register (BoxObserver o);
    void notifyAllObservers(boolean remove);
}
