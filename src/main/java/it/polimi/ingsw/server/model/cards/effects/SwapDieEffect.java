package it.polimi.ingsw.server.model.cards.effects;

import it.polimi.ingsw.server.custom_exception.InvalidOperationException;
import it.polimi.ingsw.server.custom_exception.NotValidParameterException;
import it.polimi.ingsw.server.model.MatchModel;
import it.polimi.ingsw.server.model.cards.ToolCard;
import it.polimi.ingsw.server.model.components.Die;

import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;



public class SwapDieEffect implements Effect {

    private transient MatchModel model;
    private transient ToolCard toolCard;
    private static final String NAME = "SwapDieEffect";
    private transient int indexPicked;
    private transient int valueChosen;

    @Override
    public void setParameters(MatchModel matchModel, ToolCard toolCard) {
        this.model = matchModel;
        this.toolCard = toolCard;
    }

    @Override
    public void execute() {
        String colorPicked;
        if (indexPicked!=-1){
            try {
                colorPicked=model.getDicePool().getAvailableColors().get(indexPicked);
                model.getDicePool().swapColor(toolCard.getDiceRemoved().remove(0).getColor(), indexPicked);
            } catch (NotValidParameterException e) {
                Logger logger = Logger.getLogger(this.getClass().getName());
                logger.log(Level.SEVERE, "Something wrong happened", e);
                indexPicked = -1;
                return;
            }
            try {
                ArrayList<Die> dieList = new ArrayList<>();
                dieList.add(new Die(colorPicked,valueChosen));  //note: if valueChosen was not initialized, this will throw an exception.
                toolCard.saveDiceRemoved(dieList);
            } catch (NotValidParameterException e) {
                Logger logger = Logger.getLogger(this.getClass().getName());
                logger.log(Level.SEVERE, "Something wrong happened", e);
                indexPicked = -1;
                valueChosen = -1;
                return;
            }
        }
        else {
            Logger logger = Logger.getLogger(this.getClass().getName());
            logger.log(Level.SEVERE, "execution of toolcard \"" + toolCard.getTitle() + "\" failed due to effect "+NAME+" because indexPicked attribute was not set during test execution.", new InvalidOperationException());
        }
        indexPicked = -1;
        valueChosen = -1;
    }

    @Override
    public void executeTest() throws Exception {
        ArrayList<Die> diceRemoved = new ArrayList<>();
        for (Die d : toolCard.getDiceRemoved()){
            diceRemoved.add(new Die(d));
        }
        diceRemoved.remove(0);
        indexPicked = -1;
        valueChosen = -1;
        indexPicked = new Random().nextInt(model.getDicePool().getAvailableColors().size());
        valueChosen = model.getController().toolCardLetPlayerChoose(model.getDicePool().getAvailableColors().get(indexPicked));
        diceRemoved.add(new Die(model.getDicePool().getAvailableColors().get(indexPicked), valueChosen));       //this will throw an exception if the value chosen by player is invalid.
        toolCard.setDiceRemoved(diceRemoved);
    }

    @Override
    public String getName() {
        return null;
    }
}
