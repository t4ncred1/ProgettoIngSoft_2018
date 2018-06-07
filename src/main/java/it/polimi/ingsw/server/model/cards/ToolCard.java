package it.polimi.ingsw.server.model.cards;

import it.polimi.ingsw.server.model.MatchModel;
import it.polimi.ingsw.server.model.cards.Effects.Effect;
import it.polimi.ingsw.server.model.components.Die;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class ToolCard implements Serializable {
    private ArrayList<Effect> effects;


    //TODO Note: Parameters set at runtime should be transient. We don't want to get them from a file.

    //parameters for RemoveDieFromPoolEffect
    private boolean removeAllDiceFromDicePool;
    private int numberOfDieToBeRemoved; //this isn't used if removeAllDiceFromDicePool is true.
    private transient List<Die> dieRemovedFromDicePool;


    public void setModel(MatchModel model){
        for(Effect effect: effects){
            effect.setParameters(model, this);
        }
    }

    public void useToolCard(MatchModel matchModel) {
        //TODO
    }

    //-------------------------------------------------------------
    //Methods for RemoveDieFromPoolEffect
    //-------------------------------------------------------------
    public boolean getRemoveAllDiceFromDicePool() {
        return this.removeAllDiceFromDicePool;
    }

    public int getNumberOfDieToBeRemoved() {
        return this.numberOfDieToBeRemoved;
    }

    public void saveDiceRemoved(List<Die> diceRemoved) {
        dieRemovedFromDicePool= diceRemoved;
    }
}