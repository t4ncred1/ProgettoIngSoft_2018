package it.polimi.ingsw.server.components;

public interface DieConstraints {
    int getColorRestriction();

    int getValueRestriction();

    Die getDie();

    void modifyDie(); //chiama adapter

}
