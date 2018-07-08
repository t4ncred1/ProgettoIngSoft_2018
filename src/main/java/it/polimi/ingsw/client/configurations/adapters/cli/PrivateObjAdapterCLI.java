package it.polimi.ingsw.client.configurations.adapters.cli;

import it.polimi.ingsw.client.configurations.adapters.PrivateObjInterface;
import it.polimi.ingsw.server.model.cards.PrivateObjective;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class PrivateObjAdapterCLI extends PrivateObjInterface {

    private static final Map<String,String> colors;
    static {
        HashMap<String,String> temp = new HashMap<>();
        temp.put("red", "\u001B[5m\u001B[41m\u001B[30mROSSO\u001B[0m");
        temp.put("yellow", "\u001B[5m\u001B[43m\u001B[30mGIALLO\u001B[0m");
        temp.put("green","\u001B[5m\u001B[42m\u001B[30mVERDE\u001B[0m" );
        temp.put("blue", "\u001B[5m\u001B[44m\u001B[30mBLU\u001B[0m");
        temp.put("purple", "\u001B[5m\u001B[45m\u001B[30mVIOLA\u001B[0m");
        colors = Collections.unmodifiableMap(temp);
    }


    public PrivateObjAdapterCLI(PrivateObjective objective){
        super(objective);
    }
    @Override
    public void displayInterface() {
        System.out.println(super.getTitle());
        System.out.print(super.getDescription());
        System.out.println(colors.get(super.getColor()));
        System.out.println();
    }
}
