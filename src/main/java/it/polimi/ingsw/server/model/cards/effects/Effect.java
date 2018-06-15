package it.polimi.ingsw.server.model.cards.effects;

import it.polimi.ingsw.server.custom_exception.NotValidParameterException;
import it.polimi.ingsw.server.model.MatchModel;
import it.polimi.ingsw.server.model.cards.ToolCard;

import java.io.Serializable;

public interface Effect extends Serializable {
    void setParameters(MatchModel matchModel, ToolCard toolCard);
    void execute();
    void executeTest() throws Exception;
    String getName();
}
