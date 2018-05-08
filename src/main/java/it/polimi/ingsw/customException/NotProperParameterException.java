package it.polimi.ingsw.customException;

public class NotProperParameterException extends Exception{

    public NotProperParameterException(String error, String expected){
        super("Parameter: " + error + ". Expected: " + expected);
    }

}

