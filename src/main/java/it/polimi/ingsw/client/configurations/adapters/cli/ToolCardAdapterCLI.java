package it.polimi.ingsw.client.configurations.adapters.cli;

import it.polimi.ingsw.client.configurations.Display;
import it.polimi.ingsw.client.configurations.adapters.EffectInterface;
import it.polimi.ingsw.client.configurations.adapters.ToolCardInterface;
import it.polimi.ingsw.server.model.cards.ToolCard;
import it.polimi.ingsw.server.model.cards.effects.Effect;

import java.util.ArrayList;
import java.util.List;

public class ToolCardAdapterCLI extends ToolCardInterface {
    public ToolCardAdapterCLI(ToolCard toolCard) {
        super(toolCard);
        List<Effect> realEffects= toolCard.getEffects();
        List<EffectInterface> effects= new ArrayList<>();
        for(Effect effect:realEffects){
            effects.add(new EffectAdapterCLI(effect,super.getRemoveAllDice()));
        }
        super.setEffects(effects);
    }

    @Override
    public void displayInterface() {
        StringBuilder structure=new StringBuilder();
        structure.append("\n");
        structure.append("Nome: ");
        structure.append(super.getTitle());
        structure.append("\t Costo: ");
        structure.append(super.getCost());
        structure.append("\n");
        structure.append(super.getDescription());
        structure.append("\n");
        System.out.println(structure.toString());
    }



}
