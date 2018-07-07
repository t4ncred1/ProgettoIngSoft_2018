package it.polimi.ingsw.server.model.components;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class DieToConstraintsAdapter implements DieConstraints, Serializable {

    private Die die;
    private int colorRestriction;
    private int valueRestriction;
    private static final Map<String,Integer> COLOR_HASH;
    static {
        HashMap<String,Integer> tmp =
                new HashMap<>();
        tmp.put("green" ,0);
        tmp.put("red"   ,1);
        tmp.put("blue"  ,2);
        tmp.put("yellow",3);
        tmp.put("purple",4);
        COLOR_HASH = Collections.unmodifiableMap(tmp);
    }

    public static Map<String,Integer> getColorMap(){
        return COLOR_HASH;
    }

    /**
     * Constructor for DieToConstraintsAdapter.
     * @param die The die to be assigned.
     */
    DieToConstraintsAdapter(Die die){
        //assegno die
        this.die=die;

        //assegno value: nota i vettori sono numerati da zero a cinque e non da uno a 6
        this.valueRestriction=this.die.getValue()-1;

        //assegno color
        colorRestriction=COLOR_HASH.get(die.getColor());
    }



    @Override
    public int getColorRestriction(){
        return this.colorRestriction;
    }


    @Override
    public int getValueRestriction(){
        return this.valueRestriction;
    }


    @Override
    public Die getDie() {
        return this.die;
    }

}
