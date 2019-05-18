package com.layoutxml.twelveish;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.layoutxml.twelveish.fragments.PreviewFragment;

public class CustomizationScreen extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.customize_screen);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        Fragment previewFragment = new PreviewFragment();
        fragmentTransaction.replace(R.id.fragmentPreview,previewFragment);

        fragmentTransaction.commit();
    }
}
