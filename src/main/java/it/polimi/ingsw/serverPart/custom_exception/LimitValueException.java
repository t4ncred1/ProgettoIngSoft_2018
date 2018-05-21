package it.polimi.ingsw.serverPart.custom_exception;

public class LimitValueException extends Exception {
    public LimitValueException(String attribute, String actualValue, String valueLimit){
        super("the attribute " + attribute + "can't get value :" + actualValue + "because it's value limit is: "+ valueLimit);
    }
    public LimitValueException(String attribute ,String actualValue){
        super("the attribute " + attribute + "can't get value :" + actualValue);
    }
}
