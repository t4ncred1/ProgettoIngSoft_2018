package it.polimi.ingsw.server.model.cards.effects;

import it.polimi.ingsw.server.custom_exception.NotValidParameterException;
import it.polimi.ingsw.server.model.MatchModel;
import it.polimi.ingsw.server.model.cards.ToolCard;
import it.polimi.ingsw.server.model.components.DicePool;

import java.util.logging.Level;
import java.util.logging.Logger;

public class InsertDieInPoolEffect implements Effect {
    private transient MatchModel model;
    private transient ToolCard toolCard;
    private static final String NAME = "InsertDieInDicepoolEffect";

    @Override
    public void setParameters(MatchModel matchModel, ToolCard toolCard){
        this.model=matchModel;
        this.toolCard=toolCard;
    }

    @Override
    public void executeTest() throws NotValidParameterException{
        DicePool pool = new DicePool(model.getDicePool()) ;
        try {
            pool.insertDieInPool(toolCard.getDiceRemoved().get(0), toolCard.getIndexOfDieToBeRemoved());
        } catch (NotValidParameterException e){
            throw new NotValidParameterException("Index of die to be inserted in toolcard "+toolCard.getTitle(),"should be a valid index to insert a die from dicepool.");
        }
    }

    @Override
    public void execute(){
        DicePool pool = model.getDicePool();
        try {
            pool.insertDieInPool(toolCard.getDiceRemoved().get(0), toolCard.getIndexOfDieToBeRemoved());
        } catch (NotValidParameterException e) {
            Logger logger = Logger.getLogger(getClass().getName());
            logger.log(Level.WARNING, "Failed execution of effect \""+ NAME + "\" in toolcard "+toolCard.getTitle(), e);
        }
    }
}
