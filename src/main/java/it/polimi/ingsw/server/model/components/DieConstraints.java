package it.polimi.ingsw.server.model.components;

import java.io.Serializable;

public interface DieConstraints extends Serializable {
    /**
     *
     * @return The color restriction of the die.
     */
    int getColorRestriction();

    /**
     *
     * @return The value restriction of the die.
     */
    int getValueRestriction();

    /**
     *
     * @return The die from a 'DieToConstraintAdapter' object.
     */
    Die getDie();

    //void modifyDie(); //chiama adapter

}
