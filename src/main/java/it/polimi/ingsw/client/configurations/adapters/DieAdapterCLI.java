package it.polimi.ingsw.client.configurations.adapters;

import it.polimi.ingsw.client.custom_exception.invalid_operations.DieNotExistException;
import it.polimi.ingsw.server.model.components.Die;

import java.util.HashMap;
import java.util.Map;

public class DieAdapterCLI extends DieInterface{

    private Map<String,String> colors;
    private static final String ANSI_RESET = "\u001B[0m";
    private static final String[] faces = {
            "\u2680",
            "\u2681",
            "\u2682",
            "\u2683",
            "\u2684",
            "\u2685"
    };

    public DieAdapterCLI(Die die) throws DieNotExistException {
        super(die);
        colors = new HashMap<>();
        colors.put("red", "\033[0;31m");
        colors.put("yellow", "\033[0;33m");
        colors.put("green","\033[0;92m" );
        colors.put("blue", "\033[0;34m");
        colors.put("purple", "\033[0;35m");
    }

    @Override
    public String getDieInterface() {
        StringBuilder structure= new StringBuilder();
        Die die=super.getDie();
        structure.append(colors.get(die.getColor()));
        structure.append(faces[die.getValue()-1]);
        structure.append(ANSI_RESET);
        return structure.toString();
    }
}
