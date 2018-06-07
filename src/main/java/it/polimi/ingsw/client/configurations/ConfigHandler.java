package it.polimi.ingsw.client.configurations;

import com.google.gson.Gson;
import it.polimi.ingsw.server.custom_exception.NotValidConfigPathException;
import java.io.FileNotFoundException;
import java.io.FileReader;


public class ConfigHandler {
    private static final String CONFIG_PATH="src/main/resources/clientconfig.json";
    private static ConfigHandler instance;
    private static Configurations config;

    private ConfigHandler() throws NotValidConfigPathException {
        Gson gson = new Gson();
        try {
            config = gson.fromJson(new FileReader(CONFIG_PATH), Configurations.class);
        } catch (FileNotFoundException e) {
            throw new NotValidConfigPathException("No config.json found in "+CONFIG_PATH);
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
