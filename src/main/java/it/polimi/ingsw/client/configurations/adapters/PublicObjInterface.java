package it.polimi.ingsw.client.configurations.adapters;

import it.polimi.ingsw.client.configurations.AdapterInterface;
import it.polimi.ingsw.server.model.cards.PublicObjective;

public abstract class PublicObjInterface implements AdapterInterface {
    private PublicObjective objective;
    private static final String VARIABLE_VALUE= "variabile";

    /**
     * Constructor of the abstract object.
     * @param objective is the server-side data structure for public objective
     */
    protected PublicObjInterface(PublicObjective objective){
        this.objective=objective;
    }

    /**
     *
     * @return the Public objective's title.
     */
    protected String getTitle(){
        return objective.getTitle();
    }

    /**
     * @return the Public objective's value.
     */
    protected String getValue(){

        int value= objective.getValue();
        if(value==0){
            return VARIABLE_VALUE;
        }else {
            return Integer.toString(value);
        }
    }

    /**
     * @return the Public objective's description.
     */
    protected String getDescription(){
        return objective.getDescription();
    }
}
