package com.prasilabs.screenlocker.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.PowerManager;
import android.widget.Toast;

import com.prasilabs.screenlocker.constants.KeyConstant;
import com.prasilabs.screenlocker.constants.IntentConstant;
import com.prasilabs.screenlocker.constants.RequestFor;
import com.prasilabs.screenlocker.notifications.ScreenLockNotification;
import com.prasilabs.screenlocker.utils.MyLogger;
import com.prasilabs.screenlocker.utils.PhoneData;
import com.prasilabs.screenlocker.utils.VUtil;
import com.prasilabs.screenlocker.view.MainActivity;

public class MediaButtonIntentReciever extends BroadcastReceiver
{
    private static final String TAG = MediaButtonIntentReciever.class.getSimpleName();

    private static long prevTime;

    private static PowerManager pm;

    public MediaButtonIntentReciever()
    {

    }

    @Override
    public void onReceive(Context context, Intent intent)
    {
        if(intent != null)
        {
            if(intent.getAction().equals("android.media.VOLUME_CHANGED_ACTION"))
            {
                MyLogger.l(TAG, "media button action recieved");
                if(intent.getExtras() != null)
                {
                    int prevVolume = intent.getExtras().getInt("android.media.EXTRA_PREV_VOLUME_STREAM_VALUE", 0);
                    int currentValue = intent.getExtras().getInt("android.media.EXTRA_VOLUME_STREAM_VALUE", 0);

                    if(currentValue != 0 && prevVolume != 0 && currentValue != prevVolume)
                    {
                        boolean isEnabled = PhoneData.getPhoneData(context, KeyConstant.UNLOCK_STR, false);


                        if (pm == null)
                        {
                            pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);

                            MyLogger.l(TAG, "power manager is initialized");
                        }


                        if (!isScreenOn(pm) && isEnabled)
                        {

                            PowerManager.WakeLock wakeLock = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, KeyConstant.WAKE_TAG_STR);
                            wakeLock.acquire();
                            wakeLock.release();

                            MyLogger.l(TAG, "wake lock done");
                        }
                        else if (isScreenOn(pm) && PhoneData.getPhoneData(context, KeyConstant.VOLUME_LOCK_ENABLE_STR, false) && isEnabled)
                        {
                            if (System.currentTimeMillis() - prevTime < 1000)
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

                }
            }
            else if(intent.getAction().equals(IntentConstant.LOCK_SCREEN_ACTION_INTENT))
            {
                if(VUtil.checkisDeviceAdminEnabled()) //Dont check for isEnabled
                {
                    lockScreenNow(context);
                }
                else
                {
                    Intent activityIntent = new Intent(context, MainActivity.class);
                    activityIntent.putExtra(KeyConstant.REQUEST_FOR_STR, RequestFor.ACTIVATE_DEVICE_ADMIN);
                    activityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(activityIntent);
                }
            }
            else if(intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED))
            {
                MyLogger.lw(TAG, "boot action recieved");

                ScreenLockNotification.manageNotification(context);

                ScreenLockService.manageService(context);
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

    private boolean isScreenOn(PowerManager pm)
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
