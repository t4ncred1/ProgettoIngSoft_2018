package it.polimi.ingsw.server.model.cards.effects;

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
    public void executeTest() throws Exception {
        ArrayList<Die> rt = new ArrayList<>();
        for(Die d : model.getRoundTrack()){
            rt.add(new Die(d));
        }
        if (toolCard.getIndexOfRoundTrackDie()>=rt.size() || toolCard.getIndexOfRoundTrackDie()<0)throw new NotValidParameterException("Index Of RoundTrack Die To Be Removed in toolcard: "+toolCard.getTitle()+" is out of bounds.","Should be a value between 0 and RT.size() -1");
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
        try{
            int roundTrackIndex=Integer.parseInt(temp1);
            // TODO: 04/07/2018 call toolCard's proper method (launch exceptions column and row are not in pool)
        } catch (NumberFormatException e){
            throw new NotValidParameterException("Value 1: "+temp1, "Numeric parameters");
        }
    }
}
