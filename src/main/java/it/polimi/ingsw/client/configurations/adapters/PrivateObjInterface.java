package it.polimi.ingsw.client.configurations.adapters;

import it.polimi.ingsw.client.configurations.AdapterInterface;
import it.polimi.ingsw.server.model.cards.PrivateObjective;

public abstract class PrivateObjInterface implements AdapterInterface {
    PrivateObjective privateObjective;


    /**
     * Constructor of the abstract object.
     * @param objective is the server-side data structure for private objective
     */
    protected PrivateObjInterface(PrivateObjective objective){
        privateObjective=objective;
    }

    /**
     *
     * @return the Public objective's title.
     */
    protected String getTitle(){
        return privateObjective.getTitle();
    }


    /**
     * @return the Public objective's description.
     */
    protected String getDescription(){
        return privateObjective.getDescription();
    }

    protected String getColor(){
        return privateObjective.getColor();
    }


}
