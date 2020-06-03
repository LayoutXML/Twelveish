/*
 * Copyright (c) 2018. LayoutXML
 * Created by LayoutXML.
 *
 */

package com.layoutxml.twelveish.activities;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.wearable.complications.ComplicationHelperActivity;
import android.support.wearable.complications.ComplicationProviderInfo;
import android.support.wearable.complications.ProviderChooserIntent;
import android.support.wearable.complications.ProviderInfoRetriever;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.layoutxml.twelveish.ComplicationManager;
import com.layoutxml.twelveish.R;
import com.layoutxml.twelveish.WatchFace;

import java.util.concurrent.Executors;

public class ComplicationConfigActivity extends Activity implements View.OnClickListener {
    static final int COMPLICATION_CONFIG_REQUEST_CODE = 1001;

    private int mBottomComplicationId;
    private int mLeftComplicationId;
    private int mRightComplicationId;
    private int mSelectedComplicationId;
    private ComponentName mWatchFaceComponentName;
    private ProviderInfoRetriever mProviderInfoRetriever;
    private ImageView mBottomComplicationBackground;
    private ImageButton mBottomComplication;
    private ImageView mLeftComplicationBackground;
    private ImageButton mLeftComplication;
    private ImageView mRightComplicationBackground;
    private ImageButton mRightComplication;
    private Drawable mDefaultAddComplicationDrawable;

    public enum ComplicationLocation {
        BOTTOM, LEFT, RIGHT
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_config);
        mDefaultAddComplicationDrawable = getDrawable(R.drawable.add_complication);
        mSelectedComplicationId = -1;

        mBottomComplicationId = ComplicationManager.getComplicationId(ComplicationLocation.BOTTOM);
        mLeftComplicationId = ComplicationManager.getComplicationId(ComplicationLocation.LEFT);
        mRightComplicationId = ComplicationManager.getComplicationId(ComplicationLocation.RIGHT);

        mWatchFaceComponentName = new ComponentName(getApplicationContext(), WatchFace.class);

        mBottomComplicationBackground = findViewById(R.id.bottom_complication_background);
        mBottomComplication = findViewById(R.id.bottom_complication);
        mBottomComplication.setOnClickListener(this);
        mBottomComplication.setImageDrawable(mDefaultAddComplicationDrawable);
        mBottomComplicationBackground.setVisibility(View.INVISIBLE);

        mLeftComplicationBackground = findViewById(R.id.left_complication_background);
        mLeftComplication = findViewById(R.id.left_complication);
        mLeftComplication.setOnClickListener(this);
        mLeftComplication.setImageDrawable(mDefaultAddComplicationDrawable);
        mLeftComplicationBackground.setVisibility(View.INVISIBLE);

        mRightComplicationBackground = findViewById(R.id.right_complication_background);
        mRightComplication = findViewById(R.id.right_complication);
        mRightComplication.setOnClickListener(this);
        mRightComplication.setImageDrawable(mDefaultAddComplicationDrawable);
        mRightComplicationBackground.setVisibility(View.INVISIBLE);

        mProviderInfoRetriever = new ProviderInfoRetriever(getApplicationContext(), Executors.newCachedThreadPool());
        mProviderInfoRetriever.init();

        retrieveInitialComplicationsData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mProviderInfoRetriever.release();
    }

    public void retrieveInitialComplicationsData() {

        final int[] complicationIds = ComplicationManager.getComplicationIds();

        mProviderInfoRetriever.retrieveProviderInfo(
                new ProviderInfoRetriever.OnProviderInfoReceivedCallback() {
                    @Override
                    public void onProviderInfoReceived(
                            int watchFaceComplicationId,
                            @Nullable ComplicationProviderInfo complicationProviderInfo) {
                        updateComplicationViews(watchFaceComplicationId, complicationProviderInfo);
                    }
                },
                mWatchFaceComponentName,
                complicationIds);
    }

    @Override
    public void onClick(View view) {
        if (view.equals(mBottomComplication)) {
            launchComplicationHelperActivity(ComplicationLocation.BOTTOM);
        } else if (view.equals(mLeftComplication)) {
            launchComplicationHelperActivity(ComplicationLocation.LEFT);
        } else if (view.equals(mRightComplication)) {
            launchComplicationHelperActivity(ComplicationLocation.RIGHT);
        }
    }

    private void launchComplicationHelperActivity(ComplicationLocation complicationLocation) {
        mSelectedComplicationId = ComplicationManager.getComplicationId(complicationLocation);
        if (mSelectedComplicationId >= 0) {
            int[] supportedTypes = ComplicationManager.getSupportedComplicationTypes(complicationLocation);
            startActivityForResult(
                    ComplicationHelperActivity.createProviderChooserHelperIntent(
                            getApplicationContext(),
                            mWatchFaceComponentName,
                            mSelectedComplicationId,
                            supportedTypes),
                    ComplicationConfigActivity.COMPLICATION_CONFIG_REQUEST_CODE);
        }
    }

    public void updateComplicationViews(
            int watchFaceComplicationId, ComplicationProviderInfo complicationProviderInfo) {
        SharedPreferences prefs = this.getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        if (watchFaceComplicationId == mBottomComplicationId) {
            if (complicationProviderInfo != null) {
                mBottomComplication.setImageIcon(complicationProviderInfo.providerIcon);
                mBottomComplicationBackground.setVisibility(View.VISIBLE);
            } else {
                mBottomComplication.setImageDrawable(mDefaultAddComplicationDrawable);
                mBottomComplicationBackground.setVisibility(View.INVISIBLE);
            }
        } else if (watchFaceComplicationId == mLeftComplicationId) {
            if (complicationProviderInfo != null) {
                prefs.edit().putBoolean(getString(R.string.complication_left_set), true).apply();
                mLeftComplication.setImageIcon(complicationProviderInfo.providerIcon);
                mLeftComplicationBackground.setVisibility(View.VISIBLE);
            } else {
                prefs.edit().putBoolean(getString(R.string.complication_left_set), false).apply();
                mLeftComplication.setImageDrawable(mDefaultAddComplicationDrawable);
                mLeftComplicationBackground.setVisibility(View.INVISIBLE);
            }
        } else if (watchFaceComplicationId == mRightComplicationId) {
            if (complicationProviderInfo != null) {
                prefs.edit().putBoolean(getString(R.string.complication_right_set), true).apply();
                mRightComplication.setImageIcon(complicationProviderInfo.providerIcon);
                mRightComplicationBackground.setVisibility(View.VISIBLE);
            } else {
                prefs.edit().putBoolean(getString(R.string.complication_right_set), false).apply();
                mRightComplication.setImageDrawable(mDefaultAddComplicationDrawable);
                mRightComplicationBackground.setVisibility(View.INVISIBLE);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == COMPLICATION_CONFIG_REQUEST_CODE && resultCode == RESULT_OK) {
            ComplicationProviderInfo complicationProviderInfo = data.getParcelableExtra(ProviderChooserIntent.EXTRA_PROVIDER_INFO);
            if (mSelectedComplicationId >= 0) {
                Toast.makeText(getApplicationContext(), "Complication set", Toast.LENGTH_SHORT).show();
                updateComplicationViews(mSelectedComplicationId, complicationProviderInfo);
                finish();
            }
        }
    }
}
