package com.prasilabs.screenlocker.services;

import android.app.Service;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.IBinder;

import com.prasilabs.screenlocker.R;

public class ScreenLockService extends Service
{
    private static final String TAG = ScreenLockService.class.getSimpleName();

    private static ScreenLockService screenLockService;
    private MediaPlayer mediaPlayer;
    private MediaButtonIntentReciever mediaButtonIntentReciever;

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
        }

        mediaPlayer.start();

        if(mediaButtonIntentReciever == null)
        {
            mediaButtonIntentReciever = new MediaButtonIntentReciever();
        }

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_SCREEN_OFF);
        intentFilter.addAction(Intent.ACTION_SCREEN_ON);
        registerReceiver(mediaButtonIntentReciever, intentFilter);
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

        if(mediaButtonIntentReciever != null)
        {
            unregisterReceiver(mediaButtonIntentReciever);
        }

        super.onDestroy();
    }

    public static void startService(Context context)
    {
        Intent intent = new Intent(context, ScreenLockService.class);
        context.startService(intent);
    }

    public static void stopService()
    {
        if(screenLockService != null)
        {
            screenLockService.stopSelf();
        }
    }

}
