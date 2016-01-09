package com.prasilabs.screenlocker.services;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.util.Log;
import android.widget.Toast;

import com.prasilabs.screenlocker.VApp;
import com.prasilabs.screenlocker.constants.Constant;
import com.prasilabs.screenlocker.utils.MyLogger;
import com.prasilabs.screenlocker.utils.PhoneData;
import com.prasilabs.screenlocker.utils.VUtil;

import java.util.logging.Logger;

public class MediaButtonIntentReciever extends BroadcastReceiver
{
    private static final String TAG = MediaButtonIntentReciever.class.getSimpleName();

    private static boolean isScreenOn = false;
    private static long prevTime;

    private static PowerManager pm;

    public MediaButtonIntentReciever()
    {

    }

    @Override
    public void onReceive(Context context, Intent intent)
    {
        MyLogger.l(TAG, "Intent recieved");

        if(intent != null)
        {
            MyLogger.l(TAG, "Intent recieved is: " + intent.getAction());

            if(intent.getAction().equals(Intent.ACTION_SCREEN_ON))
            {
                MyLogger.l(TAG, "screen on action recieved");

                isScreenOn = true;
            }
            else if(intent.getAction().equals(Intent.ACTION_SCREEN_OFF))
            {
                MyLogger.l(TAG, "screen off action recieved");

                isScreenOn = false;
            }
            else if(intent.getAction().equals("android.media.VOLUME_CHANGED_ACTION"))
            {
                MyLogger.l(TAG, "media button action recieved");

                boolean isEnabled = PhoneData.getPhoneData(context, Constant.UNLOCK_STR, false);

                if(!isScreenOn && isEnabled)
                {
                    if(pm == null)
                    {
                        pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);

                        MyLogger.l(TAG, "power manager is initialized");
                    }
                    PowerManager.WakeLock wakeLock = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, Constant.WAKE_TAG);
                    wakeLock.acquire();
                    wakeLock.release();

                    MyLogger.l(TAG, "wake lock done");
                }
                else if(PhoneData.getPhoneData(context, Constant.VOLUME_LOCK_ENABLE_STR, false))
                {

                    if(System.currentTimeMillis() - prevTime < 1000)
                    {
                        lockScreenNow(context);
                    }
                    else
                    {
                        MyLogger.l(TAG, "not a rapic action");
                    }

                    prevTime = System.currentTimeMillis();
                }
            }
            else if(intent.getAction().equals(Constant.LOCK_SCREEN_ACTION_INTENT))
            {
                lockScreenNow(context);
            }
            else if(intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED))
            {
                MyLogger.lw(TAG, "boot action recieved");
                if(PhoneData.getPhoneData(context, Constant.UNLOCK_STR, false))
                {
                    ScreenLockService.startService(context);
                }
            }
            else
            {
                MyLogger.l(TAG, "uncached intent arrived");
            }
        }
        else
        {
            MyLogger.l(TAG, "null Intent recieved");
        }
    }

    private void lockScreenNow(Context context)
    {
        if(VUtil.checkisDeviceAdminEnabled())
        {
            VUtil.lockDevice();
        }
        else
        {
            Toast.makeText(context, "Please Enable Device admin in app", Toast.LENGTH_LONG).show();
        }
    }


}
