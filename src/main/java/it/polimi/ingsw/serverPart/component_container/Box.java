package it.polimi.ingsw.serverPart.component_container;
import it.polimi.ingsw.serverPart.custom_exception.LimitValueException;
import it.polimi.ingsw.serverPart.custom_exception.NotValidParameterException;

import java.util.ArrayList;

public class Box implements BoxObserver, BoxSubject {


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

    private DieConstraints die;
    private int[] colorRestriction;
    private int[] valueRestriction;
    private int constraintIndex;
    private boolean kindOfConstraint; //FIXME true= value, false color if constraints are different from 0.
    private final boolean COLOR_CONSTRAINT= false; //FIXME
    private final boolean VALUE_CONSTRAINT= true; //FIXME
    private final int NO_CONSTRAINT = -1;
    private int opened;
    private transient ArrayList<BoxObserver> observerList;
    private int coordX;
    private int coordY;

    //creators
    public Box(int x, int y) throws NotValidParameterException {
        final String expectedDataType= "Coord expected value: 0<x<4 and 0<y<3";
        if((x<0||x>4)||(y<0||y>3)) throw new NotValidParameterException("Coord: "+x+", "+ y,expectedDataType);
        this.coordX= x;
        this.coordY= y;
        die=null;
        colorRestriction = new int[5];
        valueRestriction = new int[6];
        observerList= new ArrayList<>();
        constraintIndex = NO_CONSTRAINT; //FIXME

        opened=0;
        for (int i = 0; i<colorRestriction.length; i++ ) colorRestriction[i] = 0;
        for (int i=0; i<valueRestriction.length; i++ ) valueRestriction[i] = 0;
    }


    public Box(String color, int x, int y) throws NotValidParameterException {
        this(x,y);
        final String expectedDataType= "Color: red, yellow, green, blue, purple";

        if(color.equals("red")||color.equals("green")||color.equals("yellow")||color.equals("blue")||color.equals("purple")){
            DieConstraints dieSample = new DieToConstraintsAdapter(new Die(color, 1));
            constraintIndex = dieSample.getColorRestriction();
            kindOfConstraint = COLOR_CONSTRAINT; //FIXME
            for(int i=0; i<colorRestriction.length; i++)
                if(i!= constraintIndex)colorRestriction[i]=1;
        }
        else
            throw new NotValidParameterException(color, expectedDataType);
    }
    public Box(int value, int x, int y) throws NotValidParameterException {
        this(x,y);
        final String expectedDataType= "Value: 1, 2, 3, 4, 5, 6";

        if(value>=1&&value<=6){
            DieConstraints dieSample = new DieToConstraintsAdapter(new Die("red", value)); //the color isn't important, so it was randomly chosen by the author
            constraintIndex =dieSample.getValueRestriction();
            kindOfConstraint = VALUE_CONSTRAINT; //FIXME
            for(int i=0; i<valueRestriction.length; i++)
                if(i!=constraintIndex)valueRestriction[i]=1;
        }
        else
            throw new NotValidParameterException(((Integer)value).toString(), expectedDataType); //hat to put a cast in order to make it an object

    }

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
        build.append("| Open = "+Integer.toString(opened));
        build.append(" | position = ("+Integer.toString(coordX)+","+Integer.toString(coordY)+")");
        return build.toString();
    }

    //Observer
    public int getCoordX(){
        return coordX;
    }

    public int getCoordY(){
        return coordY;
    }

    public DieConstraints getDie() {
        return die;
    }

    public boolean areObserversNotInitialized() {
        return observerList.isEmpty();
    }

    public boolean tryToInsertDie(boolean colorCheck, boolean valueCheck, Die passedDie){
        DieConstraints toCheck= new DieToConstraintsAdapter(passedDie);
        if(this.die!=null)
            return false;
        if(this.opened==0)
            return false;
        if(colorCheck && (colorRestriction[toCheck.getColorRestriction()]>0))
            return false;
        if(valueCheck && (valueRestriction[toCheck.getValueRestriction()]>0))
            return false;

        return true;

    }

    //modifier
    public void setToOpened() {
        this.opened=1;
    }

    public void insertDie(Die chosenDie) //mossa standard per inserire un dado
    {
        if (chosenDie==null) throw new NullPointerException();
        this.die= new DieToConstraintsAdapter(chosenDie);
        notifyAllObservers(false);
    }

    public Die removeDie() throws LimitValueException{
        if(this.die == null) throw new LimitValueException("die attribute", "null");
        DieConstraints temp;
        temp=this.die;
        notifyAllObservers(true);
        this.die=null;
        return temp.getDie();
    }

    public void modifyDie(){
        //scegliere come modificare il dado (è stata creata una funzione in dieCostraints)
    }

    private void updateColor(boolean remove, int position){
        if(!remove)
            this.colorRestriction[position]++;
        else
            this.colorRestriction[position]--;
    }

    private void updateValue(boolean remove, int position){
        if(!remove)
            this.valueRestriction[position]++;
        else
            this.valueRestriction[position]--;
    }

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

        //invece il fatto che la casella sia apeta va aggiorato a tutti
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

    public int checkPrivatePoints(String color) throws NotValidParameterException {

        DieConstraints tempDie= new DieToConstraintsAdapter(new Die(color, 1)); //il dado è fittizio
        if(tempDie.getColorRestriction()==this.die.getColorRestriction())
            return this.die.getValueRestriction()+1;
        else
            return 0;
    }


    public String getConstraint() {
        //TODO refactor
        final String NO_CONSTRAINT_MESSAGE = "none";
        if(constraintIndex==NO_CONSTRAINT) return NO_CONSTRAINT_MESSAGE;
        else if(kindOfConstraint==VALUE_CONSTRAINT){
            return Integer.toString(constraintIndex+1);
        }
        else{
            switch (constraintIndex){
                case 0:
                    return "red";
                case 1:
                    return "green";
                case 2:
                    return "yellow";
                case 3:
                    return "blue";
                default:
                    return "purple";
            }
        }
    }
}
