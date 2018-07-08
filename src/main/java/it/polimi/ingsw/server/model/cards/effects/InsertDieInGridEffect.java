package it.polimi.ingsw.server.model.cards.effects;

import it.polimi.ingsw.server.custom_exception.EffectException;
import it.polimi.ingsw.server.custom_exception.InvalidOperationException;
import it.polimi.ingsw.server.custom_exception.NotValidParameterException;
import it.polimi.ingsw.server.model.MatchModel;
import it.polimi.ingsw.server.model.cards.ToolCard;
import it.polimi.ingsw.server.model.components.Grid;

import java.util.List;
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
    public void executeTest() throws EffectException {
        Grid playerGrid;

        //possibility to jump second turn (toolcard 8).
        if (toolCard.isJumpNextTurn() && !model.getCurrentPlayer().isFirstTurn()) {
            throw new EffectException("Must be player's second turn.");
        }

        if (toolCard.isDiceMustNotBeInserted() && model.getController().wasDieInserted()){
            throw new EffectException("Must have been inserted a die to do this action");
        }

        if(toolCard.getPlayerGrid()==null) playerGrid = new Grid(model.getPlayerCurrentGrid(model.askTurn()));
        else playerGrid = new Grid(toolCard.getPlayerGrid());

        playerGrid.initializeAllObservers();    //necessary
        try {
            playerGrid.insertDieInXY(toolCard.getDieDestinationCoordinatesX().remove(0),toolCard.getDieDestinationCoordinatesY().remove(0),toolCard.isColorCheck(),toolCard.isValueCheck(),toolCard.isOpenCheck(), toolCard.getDiceRemoved().remove(0));
        } catch (NotValidParameterException e) {
            Logger.getLogger(this.getClass().getName()).log(Level.WARNING,"Invalid parameters called in a toolcard.",e);
        } catch (InvalidOperationException e) {
            throw new EffectException("Can't insert the passed die.");
        }
        toolCard.setPlayerGrid(playerGrid);
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public void setToolCardParams(List<String> params) throws NotValidParameterException {
        final int REMOVING_INDEX=0;
        final String NOT_READ= "not read";
        if(params.isEmpty()) throw new NotValidParameterException("An empty list","A not empty list");
        String temp1=NOT_READ;
        String temp2=NOT_READ;
        int column;
        int row;
        try{
            temp1 = params.remove(REMOVING_INDEX);
            column=Integer.parseInt(temp1);
            temp2= params.remove(REMOVING_INDEX);
            row=Integer.parseInt(temp2);
        } catch (NumberFormatException e){
            throw new NotValidParameterException("Value 1: "+temp1+", value 2: "+temp2, "Numeric parameters");
        } catch (NullPointerException e){
            throw new NotValidParameterException("List is now empty", "Not enough parameters");
        }
        if(!params.isEmpty()) throw new NotValidParameterException(params.toString(),"Too many parameters");
        toolCard.addDieDestinationCoordinatesX(column);
        toolCard.addDieDestinationCoordinatesY(row);
    }

    @Override
    public void execute() {
        if (toolCard.isDiceMustNotBeInserted()) model.getController().dieWasInserted();
        if (toolCard.getPlayerGrid()==null) toolCard.setPlayerGrid(model.getPlayerCurrentGrid(model.askTurn()));
        Grid playerGrid = toolCard.getPlayerGrid();
        try {
            playerGrid.insertDieInXY(toolCard.getDieDestinationCoordinatesX().remove(0),toolCard.getDieDestinationCoordinatesY().remove(0),toolCard.isColorCheck(),toolCard.isValueCheck(),toolCard.isOpenCheck(),toolCard.getDiceRemoved().remove(0));
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
