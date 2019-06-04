package com.layoutxml.twelveish.dagger;

import android.content.Context;

import com.layoutxml.twelveish.SettingsManager;

import dagger.BindsInstance;
import dagger.Component;

@PerCustomization
@Component(modules = SettingsManagerModule.class)
public interface SettingsManagerComponent {

    SettingsManager getSettingsManager();

    @Component.Factory
    interface Factory {
        SettingsManagerComponent create(@BindsInstance Context context);
    }

}
