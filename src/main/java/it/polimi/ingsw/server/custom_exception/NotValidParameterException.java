package it.polimi.ingsw.server.custom_exception;

public class NotValidParameterException extends Exception{

    public NotValidParameterException(String error, String expected){
        super("Parameter: " + error + ". Expected: " + expected);
    }

}

