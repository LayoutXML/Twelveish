package com.layoutxml.twelveish.activities;

import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.layoutxml.twelveish.R;

public class InstallActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.install_activity);
    }
}
