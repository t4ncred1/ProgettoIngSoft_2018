package it.polimi.ingsw.server.model.cards.effects;

import it.polimi.ingsw.server.custom_exception.EffectException;
import it.polimi.ingsw.server.custom_exception.InvalidOperationException;
import it.polimi.ingsw.server.custom_exception.NotValidParameterException;
import it.polimi.ingsw.server.model.MatchModel;
import it.polimi.ingsw.server.model.cards.ToolCard;

import java.io.Serializable;
import java.util.List;

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
    void executeTest() throws EffectException;

    /**
     *
     * @return Effect's name.
     */
    String getName();


    /**
     *  Used to set effect parameters in the tool card which is using it.
     *
     * @param params is a List containing all parameters to set.
     * @throws NotValidParameterException Thrown when the list is not valid.
     */
    void setToolCardParams(List<String> params) throws NotValidParameterException;
}
