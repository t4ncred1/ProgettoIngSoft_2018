package it.polimi.ingsw.server.cards;

import it.polimi.ingsw.server.MatchModel;

public class EffectInsertPool implements Effect {
    private MatchModel model;

    public EffectInsertPool (MatchModel matchModel){
        this.model=matchModel;
    }

    @Override
    public void execute(ToolCard actual){
    //TODO

    }
}
