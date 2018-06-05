package it.polimi.ingsw.server.configurations;

import java.io.Serializable;

class Configurations implements Serializable {

    private String gridsPath;
    private String publicObjectivesPath;
    private int maxPlayersNumber;
    private int minPlayersNumber;
    private int timerbeforematch;
    private int timertochoosegrid;

    Configurations(String gridsPath,String publicObjectivesPath, int maxPlayersNumber, int minPlayersNumber, int timerToChooseGrids, int timerBeforeMatch){
        this.gridsPath=gridsPath;
        this.publicObjectivesPath=publicObjectivesPath;
        this.minPlayersNumber=minPlayersNumber;
        this.maxPlayersNumber=maxPlayersNumber;
        this.timerbeforematch=timerBeforeMatch;
        this.timertochoosegrid=timerToChooseGrids;
    }

    String getGridsPath(){
        return gridsPath;
    }

    String getPublicObjectivesPath() {
        return publicObjectivesPath;
    }

    int getMaxPlayersNumber() {
        return maxPlayersNumber;
    }

    int getMinPlayersNumber() {
        return minPlayersNumber;
    }

    int getTimerbeforematch() {
        return timerbeforematch;
    }

    int getTimertochoosegrid() {
        return timertochoosegrid;
    }
}
