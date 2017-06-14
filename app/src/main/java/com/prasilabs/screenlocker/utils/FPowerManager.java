package com.prasilabs.screenlocker.utils;

import android.content.Context;
import android.os.PowerManager;

import com.prasilabs.screenlocker.constants.KeyConstant;

/**
 * Created by Contus team on 14/6/17.
 */

public class FPowerManager {

    private static final String TAG = FPowerManager.class.getSimpleName();

    private static PowerManager pm;
    private static FPowerManager fPowerManager;

    public static FPowerManager instance(Context context) {
        if(pm == null) {
            pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);

            MyLogger.l(TAG, "power manager is initialized");
        }

        if(fPowerManager == null) {
            fPowerManager = new FPowerManager();
        }

        return fPowerManager;
    }

    public void wake() {
        PowerManager.WakeLock wakeLock = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, KeyConstant.WAKE_TAG_STR);
        wakeLock.acquire();
        wakeLock.release();
    }

    @SuppressWarnings("deprecation")
    public boolean isScreenOn()
    {
        boolean isScreenOn;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT_WATCH)
        {
            isScreenOn = pm.isInteractive();
        }
        else
        {
            isScreenOn = pm.isScreenOn();
        }

        return isScreenOn;
    }
}
