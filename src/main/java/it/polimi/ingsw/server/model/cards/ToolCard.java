package it.polimi.ingsw.server.model.cards;

import it.polimi.ingsw.server.model.MatchModel;
import it.polimi.ingsw.server.model.cards.Effects.Effect;
import it.polimi.ingsw.server.model.components.Die;

import java.util.ArrayList;


public class ToolCard {
    private ArrayList<Effect> effects;
    private Die toBeModifiedDie;

    public void setModel(MatchModel model){
        for(Effect effect: effects){
            effect.setParameters(model, this);
        }
    }

    public void useToolCard(MatchModel matchModel) {
        //TODO
    }

    public Die getToBeModifiedDie() {
        return toBeModifiedDie;
    }

    public void setToBeModifiedDie(Die toBeModifiedDie) {
        this.toBeModifiedDie = toBeModifiedDie;
    }
}