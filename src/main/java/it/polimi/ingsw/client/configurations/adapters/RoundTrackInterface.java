package it.polimi.ingsw.client.configurations.adapters;

import it.polimi.ingsw.client.configurations.AdapterInterface;
import it.polimi.ingsw.server.model.components.Die;

import java.util.List;

public abstract class RoundTrackInterface implements AdapterInterface {

    private Die[] roundTrack;

    private static final int ROUND_TRACK_DIMENSION=10;

    /**
     * Constructor for RoundTrackInterface.
     *
     * @param roundTrack The round track selected.
     */
    protected RoundTrackInterface(List<Die> roundTrack){
        this.roundTrack = new Die[ROUND_TRACK_DIMENSION];
        for(int i=0; i<roundTrack.size();i++){
            this.roundTrack[i]=roundTrack.get(i);
        }
    }

    public int getRoundTrackDimension(){
        return ROUND_TRACK_DIMENSION;
    }

    protected Die[] getRoundTrack(){
        return this.roundTrack;
    }
}
