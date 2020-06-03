package com.layoutxml.twelveish;

import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Build;
import android.support.wearable.complications.ComplicationData;
import android.support.wearable.complications.ComplicationHelperActivity;
import android.support.wearable.complications.rendering.ComplicationDrawable;
import android.util.SparseArray;

import com.layoutxml.twelveish.activities.ComplicationConfigActivity;

public class ComplicationManager {
    private static final int BOTTOM_COMPLICATION_ID = 0;
    private static final int LEFT_COMPLICATION_ID = 1;
    private static final int RIGHT_COMPLICATION_ID = 2;
    private static final int[] COMPLICATION_IDS = {BOTTOM_COMPLICATION_ID, LEFT_COMPLICATION_ID, RIGHT_COMPLICATION_ID};
    private static final int[][] COMPLICATION_SUPPORTED_TYPES = {
            {
                    ComplicationData.TYPE_RANGED_VALUE,
                    ComplicationData.TYPE_ICON,
                    ComplicationData.TYPE_LONG_TEXT,
                    ComplicationData.TYPE_SHORT_TEXT,
                    ComplicationData.TYPE_SMALL_IMAGE,
                    ComplicationData.TYPE_LARGE_IMAGE
            },
            {
                    ComplicationData.TYPE_RANGED_VALUE,
                    ComplicationData.TYPE_ICON,
                    ComplicationData.TYPE_SHORT_TEXT,
                    ComplicationData.TYPE_SMALL_IMAGE
            },
            {
                    ComplicationData.TYPE_RANGED_VALUE,
                    ComplicationData.TYPE_ICON,
                    ComplicationData.TYPE_SHORT_TEXT,
                    ComplicationData.TYPE_SMALL_IMAGE
            }
    };

    private final Context context;
    private final PreferenceManager preferenceManager;

    private SparseArray<ComplicationData> mActiveComplicationDataSparseArray;
    private SparseArray<ComplicationDrawable> mComplicationDrawableSparseArray;

    ComplicationManager(Context context, PreferenceManager preferenceManager) {
        this.context = context;
        this.preferenceManager = preferenceManager;
    }

    public static int getComplicationId(ComplicationConfigActivity.ComplicationLocation complicationLocation) {
        switch (complicationLocation) {
            case BOTTOM:
                return BOTTOM_COMPLICATION_ID;
            case LEFT:
                return LEFT_COMPLICATION_ID;
            case RIGHT:
                return RIGHT_COMPLICATION_ID;
            default:
                return -1;
        }
    }

    public static int[] getComplicationIds() {
        return COMPLICATION_IDS;
    }

    public static int[] getSupportedComplicationTypes(ComplicationConfigActivity.ComplicationLocation complicationLocation) {
        switch (complicationLocation) {
            case BOTTOM:
                return COMPLICATION_SUPPORTED_TYPES[0];
            case LEFT:
                return COMPLICATION_SUPPORTED_TYPES[1];
            case RIGHT:
                return COMPLICATION_SUPPORTED_TYPES[2];
            default:
                return new int[]{};
        }
    }

    public void initializeComplications() {
        if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            return;
        }

        mActiveComplicationDataSparseArray = new SparseArray<>(COMPLICATION_IDS.length);
        ComplicationDrawable bottomComplicationDrawable = (ComplicationDrawable) context.getDrawable(R.drawable.custom_complication_styles);
        ComplicationDrawable leftComplicationDrawable = (ComplicationDrawable) context.getDrawable(R.drawable.custom_complication_styles);
        ComplicationDrawable rightComplicationDrawable = (ComplicationDrawable) context.getDrawable(R.drawable.custom_complication_styles);
        assert bottomComplicationDrawable != null;
        bottomComplicationDrawable.setContext(context);
        assert leftComplicationDrawable != null;
        leftComplicationDrawable.setContext(context);
        assert rightComplicationDrawable != null;
        rightComplicationDrawable.setContext(context);
        mComplicationDrawableSparseArray = new SparseArray<>(COMPLICATION_IDS.length);
        mComplicationDrawableSparseArray.put(BOTTOM_COMPLICATION_ID, bottomComplicationDrawable);
        mComplicationDrawableSparseArray.put(LEFT_COMPLICATION_ID, leftComplicationDrawable);
        mComplicationDrawableSparseArray.put(RIGHT_COMPLICATION_ID, rightComplicationDrawable);
    }

    public void drawComplications(Canvas canvas, long currentTimeMillis) {
        if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            return;
        }

        int complicationId;
        ComplicationDrawable complicationDrawable;
        for (int COMPLICATION_ID : COMPLICATION_IDS) {
            complicationId = COMPLICATION_ID;
            complicationDrawable = mComplicationDrawableSparseArray.get(complicationId);
            complicationDrawable.setBackgroundColorActive(preferenceManager.getBackgroundColor());
            complicationDrawable.setHighlightColorActive(preferenceManager.getSecondaryColor());
            complicationDrawable.setHighlightColorAmbient(preferenceManager.getSecondaryColorAmbient());
            complicationDrawable.setIconColorActive(preferenceManager.getSecondaryColor());
            complicationDrawable.setIconColorAmbient(preferenceManager.getSecondaryColorAmbient());
            complicationDrawable.setTextColorActive(preferenceManager.getSecondaryColor());
            complicationDrawable.setTextColorAmbient(preferenceManager.getSecondaryColorAmbient());
            complicationDrawable.setRangedValuePrimaryColorActive(preferenceManager.getSecondaryColor());
            complicationDrawable.setRangedValuePrimaryColorAmbient(preferenceManager.getSecondaryColorAmbient());
            complicationDrawable.setRangedValueSecondaryColorActive(preferenceManager.getSecondaryColor());
            complicationDrawable.setRangedValueSecondaryColorAmbient(preferenceManager.getSecondaryColorAmbient());
            complicationDrawable.setRangedValueRingWidthActive(preferenceManager.getSecondaryColor());
            complicationDrawable.setRangedValueRingWidthAmbient(preferenceManager.getSecondaryColorAmbient());
            complicationDrawable.setTitleColorActive(preferenceManager.getSecondaryColor());
            complicationDrawable.setTitleColorAmbient(preferenceManager.getSecondaryColorAmbient());
            complicationDrawable.draw(canvas, currentTimeMillis);
        }
    }

    public void handleTap(int x, int y) {
        if (preferenceManager.isDisableComplicationTap() || android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            return;
        }

        int tappedComplicationId = getTappedComplicationId(x, y);
        if (tappedComplicationId != -1) {
            onComplicationTap(tappedComplicationId);
        }
    }

    public void changeAmbientMode(boolean isAmbient) {
        if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            return;
        }

        ComplicationDrawable complicationDrawable;
        for (int COMPLICATION_ID : COMPLICATION_IDS) {
            complicationDrawable = mComplicationDrawableSparseArray.get(COMPLICATION_ID);
            complicationDrawable.setInAmbientMode(isAmbient);
        }
    }

    public void setBounds(int width, int height, float chinSize) {
        if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            return;
        }

        Rect bottomBounds = new Rect(width / 2 - width / 4,
                (int) (height * 3 / 4 - height),
                width / 2 + width / 4,
                (int) (height - chinSize));
        Rect leftBounds = new Rect(0,
                height * 3 / 8,
                width / 4,
                height * 5 / 8);
        Rect rightBounds = new Rect(width * 3 / 4,
                height * 3 / 8,
                width,
                height * 5 / 8);
        ComplicationDrawable bottomComplicationDrawable = mComplicationDrawableSparseArray.get(BOTTOM_COMPLICATION_ID);
        bottomComplicationDrawable.setBounds(bottomBounds);
        ComplicationDrawable leftComplicationDrawable = mComplicationDrawableSparseArray.get(LEFT_COMPLICATION_ID);
        leftComplicationDrawable.setBounds(leftBounds);
        ComplicationDrawable rightComplicationDrawable = mComplicationDrawableSparseArray.get(RIGHT_COMPLICATION_ID);
        rightComplicationDrawable.setBounds(rightBounds);
    }

    public void updateData(int id, ComplicationData data) {
        mActiveComplicationDataSparseArray.put(id, data);
        ComplicationDrawable complicationDrawable = mComplicationDrawableSparseArray.get(id);
        complicationDrawable.setComplicationData(data);
    }

    private int getTappedComplicationId(int x, int y) {
        int complicationId;
        ComplicationData complicationData;
        ComplicationDrawable complicationDrawable;
        long currentTimeMillis = System.currentTimeMillis();
        for (int COMPLICATION_ID : COMPLICATION_IDS) {
            complicationId = COMPLICATION_ID;
            complicationData = mActiveComplicationDataSparseArray.get(complicationId);
            if ((complicationData != null)
                    && (complicationData.isActive(currentTimeMillis))
                    && (complicationData.getType() != ComplicationData.TYPE_NOT_CONFIGURED)
                    && (complicationData.getType() != ComplicationData.TYPE_EMPTY)) {
                complicationDrawable = mComplicationDrawableSparseArray.get(complicationId);
                Rect complicationBoundingRect = complicationDrawable.getBounds();
                if (complicationBoundingRect.width() > 0) {
                    if (complicationBoundingRect.contains(x, y)) {
                        return complicationId;
                    }
                }
            }
        }
        return -1;
    }

    private void onComplicationTap(int complicationId) {
        ComplicationData complicationData = mActiveComplicationDataSparseArray.get(complicationId);
        if (complicationData != null) {
            if (complicationData.getTapAction() != null) {
                try {
                    complicationData.getTapAction().send();
                } catch (PendingIntent.CanceledException ignored) {
                }
            } else {
                if (complicationData.getType() == ComplicationData.TYPE_NO_PERMISSION) {
                    ComponentName componentName =
                            new ComponentName(
                                    context, WatchFace.class);
                    Intent permissionRequestIntent =
                            ComplicationHelperActivity.createPermissionRequestHelperIntent(
                                    context, componentName);
                    context.startActivity(permissionRequestIntent);
                }
            }
        }
    }
}
