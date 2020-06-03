package com.layoutxml.twelveish;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

import lombok.Getter;

public class Communicator {
    private static final String TAG = "Communicator";
    @Getter
    private final String path = "/twelveish";
    private final String DATA_KEY = "rokas-twelveish";
    private final String HANDSHAKE_KEY = "rokas-twelveish-hs";
    private final String GOODBYE_KEY = "rokas-twelveish-gb";
    private final String DATA_REQUEST_KEY = "rokas-twelveish-dr";
    private final String DATA_REQUEST_KEY2 = "rokas-twelveish-dr2";
    private final String CONFIG_REQUEST_KEY = "rokas-twelveish-cr";
    private final String CONFIG_REQUEST_KEY2 = "rokas-twelveish-cr2";
    private final String PREFERENCES_KEY = "rokas-twelveish-pr";
    private final String TIMESTAMP_KEY = "Timestamp";

    private final Context context;
    private final PreferenceManager preferenceManager;

    Communicator(Context context, PreferenceManager preferenceManager) {
        this.context = context;
        this.preferenceManager = preferenceManager;
    }

    public void processData(DataItem dataItem) {
        DataMapItem mDataMapItem = DataMapItem.fromDataItem(dataItem);

        String[] array = mDataMapItem.getDataMap().getStringArray(DATA_KEY);
        if (array != null && array.length == 3) {
            savePreference(array);
        }

        boolean handshake = mDataMapItem.getDataMap().getBoolean(HANDSHAKE_KEY);
        if (!handshake) {
            performHandshake();
        }

        boolean preferences = mDataMapItem.getDataMap().getBoolean(DATA_REQUEST_KEY);
        if (preferences) {
            sendCurrentPreferences();
        }

        boolean config = mDataMapItem.getDataMap().getBoolean(CONFIG_REQUEST_KEY);
        if (config) {
            sendConfigurationData();
        }
    }

    public void performHandshake() {
        final PutDataMapRequest mPutDataMapRequest = PutDataMapRequest.create(path);
        mPutDataMapRequest.getDataMap().putLong(TIMESTAMP_KEY, System.currentTimeMillis());
        mPutDataMapRequest.getDataMap().putBoolean(HANDSHAKE_KEY, true);
        mPutDataMapRequest.setUrgent();
        PutDataRequest mPutDataRequest = mPutDataMapRequest.asPutDataRequest();
        Wearable.getDataClient(context).putDataItem(mPutDataRequest);
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mPutDataMapRequest.getDataMap().clear();
            }
        }, 5000);
    }

    public void disconnect() {
        final PutDataMapRequest mPutDataMapRequest = PutDataMapRequest.create(path);
        mPutDataMapRequest.getDataMap().putBoolean(GOODBYE_KEY, true);
        mPutDataMapRequest.setUrgent();
        PutDataRequest mPutDataRequest = mPutDataMapRequest.asPutDataRequest();
        Wearable.getDataClient(context).putDataItem(mPutDataRequest);
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mPutDataMapRequest.getDataMap().clear();
            }
        }, 5000);
    }

    private void savePreference(String[] preferenceArray) {
        // TODO: create a listener in WatchFace class to force refresh
        log("savePreference");
        switch (preferenceArray[2]) {
            case "String":
                preferenceManager.saveString(preferenceArray[0], preferenceArray[1]);
                break;
            case "Integer":
                try {
                    int value = Integer.parseInt(preferenceArray[1]);
                    preferenceManager.saveInt(preferenceArray[0], value);
                } catch (NumberFormatException e) {
                    log("Preference error");
                }
                break;
            case "Boolean":
                boolean value = Boolean.parseBoolean(preferenceArray[1]);
                preferenceManager.saveBoolean(preferenceArray[0], value);
            default:
                log("Unknown type in processData");
                break;
        }
    }

    private void sendCurrentPreferences() {
        final PutDataMapRequest mPutDataMapRequest = PutDataMapRequest.create(path);
        mPutDataMapRequest.getDataMap().putLong(TIMESTAMP_KEY, System.currentTimeMillis());
        mPutDataMapRequest.getDataMap().putStringArrayList(PREFERENCES_KEY, preferenceManager.getPreferencesList());
        mPutDataMapRequest.getDataMap().putBoolean(DATA_REQUEST_KEY, false);
        mPutDataMapRequest.getDataMap().putBoolean(DATA_REQUEST_KEY2, true);
        mPutDataMapRequest.setUrgent();
        PutDataRequest mPutDataRequest = mPutDataMapRequest.asPutDataRequest();
        Wearable.getDataClient(context).putDataItem(mPutDataRequest);
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mPutDataMapRequest.getDataMap().clear();
            }
        }, 5000);
    }

    private void sendConfigurationData() {
        String[] configToSend = new String[3];
        configToSend[0] = (int) 0 + ""; // TODO: set chin size
        configToSend[1] = Boolean.toString(preferenceManager.isComplicationLeftSet());
        configToSend[2] = Boolean.toString(preferenceManager.isComplicationRightSet());

        final PutDataMapRequest mPutDataMapRequest = PutDataMapRequest.create(path);
        mPutDataMapRequest.getDataMap().putLong(TIMESTAMP_KEY, System.currentTimeMillis());
        mPutDataMapRequest.getDataMap().putStringArray(PREFERENCES_KEY, configToSend);
        mPutDataMapRequest.getDataMap().putBoolean(CONFIG_REQUEST_KEY, false);
        mPutDataMapRequest.getDataMap().putBoolean(CONFIG_REQUEST_KEY2, true);
        mPutDataMapRequest.setUrgent();
        PutDataRequest mPutDataRequest = mPutDataMapRequest.asPutDataRequest();
        Wearable.getDataClient(context).putDataItem(mPutDataRequest);
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mPutDataMapRequest.getDataMap().clear();
            }
        }, 5000);
    }

    private void log(String message) {
        Log.d(TAG, message);
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
}
