package it.polimi.ingsw.client.configurations.adapters;

import it.polimi.ingsw.server.model.cards.ToolCard;
import it.polimi.ingsw.server.model.cards.effects.Effect;

import java.util.ArrayList;
import java.util.List;

public abstract class ToolCardAdapter {
    ToolCard toolCard;
    ArrayList<EffectAdapter> effects;
    private static final int NOT_USED_COST=1;
    private static final int USED_COST=2;

    protected ToolCardAdapter(ToolCard toolCard){
        this.toolCard=toolCard;
    }

    protected void setEffects(List<EffectAdapter> effects){
        this.effects= (ArrayList<EffectAdapter>) effects;
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

    public List<EffectAdapter> getEffects(){
        return this.effects;
    }

    public abstract String getToolCardInterface();

    protected boolean getRemoveAllDice(){
        return toolCard.getRemoveAllDiceFromDicePool();
    }
}
