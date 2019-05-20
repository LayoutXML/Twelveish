package com.layoutxml.twelveish.dagger;

import android.app.Application;

public class App extends Application {

    private CommunicatorComponent component;

    @Override
    public void onCreate() {
        super.onCreate();

        component = DaggerCommunicatorComponent.factory()
                .create(getApplicationContext());
    }

    public CommunicatorComponent getCommunicatorComponent() {
        return component;
    }



}
