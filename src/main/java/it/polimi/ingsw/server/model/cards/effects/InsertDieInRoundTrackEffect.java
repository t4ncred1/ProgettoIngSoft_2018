package it.polimi.ingsw.server.model.cards.effects;

import it.polimi.ingsw.server.custom_exception.NotValidParameterException;
import it.polimi.ingsw.server.model.MatchModel;
import it.polimi.ingsw.server.model.cards.ToolCard;
import it.polimi.ingsw.server.model.components.Die;

import java.util.ArrayList;
import java.util.List;

public class InsertDieInRoundTrackEffect implements Effect {

    private transient MatchModel model;
    private transient ToolCard toolCard;
    private static final String NAME = "InsertDieInRoundTrackEffect";

    @Override
    public void setParameters(MatchModel matchModel, ToolCard toolCard) {
        this.model = matchModel;
        this.toolCard = toolCard;
    }

    @Override
    public void executeTest() throws Exception {
        ArrayList<Die> rt = new ArrayList<>();
        for (Die d :toolCard.getRoundTrack()){
               rt.add(new Die(d));
        }
        if (toolCard.getRemovedDieFromRoundTrack()==null) throw new NotValidParameterException("Die removed from roundtrack is null when executing "+ NAME,"Should be a die removed from roundtrack.");
        rt.add(toolCard.getIndexOfRoundTrackDie(),toolCard.getRemovedDieFromRoundTrack());
        toolCard.setRoundTrack(rt);
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
        model.getRoundTrack().add(toolCard.getIndexOfRoundTrackDie(),toolCard.getRemovedDieFromRoundTrack());
    }

}
