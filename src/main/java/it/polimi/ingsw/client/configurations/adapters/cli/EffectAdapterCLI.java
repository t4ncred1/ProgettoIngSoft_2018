package it.polimi.ingsw.client.configurations.adapters;


import it.polimi.ingsw.client.MainClient;
import it.polimi.ingsw.server.model.cards.effects.Effect;


import java.util.ArrayList;
import java.util.HashMap;

public class EffectAdapterCLI extends EffectAdapter {

    HashMap<String,String[]> parameters;
    public EffectAdapterCLI(Effect effect, boolean removeAllDice) {
        super(effect);
        parameters=new HashMap<>();
        if(!removeAllDice) {
            parameters.put("RemoveDieFromPoolEffect", new String[]{"Inserisci l'indice del dado nella dice pool"});
        }else {
            parameters.put("RemoveDieFromPoolEffect", new String[]{});
        }
        parameters.put("InverseValueEffect", new String[]{});
        parameters.put("InsertDieInPoolEffect", new String[]{});
        parameters.put("IncrementDiceEffect", new String[]{"Inserire true se vuoi incrementare il dado nella dicePool, altrimenti false per decrementarlo"});
        parameters.put("RemoveDieFromGridEffect", new String[]{"Inserisci la colonna da cui rimuovere il dado","Inserisci la riga da cui rimuovere il dado"});
        parameters.put("InsertDieInGridEffect", new String[]{"Inserisci la colonna in cui inserire il dado","Inserisci la riga in cui inserire il dado"});
        parameters.put("RemoveDieFromRoundTrackEffect", new String[]{"Inserisci l'indice del dado da sostituire nella round track"});
        parameters.put("ChangeValueDiceEffect", new String[]{});
        parameters.put("SwapDieEffect", new String[]{});
        parameters.put("SwapRTDieAndDPDieEffect", new String[]{});
        parameters.put("InsertDieInRoundTrackEffect", new String[]{});
    }

    @Override
    public void getEffectInterface(){
        String[] params = parameters.get(super.getName());
        if(params==null) System.err.println(super.getName());
        ArrayList<String> temp=new ArrayList<>();
        for(String s: params){
            if(s!=null) temp.add(s);
        }
        MainClient.getInstance().setPrint(temp);
    }
}
