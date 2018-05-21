package it.polimi.ingsw.serverPart;

class Configurations implements MatchConfigurationsInterface{
    private String gridsPath;
    private String publicObjectivesPath;

    public String getGridsPath(){
        return gridsPath;
    }

    public String getPublicObjectivesPath() {
        return publicObjectivesPath;
    }
}
