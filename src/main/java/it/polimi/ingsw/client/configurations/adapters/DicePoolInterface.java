package it.polimi.ingsw.client.configurations.adapters;

import it.polimi.ingsw.client.configurations.AdapterInterface;
import it.polimi.ingsw.client.configurations.Display;
import it.polimi.ingsw.server.model.components.Die;

import java.util.List;

public abstract class DicePoolInterface implements AdapterInterface {

    private List<Die> dicePool;

    public DicePoolInterface(List<Die> dice){
        dicePool= dice;
    }


    protected List<Die> getDicePool(){
        return this.dicePool;
    }

    @Override
    public abstract Display<Void> getAdapterInterface();

    public Die getDie(int position){
        if(position<0||position>=dicePool.size()) throw new IndexOutOfBoundsException();
        return new Die(dicePool.get(position));
    }

    public int getDicePoolSize(){
        return dicePool.size();
    }

}
