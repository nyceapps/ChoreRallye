package com.nyceapps.chorerallye;

import static com.nyceapps.chorerallye.Constants.SETTINGS_DEFAULT_VALUE_RACE_WINNING_PERCENTAGE;

/**
 * Created by lugosi on 09.03.17.
 */

public class Settings {
    //private static final String TAG = Settings.class.getSimpleName();

    private boolean isRunning;
    private int winningPercentage;

    public Settings() {
        isRunning = false;
        winningPercentage = SETTINGS_DEFAULT_VALUE_RACE_WINNING_PERCENTAGE;
    }

    public boolean isRunning() {
        return isRunning;
    }

    public void setRunning(boolean pIsRunning) {
        isRunning = pIsRunning;
    }

    public int getWinningPercentage() {
        return winningPercentage;
    }

    public void setWinningPercentage(int pWinningPercentage) {
        winningPercentage = pWinningPercentage;
    }
}
