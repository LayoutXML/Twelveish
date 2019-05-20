package com.layoutxml.twelveish.dagger;

import android.content.Context;

import com.layoutxml.twelveish.Communicator;
import com.layoutxml.twelveish.objects.BooleanOptionP;

import javax.inject.Singleton;

import dagger.BindsInstance;
import dagger.Component;

@Singleton
@Component
public interface CommunicatorComponent {

    Communicator getCommunicator();

    @Component.Factory
    interface Factory {
        CommunicatorComponent create(@BindsInstance Context context);
    }

}
