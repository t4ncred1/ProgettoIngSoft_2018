package it.polimi.ingsw.client.configurations.adapters;

import it.polimi.ingsw.server.custom_exception.InvalidOperationException;
import it.polimi.ingsw.server.custom_exception.NotValidParameterException;
import it.polimi.ingsw.server.model.components.Die;

public interface GridInterface {
    String getGridInterface();

    void insertDieInXY(int row, int column, boolean colorCheck, boolean valueCheck, Die die) throws InvalidOperationException, NotValidParameterException;
}
