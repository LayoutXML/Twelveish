package com.layoutxml.twelveish;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityOptionsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.wearable.Wearable;
import com.layoutxml.twelveish.activities.InstallActivity;
import com.layoutxml.twelveish.dagger.App;
import com.layoutxml.twelveish.fragments.PreviewFragment;

public class HomeScreen extends AppCompatActivity {

    private Communicator communicator;
    private static final String TAG = "HomeScreen";
    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_screen);

        communicator = ((App) getApplication()).getCommunicatorComponent().getCommunicator();
        communicator.initiateHandshake();
        Log.d(TAG, "communicatorID" + communicator);

        final View preview = findViewById(R.id.fragmentPreview);

        Button buttonCustomize = findViewById(R.id.buttonCustomize);
        buttonCustomize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),CustomizationScreen.class);
                ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(HomeScreen.this, preview, "preview");
                startActivity(intent, options.toBundle());
            }
        });

        Button install = findViewById(R.id.buttonInstall);
        install.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), InstallActivity.class);
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

        fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        Fragment previewFragment = new PreviewFragment();
        fragmentTransaction.replace(R.id.fragmentPreview,previewFragment);
        fragmentTransaction.commit();
    }
}
