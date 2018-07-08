package it.polimi.ingsw.client.configurations.adapters;

import it.polimi.ingsw.client.configurations.AdapterInterface;
import it.polimi.ingsw.client.configurations.Display;
import it.polimi.ingsw.server.model.cards.ToolCard;

import java.util.List;

public abstract class ToolCardAdapter implements AdapterInterface {
    ToolCard toolCard;
    List<EffectAdapter> effects;
    private static final int NOT_USED_COST=1;
    private static final int USED_COST=2;

    /**
     * Constructor for ToolCardAdapter.
     *
     * @param toolCard The tool card selected.
     */
    protected ToolCardAdapter(ToolCard toolCard){
        this.toolCard=toolCard;
    }

    protected void setEffects(List<EffectAdapter> effects){
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

    public List<EffectAdapter> getEffects(){
        return this.effects;
    }

    @Override
    public abstract Display<Void> getAdapterInterface();

    protected boolean getRemoveAllDice(){
        return toolCard.getRemoveAllDiceFromDicePool();
    }
}
