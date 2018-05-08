package it.polimi.ingsw;
import it.polimi.ingsw.customException.LimitValueException;
import it.polimi.ingsw.customException.NotProperParameterException;

import java.util.ArrayList;

public class Box implements BoxObserver, BoxSubject{


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

    private DieCostraints die;
    private int[] colorRestriction;
    private int[] valueRestriction;
    private boolean colorConstraint;
    private int constraintIndex;
    private int opened;
    private ArrayList<BoxObserver> observerList;
    private int coordX;
    private int coordY;

    //creators
    public Box(int x, int y) throws NotProperParameterException {
        final String expectedDataType= new String ("Coord expected value: 0<x<4 and 0<y<3");
        if((x<0||x>4)||(y<0||y>3)) throw new NotProperParameterException("Coord: "+x+", "+ y,expectedDataType);
        this.coordX= x;
        this.coordY= y;
        die=null;
        colorRestriction = new int[5];
        valueRestriction = new int[6];
        observerList= new ArrayList<BoxObserver>();

        opened=0;
        for (int i = 0; i<colorRestriction.length; i++ ) colorRestriction[i] = 0;
        for (int i=0; i<valueRestriction.length; i++ ) valueRestriction[i] = 0;
    }


    public Box(String color, int x, int y) throws NotProperParameterException {
        this(x,y);
        final String expectedDataType= new String("Color: red, yellow, green, blue, purple");

        if(color.equals("red")||color.equals("green")||color.equals("yellow")||color.equals("blue")||color.equals("purple")){
            DieCostraints die = new DieToCostraintsAdapter(new Die(color, 1));
            colorConstraint =true;
            constraintIndex = die.getColorRestriction();
            for(int i=0; i<colorRestriction.length; i++)
                if(i!= constraintIndex)colorRestriction[i]=1;
        }
        else
            throw new NotProperParameterException(color, expectedDataType);
    }
    public Box(int value, int x, int y) throws NotProperParameterException {
        this(x,y);
        final String expectedDataType= new String("Value: 1, 2, 3, 4, 5, 6");

        if(value>=1&&value<=6){
            DieCostraints die = new DieToCostraintsAdapter(new Die("red", value)); //the color isn't important, so it was randomly chosen by the author
            colorConstraint =false;
            constraintIndex = die.getValueRestriction();
            for(int i=0; i<valueRestriction.length; i++)
                if(i!=constraintIndex)valueRestriction[i]=1;
        }
        else
            throw new NotProperParameterException(((Integer)value).toString(), expectedDataType); //hat to put a cast in order to make it an object

    }

    //Observer
    public int getCoordX(){
        return coordX;
    }

    public int getCoordY(){
        return coordY;
    }


    public boolean areObserversNotInitialized() {
        return observerList.size() <= 0;
    }

    public boolean tryToInsertDie(boolean colorCheck, boolean valueCheck, Die passedDie){
        DieCostraints toCheck= new DieToCostraintsAdapter(passedDie);
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
    public void insertDie(Die chosenDie) //mossa standard per inserire un dado
    {
        if (chosenDie==null) throw new NullPointerException();
        this.die= new DieToCostraintsAdapter(chosenDie);
        notifyAllObservers(false);
    }

    public Die removeDie() throws LimitValueException{
        if(this.die == null) throw new LimitValueException("die attribute", "null");
        DieCostraints temp;
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
    public void update(boolean remove, DieCostraints nearDie, int x, int y) throws NotProperParameterException{
        //se le casella in cui ho inserito il dado non è diagonali aggiorno i costraints di colore e valore
//        if (nearDie.getValueRestriction()<0 || nearDie.getValueRestriction()>5)  throw new NotProperParameterException("value constraint of the near box: "+ nearDie.getValueRestriction(),"value between 0 and 5");      //these tests are not meant here but in DieToCostraintsAdapter class
//        if (nearDie.getColorRestriction()<0 || nearDie.getColorRestriction()>4)  throw new NotProperParameterException("color constraint of the near box: "+ nearDie.getColorRestriction(),"value between 0 and 4");
        if (x!=this.getCoordX()-1 && x!=this.getCoordX()+1 && y!=this.getCoordY()+1 && y!=this.getCoordY()-1) throw new NotProperParameterException("Position of the near box:"+ String.valueOf(x) +", "+String.valueOf(y), "this box should not be between the observers of the box which called the update");
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
    public void unregister(BoxObserver observer){
        this.observerList.remove(observer);
    }

    @Override
    public void notifyAllObservers(boolean remove){     //for now, it just prints an error
        for(BoxObserver e: observerList)
        {
            try {
                e.update(remove, this.die, this.getCoordY(), this.getCoordY());
            } catch(NotProperParameterException err){
                err.printStackTrace();
            }
        }
    }

    public int checkPrivatePoints(String color) throws NotProperParameterException{

        DieCostraints tempDie= new DieToCostraintsAdapter(new Die(color, 1)); //il dado è fittizio
        if(tempDie.getColorRestriction()==this.die.getColorRestriction())
            return this.die.getValueRestriction()+1;
        else
            return 0;
    }
}
