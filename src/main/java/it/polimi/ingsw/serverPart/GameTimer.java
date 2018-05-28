package it.polimi.ingsw.serverPart;

import java.util.Timer;
import java.util.TimerTask;

public class GameTimer {

    private int secondsPassed = 0;
    private int minutesPassed = 0;
    private long timerSet;
    private long timerToStart=15;
    private long timeForGridsInitialization=30;
    private MatchController gameHandled;
    private boolean gameTimeout;

    private final int SECOND_TO_MINUTE_RATIO = 60;

    Timer timer = new Timer();
    TimerTask handlerTimerTask = new TimerTask() {
        @Override
        public void run() {
            secondsPassed++;
            if (secondsPassed >= 60) {
                secondsPassed = secondsPassed % SECOND_TO_MINUTE_RATIO;
                minutesPassed++;
            }
            System.out.println(minutesPassed+ ":"+secondsPassed);
            if ((secondsPassed + minutesPassed * SECOND_TO_MINUTE_RATIO) >= timerSet) {
                System.out.println("Timeout");
                MatchHandler.notifyTimeout();
            }

        }
    };

    TimerTask gameTimerTask = new TimerTask() {
        @Override
        public void run() {
            secondsPassed++;
            if (secondsPassed >= 60) {
                secondsPassed = secondsPassed % SECOND_TO_MINUTE_RATIO;
                minutesPassed++;
            }
            System.out.println(minutesPassed+ ":"+secondsPassed);
            if ((secondsPassed + minutesPassed * SECOND_TO_MINUTE_RATIO) >= timerSet) {
                System.out.println("Timeout");
                gameTimeout=true;
                gameHandled.wakeUpController();
            }

        }
    };

    public GameTimer(String message){
        switch (message){
            case "game":
                timerSet= timerToStart;
                break;
        }
        timer.scheduleAtFixedRate(handlerTimerTask,1,1000);
    }

    public GameTimer(MatchController matchController, String message){
        this.gameTimeout=false;
        this.gameHandled=matchController;
        switch (message){
            case "initialization":
                timerSet= timeForGridsInitialization;
                break;
        }
        timer.scheduleAtFixedRate(gameTimerTask,1,1000);
    }

    public void stop() {
        timer.cancel();
    }

    public boolean getTimeoutEvent() {
        return this.gameTimeout;
    }
}
