package com.layoutxml.twelveish;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.wearable.DataClient;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;
import com.layoutxml.twelveish.objects.WatchPreviewView;

import androidx.annotation.NonNull;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class Communicator implements DataClient.OnDataChangedListener {

    private final String path = "/twelveish";
    private final String DATA_KEY = "rokas-twelveish";
    private final String HANDSHAKE_KEY = "rokas-twelveish-hs";
    private final String GOODBYE_KEY = "rokas-twelveish-gb";
    private final String DATA_REQUEST_KEY = "rokas-twelveish-dr";
    private final String DATA_REQUEST_KEY2 = "rokas-twelveish-dr2";
    private final String PREFERENCES_KEY = "rokas-twelveish-pr";
    private final String CONFIG_REQUEST_KEY = "rokas-twelveish-cr";
    private final String CONFIG_REQUEST_KEY2 = "rokas-twelveish-cr2";
    private PutDataMapRequest mPutDataMapRequest;
    private Context applicationContext;
    private boolean currentStatus = true; //temporary value for waiting period if watch not found to not create false negatives
    public String[] booleanPreferences;
    public boolean isWatchConnected = false;
    private static final String TAG = "Communicator";
    private WeakReference<WatchPreviewView> previewListener;

    @Inject
    public Communicator(Context context) {
        mPutDataMapRequest = PutDataMapRequest.create(path);
        applicationContext = context;
        //initiateHandshake();
    }

    public void initiateHandshake() {
        Log.d(TAG, "initiateHandshake");
        setCurrentStatus(false);

        mPutDataMapRequest.getDataMap().putLong("Timestamp", System.currentTimeMillis());
        mPutDataMapRequest.getDataMap().putBoolean(HANDSHAKE_KEY, true);
        mPutDataMapRequest.setUrgent();
        PutDataRequest mPutDataRequest = mPutDataMapRequest.asPutDataRequest();
        Wearable.getDataClient(applicationContext).putDataItem(mPutDataRequest);
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mPutDataMapRequest.getDataMap().clear();
            }
        }, 5000);
    }

    private void setCurrentStatus(boolean value) {
        if (!value) {
            //There is a need to start a timer
            if (currentStatus) {
                //Start timer
                currentStatus = false;

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        if (!currentStatus) {
                            if (isWatchConnected) {
                                Toast.makeText(applicationContext, "Watch disconnected", Toast.LENGTH_SHORT).show();
                            }
                            isWatchConnected = false;
                        } else {
                            if (!isWatchConnected) {
                            Toast.makeText(applicationContext, "Watch connected", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }, 3000);
            }
            //else - timer already running, do nothing
        } else {
            currentStatus = true;
            if (!isWatchConnected) {
            Toast.makeText(applicationContext, "Watch connected", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void sendPreference(String key, String value, String type, Context context) {
        String[] array = new String[3];
        array[0] = key;
        array[1] = value;
        array[2] = type;
        mPutDataMapRequest.getDataMap().putLong("Timestamp", System.currentTimeMillis());
        mPutDataMapRequest.getDataMap().putStringArray(DATA_KEY, array);
        mPutDataMapRequest.setUrgent();
        PutDataRequest mPutDataRequest = mPutDataMapRequest.asPutDataRequest();
        Wearable.getDataClient(context).putDataItem(mPutDataRequest);
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mPutDataMapRequest.getDataMap().clear();
            }
        }, 5000); //deleting as described in google's documentation does not actually work, so I have to resolve to clearing with delay
    }

    public void sendWatchFace(SettingsManager settingsManager, Context context){
        String[] preferencesToSend = new String[(settingsManager.booleanHashmap.size() + settingsManager.integerHashmap.size() + settingsManager.stringHashmap.size())*3];

        int index = 0;
        for(Map.Entry<String, String> preference : settingsManager.stringHashmap.entrySet()){
            String key = preference.getKey();
            String value = preference.getValue();
            String type = "String";

            preferencesToSend[index] = key;
            preferencesToSend[index + 1] = value;
            preferencesToSend[index + 2] = type;
            index += 3;
        }

        for(Map.Entry<String, Integer> preference : settingsManager.integerHashmap.entrySet()){
            String key = preference.getKey();
            int value = preference.getValue();
            String type = "Integer";

            preferencesToSend[index] = key;
            preferencesToSend[index + 1] = String.valueOf(value);
            preferencesToSend[index + 2] = type;
            index += 3;
        }

        for(Map.Entry<String, Boolean> preference : settingsManager.booleanHashmap.entrySet()){
            String key = preference.getKey();
            boolean value = preference.getValue();
            String type = "Boolean";

            preferencesToSend[index] = key;
            preferencesToSend[index + 1] = value ? "true" : "false";
            preferencesToSend[index + 2] = type;
            index += 3;
        }


        if(preferencesToSend != null){
            Log.d(TAG, "Preferences found");
        }

        mPutDataMapRequest.getDataMap().putLong("Timestamp", System.currentTimeMillis());
        mPutDataMapRequest.getDataMap().putStringArray(DATA_KEY, preferencesToSend);
        mPutDataMapRequest.setUrgent();
        PutDataRequest mPutDataRequest = mPutDataMapRequest.asPutDataRequest();
        Wearable.getDataClient(context).putDataItem(mPutDataRequest);
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mPutDataMapRequest.getDataMap().clear();
            }
        }, 5000); //deleting as described in google's documentation does not actually work, so I have to resolve to clearing with delay

    }

    /* public void requestBooleanPreferences(Context context, WeakReference<HomeScreen> listenerActivity) {
        mPutDataMapRequest.getDataMap().putLong("Timestamp", System.currentTimeMillis());
        mPutDataMapRequest.getDataMap().putBoolean(DATA_REQUEST_KEY, true);
        mPutDataMapRequest.setUrgent();
        PutDataRequest mPutDataRequest = mPutDataMapRequest.asPutDataRequest();
        Wearable.getDataClient(context).putDataItem(mPutDataRequest);
        customizationListener = listenerActivity;
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mPutDataMapRequest.getDataMap().clear();
            }
          }, 5000);
    }*/

   /* public void requestPreferences(Context context, WeakReference<CustomizationScreen> listenerActivity){
        mPutDataMapRequest.getDataMap().putLong("Timestamp", System.currentTimeMillis());
        mPutDataMapRequest.getDataMap().putBoolean();
    }*/

    public void requestConfig(Context context, WeakReference<WatchPreviewView> listenerActivity) {
        Log.d(TAG, "requestConfig");
        mPutDataMapRequest.getDataMap().putLong("Timestamp", System.currentTimeMillis());
        mPutDataMapRequest.getDataMap().putBoolean(CONFIG_REQUEST_KEY, true);
        mPutDataMapRequest.setUrgent();
        PutDataRequest mPutDataRequest = mPutDataMapRequest.asPutDataRequest();
        Wearable.getDataClient(context).putDataItem(mPutDataRequest);
        previewListener = listenerActivity;
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mPutDataMapRequest.getDataMap().clear();
            }
        }, 5000);
    }

    @Override
    public void onDataChanged(@NonNull DataEventBuffer dataEventBuffer) {
        for (DataEvent event: dataEventBuffer) {
            if (event.getType()==DataEvent.TYPE_CHANGED && event.getDataItem().getUri().getPath()!=null && event.getDataItem().getUri().getPath().equals(path)) {
                Log.d(TAG, "onDataChanged: received something");
                DataMapItem mDataMapItem = DataMapItem.fromDataItem(event.getDataItem());
                boolean handshake = mDataMapItem.getDataMap().getBoolean(HANDSHAKE_KEY);
                boolean goodbye = mDataMapItem.getDataMap().getBoolean(GOODBYE_KEY);
                boolean config = mDataMapItem.getDataMap().getBoolean(CONFIG_REQUEST_KEY2);
                boolean preferences = mDataMapItem.getDataMap().getBoolean(DATA_REQUEST_KEY2);
                if (handshake) {
                    Log.d(TAG,"handshake received");
                    setCurrentStatus(true);
                    if (!isWatchConnected) {
                        Toast.makeText(applicationContext, "Watch connected", Toast.LENGTH_SHORT).show();
                    }
                    isWatchConnected = true;
                }
                if (isWatchConnected && goodbye) {
                    Toast.makeText(applicationContext, "Watch disconnected", Toast.LENGTH_SHORT).show();
                    isWatchConnected=false;
                    initiateHandshake();
                }

                /* if(preferences){
                    Log.d(TAG, "onDataChanged: preferences");
                    String[] newPreferences = mDataMapItem.getDataMap().getStringArray(PREFERENCES_KEY);
                    HomeScreen customizationScreen = customizationListener.get();
                    SettingsManager settingsManager = ((App) customizationScreen.getApplication()).getSettingsManagerComponent().getSettingsManager();
                    if(newPreferences != null){
                        for(int i = 0; i < newPreferences.length; i+=2){
                            if(settingsManager.stringHashmap.containsKey(newPreferences[i])){
                                settingsManager.stringHashmap.put(newPreferences[i], newPreferences[i+1]);
                            } else if(settingsManager.integerHashmap.containsKey(newPreferences[i])){
                                settingsManager.integerHashmap.put(newPreferences[i], Integer.valueOf(newPreferences[i+1]));
                            } else if(settingsManager.booleanHashmap.containsKey(newPreferences[i])){
                                settingsManager.booleanHashmap.put(newPreferences[i], Boolean.valueOf(newPreferences[i+1]));
                            } else {
                                Log.d(TAG, "Unknown preference key: " + newPreferences[i]);
                            }
                        }

                        settingsManager.significantTimeChange=true;
                        customizationScreen.invalidatePreview();
                    }*
                }*/

                if (config) {
                    Log.d(TAG, "onDataChanged: config");
                    String[] booleanPreferencesTemp = mDataMapItem.getDataMap().getStringArray(PREFERENCES_KEY);
                    if (booleanPreferencesTemp != null) {
                        if (booleanPreferencesTemp.length == 3) {
                            booleanPreferences = booleanPreferencesTemp;
                            if (previewListener != null) {
                                if (previewListener.get() != null) {
                                    Log.d(TAG, "onDataChanged: config activity exists");
                                    WatchPreviewView activity = previewListener.get();
                                    activity.receivedDataListener(booleanPreferencesTemp);
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
