/*
 * Copyright (c) 2018. LayoutXML
 * Created by LayoutXML.
 * This product is protected by copyright and distributed under licenses restricting copying, distribution and decompilation.
 */

package com.layoutxml.twelveish.config;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.wearable.complications.ComplicationHelperActivity;
import android.support.wearable.complications.ComplicationProviderInfo;
import android.support.wearable.complications.ProviderChooserIntent;
import android.support.wearable.complications.ProviderInfoRetriever;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.layoutxml.twelveish.MyWatchFace;
import com.layoutxml.twelveish.R;

import java.util.concurrent.Executors;

public class ComplicationConfigActivity extends Activity implements View.OnClickListener {

    private static final String TAG = "ConfigActivity";
    static final int COMPLICATION_CONFIG_REQUEST_CODE = 1001;
    public enum ComplicationLocation {
        BOTTOM
    }
    private int mBottomComplicationId;
    private int mSelectedComplicationId;
    private ComponentName mWatchFaceComponentName;
    private ProviderInfoRetriever mProviderInfoRetriever;
    private ImageView mBottomComplicationBackground;
    private ImageButton mBottomComplication;
    private Drawable mDefaultAddComplicationDrawable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_config);
        mDefaultAddComplicationDrawable = getDrawable(R.drawable.add_complication);
        mSelectedComplicationId = -1;

        mBottomComplicationId =
                MyWatchFace.getComplicationId(ComplicationLocation.BOTTOM);

        mWatchFaceComponentName =
                new ComponentName(getApplicationContext(), MyWatchFace.class);

        mBottomComplicationBackground = findViewById(R.id.bottom_complication_background);
        mBottomComplication = findViewById(R.id.bottom_complication);
        mBottomComplication.setOnClickListener(this);

        mBottomComplication.setImageDrawable(mDefaultAddComplicationDrawable);
        mBottomComplicationBackground.setVisibility(View.INVISIBLE);

        mProviderInfoRetriever =
                new ProviderInfoRetriever(getApplicationContext(), Executors.newCachedThreadPool());
        mProviderInfoRetriever.init();

        retrieveInitialComplicationsData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mProviderInfoRetriever.release();
    }

    public void retrieveInitialComplicationsData() {

        final int[] complicationIds = MyWatchFace.getComplicationIds();

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
        }
    }

    private void launchComplicationHelperActivity(ComplicationLocation complicationLocation) {
        mSelectedComplicationId =
                MyWatchFace.getComplicationId(complicationLocation);
        if (mSelectedComplicationId >= 0) {
            int[] supportedTypes =
                    MyWatchFace.getSupportedComplicationTypes(
                            complicationLocation);
            startActivityForResult(
                    ComplicationHelperActivity.createProviderChooserHelperIntent(
                            getApplicationContext(),
                            mWatchFaceComponentName,
                            mSelectedComplicationId,
                            supportedTypes),
                    ComplicationConfigActivity.COMPLICATION_CONFIG_REQUEST_CODE);
        } else {
            Log.d(TAG, "Complication not supported by watch face.");
        }
    }

    public void updateComplicationViews(
            int watchFaceComplicationId, ComplicationProviderInfo complicationProviderInfo) {
        if (watchFaceComplicationId == mBottomComplicationId) {
            if (complicationProviderInfo != null) {
                mBottomComplication.setImageIcon(complicationProviderInfo.providerIcon);
                mBottomComplicationBackground.setVisibility(View.VISIBLE);

            } else {
                mBottomComplication.setImageDrawable(mDefaultAddComplicationDrawable);
                mBottomComplicationBackground.setVisibility(View.INVISIBLE);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == COMPLICATION_CONFIG_REQUEST_CODE && resultCode == RESULT_OK) {
            ComplicationProviderInfo complicationProviderInfo =
                    data.getParcelableExtra(ProviderChooserIntent.EXTRA_PROVIDER_INFO);
            if (mSelectedComplicationId >= 0) {
                Toast.makeText(getApplicationContext(), "Complication set", Toast.LENGTH_SHORT).show();
                updateComplicationViews(mSelectedComplicationId, complicationProviderInfo);
                finish();
            }
        }
    }
}
