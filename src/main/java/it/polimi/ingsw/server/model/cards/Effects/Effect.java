package it.polimi.ingsw.server.model.cards.Effects;

import it.polimi.ingsw.server.model.MatchModel;
import it.polimi.ingsw.server.model.cards.ToolCard;

public interface Effect {
    void setParameters(MatchModel matchModel, ToolCard toolCard);
    void execute();
}
