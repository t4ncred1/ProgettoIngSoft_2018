package it.polimi.ingsw.server.model.cards.effects;

import it.polimi.ingsw.server.model.MatchModel;
import it.polimi.ingsw.server.model.cards.ToolCard;

public class InsertDieInDicePoolEffect implements Effect {
    private MatchModel model;
    private ToolCard toolCard;

    public void setParameters(MatchModel matchModel, ToolCard toolCard){
        this.model=matchModel;
        this.toolCard=toolCard;
    }

    @Override
    public void execute(){
        // TODO: 07/06/2018  
    }
}
