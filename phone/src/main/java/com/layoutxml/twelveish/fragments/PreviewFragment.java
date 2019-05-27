package com.layoutxml.twelveish.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.layoutxml.twelveish.CustomizationScreen;
import com.layoutxml.twelveish.R;
import com.layoutxml.twelveish.objects.WatchPreviewView;

public class PreviewFragment extends Fragment implements CustomizationScreen.AmoledChange {

    private WatchPreviewView previewView;
    private static final String TAG = "PreviewFragment";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.preview_fragment,container,false);

        previewView = view.findViewById(R.id.viewWatch);

        return view;
    }

    @Override
    public void ambientModeChange(boolean value) {
        previewView.changeAmbientMode(value);
        Log.d(TAG, "ambientModeChange: "+value);
    }
}
