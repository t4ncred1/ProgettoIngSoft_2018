package it.polimi.ingsw.client.configurations.adapters.cli;

import it.polimi.ingsw.client.configurations.adapters.DicePoolInterface;
import it.polimi.ingsw.client.configurations.adapters.DieInterface;
import it.polimi.ingsw.client.custom_exception.invalid_operations.DieNotExistException;
import it.polimi.ingsw.server.model.components.Die;

import java.util.List;

public class DicePoolAdapterCLI extends DicePoolInterface {
    /**
     * Constructor for DicePoolAdapterCLI.
     *
     * @param dicePool The dice pool selected.
     */
    public DicePoolAdapterCLI(List<Die> dicePool) {
        super(dicePool);
    }

    @Override
    public void displayInterface(){
        List<Die> dicePool= super.getDicePool();
        int i=1;
        System.out.println("La riserva: ");
        for(Die die : dicePool){
            System.out.print(i++);
            System.out.print(".");
            try {
                DieInterface temp= new DieAdapterCLI(die);
                temp.displayInterface();
            } catch (DieNotExistException e) {
                System.out.print(" ");
            }
            System.out.print("   ");
        }
        System.out.println();
    }



}
