package it.polimi.ingsw.client.configurations.adapters;

import it.polimi.ingsw.client.configurations.AdapterInterface;
import it.polimi.ingsw.client.configurations.Display;
import it.polimi.ingsw.client.custom_exception.invalid_operations.DieNotExistException;
import it.polimi.ingsw.server.model.components.Die;

public abstract class DieInterface implements AdapterInterface {
    private Die die;

    public DieInterface(Die die) throws DieNotExistException {
        if(die==null) throw new DieNotExistException();
        this.die=die;
    }

    @Override
    public abstract Display<Void> getAdapterInterface();

    protected Die getDie(){
        return this.die;
    }
}
