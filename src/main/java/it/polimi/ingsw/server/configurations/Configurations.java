package it.polimi.ingsw.server.configurations;

import java.io.Serializable;

class Configurations implements Serializable {

    /**
     * These parameters are read from file only.
     */

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
    int getToolCardsDistributed() {
        return toolCardsDistributed;
    }

    /**
     *
     * @return An integer containing 'timerForOperation'.
     */
    int getTimerForOperation() {
        return timerForOperation;
    }

    /**
     *
     * @return An integer containing 'numberOfMatchHandled'.
     */
    int getNumberOfMatchHandled() {
        return numberOfMatchHandled;
    }
}
