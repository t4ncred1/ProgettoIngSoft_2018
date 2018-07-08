package it.polimi.ingsw.client.configurations;

import com.google.gson.Gson;
import it.polimi.ingsw.client.MainClient;
import it.polimi.ingsw.server.custom_exception.NotValidConfigPathException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.logging.Level;
import java.util.logging.Logger;


public class ConfigHandler {
    private static final String DEFAULT_CONFIG_PATH = "src/main/resources/clientconfig.json";
    private static String CONFIG_PATH;
    private static ConfigHandler instance;
    private static Configurations config;
    private static boolean customPath;

    private ConfigHandler() throws NotValidConfigPathException {
        boolean succeed = true;
        File jarPath=new File(MainClient.class.getProtectionDomain().getCodeSource().getLocation().getPath());
        CONFIG_PATH=jarPath.getParentFile().getAbsolutePath()+"/resources/clientconfig.json";
        customPath=true;
        Gson gson = new Gson();
        try {
            config = gson.fromJson(new FileReader(CONFIG_PATH), Configurations.class);
        } catch (FileNotFoundException e) {
            Logger.getLogger(this.getClass().getName()).log(Level.CONFIG,"File not found "+CONFIG_PATH+", trying default: "+DEFAULT_CONFIG_PATH);
            customPath=false;
            succeed = false;
        }
        if(!succeed){
            try {
                config = gson.fromJson(new FileReader(DEFAULT_CONFIG_PATH),Configurations.class);
                Logger.getLogger(this.getClass().getName()).log(Level.FINE,"correctly loaded configurations at "+DEFAULT_CONFIG_PATH);
            } catch (FileNotFoundException e) {
                throw new NotValidConfigPathException("No config.json found in "+DEFAULT_CONFIG_PATH);
            }
            Logger.getLogger(this.getClass().getName()).log(Level.CONFIG,"correctly loaded configurations at "+DEFAULT_CONFIG_PATH);
        }
    }

    public static ConfigHandler getInstance() throws NotValidConfigPathException {
        if (instance==null) instance=new ConfigHandler();
        return instance;
    }

    public int getRmiPort() {
        return config.getRmiPort();
    }
    public String getRegisterName() {
        return config.getRegisterName();
    }
    public int getSocketPort() {
        return config.getSocketPort();
    }
    public String getServerIp() {
        return config.getServerIp();
    }
}
