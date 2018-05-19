package it.polimi.ingsw.serverPart;

import java.util.Timer;
import java.util.TimerTask;

public class GameTimer {

    private int secondsPassed = 0;
    private int minutesPassed = 0;
    private long timerSet;
    private long timerToStart=15;

    private final int secondsToMinutesRatio = 60;

    public GameTimer(String message){
        switch (message){
            case "game":
                timerSet= timerToStart;
                break;
        }
        timer.scheduleAtFixedRate(timerTask,1,1000);
    }

    Timer timer = new Timer();
    TimerTask timerTask = new TimerTask() {
        @Override
        public void run() {
            secondsPassed++;
            if (secondsPassed >= 60) {
                secondsPassed = secondsPassed % secondsToMinutesRatio;
                minutesPassed++;
            }
            System.out.println(minutesPassed+ ":"+secondsPassed);
            if ((secondsPassed + minutesPassed * secondsToMinutesRatio) >= timerSet) {
                System.out.println("Timeout");
                MatchHandler.notifyTimeout();
            }

        }
    };

    public void stop() {
        timer.cancel();
    }
}
