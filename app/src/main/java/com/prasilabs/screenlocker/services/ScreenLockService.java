package com.prasilabs.screenlocker.services;

import android.app.Service;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.IBinder;

import com.prasilabs.screenlocker.R;
import com.prasilabs.screenlocker.constants.KeyConstant;
import com.prasilabs.screenlocker.utils.MyLogger;
import com.prasilabs.screenlocker.utils.PhoneData;
import com.prasilabs.screenlocker.utils.ShakeSensorUtil;

public class ScreenLockService extends Service implements SensorEventListener
{
    private static final String TAG = ScreenLockService.class.getSimpleName();

    private static ScreenLockService screenLockService;
    private MediaPlayer mediaPlayer;
    private SensorManager sensorManager;

    private int shakeThresold;

    public ScreenLockService()
    {
        screenLockService = this;
    }


    @Override
    public void onCreate()
    {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        if(PhoneData.getPhoneData(this, KeyConstant.SHAKE_LOCK_STR, false))
        {
            sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
            Sensor accelSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            sensorManager.registerListener(this, accelSensor, SensorManager.SENSOR_DELAY_NORMAL);
            shakeThresold = PhoneData.getPhoneData(this, KeyConstant.SHAKE_THRESOLD_STR, 1000);
        }
        else
        {
            flushShakeListener();
        }

        if(PhoneData.getPhoneData(this, KeyConstant.UNLOCK_STR, false))
        {
            if (mediaPlayer == null)
            {
                mediaPlayer = MediaPlayer.create(this, R.raw.sound);
                mediaPlayer.setVolume(0, 0);
                mediaPlayer.setLooping(true);
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            }

            mediaPlayer.start();
        }
        else
        {
            stopMediaPlay();
        }

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent)
    {
        throw new UnsupportedOperationException("Not yet implemented");
    }


    @Override
    public void onDestroy()
    {
        stopMediaPlay();

        flushShakeListener();

        super.onDestroy();
    }

    private void stopMediaPlay()
    {
        if (mediaPlayer != null)
        {
            mediaPlayer.stop();
        }
    }

    private void flushShakeListener()
    {
        if(sensorManager != null)
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
            {
                sensorManager.flush(this);
            }
            else
            {
                sensorManager.unregisterListener(this);
            }
        }
    }

    public static void manageService(Context context)
    {
        if (PhoneData.getPhoneData(context, KeyConstant.UNLOCK_STR, false) || (PhoneData.getPhoneData(context, KeyConstant.SHAKE_LOCK_STR, false)))
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

    private static void stopService() {
        try
        {   //safety
            if (screenLockService != null)
            {
                screenLockService.stopSelf();
            }
        } catch (Exception e) {
            MyLogger.e(e);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event)
    {
        ShakeSensorUtil.handleShake(event, shakeThresold);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy)
    {

    }
}
