package it.polimi.ingsw.server.model.cards.effects;

import it.polimi.ingsw.server.custom_exception.EffectException;
import it.polimi.ingsw.server.custom_exception.NotValidParameterException;
import it.polimi.ingsw.server.model.MatchModel;
import it.polimi.ingsw.server.model.cards.ToolCard;
import it.polimi.ingsw.server.model.components.Die;

import java.util.ArrayList;
import java.util.List;

public class RemoveDieFromRoundTrackEffect implements Effect {

    private transient MatchModel model;
    private transient ToolCard toolCard;
    private static final String NAME = "RemoveDieFromRoundTrackEffect";

    @Override
    public void setParameters(MatchModel matchModel, ToolCard toolCard) {
        this.model = matchModel;
        this.toolCard = toolCard;
    }

    @Override
    public void execute() {
        toolCard.setRemovedDieFromRoundTrack(model.getRoundTrack().remove(toolCard.getIndexOfRoundTrackDie()));
        toolCard.setRoundTrack(model.getRoundTrack());
    }

    @Override
    public void executeTest() throws EffectException {
        ArrayList<Die> rt = new ArrayList<>();
        for(Die d : model.getRoundTrack()){
            rt.add(new Die(d));
        }
        if (toolCard.getIndexOfRoundTrackDie()>=rt.size() || toolCard.getIndexOfRoundTrackDie()<0)throw new EffectException("Index Of RoundTrack Die To Be Removed in toolcard: "+toolCard.getTitle()+" is out of bounds.");
        toolCard.setRemovedDieFromRoundTrack(rt.remove(toolCard.getIndexOfRoundTrackDie()));
        toolCard.setRoundTrack(rt);
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public void setToolCardParams(List<String> params) throws NotValidParameterException {
        final int REMOVING_INDEX=0;
        if(params.isEmpty()) throw new NotValidParameterException("An empty list","A not empty list");
        String temp1=params.remove(REMOVING_INDEX);
        int roundTrackIndex;
        try {
            roundTrackIndex = Integer.parseInt(temp1);
        } catch (NumberFormatException e){
            throw new NotValidParameterException("Value 1: "+temp1, "Numeric parameters");
        }
        toolCard.setIndexOfRoundTrackDie(roundTrackIndex);
    }
}
