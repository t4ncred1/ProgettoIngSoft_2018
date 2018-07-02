package it.polimi.ingsw.server.custom_exception;

public class LimitValueException extends Exception {
    /**
     *
     * @param attribute The name of the attribute.
     * @param actualValue Actual value of the attribute.
     * @param valueLimit Value limit of the attribute.
     */
    public LimitValueException(String attribute, String actualValue, String valueLimit){
        super("the attribute " + attribute + "can't get value :" + actualValue + "because it's value limit is: "+ valueLimit);
    }

    /**
     *
     * @param attribute The name of the attribute.
     * @param actualValue Actual value of the attribute.
     */
    public LimitValueException(String attribute ,String actualValue){
        super("the attribute " + attribute + "can't get value :" + actualValue);
    }
}
