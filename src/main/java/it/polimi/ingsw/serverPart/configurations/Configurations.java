package it.polimi.ingsw.serverPart.configurations;

import com.google.gson.Gson;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Serializable;

class Configurations implements Serializable {

    private String gridsPath;
    private String publicObjectivesPath;
    private int MaxPlayersNumber;
    private int MinPlayersNumber;

    Configurations(String gridsPath,String publicObjectivesPath){
        this.gridsPath=gridsPath;
        this.publicObjectivesPath=publicObjectivesPath;
    }

    String getGridsPath(){
        return gridsPath;
    }

    String getPublicObjectivesPath() {
        return publicObjectivesPath;
    }

    int getMaxPlayersNumber() {
        return MaxPlayersNumber;
    }

    int getMinPlayersNumber() {
        return MinPlayersNumber;
    }
}
