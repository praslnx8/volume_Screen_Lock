package com.prasilabs.screenlocker.services;

import android.app.Service;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.IBinder;

import com.prasilabs.screenlocker.R;
import com.prasilabs.screenlocker.constants.KeyConstant;
import com.prasilabs.screenlocker.utils.MyLogger;
import com.prasilabs.screenlocker.utils.PhoneData;

public class ScreenLockService extends Service
{
    private static final String TAG = ScreenLockService.class.getSimpleName();

    private static ScreenLockService screenLockService;
    private MediaPlayer mediaPlayer;

    public ScreenLockService()
    {
        screenLockService = this;
    }


    @Override
    public void onCreate()
    {
        super.onCreate();

        if(mediaPlayer == null)
        {
            mediaPlayer = MediaPlayer.create(this, R.raw.sound);
            mediaPlayer.setVolume(0, 0);
            mediaPlayer.setLooping(true);
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        }

        mediaPlayer.start();
    }


    @Override
    public IBinder onBind(Intent intent)
    {
        throw new UnsupportedOperationException("Not yet implemented");
    }


    @Override
    public void onDestroy()
    {
        if(mediaPlayer != null)
        {
            mediaPlayer.stop();
        }

        super.onDestroy();
    }

    public static void manageService(Context context)
    {
        if(PhoneData.getPhoneData(context, KeyConstant.UNLOCK_STR, false))
        {
            Intent intent = new Intent(context, ScreenLockService.class);
            context.startService(intent);
            MyLogger.lw(TAG, "Service started");
        }
        else
        {
            stopService();
        }
    }

    private static void stopService()
    {
        try
        {   //safety
            if (screenLockService != null)
            {
                screenLockService.stopSelf();
            }
        }catch (Exception e)
        {
            MyLogger.e(e);
        }
    }

}
