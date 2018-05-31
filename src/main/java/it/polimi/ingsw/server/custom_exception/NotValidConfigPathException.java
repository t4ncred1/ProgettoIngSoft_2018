package it.polimi.ingsw.serverPart.custom_exception;

public class NotValidConfigPathException extends Exception{
    public NotValidConfigPathException(String invalidPath){
        super(invalidPath);
    }
}
