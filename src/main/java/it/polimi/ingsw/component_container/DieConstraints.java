package it.polimi.ingsw.component_container;

import it.polimi.ingsw.component_container.Die;

public interface DieConstraints {
    public int getColorRestriction();

    public int getValueRestriction();

    public Die getDie();

    public void modifyDie(); //chiama adapter

}
