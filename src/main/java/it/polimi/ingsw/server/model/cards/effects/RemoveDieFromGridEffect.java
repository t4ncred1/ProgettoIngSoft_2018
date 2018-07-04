package it.polimi.ingsw.server.model.cards.effects;

import it.polimi.ingsw.server.custom_exception.InvalidOperationException;
import it.polimi.ingsw.server.custom_exception.NotValidParameterException;
import it.polimi.ingsw.server.model.MatchModel;
import it.polimi.ingsw.server.model.cards.ToolCard;
import it.polimi.ingsw.server.model.components.Die;
import it.polimi.ingsw.server.model.components.Grid;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RemoveDieFromGridEffect implements Effect {

    private transient MatchModel model;
    private transient ToolCard toolCard;
    private static final String NAME = "RemoveDieFromGridEffect";

    @Override
    public void setParameters(MatchModel matchModel, ToolCard toolCard) {
        this.model = matchModel;
        this.toolCard = toolCard;
    }

    @Override
    public void executeTest() throws Exception {
        if (model.getPlayerCurrentGrid(model.askTurn()) == null){
            Logger logger = Logger.getLogger(getClass().getName());
            logger.log(Level.WARNING, "Something went wrong on server side during execution of \"" + NAME + "\" effect in toolcard " + toolCard.getTitle());
            throw new InvalidOperationException();
        }
        Grid playerGrid;
        if (toolCard.getPlayerGrid() == null) playerGrid = new Grid(model.getPlayerCurrentGrid(model.askTurn()));
        else playerGrid = toolCard.getPlayerGrid();
        playerGrid.initializeAllObservers();
        Die die = playerGrid.removeDieFromXY(toolCard.getDieCoordinatesX().remove(0), toolCard.getDieCoordinatesY().remove(0)); //this way, the parameter was modified.
        if (toolCard.isColourInRoundtrack()) {
            if (toolCard.getRoundTrackColor()==null){
                boolean ok = false;
                for(Die d : model.getRoundTrack()){
                    if (die.getColor().equals(d.getColor())){
                        toolCard.setRoundTrackColor(d.getColor());
                        ok=true;
                    }
                }
                if (!ok) throw new InvalidOperationException();
            }
            if(!(die.getColor().equals(toolCard.getRoundTrackColor()))) throw new InvalidOperationException();
        }
        ArrayList<Die> dieList = new ArrayList<>();
        dieList.add(die);
        toolCard.saveDiceRemoved(dieList);
        toolCard.setPlayerGrid(playerGrid);

    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public void setToolCardParams(List<String> params) throws NotValidParameterException {
        final String NOT_READ= "not read";
        final int REMOVING_INDEX=0;
        if(params.isEmpty()) throw new NotValidParameterException("An empty list","A not empty list");
        String temp1=NOT_READ;
        String temp2=NOT_READ;
        try{
            temp1 = params.remove(REMOVING_INDEX);
            temp2= params.remove(REMOVING_INDEX);
            int column=Integer.parseInt(temp1);
            int row=Integer.parseInt(temp2);
            // TODO: 04/07/2018 call toolCard's proper method (launch exceptions column and row are not in grid)
        } catch (NumberFormatException e){
            throw new NotValidParameterException("Value 1: "+temp1+", value 2: "+temp2, "Numeric parameters");
        } catch (NullPointerException e){
            throw new NotValidParameterException("List is now empty", "Not enough parameters");
        }
        if(!params.isEmpty()) throw new NotValidParameterException(params.toString(),"Too many parameters");
    }

    @Override
    public void execute() {
        if (model.getPlayerCurrentGrid(model.askTurn()) == null){
            Logger logger = Logger.getLogger(getClass().getName());
            logger.log(Level.WARNING, "Failed execution of effect \"" + NAME + "\" in toolcard " + toolCard.getTitle());
            return;
        }
        Grid playerGrid;
        if (toolCard.getPlayerGrid() == null) playerGrid = model.getPlayerCurrentGrid(model.askTurn());
        else playerGrid=toolCard.getPlayerGrid();
        try{
            Die die = playerGrid.removeDieFromXY(toolCard.getDieCoordinatesX().remove(0), toolCard.getDieCoordinatesY().remove(0)); //note: this way, one of the coordinates was removed.

            if (toolCard.isColourInRoundtrack()){
                if (toolCard.getRoundTrackColor()==null) {
                    toolCard.setRoundTrackColor(die.getColor());
                }
            }
            ArrayList<Die> dieList = new ArrayList<>();
            dieList.add(die);
            toolCard.saveDiceRemoved(dieList);
        } catch (NotValidParameterException | InvalidOperationException e) {
            Logger logger = Logger.getLogger(getClass().getName());
            logger.log(Level.SEVERE, "Failed execution of effect \"" + NAME + "\" in toolcard " + toolCard.getTitle(),e);
            return;
        }
        toolCard.setPlayerGrid(playerGrid);
    }

}
