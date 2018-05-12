package it.polimi.ingsw.customException;

public class NotValidParameterException extends Exception{

    public NotValidParameterException(String error, String expected){
        super("Parameter: " + error + ". Expected: " + expected);
    }

}

