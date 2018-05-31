package it.polimi.ingsw.serverPart.card_container;
import it.polimi.ingsw.serverPart.component_container.Box;
import it.polimi.ingsw.serverPart.component_container.Die;
import it.polimi.ingsw.serverPart.component_container.DieConstraints;
import it.polimi.ingsw.serverPart.component_container.Grid;
import it.polimi.ingsw.serverPart.custom_exception.NotValidParameterException;

public class PrivateObjective extends Objective {
    private String color;
    private final static String TYPE = "private";

    public PrivateObjective(String color)throws NotValidParameterException {
        final String expectedColorType= "Color: red, yellow, green, blue, purple";
        if(color.equals("red")||color.equals("green")||color.equals("yellow")||color.equals("blue")||color.equals("purple"))
        this.color=color.toLowerCase();
        else throw new NotValidParameterException(color,expectedColorType);
    }

    public String getType(){
        return TYPE;
    }


    public String showPrivateObjective(){
        return this.color;
    }

    @Override
    public int calculatePoints(Grid grid) {
        int points_to_add, i, j;
        String color_to_check;
        DieConstraints die_temp = null;
        Die die_temp1 = null;
        int return_value = 0;
        Box[][] actual_grid = grid.getGrid();

        for (i = 0; i < actual_grid.length; i++) {
            for (j = 0; j < actual_grid[i].length; j++) {

                try {
                    die_temp = actual_grid[i][j].getDie();
                    die_temp1 = die_temp.getDie();
                } catch (NullPointerException e) {
                    continue;
                }
                color_to_check = die_temp1.getColor();
                if (color_to_check.equals(color)) {
                    points_to_add = die_temp1.getValue();
                    return_value = return_value + points_to_add;
                }
            }
        }
        return return_value;
    }
}
