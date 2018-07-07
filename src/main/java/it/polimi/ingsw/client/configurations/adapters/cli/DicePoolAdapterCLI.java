package it.polimi.ingsw.client.configurations.adapters.cli;

import it.polimi.ingsw.client.configurations.adapters.DicePoolInterface;
import it.polimi.ingsw.client.configurations.adapters.DieInterface;
import it.polimi.ingsw.client.custom_exception.invalid_operations.DieNotExistException;
import it.polimi.ingsw.server.model.components.Die;

import java.util.ArrayList;
import java.util.List;

public class DicePoolAdapterCLI extends DicePoolInterface {

    public DicePoolAdapterCLI(List<Die> dicePool) {
        super(dicePool);
    }

    @Override
    public String getDicePoolInterface(){
        ArrayList<Die> dicePool= (ArrayList<Die>) super.getDicePool();
        StringBuilder structure= new StringBuilder();
        int i=1;
        for(Die die : dicePool){
            structure.append(i++);
            structure.append(".");
            try {
                DieInterface temp= new DieAdapterCLI(die);
                structure.append(temp.getDieInterface());
            } catch (DieNotExistException e) {
                structure.append(" ");
            }
            structure.append("\t\t");
        }
        return structure.toString();
    }


}
