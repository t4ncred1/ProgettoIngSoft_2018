package it.polimi.ingsw.server.configurations;

import java.io.Serializable;

class Configurations implements Serializable {

    private String gridsPath;
    private String publicObjectivesPath;
    private int maxPlayersNumber;
    private int minPlayersNumber;
    private int timerBeforeMatch;
    private int timerToChooseGrid;

    Configurations(String gridsPath,String publicObjectivesPath, int maxPlayersNumber, int minPlayersNumber, int timerToChooseGrids, int timerBeforeMatch){
        this.gridsPath=gridsPath;
        this.publicObjectivesPath=publicObjectivesPath;
        this.minPlayersNumber=minPlayersNumber;
        this.maxPlayersNumber=maxPlayersNumber;
        this.timerBeforeMatch =timerBeforeMatch;
        this.timerToChooseGrid =timerToChooseGrids;
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

    int getTimerBeforeMatch() {
        return timerBeforeMatch;
    }

    int getTimerToChooseGrid() {
        return timerToChooseGrid;
    }
}
