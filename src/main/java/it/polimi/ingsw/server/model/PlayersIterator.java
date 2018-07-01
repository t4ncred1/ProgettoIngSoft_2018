package it.polimi.ingsw.server.model;

import it.polimi.ingsw.server.custom_exception.NotEnoughPlayersException;
import it.polimi.ingsw.server.model.components.Player;

import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

public class PlayersIterator implements Iterator<Player> {

    private boolean leftToRight;
    private boolean justChanged;
    private boolean turnEnded;
    private int currentTurn;
    private List<Player> playerList;

    PlayersIterator(List<Player> pList){
        this.playerList = pList;
        leftToRight = true;
        justChanged = true;
        turnEnded = false;
        currentTurn = 0;
    }

    @Override
    public boolean hasNext() {  //shall return false when turn ends
        return(!turnEnded);
    }

    @Override
    public Player next() {
        if (this.currentTurn>=playerList.size()) currentTurn=playerList.size()-1;   // this if statement is put here because disconnection might violate currentTurn's validity.
        if (this.hasNext()){
            if(leftToRight){
                if(justChanged) {  //Se il verso di percorrenza è appena stato modificato currentTurn non deve cambiare.
                    justChanged = false;
                    currentTurn=0;
                    playerList.get(currentTurn).setFirstTurn(true);
                }
                else {
                    currentTurn++;
                    playerList.get(currentTurn).setFirstTurn(true);
                    if(currentTurn>=playerList.size()-1){
                        leftToRight=false;
                        justChanged=true;
                    }
                }
            }
            else {
                if(justChanged) {  //Se il verso di percorrenza è appena stato modificato currentTurn non deve cambiare.
                    playerList.get(currentTurn).setFirstTurn(false);
                    if(playerList.get(currentTurn).isJumpSecondTurn()) currentTurn--;
                    justChanged = false;
                }
                else {
                    if (currentTurn!=0 && playerList.get(currentTurn-1).isJumpSecondTurn()){
                        if(currentTurn-1 == 0) {
                            leftToRight=true;
                            justChanged=true;
                            turnEnded=true;
                            return playerList.get(currentTurn);
                        }
                        else {
                            currentTurn = currentTurn-2;
                        }
                    }
                    else currentTurn--;
                }
                if(currentTurn<=0){
                    currentTurn=0;
                    leftToRight=true;
                    justChanged=true;
                    turnEnded=true;
                    playerList.get(currentTurn).setFirstTurn(true);
                }

            }
            return playerList.get(currentTurn);
        }
        else throw new NoSuchElementException();
    }
}
