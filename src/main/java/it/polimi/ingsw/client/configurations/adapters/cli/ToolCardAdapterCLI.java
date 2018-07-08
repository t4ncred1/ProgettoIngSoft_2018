package it.polimi.ingsw.client.configurations.adapters.cli;

import it.polimi.ingsw.client.configurations.Display;
import it.polimi.ingsw.client.configurations.adapters.EffectAdapter;
import it.polimi.ingsw.client.configurations.adapters.ToolCardAdapter;
import it.polimi.ingsw.server.model.cards.ToolCard;
import it.polimi.ingsw.server.model.cards.effects.Effect;

import java.util.ArrayList;
import java.util.List;

public class ToolCardAdapterCLI extends ToolCardAdapter {
    public ToolCardAdapterCLI(ToolCard toolCard) {
        super(toolCard);
        List<Effect> realEffects= toolCard.getEffects();
        List<EffectAdapter> effects= new ArrayList<>();
        for(Effect effect:realEffects){
            effects.add(new EffectAdapterCLI(effect,super.getRemoveAllDice()));
        }
        super.setEffects(effects);
    }

    @Override
    public Display<Void> getAdapterInterface() {
        return this::displayToolCard;
    }

    public Void displayToolCard(){
        StringBuilder structure=new StringBuilder();
        structure.append("\n");
        structure.append("Nome: ");
        structure.append(super.getTitle());
        structure.append("\t Difficoltà: ");
        structure.append(super.getCost());
        structure.append("\n");
        structure.append(super.getDescription());
        structure.append("\n");
        System.out.println(structure.toString());
        return null;
    }


}
