package it.polimi.ingsw.client.configurations.adapters;

import it.polimi.ingsw.client.configurations.AdapterInterface;
import it.polimi.ingsw.server.model.cards.ToolCard;

import java.util.List;

public abstract class ToolCardInterface implements AdapterInterface {
    ToolCard toolCard;
    List<EffectInterface> effects;
    private static final int NOT_USED_COST=1;
    private static final int USED_COST=2;

    /**
     * Constructor for ToolCardInterface.
     *
     * @param toolCard The tool card selected.
     */
    protected ToolCardInterface(ToolCard toolCard){
        this.toolCard=toolCard;
    }

    protected void setEffects(List<EffectInterface> effects){
        this.effects= effects;
    }

    public String getTitle(){
        return toolCard.getTitle();
    }

    public String getDescription(){
        return toolCard.getDescription();
    }

    public int getCost(){
        if(toolCard.isUsed()){
            return USED_COST;
        }else {
            return NOT_USED_COST;
        }
    }

    public List<EffectInterface> getEffects(){
        return this.effects;
    }

    protected boolean getRemoveAllDice(){
        return toolCard.getRemoveAllDiceFromDicePool();
    }
}
