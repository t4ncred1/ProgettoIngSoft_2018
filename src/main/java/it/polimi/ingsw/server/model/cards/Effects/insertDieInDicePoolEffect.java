package it.polimi.ingsw.server.model.cards.Effects;

import it.polimi.ingsw.server.model.MatchModel;
import it.polimi.ingsw.server.model.cards.Effects.Effect;
import it.polimi.ingsw.server.model.cards.ToolCard;

public class insertDieInDicePoolEffect implements Effect {
    private MatchModel model;
    private ToolCard toolCard;

    public void setParameters(MatchModel matchModel, ToolCard toolCard){
        this.model=matchModel;
        this.toolCard=toolCard;
    }

    @Override
    public void execute(){
    //TODO

    }
}
