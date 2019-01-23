package com.layoutxml.twelveish;

import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.widget.Toast;

import com.google.android.gms.wearable.DataClient;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

import androidx.annotation.NonNull;

import static com.layoutxml.twelveish.MainActivity.isWatchConnected;

public class Communicator implements DataClient.OnDataChangedListener {

    private final String path = "/twelveish";
    private final String DATA_KEY = "rokas-twelveish";
    private final String HANDSHAKE_KEY = "rokas-twelveish-hs";
    private final String GOODBYE_KEY = "rokas-twelveish-gb";
    private PutDataMapRequest mPutDataMapRequest;
    private Context applicationContext;
    private boolean currentStatus = true; //temporary value for waiting period if watch not found to not create false negatives

    public Communicator(Context context) {
        mPutDataMapRequest = PutDataMapRequest.create(path);
        applicationContext = context;
    }

    public void initiateHandshake(Context context) {
        setCurrentStatus(false);

        mPutDataMapRequest.getDataMap().putLong("Timestamp", System.currentTimeMillis());
        mPutDataMapRequest.getDataMap().putBoolean(HANDSHAKE_KEY, false);
        mPutDataMapRequest.setUrgent();
        PutDataRequest mPutDataRequest = mPutDataMapRequest.asPutDataRequest();
        Wearable.getDataClient(context).putDataItem(mPutDataRequest);
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
                                Toast.makeText(applicationContext, "Watch disconnected (b)", Toast.LENGTH_SHORT).show();
                            }
                            isWatchConnected = false;
                        } else {
                            if (!isWatchConnected) {
                            Toast.makeText(applicationContext, "Watch connected (c)", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }, 3000);
            }
            //else - timer already running, do nothing
        } else {
            currentStatus = true;
            if (!isWatchConnected) {
            Toast.makeText(applicationContext, "Watch connected (d)", Toast.LENGTH_SHORT).show();
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
    }

    @Override
    public void onDataChanged(@NonNull DataEventBuffer dataEventBuffer) {
        for (DataEvent event: dataEventBuffer) {
            if (event.getType()==DataEvent.TYPE_CHANGED && event.getDataItem().getUri().getPath()!=null && event.getDataItem().getUri().getPath().equals(path)) {
                DataMapItem mDataMapItem = DataMapItem.fromDataItem(event.getDataItem());
                boolean handshake = mDataMapItem.getDataMap().getBoolean(HANDSHAKE_KEY);
                boolean goodbye = mDataMapItem.getDataMap().getBoolean(GOODBYE_KEY);
                if (handshake) {
                    setCurrentStatus(true);
                    if (!isWatchConnected) {
                        Toast.makeText(applicationContext, "Watch connected (a)", Toast.LENGTH_SHORT).show();
                    }
                    isWatchConnected = true;
                    Uri mUri =  new Uri.Builder()
                            .scheme(PutDataRequest.WEAR_URI_SCHEME)
                            .path(path)
                            .authority(HANDSHAKE_KEY)
                            .build();
                    Wearable.getDataClient(applicationContext).deleteDataItems(mUri);
                }
                if (isWatchConnected && goodbye) {
                    Toast.makeText(applicationContext, "Watch disconnected (a)", Toast.LENGTH_SHORT).show();
                    isWatchConnected=false;
                    initiateHandshake(applicationContext);

                    Uri mUri =  new Uri.Builder()
                            .scheme(PutDataRequest.WEAR_URI_SCHEME)
                            .path(path)
                            .authority(GOODBYE_KEY)
                            .build();
                    Wearable.getDataClient(applicationContext).deleteDataItems(mUri);
                }
            }
        }
    }

    public void destroy() {
        applicationContext = null;
    }
}
