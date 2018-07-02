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
    private int timerForOperation;
    private int numberOfMatchHandled;

    /**
     * Constructor for Configurations.
     *
     * @param gridsPath Path of the grids.
     * @param publicObjectivesPath Path of the public objectives.
     * @param toolCardsPath Path of the tool cards.
     * @param maxPlayersNumber Maximum number of players.
     * @param minPlayersNumber Minimum number of players.
     * @param timerToChooseGrids Timer for a player to choose his grid.
     * @param timerBeforeMatch Timer before a match starts.
     * @param rmiPort RMI port.
     * @param socketPort Socket port.
     * @param publicObjectivesDistributed Number of public objectives per match.
     * @param toolCardsDistributed Number of tool cards per match.
     * @param timerForOperation Timer for a player to choose an operation in game.
     * @param numberOfMatchHandled Number of matches handled.
     */
    Configurations(String gridsPath,String publicObjectivesPath, String toolCardsPath, int maxPlayersNumber, int minPlayersNumber, int timerToChooseGrids, int timerBeforeMatch, int rmiPort, int socketPort, int publicObjectivesDistributed, int toolCardsDistributed, int timerForOperation, int numberOfMatchHandled){
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
        this.timerForOperation=timerForOperation;
        this.numberOfMatchHandled=numberOfMatchHandled;
    }

    /**
     *
     * @return A string containing 'gridsPath'.
     */
    String getGridsPath(){
        return this.gridsPath;
    }

    /**
     *
     * @return A string containing 'publicObjectivesPath'.
     */
    String getPublicObjectivesPath() {
        return this.publicObjectivesPath;
    }

    /**
     *
     * @return A string containing 'toolCardsPath'.
     */
    String getToolCardsPath(){return this.toolCardsPath;}

    /**
     *
     * @return An integer containing 'maxPlayersNumber'.
     */
    int getMaxPlayersNumber() {
        return this.maxPlayersNumber;
    }

    /**
     *
     * @return An integer containing 'minPlayersNumber'.
     */
    int getMinPlayersNumber() {
        return this.minPlayersNumber;
    }

    /**
     *
     * @return An integer containing 'timerBeforeMatch'.
     */
    int getTimerBeforeMatch() {
        return this.timerBeforeMatch;
    }

    /**
     *
     * @return An integer containing 'timerToChooseGrid'.
     */
    int getTimerToChooseGrid() {
        return this.timerToChooseGrid;
    }

    /**
     *
     * @return An integer containing 'rmiPort'.
     */
    int getRmiPort() { return rmiPort; }

    /**
     *
     * @return An integer containing 'socketPort'.
     */
    int getSocketPort(){ return socketPort; }

    /**
     *
     * @return An integer containing 'publicObjectivesDistributed'.
     */
    int getPublicObjectivesDistributed() {
        return publicObjectivesDistributed;
    }

    /**
     *
     * @return An integer containing 'toolCardsDistributed'.
     */
    public int getToolCardsDistributed() {
        return toolCardsDistributed;
    }

    /**
     *
     * @return An integer containing 'timerForOperation'.
     */
    public int getTimerForOperation() {
        return timerForOperation;
    }

    /**
     *
     * @return An integer containing 'numberOfMatchHandled'.
     */
    public int getNumberOfMatchHandled() {
        return numberOfMatchHandled;
    }
}
