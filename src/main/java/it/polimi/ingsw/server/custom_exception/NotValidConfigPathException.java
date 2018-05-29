package it.polimi.ingsw.server.custom_exception;

public class NotValidConfigPathException extends Exception{
    public NotValidConfigPathException(String invalidPath){
        super(invalidPath);
    }
}
