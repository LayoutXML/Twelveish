package com.layoutxml.twelveish.dagger;

import android.app.Application;

import javax.inject.Inject;

public class App extends Application {

    @Inject
    CommunicatorComponent component;

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
