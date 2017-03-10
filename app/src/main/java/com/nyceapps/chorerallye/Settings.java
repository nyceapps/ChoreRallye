package com.nyceapps.chorerallye;

/**
 * Created by lugosi on 09.03.17.
 */

public class Settings {
    //private static final String TAG = Settings.class.getSimpleName();

    private boolean isRunning;

    public Settings() {
        isRunning = false;
    }

    public boolean isRunning() {
        return isRunning;
    }

    public void setRunning(boolean pIsRunning) {
        isRunning = pIsRunning;
    }

}
