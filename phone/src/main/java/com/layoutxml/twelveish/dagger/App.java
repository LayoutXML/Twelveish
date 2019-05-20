package com.layoutxml.twelveish.dagger;

import android.app.Application;

import com.google.android.gms.wearable.Wearable;
import com.layoutxml.twelveish.Communicator;

public class App extends Application {

    private CommunicatorComponent component;
    private Communicator communicator;

    @Override
    public void onCreate() {
        super.onCreate();

        component = DaggerCommunicatorComponent.factory()
                .create(getApplicationContext());

        communicator = component.getCommunicator();
        communicator.initiateHandshake();
        Wearable.getDataClient(getApplicationContext()).addListener(communicator);
    }

    public CommunicatorComponent getCommunicatorComponent() {
        return component;
    }



}
