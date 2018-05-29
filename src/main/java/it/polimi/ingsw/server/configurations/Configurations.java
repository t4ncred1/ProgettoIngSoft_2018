package it.polimi.ingsw.server.configurations;

import java.io.Serializable;

class Configurations implements Serializable {

    private String gridsPath;
    private String publicObjectivesPath;
    private int maxPlayersNumber;
    private int minPlayersNumber;

    Configurations(String gridsPath,String publicObjectivesPath, int MaxPlayersNumber, int MinPlayersNumber){
        this.gridsPath=gridsPath;
        this.publicObjectivesPath=publicObjectivesPath;
        this.minPlayersNumber=MinPlayersNumber;
        this.maxPlayersNumber=MaxPlayersNumber;
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
}
