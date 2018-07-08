package it.polimi.ingsw.client.configurations.adapters.cli;

import it.polimi.ingsw.client.Proxy;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public final class EffectHandler {

    private static final String ANSI_RED="\033[0;31m";
    private static final String ANSI_RESET="\u001B[0m";

    /**
     * Constructor for EffectHandler.
     */
    private EffectHandler(){
        throw new AssertionError();
    }

    /**
     *
     * @return The index of the removed die.
     */
    public static List<String> removeASingleDieFromPoolEffect(){
        System.out.println("Inserisci l'indice del dado nella dice pool");
        List<String> toReturn=new ArrayList<>();
        Scanner scanner= new Scanner(System.in);
        int index;
        boolean ok;
        do{
            try {
                index = Integer.parseInt(scanner.nextLine());
                int dicePoolDimension=Proxy.getInstance().getDicePool().getDicePoolSize();
                ok = okInsertion(toReturn, index, dicePoolDimension);
            }catch (NumberFormatException e){
                System.out.println(ANSI_RED+"Errore: inserire un valore numerico"+ANSI_RESET);
                ok=false;
            }
        }while (!ok);
        return toReturn;
    }

    /**
     *
     * @return An empty arraylist.
     */
    public static List<String> removeAllDiceFromPoolEffect(){
        return new ArrayList<>();
    }

    /**
     *
     * @return An empty arraylist.
     */
    public static List<String> inverseValueEffect(){
        return new ArrayList<>();
    }

    /**
     *
     * @return An empty arraylist.
     */
    public static List<String> insertDieInPoolEffect(){
        return new ArrayList<>();
    }

    /**
     *
     * @return True if the die is correctly incremented.
     */
    public static List<String> incrementDiceEffect(){
        System.out.println("Inserire true se vuoi incrementare il dado nella dicePool, altrimenti false per decrementarlo");
        final String FALSE="false";
        List<String> toReturn= new ArrayList<>();
        Scanner scanner= new Scanner(System.in);
        boolean doIncrement;
        boolean ok;
        String read;
        do{
            read=scanner.nextLine().toLowerCase();
            doIncrement = Boolean.valueOf(read);
            if(doIncrement){
                ok=true;
                toReturn.add(Boolean.toString(true));
            }else {
                if(read.equals(FALSE)){
                    ok=true;
                    toReturn.add(Boolean.toString(false));
                }else {
                    ok=false;
                    System.out.println(ANSI_RED+"Errore: inserire true o false:"+read+ANSI_RESET);
                }
            }

        }while (!ok);
        return toReturn;
    }

    /**
     *
     * @return A list containing column and row of the grid where to remove the die.
     */
    public static List<String> removeDieFromGridEffect(){
        List<String> toReturn= new ArrayList<>();
        int row;
        int column;
        row= chooseRemoveRow();
        column= chooseRemoveColumn();
        toReturn.add(Integer.toString(column));
        toReturn.add(Integer.toString(row));
        return toReturn;
    }

    /**
     *
     * @return A list containing column and row of the grid where to insert the die.
     */
    public static List<String> insertDieInGridEffect(){
        List<String> toReturn= new ArrayList<>();
        int row;
        int column;
        row= chooseInsertRow();
        column= chooseInsertColumn();
        toReturn.add(Integer.toString(column));
        toReturn.add(Integer.toString(row));
        return toReturn;
    }

    /**
     *
     * @return An integer containing the column of the grid where to remove the die.
     */
    private static int chooseRemoveColumn() {
        final int MAX_COLUMN=5;
        System.out.println("Inserisci l'indice della colonna da cui rimuovere il dado");
        Scanner scanner= new Scanner(System.in);
        return getCoordinate(MAX_COLUMN, scanner);
    }

    /**
     *
     * @return An integer containing the column of the grid where to insert the die.
     */
    private static int chooseInsertColumn() {
        final int MAX_COLUMN=5;
        System.out.println("Inserisci l'indice della colonna in cui inserire il dado");
        Scanner scanner= new Scanner(System.in);
        return getCoordinate(MAX_COLUMN, scanner);
    }

    /**
     *
     * @return An integer containing the row of the grid where to remove the die.
     */
    private static int chooseRemoveRow() {
        final int MAX_ROW=4;
        System.out.println("Inserisci l'indice della riga da cui rimuovere il dado");
        Scanner scanner= new Scanner(System.in);
        return getCoordinate(MAX_ROW, scanner);
    }

    /**
     *
     * @return An integer containing the row of the grid where to insert the die.
     */
    private static int chooseInsertRow() {
        final int MAX_ROW=4;
        System.out.println("Inserisci l'indice della riga da cui inserire il dado");
        Scanner scanner= new Scanner(System.in);
        return getCoordinate(MAX_ROW, scanner);
    }

    /**
     *
     * @param limitValue Limit value of the rows.
     * @param scanner A scanner.
     * @return An integer containing the row coordinate of the grid.
     */
    private static int getCoordinate(int limitValue, Scanner scanner) {
        int coordinate;
        do{
            try {
                coordinate = Integer.parseInt(scanner.nextLine());
                if(coordinate<1||coordinate>= limitValue){
                    System.out.println(ANSI_RED+"Errore: inserire un numero tra 1 e "+ limitValue +ANSI_RESET+":");
                }else {
                    return coordinate-1;
                }
            }catch (NumberFormatException e){
                System.out.println(ANSI_RED+"Errore: inserire un valore numerico"+ANSI_RESET);
            }
        }while (true);
    }

    /**
     *
     * @return The index of the die removed.
     */
    public static List<String> removeADieFromRoundTrackEffect(){
        System.out.println("Inserisci l'indice del dado nella round track");
        List<String> toReturn=new ArrayList<>();
        Scanner scanner= new Scanner(System.in);
        int index;
        boolean ok;
        do{
            try {
                index = Integer.parseInt(scanner.nextLine());
                int roundTrackDimension=Proxy.getInstance().getRoundTrack().getRoundTrackDimension();
                ok = okInsertion(toReturn, index, roundTrackDimension);
            }catch (NumberFormatException e){
                System.out.println(ANSI_RED+"Errore: inserire un valore numerico"+ANSI_RESET);
                ok=false;
            }
        }while (!ok);
        return toReturn;
    }

    /**
     *
     * @param toReturn A list of strings. The real content depends on which method calls okInsertion.
     * @param index An index of the round track.
     * @param roundTrackDimension The size of the round track.
     * @return True if the operation goes fine.
     */
    private static boolean okInsertion(List<String> toReturn, int index, int roundTrackDimension) {
        boolean ok;
        if(index<1||index>roundTrackDimension){
            ok=false;
            System.out.println(ANSI_RED+"Errore: inserire un numero tra 1 e "+roundTrackDimension+ANSI_RESET+":");
        }else {
            ok=true;
            toReturn.add(Integer.toString(index-1));
        }
        return ok;
    }

    /**
     *
     * @return An empty arraylist.
     */
    public static List<String> changeValueDiceEffect(){
        return new ArrayList<>();
    }

    /**
     *
     * @return An empty arraylist.
     */
    public static List<String> swapDieEffect(){
        return new ArrayList<>();
    }
    /**
     *
     * @return An empty arraylist.
     */
    public static List<String> swapRTDieAndDPDieEffect(){
        return new ArrayList<>();
    }
    /**
     *
     * @return An empty arraylist.
     */
    public static List<String> insertDieInRoundtrackEffect(){
        return new ArrayList<>();
    }
}
