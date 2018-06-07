package it.polimi.ingsw.server.model.components;

import java.io.Serializable;

public interface DieConstraints extends Serializable {

    int getColorRestriction();

    int getValueRestriction();

    Die getDie();

    void modifyDie(); //chiama adapter

}
