package it.polimi.ingsw.server.model.cards.effects;

import it.polimi.ingsw.server.custom_exception.InvalidOperationException;
import it.polimi.ingsw.server.custom_exception.NotValidParameterException;
import it.polimi.ingsw.server.model.MatchModel;
import it.polimi.ingsw.server.model.cards.ToolCard;
import it.polimi.ingsw.server.model.components.Grid;

import java.util.logging.Level;
import java.util.logging.Logger;

public class InsertDieInGridEffect implements Effect {
    private transient MatchModel model;
    private transient ToolCard toolCard;
    private static final String NAME = "InsertDieInGridEffect";

    @Override
    public void setParameters(MatchModel matchModel, ToolCard toolCard) {
        this.model = matchModel;
        this.toolCard = toolCard;
    }

    @Override
    public void executeTest() throws Exception {
        Grid playerGrid;

        if(toolCard.getPlayerGrid()==null) playerGrid = new Grid(model.getPlayerCurrentGrid(model.askTurn()));
        else playerGrid = new Grid(toolCard.getPlayerGrid());

        playerGrid.initializeAllObservers();    //necessary
        playerGrid.insertDieInXY(toolCard.getDieDestinationCoordinatesX().remove(0),toolCard.getDieDestinationCoordinatesY().remove(0),toolCard.isColorCheck(),toolCard.isValueCheck(),toolCard.isOpenCheck(), toolCard.getDiceRemoved().get(0));
        toolCard.setPlayerGrid(playerGrid);

        //possibility to jump second turn (toolcard 8).
        if (toolCard.isJumpNextTurn() && !model.getCurrentPlayer().isFirstTurn()) {
            throw new InvalidOperationException();
        }
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public void execute() {
        if (toolCard.getPlayerGrid()==null) toolCard.setPlayerGrid(model.getPlayerCurrentGrid(model.askTurn()));
        Grid playerGrid = toolCard.getPlayerGrid();
        try {
            playerGrid.insertDieInXY(toolCard.getDieDestinationCoordinatesX().remove(0),toolCard.getDieDestinationCoordinatesY().remove(0),toolCard.isColorCheck(),toolCard.isValueCheck(),toolCard.isOpenCheck(),toolCard.getDiceRemoved().get(0));
        } catch (NotValidParameterException | InvalidOperationException e) {
            Logger logger = Logger.getLogger(getClass().getName());
            logger.log(Level.SEVERE, "Failed execution of effect \"" + NAME + "\" in toolcard " + toolCard.getTitle(),e);
        }
        //aggiunta possibilitá di salto turno tramite variabile (da file)

        if (toolCard.isJumpNextTurn()){
            if (!model.getCurrentPlayer().isFirstTurn()) {
                Logger logger = Logger.getLogger(getClass().getName());
                logger.log(Level.SEVERE,"Invalid operation : execute was called from effect "+NAME+" from toolcard "+toolCard.getTitle()+" but it's not current player's first turn.");
            }
            else{
                model.getCurrentPlayer().setJumpSecondTurn(true);
            }
        }
    }
}
