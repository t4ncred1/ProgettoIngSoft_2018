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
    private int rmiPort;
    private int socketPort;
    private int publicObjectivesDistributed;
    private int toolCardsDistributed;


    Configurations(String gridsPath,String publicObjectivesPath, String toolCardsPath, int maxPlayersNumber, int minPlayersNumber, int timerToChooseGrids, int timerBeforeMatch, int rmiPort, int socketPort, int publicObjectivesDistributed, int toolCardsDistributed){
        this.gridsPath=gridsPath;
        this.publicObjectivesPath=publicObjectivesPath;
        this.toolCardsPath = toolCardsPath;
        this.minPlayersNumber=minPlayersNumber;
        this.maxPlayersNumber=maxPlayersNumber;
        this.timerBeforeMatch =timerBeforeMatch;
        this.timerToChooseGrid =timerToChooseGrids;
        this.rmiPort=rmiPort;
        this.socketPort=socketPort;
        this.publicObjectivesDistributed=publicObjectivesDistributed;
        this.toolCardsDistributed=toolCardsDistributed;
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

    int getRmiPort() { return rmiPort; }

    int getSocketPort(){ return socketPort; }

    int getPublicObjectivesDistributed() {
        return publicObjectivesDistributed;
    }

    public int getToolCardsDistributed() {
        return toolCardsDistributed;
    }
}
