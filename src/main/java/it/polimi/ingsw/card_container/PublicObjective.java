package it.polimi.ingsw.card_container;
import it.polimi.ingsw.component_container.*;

public class PublicObjective extends Objective{
    private boolean on_rows;
    private boolean on_columns;
    private boolean on_values;
    private boolean on_colors;
    private int value;
    private int[] valuesconsidered;

    public PublicObjective(String tit, String desc, boolean rows, boolean columns, boolean valueconst, boolean colors, int points, int[]values){
        title=tit;
        description=desc;
        on_rows=rows;
        on_columns=columns;
        on_values=valueconst;
        on_colors=colors;
        value=points;
        valuesconsidered=values;
    }


    public String toString(){
        StringBuilder build = new StringBuilder("Title = ");
        build.append(this.getTitle());
        build.append("\n");
        build.append("Description = ");
        build.append(this.getDescription());
        build.append("\nrows = " + Boolean.toString(on_rows));
        build.append("\ncolumns = " + Boolean.toString(on_columns));
        build.append("\nvalues = " + Boolean.toString(on_values));
        build.append("\ncolors = " + Boolean.toString(on_colors));
        build.append("\npoints = " + Integer.toString(value));
        build.append("\nvalues considered: ");
        for (int i : valuesconsidered){
            build.append(Integer.toString(i)+",");
        }
        build.append("\n");

        return build.toString();
    }
    @Override
    public int calculatePoints(Grid grid) {
        //TODO create all the various point cases
        return 0;
    }
    public boolean isObjectiveOk(){
        return ((!(on_values && on_colors)) && ((!(on_rows && on_columns )) || (on_colors)) && (on_colors || on_values));
    }

}
