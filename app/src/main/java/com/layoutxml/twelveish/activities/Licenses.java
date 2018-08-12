package com.layoutxml.twelveish.activities;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import com.layoutxml.twelveish.BuildConfig;
import com.layoutxml.twelveish.R;

public class Licenses extends Activity{

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.licenses);

        int versionCode = BuildConfig.VERSION_CODE;
        String versionName = BuildConfig.VERSION_NAME;

        TextView License2 = findViewById(R.id.license2);
        License2.setText(versionName);

        TextView License3 = findViewById(R.id.license3);
        License3.setText(""+versionCode);
    }
}
