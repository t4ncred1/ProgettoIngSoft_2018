package it.polimi.ingsw.serverPart.component_container;

public interface DieConstraints {
    public int getColorRestriction();

    public int getValueRestriction();

    public Die getDie();

    public void modifyDie(); //chiama adapter

}
