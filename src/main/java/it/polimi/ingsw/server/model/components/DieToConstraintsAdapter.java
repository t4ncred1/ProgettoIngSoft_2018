package it.polimi.ingsw.server.model.components;

import java.io.Serializable;

public class DieToConstraintsAdapter implements DieConstraints, Serializable {

    private Die die;
    private int colorRestriction;
    private int valueRestriction;

    /**
     * Constructor for DieToConstraintsAdapter.
     * @param die The die to be assigned.
     */
    public DieToConstraintsAdapter(Die die){
        //assegno die
        this.die=die;

        //assegno value: nota i vettori sono numerati da zero a cinque e non da uno a 6
        this.valueRestriction=this.die.getValue()-1;

        //assegno color
        switch(this.die.getColor()){
            case "green":
                this.colorRestriction=0;
                break;
            case "red":
                this.colorRestriction=1;
                break;
            case "blue":
                this.colorRestriction=2;
                break;
            case "yellow":
                this.colorRestriction=3;
                break;
            default : this.colorRestriction=4;
        }
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

   /* @Override
    public void modifyDie(){
        //TODO modifier for die
    }*/

}
