package it.polimi.ingsw.client.configurations.adapters.cli;

import it.polimi.ingsw.client.configurations.adapters.DieInterface;
import it.polimi.ingsw.client.configurations.adapters.RoundTrackInterface;
import it.polimi.ingsw.client.custom_exception.invalid_operations.DieNotExistException;
import it.polimi.ingsw.server.model.components.Die;

import java.util.List;

public class RoundTrackAdapterCLI extends RoundTrackInterface {
    /**
     * Constructor for RoundTrackAdapterCLI.
     *
     * @param roundTrack The roundTrack selected.
     */
    public RoundTrackAdapterCLI(List<Die> roundTrack) {
        super(roundTrack);
    }

    @Override
    public void displayInterface() {
        Die[] roundTrack= super.getRoundTrack();
        int i=1;
        System.out.println("La round track:");
        for(Die die : roundTrack){
            System.out.print(i++);
            System.out.print(".");
            if(die!=null){
                try {
                    DieInterface temp= new DieAdapterCLI(die);
                    temp.displayInterface();
                } catch (DieNotExistException e) {
                    System.out.print(" ");
                }
            }
            System.out.print("\t\t");
        }
        System.out.println();
    }


}
