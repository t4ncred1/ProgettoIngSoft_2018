package it.polimi.ingsw.client.configurations.adapters.cli;

import it.polimi.ingsw.client.configurations.adapters.PublicObjInterface;
import it.polimi.ingsw.server.model.cards.PublicObjective;

public class PublicObjAdapterCLI extends PublicObjInterface {

    public PublicObjAdapterCLI(PublicObjective objective){
        super(objective);
    }

    @Override
    public void displayInterface() {
        System.out.print("Nome: ");
        System.out.print(super.getTitle());
        System.out.print("\t Valore: ");
        System.out.println(super.getValue());
        System.out.println(super.getDescription());
        System.out.println();
    }
}
