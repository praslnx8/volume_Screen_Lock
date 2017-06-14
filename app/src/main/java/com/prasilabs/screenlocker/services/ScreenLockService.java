package com.prasilabs.screenlocker.services;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ServiceInfo;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.os.SystemClock;
import android.provider.Settings;

import com.prasilabs.screenlocker.R;
import com.prasilabs.screenlocker.constants.KeyConstant;
import com.prasilabs.screenlocker.utils.FPowerManager;
import com.prasilabs.screenlocker.utils.MyLogger;
import com.prasilabs.screenlocker.utils.PhoneData;
import com.prasilabs.screenlocker.utils.ShakeSensorUtil;
import com.prasilabs.screenlocker.utils.WindowManagerUtil;

public class ScreenLockService extends Service implements SensorEventListener, FSensorTracker.FSensorCallBack {
    private static final String TAG = ScreenLockService.class.getSimpleName();

    private static ScreenLockService screenLockService;
    private MediaPlayer mediaPlayer;
    private SensorManager sensorManager;

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
            if(sensorManager == null)
            {
                sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
            }
            Sensor accelSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            sensorManager.registerListener(this, accelSensor, SensorManager.SENSOR_DELAY_NORMAL);
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

            if(!mediaPlayer.isPlaying())
            {
                mediaPlayer.start();
            }
        }
        else
        {
            stopMediaPlay();
        }

        if(PhoneData.getPhoneData(this, KeyConstant.FLOATING_LOCK_STR, false))
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if(Settings.canDrawOverlays(this)) {
                    WindowManagerUtil.showFloatingButton(this);
                }
            }else {
                WindowManagerUtil.showFloatingButton(this);
            }
        }
        else
        {
            WindowManagerUtil.removeFloatingButton();
        }

        FSensorTracker.getInstance(this).register(this);

        return Service.START_STICKY;
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

        WindowManagerUtil.removeFloatingButton();

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
                sensorManager.unregisterListener(this);
            }
            else
            {
                sensorManager.unregisterListener(this);
            }
        }
    }

    public static void manageService(Context context)
    {
        if (PhoneData.getPhoneData(context, KeyConstant.UNLOCK_STR, false) || (PhoneData.getPhoneData(context, KeyConstant.SHAKE_LOCK_STR, false)) || (PhoneData.getPhoneData(context, KeyConstant.FLOATING_LOCK_STR, false)))
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
        ShakeSensorUtil.handleShake(this, event);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy)
    {

    }

    @Override
    public void onTaskRemoved(Intent rootIntent)
    {
        MyLogger.l(TAG, ServiceInfo.FLAG_STOP_WITH_TASK + "");
        Intent restartServiceIntent = new Intent(getApplicationContext(),
                this.getClass());
        restartServiceIntent.setPackage(getPackageName());

        PendingIntent restartServicePendingIntent = PendingIntent.getService(
                getApplicationContext(), 1, restartServiceIntent,
                PendingIntent.FLAG_ONE_SHOT);
        AlarmManager alarmService = (AlarmManager) getApplicationContext()
                .getSystemService(Context.ALARM_SERVICE);
        alarmService.set(AlarmManager.ELAPSED_REALTIME,
                SystemClock.elapsedRealtime() + 1000,
                restartServicePendingIntent);

        super.onTaskRemoved(rootIntent);
    }

    @Override
    public void disturbed() {
        if(!FPowerManager.instance(this).isScreenOn()) {
            FPowerManager.instance(this).wake();
        }
    }
}
