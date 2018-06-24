package it.polimi.ingsw.server.model.cards.effects;

import it.polimi.ingsw.server.custom_exception.NotInPoolException;
import it.polimi.ingsw.server.custom_exception.NotValidParameterException;
import it.polimi.ingsw.server.model.MatchModel;
import it.polimi.ingsw.server.model.cards.ToolCard;
import it.polimi.ingsw.server.model.components.Die;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RemoveDieFromPoolEffect implements Effect {
    private transient MatchModel model;
    private transient ToolCard toolCard;
    private static final String NAME = "RemoveDieFromPoolEffect";

    @Override
    public void setParameters(MatchModel matchModel, ToolCard toolCard) {
        this.model=matchModel;
        this.toolCard=toolCard;
    }

    @Override
    public void executeTest() throws NotValidParameterException{
        boolean removeAllDiceFromDicePool= toolCard.getRemoveAllDiceFromDicePool();
        List<Die> diceRemoved = new ArrayList<>();
        if (!removeAllDiceFromDicePool){
            try {
                Die exDie = new Die(model.getDicePool().showDiceInPool().get(toolCard.getIndexOfDieToBeRemoved()));
                diceRemoved.add(exDie);
            } catch (IndexOutOfBoundsException e){
                throw new NotValidParameterException("Index of die to be removed in toolcard "+toolCard.getTitle(),"should be a valid index to remove a die from dicepool.");
            }
            toolCard.saveDiceRemoved(diceRemoved);
        }
        else{
            for(Die exDie : model.getDicePool().showDiceInPool()){
                diceRemoved.add(new Die(exDie));
            }
            toolCard.setDiceRemoved(diceRemoved);
        }
          //it's ok if, during test, only toolcard parameters (not read from file) are written, because they they will be rewritten later on by execute().
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public void execute(){
        boolean removeAllDiceFromDicePool= toolCard.getRemoveAllDiceFromDicePool();
        List<Die> diceRemoved = new ArrayList<>();

        if (!removeAllDiceFromDicePool){
                Die exDie = model.getDicePool().showDiceInPool().remove(toolCard.getIndexOfDieToBeRemoved());
                diceRemoved.add(exDie);
            toolCard.saveDiceRemoved(diceRemoved);
        }
        else{
            for(int i=0; i<model.getDicePool().showDiceInPool().size(); i++){
                try {
                    diceRemoved.add(model.getDicePool().getDieFromPool(0));
                    model.getDicePool().removeDieFromPool(0);
                } catch (NotInPoolException e){
                    Logger logger = Logger.getLogger(getClass().getName());
                    logger.log(Level.WARNING, "Failed execution of effect \""+ NAME + "\" in toolcard "+toolCard.getTitle(), e);
                }
            }
            toolCard.setDiceRemoved(diceRemoved);
        }


    }
}
