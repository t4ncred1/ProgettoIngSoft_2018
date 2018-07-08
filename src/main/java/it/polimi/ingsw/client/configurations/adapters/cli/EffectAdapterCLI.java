package it.polimi.ingsw.client.configurations.adapters.cli;

import it.polimi.ingsw.client.configurations.adapters.EffectInterface;
import it.polimi.ingsw.server.model.cards.effects.Effect;

import java.util.HashMap;
import java.util.List;

@FunctionalInterface
interface Function<R>{
    /**
     * Define a model for getParameters method.
     */
    R getParameters();
}

public class EffectAdapterCLI extends EffectInterface {

    private HashMap<String,Function<List<String>>> parameters;

    /**
     * Constructor for EffectAdapterCLI.
     *
     * @param effect The effect selected.
     * @param removeAllDice True if all the dice have to be removed from the dice pool.
     */
    public EffectAdapterCLI(Effect effect, boolean removeAllDice) {
        super(effect);
        parameters=new HashMap<>();
        if(removeAllDice) {
            parameters.put("RemoveDieFromPoolEffect", EffectHandler::removeAllDiceFromPoolEffect);
        }else {
            parameters.put("RemoveDieFromPoolEffect", EffectHandler::removeASingleDieFromPoolEffect);
        }
        parameters.put("InverseValueEffect", EffectHandler::inverseValueEffect);
        parameters.put("InsertDieInPoolEffect", EffectHandler::insertDieInPoolEffect);
        parameters.put("IncrementDiceEffect", EffectHandler::incrementDiceEffect);
        parameters.put("RemoveDieFromGridEffect", EffectHandler::removeDieFromGridEffect);
        parameters.put("InsertDieInGridEffect", EffectHandler::insertDieInGridEffect);
        parameters.put("RemoveDieFromRoundTrackEffect", EffectHandler::removeADieFromRoundTrackEffect);
        parameters.put("ChangeValueDiceEffect", EffectHandler::changeValueDiceEffect);
        parameters.put("SwapDieEffect", EffectHandler::swapDieEffect);
        parameters.put("SwapRTDieAndDPDieEffect", EffectHandler::swapRTDieAndDPDieEffect);
        parameters.put("InsertDieInRoundTrackEffect", EffectHandler::insertDieInRoundtrackEffect);
    }


    @Override
    public List<String> computeEffect(){
        Function<List<String>> function= parameters.get(super.getName());
        return function.getParameters();
    }
}
