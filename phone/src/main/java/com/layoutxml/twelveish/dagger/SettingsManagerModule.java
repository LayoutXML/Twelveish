package com.layoutxml.twelveish.dagger;

import android.content.Context;

import com.layoutxml.twelveish.SettingsManager;

import dagger.Module;
import dagger.Provides;

@Module
public class SettingsManagerModule {

    @PerCustomization
    @Provides
    SettingsManager provideSettingsManager(Context context) {
        SettingsManager sm = new SettingsManager(context);
        sm.initializeDefaultBooleans();
        sm.initializeDefaultIntegers();
        sm.initializeDefaultStrings();
        return sm;
    }

}
