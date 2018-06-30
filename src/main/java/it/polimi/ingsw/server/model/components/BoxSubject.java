package it.polimi.ingsw.server.model.components;

public interface BoxSubject {
    /**
     * Add a new observer to a list of observers.
     *
     * @param o Observer to be added to a list of observers.
     */
    void register (BoxObserver o);
    /**
     * Update a list of observers.
     *
     * @param remove True if a die has been removed, false if a die has been added.
     */
    void notifyAllObservers(boolean remove);
}
