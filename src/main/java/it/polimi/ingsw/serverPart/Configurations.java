package it.polimi.ingsw.serverPart;

import com.google.gson.Gson;
import java.io.FileNotFoundException;
import java.io.FileReader;

public class Configurations implements MatchConfigurationsInterface{

    private static Configurations instance;
    private static final String CONFIG_PATH="";
    private String gridsPath;
    private String publicObjectivesPath;

    private Configurations(){
        try {
            instance = new Gson().fromJson(new FileReader(CONFIG_PATH), Configurations.class);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static Configurations getInstance(){
        if(instance==null) instance= new Configurations();
        return instance;
    }

    public String getGridsPath(){
        return gridsPath;
    }

    public String getPublicObjectivesPath() {
        return publicObjectivesPath;
    }
}
