package it.polimi.ingsw;

public interface DieConstraints {
    public int getColorRestriction();

    public int getValueRestriction();

    public Die getDie();

    public void modifyDie(); //chiama adapter

}
