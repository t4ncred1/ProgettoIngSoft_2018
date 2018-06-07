package it.polimi.ingsw.server.model.cards.effects;

import it.polimi.ingsw.server.model.MatchModel;
import it.polimi.ingsw.server.model.cards.ToolCard;
import it.polimi.ingsw.server.model.components.Die;

import java.util.ArrayList;
import java.util.List;

public class RemoveDieFromPoolEffect implements Effect {
    private MatchModel model;
    private ToolCard toolCard;

    @Override
    public void setParameters(MatchModel matchModel, ToolCard toolCard) {
        this.model=matchModel;
        this.toolCard=toolCard;
    }

    @Override
    public void execute(){
        boolean removeAllDiceFromDicePool= toolCard.getRemoveAllDiceFromDicePool();
        List<Die> diceRemoved = new ArrayList<>();
        if(removeAllDiceFromDicePool){
            //TODO use proper methods to do this.
            // DicePool dicePool= matchModel.getDicePool();
            // for(Die die: dicePool) {
            //   diceRemoved.add(dicePool.remove(0));
            //}
        }
        else{
            int numberOfDieToBeRemoved= toolCard.getNumberOfDieToBeRemoved();
            //for (int i=0;i<numberOfDieToBeRemoved;i++){
            //   diceRemoved.add(dicePool.remove(0));
            //}
        }
        toolCard.saveDiceRemoved(diceRemoved);
    }
}
