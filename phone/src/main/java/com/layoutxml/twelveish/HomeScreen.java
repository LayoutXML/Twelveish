package com.layoutxml.twelveish;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.wearable.Wearable;
import com.layoutxml.twelveish.dagger.App;

public class HomeScreen extends AppCompatActivity {

    private Communicator communicator;
    private static final String TAG = "HomeScreen";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_screen);

        communicator = ((App) getApplication()).getCommunicatorComponent().getCommunicator();
        communicator.initiateHandshake();
        Log.d(TAG, "communicatorID" + communicator);

        Button buttonCustomize = findViewById(R.id.buttonCustomize);
        buttonCustomize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),CustomizationScreen.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        Wearable.getDataClient(getApplicationContext()).removeListener(communicator); //unregistering and registering in every activity separately
        //because we want to unregister when application closes and there's no onPause/onDestroy for classes like App.java that extend Application
    }

    @Override
    protected void onResume() {
        super.onResume();
        Wearable.getDataClient(getApplicationContext()).addListener(communicator);
        communicator.initiateHandshake();
    }
}
