package it.polimi.ingsw.server.model.cards.effects;

import it.polimi.ingsw.server.custom_exception.NotValidParameterException;
import it.polimi.ingsw.server.model.MatchModel;
import it.polimi.ingsw.server.model.cards.ToolCard;
import it.polimi.ingsw.server.model.components.Die;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class InverseDieValueEffect implements Effect{

    private MatchModel model;
    private ToolCard toolCard;
    private static final String NAME = "InverseValueEffect";

    @Override
    public void setParameters(MatchModel model, ToolCard toolCard) {
        this.model = model;
        this.toolCard = toolCard;
    }

    @Override
    public void executeTest() throws Exception {
        ArrayList<Die> dice = new ArrayList<>(toolCard.getDiceRemoved());
        if (dice.isEmpty()) throw new NotValidParameterException("dieRemovedFromDicePool is empty in toolcard" + toolCard.getTitle(), "Should contain at least one die.");
        for (int i=0; i<dice.size(); i++){
            Die d = dice.remove(i);
            switch(d.getValue()){
                case 1 :
                    dice.add(i,new Die(d.getColor(), 6));
                    break;
                case 2 :
                    dice.add(i,new Die(d.getColor(), 5));
                    break;
                case 3 :
                    dice.add(i,new Die(d.getColor(), 4));
                    break;
                case 4 :
                    dice.add(i,new Die(d.getColor(), 3));
                    break;
                case 5 :
                    dice.add(i,new Die(d.getColor(), 2));
                    break;
                default:
                    dice.add(i,new Die(d.getColor(), 1));
            }

        }
        toolCard.setDiceRemoved(dice);
    }

    @Override
    public void execute() {
        List<Die> dice = toolCard.getDiceRemoved();
        for (int i=0; i<dice.size(); i++){
            Die d = dice.remove(i);
            try {
                switch(d.getValue()){
                    case 1 :
                        dice.add(i,new Die(d.getColor(), 6));
                        break;
                    case 2 :
                        dice.add(i,new Die(d.getColor(), 5));
                        break;
                    case 3 :
                        dice.add(i,new Die(d.getColor(), 4));
                        break;
                    case 4 :
                        dice.add(i,new Die(d.getColor(), 3));
                        break;
                    case 5 :
                        dice.add(i,new Die(d.getColor(), 2));
                        break;
                    default:
                        dice.add(i,new Die(d.getColor(), 1));
                }
            } catch (NotValidParameterException e) {
                Logger logger = Logger.getLogger(getClass().getName());
                logger.log(Level.WARNING, "Failed execution of effect \"" + NAME + "\" in toolcard " + toolCard.getTitle(), e);
            }
        }
        toolCard.setDiceRemoved(dice);
    }
}
