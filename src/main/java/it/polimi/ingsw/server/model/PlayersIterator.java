package it.polimi.ingsw.server.model;

import it.polimi.ingsw.server.custom_exception.NotEnoughPlayersException;
import it.polimi.ingsw.server.model.components.Player;

import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

public class PlayersIterator implements Iterator<Player> {

    private boolean leftToRight;
    private boolean justChanged;
    private int currentTurn;
    private List<Player> playerList;

    /**
     * Constructor for PlayerIterator.
     *
     * @param pList The list of players.
     */
    PlayersIterator(List<Player> pList) {
        this.playerList = pList;
        leftToRight = true;
        justChanged = true;
        currentTurn = -1;
    }

    /**
     * @return True if the iteration has more elements.
     */

    private boolean hasSuccessor() {  //shall return false when turn ends
        if (leftToRight) {
            int index = currentTurn + 1;
            while (index < playerList.size() && playerList.get(index).isDisconnected()) {
                index++;
            }
            if (index == playerList.size()) {
                leftToRight = false;
                justChanged = true;
            }
            return true;
        } else {
            int index = currentTurn-1;
            while (index >= 0 && playerList.get(index).isDisconnected()) {
                index--;
            }
            if (index == -1) {
                leftToRight = true;
                justChanged = true;
                return false;
            }
            return true;
        }
    }

    public boolean hasNext(){
        if (leftToRight){
            return true;
        }
        else{
            int index = currentTurn-1;
            while (index >= 0 && playerList.get(index).isDisconnected()) {
                index--;
            }
            return (index!=-1);
        }
    }



    /**
     * @return The next element in the iteration.
     */
    @Override
    public Player next() {
        if(hasSuccessor()) {
            if (leftToRight) {
                if (justChanged) {  //Se il verso di percorrenza è appena stato modificato currentTurn non deve cambiare.
                    justChanged = false;
                    int index=currentTurn+1;
                    while (index < playerList.size() && playerList.get(index).isDisconnected()) {
                        index++;
                    }
                    currentTurn=index;
                    playerList.get(currentTurn).setFirstTurn(true);
                } else {
                    int index = currentTurn+1;
                    while (index < playerList.size() && playerList.get(index).isDisconnected()) {
                        index++;
                    }
                    currentTurn = index;
                    playerList.get(currentTurn).setFirstTurn(true);
                }
            } else {
                int index;
                if (justChanged) {  //Se il verso di percorrenza è appena stato modificato currentTurn non deve cambiare.
                    index = currentTurn;
                    justChanged=false;
                } else {
                    index = currentTurn - 1;
                }
                while (index >= 0 && (playerList.get(index).isDisconnected() || playerList.get(index).isJumpSecondTurn())) {
                    if (playerList.get(index).isJumpSecondTurn()) {
                        playerList.get(index).setJumpSecondTurn(false);
                    }
                    playerList.get(index).setFirstTurn(false);
                    index--;
                }
                currentTurn = index;

            }
            return playerList.get(currentTurn);
        } else throw new NoSuchElementException();
    }
}


