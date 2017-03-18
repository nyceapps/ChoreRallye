package com.nyceapps.chorerallye.main;

import static com.nyceapps.chorerallye.main.Constants.SETTINGS_DEFAULT_VALUE_DISPLAY_MODE;
import static com.nyceapps.chorerallye.main.Constants.SETTINGS_DEFAULT_VALUE_LENGTH_OF_RACE_IN_DAYS;
import static com.nyceapps.chorerallye.main.Constants.SETTINGS_DEFAULT_VALUE_RACE_WINNING_PERCENTAGE;

/**
 * Created by lugosi on 09.03.17.
 */

public class Settings {
    //private static final String TAG = Settings.class.getSimpleName();

    private boolean isRunning;
    private String displayMode;
    private int winningPercentage;
    private int lengthOfRallyeInDays;

    public Settings() {
        isRunning = false;
        displayMode = SETTINGS_DEFAULT_VALUE_DISPLAY_MODE;
        winningPercentage = SETTINGS_DEFAULT_VALUE_RACE_WINNING_PERCENTAGE;
        lengthOfRallyeInDays = SETTINGS_DEFAULT_VALUE_LENGTH_OF_RACE_IN_DAYS;
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

    public int getLengthOfRallyeInDays() {
        return lengthOfRallyeInDays;
    }

    public void setLengthOfRallyeInDays(int pLengthOfRallyeInDays) {
        lengthOfRallyeInDays = pLengthOfRallyeInDays;
    }
}
