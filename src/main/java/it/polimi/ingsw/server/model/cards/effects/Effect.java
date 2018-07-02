package it.polimi.ingsw.server.model.cards.effects;

import it.polimi.ingsw.server.custom_exception.NotValidParameterException;
import it.polimi.ingsw.server.model.MatchModel;
import it.polimi.ingsw.server.model.cards.ToolCard;

import java.io.Serializable;

public interface Effect extends Serializable {
    /**
     * Setter for effect's parameters.
     *
     * @param matchModel Matchmodel.
     * @param toolCard Tool card.
     */
    void setParameters(MatchModel matchModel, ToolCard toolCard);

    /**
     * Effect run.
     */
    void execute();

    /**
     * Effect test run. All executeTest are run before any execute.
     *
     * @throws Exception Thrown when execution fails.
     */
    void executeTest() throws Exception;

    /**
     *
     * @return Effect's name.
     */
    String getName();
}
