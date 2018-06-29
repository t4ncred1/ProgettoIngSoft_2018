package it.polimi.ingsw.client.configurations.adapters;

import it.polimi.ingsw.client.custom_exception.invalid_operations.DieNotExistException;
import it.polimi.ingsw.server.model.components.Die;

import java.util.List;

public class RoundTrackAdapterCLI extends RoundTrackInterface{

    public RoundTrackAdapterCLI(List<Die> roundTrack) {
        super(roundTrack);
    }

    @Override
    public String getRoundTrackInterface() {
        Die[] roundTrack= super.getRoundTrack();
        StringBuilder structure= new StringBuilder();
        int i=1;
        for(Die die : roundTrack){
            structure.append(i++);
            structure.append(".");
            if(die!=null){
                try {
                    DieInterface temp= new DieAdapterCLI(die);
                    structure.append(temp.getDieInterface());
                } catch (DieNotExistException e) {
                    structure.append(" ");
                }
            }
            structure.append("\t\t");
        }
        return structure.toString();
    }

}
