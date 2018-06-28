package it.polimi.ingsw.client.configurations.adapters;

import it.polimi.ingsw.server.model.components.Die;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DicePoolAdapterCLI extends DicePoolInterface{

    private static final String ANSI_RESET = "\u001B[0m";
    private static final String[] faces = {
            "\u2680",
            "\u2681",
            "\u2682",
            "\u2683",
            "\u2684",
            "\u2685"
    };

    private Map<String,String> colors;

    public DicePoolAdapterCLI(List<Die> dicePool) {
        super(dicePool);
        colors = new HashMap<>();
        colors.put("red", "\033[0;31m");
        colors.put("yellow", "\033[0;33m");
        colors.put("green","\033[0;92m" );
        colors.put("blue", "\033[0;34m");
        colors.put("purple", "\033[0;35m");
    }

    @Override
    public String getDicePoolInterface(){
        ArrayList<Die> dicePool= (ArrayList<Die>) super.getDicePool();
        StringBuilder structure= new StringBuilder();
        int i=1;
        for(Die die : dicePool){
            structure.append(i++);
            structure.append(".");
            structure.append(colors.get(die.getColor()));
            structure.append(faces[die.getValue()-1]);
            structure.append(ANSI_RESET);
            structure.append("\t\t");
        }
        return structure.toString();
    }


}
