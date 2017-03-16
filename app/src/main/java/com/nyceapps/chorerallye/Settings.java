package com.nyceapps.chorerallye;

import static com.nyceapps.chorerallye.Constants.SETTINGS_DEFAULT_VALUE_DISPLAY_MODE;
import static com.nyceapps.chorerallye.Constants.SETTINGS_DEFAULT_VALUE_RACE_WINNING_PERCENTAGE;

/**
 * Created by lugosi on 09.03.17.
 */

public class Settings {
    //private static final String TAG = Settings.class.getSimpleName();

    private boolean isRunning;
    private String displayMode;
    private int winningPercentage;

    public Settings() {
        isRunning = false;
        displayMode = SETTINGS_DEFAULT_VALUE_DISPLAY_MODE;
        winningPercentage = SETTINGS_DEFAULT_VALUE_RACE_WINNING_PERCENTAGE;
    }

    public boolean isRunning() {
        return isRunning;
    }

    public void setRunning(boolean pIsRunning) {
        isRunning = pIsRunning;
    }

    public String getDisplayMode() {
        return displayMode;
    }

    public void setDisplayMode(String pDisplayMode) {
        displayMode = pDisplayMode;
    }

    public int getWinningPercentage() {
        return winningPercentage;
    }

    public void setWinningPercentage(int pWinningPercentage) {
        winningPercentage = pWinningPercentage;
    }
}
