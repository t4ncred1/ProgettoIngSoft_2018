package it.polimi.ingsw.client.configurations.adapters;

import it.polimi.ingsw.server.model.cards.effects.Effect;

import java.util.List;

public abstract class EffectInterface {
    Effect effect;
    /**
     * Constructor for EffectInterface.
     *
     * @param effect The effect selected.
     */
    public EffectInterface(Effect effect){
        this.effect=effect;
    }

    public String getName(){
        return effect.getName();
    }

    /**
     *
     * @return A list of strings containing the names of the parameters for this effect.
     */
    public abstract List<String> computeEffect();
}
