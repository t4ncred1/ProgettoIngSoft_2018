package it.polimi.ingsw.server.cards;

import it.polimi.ingsw.server.MatchModel;
import it.polimi.ingsw.server.components.Die;


public class ToolCard {
    int[] effectNumbers;
    private Die tobemodifieddie;

    public void useToolCard(MatchModel matchModel) {
        //TODO
    }

    public Die getTobemodifieddie() {
        return tobemodifieddie;
    }

    public void setTobemodifieddie(Die tobemodifieddie) {
        this.tobemodifieddie = tobemodifieddie;
    }
}