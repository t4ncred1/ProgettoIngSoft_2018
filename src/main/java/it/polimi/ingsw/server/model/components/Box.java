package it.polimi.ingsw.server.model.components;
import it.polimi.ingsw.server.custom_exception.EmptyBoxException;
import it.polimi.ingsw.server.custom_exception.LimitValueException;
import it.polimi.ingsw.server.custom_exception.NotValidParameterException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

public class Box implements BoxObserver, BoxSubject, Serializable {


    /*
    * problema:
    * Esistono sia COSTRAINTS PROPRIE della CASELLA che EREDITATE dal posizionamento dei DADI nel corso della partita
    * Per risolvere questo problema rappresentiamo le restrizioni come dei vettori di interi in cui ogni elemento
    * rappresenta una specifica regola:
    *       -esempio-
    *           colorRestriction[0] potrebbe rappresentare il fatto che la casella non possa essere verde
    *           colorRestriction[1] il rosso ecc.
    *       -----------
    * se l'elemento in posizione n-esima del vettore è 0 la regola non è presente, se è >=1 è presente.
    *
    * Ogni volta che inserisco un dado i costraints vengono aggiornati mediante degli observer sulle caselle vicine.
    *
    *
    * Le caselle con costraints "innati" sono viste come caselle speciali e i loro vettori possono avere elementi con
    * valore iniziale =1. Questo fa si che le regole "innate" della cella non vengano perse con gli aggiornamenti.
    *
    * Anche per le caselle "aperte" si usa lo stesso ragionamento ( attributo opened )
    */

    private DieConstraints die = null;
    private int[] colorRestriction;
    private int[] valueRestriction;
    private int constraintIndex;
    private boolean kindOfConstraint;
    private transient final boolean COLOR_CONSTRAINT= false;
    private transient final boolean VALUE_CONSTRAINT= true;
    private transient final int NO_CONSTRAINT = -1;
    private int opened;
    private transient ArrayList<BoxObserver> observerList;
    private int coordX;
    private int coordY;

    //creators

    /**
     * Constructor for Box (clone).
     * @param aBox The box to clone.
     */
    public Box(Box aBox) {
        this.coordX = aBox.getCoordX();
        this.coordY = aBox.getCoordY();
        this.opened = aBox.opened;
        this.colorRestriction = Arrays.copyOf(aBox.colorRestriction,aBox.colorRestriction.length);
        this.valueRestriction = Arrays.copyOf(aBox.valueRestriction,aBox.valueRestriction.length);
        this.constraintIndex = aBox.constraintIndex;
        if(aBox.getDieConstraint()!=null) this.die = new DieToConstraintsAdapter(new Die(aBox.getDieConstraint().getDie()));
        this.kindOfConstraint = aBox.kindOfConstraint;
    }

    /**
     * Constructor for box (basic box).
     * @param x Abscissa of the box in a grid.
     * @param y Ordinate of the box in a grid.
     * @throws NotValidParameterException Thrown when 'x' or 'y' (or both) is out of bounds (grid bounds).
     */
    public Box(int x, int y) throws NotValidParameterException {
        final String expectedDataType= "Coord expected value: 0<x<4 and 0<y<3";
        if((x<0||x>4)||(y<0||y>3)) throw new NotValidParameterException("Coord: "+x+", "+ y,expectedDataType);
        this.coordX= x;
        this.coordY= y;
        die=null;
        colorRestriction = new int[5];
        valueRestriction = new int[6];
        observerList= new ArrayList<>();
        constraintIndex = NO_CONSTRAINT;

        opened=0;
        for (int i = 0; i<colorRestriction.length; i++ ) colorRestriction[i] = 0;
        for (int i=0; i<valueRestriction.length; i++ ) valueRestriction[i] = 0;
    }

    /**
     * Constructor for Box (box with a color restriction).
     * @param color The color restriction of the box.
     * @param x Abscissa of the box in a grid.
     * @param y Ordinate of the box in a grid.
     * @throws NotValidParameterException Thrown when 'color' is not one of 5 the admitted ones.
     */
    public Box(String color, int x, int y) throws NotValidParameterException {
        this(x,y);
        final String expectedDataType= "Color: red, yellow, green, blue, purple";

        if(color.equals("red")||color.equals("green")||color.equals("yellow")||color.equals("blue")||color.equals("purple")){
            DieConstraints dieSample = new DieToConstraintsAdapter(new Die(color, 1));
            constraintIndex = dieSample.getColorRestriction();
            kindOfConstraint = COLOR_CONSTRAINT;
            for(int i=0; i<colorRestriction.length; i++)
                if(i!= constraintIndex)colorRestriction[i]=1;
        }
        else
            throw new NotValidParameterException(color, expectedDataType);
    }

    /**
     * Constructor for box (box with a value restriction).
     * @param value The value restriction of the box.
     * @param x Abscissa of the box in a grid.
     * @param y Ordinate of the box in a grid.
     * @throws NotValidParameterException Thrown when 'value' is not between 1 and 6.
     */
    public Box(int value, int x, int y) throws NotValidParameterException {
        this(x,y);
        final String expectedDataType= "Value: 1, 2, 3, 4, 5, 6";

        if(value>=1&&value<=6){
            DieConstraints dieSample = new DieToConstraintsAdapter(new Die("red", value)); //the color isn't important, so it was randomly chosen by the author
            constraintIndex =dieSample.getValueRestriction();
            kindOfConstraint = VALUE_CONSTRAINT;
            for(int i=0; i<valueRestriction.length; i++)
                if(i!=constraintIndex)valueRestriction[i]=1;
        }
        else
            throw new NotValidParameterException(((Integer)value).toString(), expectedDataType); //hat to put a cast in order to make it an object

    }

    /**
     *
     * @return A string that textually represents a box object.
     */
    @Override
    public String toString(){
        StringBuilder build = new StringBuilder("color Restriction: ");
        for (int i : colorRestriction){
            build.append(Integer.toString(i));
            build.append("; ");
        }
        build.append("| value Restriction: ");
        for (int j : valueRestriction){
            build.append(Integer.toString(j));
            build.append("; ");
        }
        build.append(" | Kind of constraint (T:value, F:color) " + Boolean.toString(kindOfConstraint));
        build.append("| Open = "+Integer.toString(opened));
        build.append(" | position = ("+Integer.toString(coordX)+","+Integer.toString(coordY)+")");
        return build.toString();
    }

    //Observer

    /**
     *
     * @return The abscissa of a box.
     */
    public int getCoordX(){
        return coordX;
    }

    /**
     *
     * @return The ordinate of a box.
     */
    public int getCoordY(){
        return coordY;
    }

    /**
     *
     * @return A die with some restrictions.
     */
    public DieConstraints getDieConstraint() {
        return die;
    }

    public boolean areObserversNotInitialized() {
        return observerList.isEmpty();
    }

    /**
     * Method that checks if a die can be inserted correctly.
     *
     * @param colorCheck If true, the method ignores the color restriction.
     * @param valueCheck If true, the method ignores the value restriction.
     * @param openCheck If true, the method ignores 'opened' flag.
     * @param passedDie The die to be inserted.
     * @return True if a die can be correctly inserted, false if not.
     */
    public boolean tryToInsertDie(boolean colorCheck, boolean valueCheck, boolean openCheck, Die passedDie){
        DieConstraints toCheck= new DieToConstraintsAdapter(passedDie);
        if(this.die!=null)
            return false;
        if(this.opened==0 && openCheck){
            return false;
        }

        if(colorCheck && (colorRestriction[toCheck.getColorRestriction()]>0)) {
            return false;
        }
        if(valueCheck && (valueRestriction[toCheck.getValueRestriction()]>0)) {
            return false;
        }

        return true;

    }

    //modifier

    /**
     * Set opened to 1.
     */
    public void setToOpened() {
        this.opened=1;
    }

    /**
     * Standard method to insert a die.
     *
     * @param chosenDie The die to be inserted.
     */
    public void insertDie(Die chosenDie) //mossa standard per inserire un dado
    {
        if (chosenDie==null) throw new NullPointerException();
        this.die= new DieToConstraintsAdapter(chosenDie);
        notifyAllObservers(false);
    }

    /**
     *
     * @return The die removed.
     * @throws LimitValueException Thrown when there isn't a die to remove in the box.
     */
    public Die removeDie() throws LimitValueException{
        if(this.die == null) throw new LimitValueException("die attribute", "null");
        DieConstraints temp;
        temp=this.die;
        notifyAllObservers(true);
        this.die=null;
        return temp.getDie();
    }

    /**
     *
     * @param remove True if a die has been removed, false if a die has been added.
     * @param position The position of the constraint to update.
     */
    private void updateColor(boolean remove, int position){
        if(!remove)
            this.colorRestriction[position]++;
        else
            this.colorRestriction[position]--;
    }

    /**
     *
     * @param remove True if a die has been removed, false if a die has been added.
     * @param position The position of the constraint to update.
     */
    private void updateValue(boolean remove, int position){
        if(!remove)
            this.valueRestriction[position]++;
        else
            this.valueRestriction[position]--;
    }

    /**
     *
     * @param remove True if a die has been removed, false if a die has been added.
     */
    private void updateOpened(boolean remove){
        if(!remove)
            this.opened++;
        else
            this.opened--;
    }


    @Override
    public void update(boolean remove, DieConstraints nearDie, int x, int y) throws NotValidParameterException {
        if (x!=this.getCoordX()-1 && x!=this.getCoordX()+1 && y!=this.getCoordY()+1 && y!=this.getCoordY()-1) throw new NotValidParameterException("Position of the near box: "+ Integer.toString(x) +", "+Integer.toString(y), "this box should not be between the observers of the box which called the update.");
        if(this.coordX==x||this.coordY==y) {
                this.updateValue(remove, nearDie.getValueRestriction());
                this.updateColor(remove, nearDie.getColorRestriction());
        }

        //invece il fatto che la casella sia aperta va aggiornato a tutti
        this.updateOpened(remove);
    }


    @Override
    public void register(BoxObserver observer){
        this.observerList.add(observer);
    }


    @Override
    public void notifyAllObservers(boolean remove){     //for now, it just prints an error
        for(BoxObserver e: observerList)
        {
            try {
                e.update(remove, this.die, this.getCoordX(), this.getCoordY());
            } catch(NotValidParameterException err){
                err.printStackTrace();
            }
        }
    }


    /**
     * Initialize the observer list.
     */
    public void initializeObserverList(){
        if(this.observerList==null) this.observerList= new ArrayList<>();
        //TODO else throw something.
    }

    /**
     *
     * @return A string that textually represents the constraint of the box.
     */
    public String getConstraint() {
        //TODO refactor
        final String NO_CONSTRAINT_MESSAGE = " ";
        if(constraintIndex==NO_CONSTRAINT) return NO_CONSTRAINT_MESSAGE;
        else if(kindOfConstraint==VALUE_CONSTRAINT){
            return Integer.toString(constraintIndex+1);
        }
        else{
            switch (constraintIndex){
                case 0:
                    return "G";
                case 1:
                    return "R";
                case 2:
                    return "B";
                case 3:
                    return "Y";
                default:
                    return "P";
            }
        }
    }

    /**
     * Set 'opened' to 0.
     */
    public void setToClosed() {
        this.opened=0;
    }

    /**
     *
     * @return The die inserted in the box.
     * @throws EmptyBoxException Thrown if there isn't a die inserted in the box.
     */
    public Die getDie() throws EmptyBoxException {
        if(die!=null)
            return new Die(this.die.getDie());
        else
            throw new EmptyBoxException();
    }
}
