package it.polimi.ingsw.server.configurations;

import java.io.Serializable;

class Configurations implements Serializable {

    private String gridsPath;
    private String publicObjectivesPath;
    private String toolCardsPath;
    private int maxPlayersNumber;
    private int minPlayersNumber;
    private int timerBeforeMatch;
    private int timerToChooseGrid;

    Configurations(String gridsPath, String publicObjectivesPath, String toolCardsPath, int maxPlayersNumber, int minPlayersNumber, int timerToChooseGrids, int timerBeforeMatch){
        this.gridsPath=gridsPath;
        this.publicObjectivesPath=publicObjectivesPath;
        this.toolCardsPath = toolCardsPath;
        this.minPlayersNumber=minPlayersNumber;
        this.maxPlayersNumber=maxPlayersNumber;
        this.timerBeforeMatch =timerBeforeMatch;
        this.timerToChooseGrid =timerToChooseGrids;
    }

    String getGridsPath(){
        return this.gridsPath;
    }

    String getPublicObjectivesPath() {
        return this.publicObjectivesPath;
    }

    String getToolCardsPath(){return this.toolCardsPath;}

    int getMaxPlayersNumber() {
        return this.maxPlayersNumber;
    }

    int getMinPlayersNumber() {
        return this.minPlayersNumber;
    }

    int getTimerBeforeMatch() {
        return this.timerBeforeMatch;
    }

    int getTimerToChooseGrid() {
        return this.timerToChooseGrid;
    }
}
