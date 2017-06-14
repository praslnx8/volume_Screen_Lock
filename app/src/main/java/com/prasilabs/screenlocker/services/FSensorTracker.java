package com.prasilabs.screenlocker.services;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;

import com.prasilabs.screenlocker.utils.MyLogger;

/**
 * Created by prasi on 14/12/16.
 */

public class FSensorTracker implements SensorEventListener
{
    private static final String TAG = FSensorTracker.class.getSimpleName();
    private static FSensorTracker instance;
    private float prevAzimuth;
    private SensorManager mSensorManager;
    private FSensorCallBack fSensorCallBack;
    private float[] mGravity;
    private float[] mPrevGravity;

    private FSensorTracker() {
    }

    public static FSensorTracker getInstance(FSensorCallBack fSensorCallBack)
    {
        if(instance == null)
        {
            instance = new FSensorTracker();
        }
        instance.fSensorCallBack = fSensorCallBack;

        return instance;
    }

    public void register(Context context)
    {
        mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        Sensor accelerometerSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        Sensor orientationSendor = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
        mSensorManager.registerListener(this, accelerometerSensor, Sensor.TYPE_ACCELEROMETER);
        mSensorManager.registerListener(this, orientationSendor, Sensor.TYPE_ORIENTATION);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            Sensor significantMotionSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_SIGNIFICANT_MOTION);
            mSensorManager.registerListener(this, significantMotionSensor, Sensor.TYPE_SIGNIFICANT_MOTION);
        }
    }

    public void unRegister()
    {
        if(mSensorManager != null)
        {
            mSensorManager.unregisterListener(this);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent)
    {
        if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
        {
            mGravity = sensorEvent.values.clone();
            // Shake detection
            float x = mGravity[0];
            float y = mGravity[1];
            float z = mGravity[2];

            //mAccelCurrent = Math.sqrt(x*x + y*y + z*z);
            //float delta = mAccelCurrent - mAccelLast;
            /*mAccel = mAccel * 0.9f + delta;
            // Make this higher or lower according to how much
            // motion you want to detect
            if(mAccel > 3){
                // do something
            }*/



            if(mPrevGravity != null)
            {
                if((Math.abs((mPrevGravity[0]-mGravity[0])) > 1) || ((Math.abs((mPrevGravity[1]-mGravity[1])) > 1)) || ((Math.abs((mPrevGravity[2]-mGravity[2])) > 1)))
                {
                    MyLogger.l(TAG, "x is " + (mPrevGravity[0]-mGravity[0]));
                    MyLogger.l(TAG, "y is " + (mPrevGravity[1]-mGravity[1]));
                    MyLogger.l(TAG, "z is " + (mPrevGravity[2]-mGravity[2]));

                    if(fSensorCallBack != null) {
                        fSensorCallBack.disturbed();
                    }
                }
            }

            mPrevGravity = mGravity;
        }
        else if(sensorEvent.sensor.getType() == Sensor.TYPE_ORIENTATION)
        {
            float azimuth_angle = sensorEvent.values[0];
            int precision = 1;

            if (prevAzimuth - azimuth_angle < precision * -1)
            {
                MyLogger.l(TAG, "RIGHT");
            }
            else if (prevAzimuth - azimuth_angle > precision)
            {
                MyLogger.l(TAG, "LEFT");
            }

            prevAzimuth = azimuth_angle;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i)
    {

    }

    public interface FSensorCallBack
    {
        void disturbed();
    }
}
