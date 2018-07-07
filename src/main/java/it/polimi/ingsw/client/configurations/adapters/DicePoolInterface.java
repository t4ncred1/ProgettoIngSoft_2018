package it.polimi.ingsw.client.configurations.adapters;

import it.polimi.ingsw.server.model.components.Die;

import java.util.ArrayList;
import java.util.List;

public abstract class DicePoolInterface {

    private ArrayList<Die> dicePool;

    public DicePoolInterface(List<Die> dice){
        dicePool= (ArrayList<Die>) dice;
    }


    protected List<Die> getDicePool(){
        return this.dicePool;
    }
    public abstract String getDicePoolInterface();
    public Die getDie(int position){
        if(position<0||position>=dicePool.size()) throw new IndexOutOfBoundsException();
        return new Die(dicePool.get(position));
    }

    public int getDicePoolSize(){
        return dicePool.size();
    }

}
