/*
 * Copyright (c) 2018. LayoutXML
 * Created by LayoutXML.
 *
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
        BOTTOM, LEFT, RIGHT
    }
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_config);
        mDefaultAddComplicationDrawable = getDrawable(R.drawable.add_complication);
        mSelectedComplicationId = -1;

        mBottomComplicationId = MyWatchFace.getComplicationId(ComplicationLocation.BOTTOM);
        mLeftComplicationId = MyWatchFace.getComplicationId(ComplicationLocation.LEFT);
        mRightComplicationId = MyWatchFace.getComplicationId(ComplicationLocation.RIGHT);

        mWatchFaceComponentName = new ComponentName(getApplicationContext(), MyWatchFace.class);

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
        } else if (view.equals(mLeftComplication)) {
            launchComplicationHelperActivity(ComplicationLocation.LEFT);
        } else if (view.equals(mRightComplication)) {
            launchComplicationHelperActivity(ComplicationLocation.RIGHT);
        }
    }

    private void launchComplicationHelperActivity(ComplicationLocation complicationLocation) {
        mSelectedComplicationId = MyWatchFace.getComplicationId(complicationLocation);
        if (mSelectedComplicationId >= 0) {
            int[] supportedTypes = MyWatchFace.getSupportedComplicationTypes(complicationLocation);
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
        } else if (watchFaceComplicationId == mLeftComplicationId) {
            if (complicationProviderInfo != null) {
                mLeftComplication.setImageIcon(complicationProviderInfo.providerIcon);
                mLeftComplicationBackground.setVisibility(View.VISIBLE);

            } else {
                mLeftComplication.setImageDrawable(mDefaultAddComplicationDrawable);
                mLeftComplicationBackground.setVisibility(View.INVISIBLE);
            }
        } else if (watchFaceComplicationId == mRightComplicationId) {
            if (complicationProviderInfo != null) {
                mRightComplication.setImageIcon(complicationProviderInfo.providerIcon);
                mRightComplicationBackground.setVisibility(View.VISIBLE);

            } else {
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
