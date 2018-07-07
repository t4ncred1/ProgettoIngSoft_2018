package it.polimi.ingsw.server.model.cards.effects;

import it.polimi.ingsw.server.custom_exception.EffectException;
import it.polimi.ingsw.server.custom_exception.NotValidParameterException;
import it.polimi.ingsw.server.model.MatchModel;
import it.polimi.ingsw.server.model.cards.ToolCard;
import it.polimi.ingsw.server.model.components.Die;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SwapRTDieAndDPDieEffect implements Effect{
    private transient MatchModel model;
    private transient ToolCard toolCard;
    private static final String NAME = "SwapRTDieAndDPDieEffect";

    @Override
    public void setParameters(MatchModel matchModel, ToolCard toolCard) {
        this.model=matchModel;
        this.toolCard=toolCard;
    }

    @Override
    public void executeTest() throws EffectException {
        Die roundTrackDie = toolCard.getRemovedDieFromRoundTrack();
        if ( toolCard.getDiceRemoved().isEmpty()){
            Logger.getLogger(this.getClass().getName()).log(Level.WARNING,"Invalid toolcard "+toolCard.getTitle());
            throw new EffectException("There is no die removed from dicepool!");
        }
        Die dicePoolDie = toolCard.getDiceRemoved().remove(0);
        ArrayList<Die>removedDice = new ArrayList<>();
        removedDice.add(roundTrackDie);
        toolCard.saveDiceRemoved(removedDice);
        toolCard.setRemovedDieFromRoundTrack(dicePoolDie);
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public void setToolCardParams(List<String> params) throws NotValidParameterException {
        if(!params.isEmpty()) throw new NotValidParameterException(params.toString(),"expected a void list");
    }

    @Override
    public void execute() {
        Die roundTrackDie = toolCard.getRemovedDieFromRoundTrack();
        Die dicePoolDie = toolCard.getDiceRemoved().remove(0);
        ArrayList<Die>removedDice = new ArrayList<>();
        removedDice.add(roundTrackDie);
        toolCard.saveDiceRemoved(removedDice);
        toolCard.setRemovedDieFromRoundTrack(dicePoolDie);
    }

}
