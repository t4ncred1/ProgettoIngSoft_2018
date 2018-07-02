package it.polimi.ingsw.server.custom_exception;

public class NotValidParameterException extends Exception{
    /**
     *
     * @param error Actual parameter.
     * @param expected Expected parameter.
     */
    public NotValidParameterException(String error, String expected){
        super("Parameter: " + error + ". Expected: " + expected);
    }

}

