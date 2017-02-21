package com.nyceapps.chorerallye;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by lugosi on 10.02.17.
 */

public class RallyeApplication extends Application {
    private RallyeData rallyeData;

    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }

    public RallyeData getRallyeData() {
        return rallyeData;
    }

    public void setRallyeData(RallyeData pRallyeData) {
        rallyeData = pRallyeData;
    }
}
