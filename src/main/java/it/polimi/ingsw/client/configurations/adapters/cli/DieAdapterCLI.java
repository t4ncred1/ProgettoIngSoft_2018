package it.polimi.ingsw.client.configurations.adapters.cli;

import it.polimi.ingsw.client.configurations.adapters.DieInterface;
import it.polimi.ingsw.client.custom_exception.invalid_operations.DieNotExistException;
import it.polimi.ingsw.server.model.components.Die;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class DieAdapterCLI extends DieInterface {

    private static final String ANSI_RESET = "\u001B[0m";
    private static final String[] faces = {
            "\u2680",
            "\u2681",
            "\u2682",
            "\u2683",
            "\u2684",
            "\u2685"
    };
    private static final Map<String,String> colors;
    static {
        HashMap<String,String> temp = new HashMap<>();
        temp.put("red", "\033[0;31m");
        temp.put("yellow", "\033[0;33m");
        temp.put("green","\033[0;92m" );
        temp.put("blue", "\033[0;34m");
        temp.put("purple", "\033[0;35m");
        colors = Collections.unmodifiableMap(temp);
    }

    public DieAdapterCLI(Die die) throws DieNotExistException {
        super(die);
    }

    @Override
    public void displayInterface() {
        StringBuilder structure= new StringBuilder();
        Die die=super.getDie();
        structure.append(colors.get(die.getColor()));
        structure.append(faces[die.getValue()-1]);
        structure.append(ANSI_RESET);
        System.out.print(structure.toString());
    }

}
