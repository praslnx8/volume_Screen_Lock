package com.prasilabs.screenlocker.utils;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.widget.Toast;

import com.prasilabs.screenlocker.constants.KeyConstant;

/**
 * Created by prasi on 15/1/16.
 */
public class ShakeSensorUtil
{
    private static final String TAG = ShakeSensorUtil.class.getSimpleName()
            ;
    private static long lastUpdate = -1;
    private static float x, y, z;
    private static float last_x, last_y, last_z;

    public static void handleShake(Context context, SensorEvent event)
    {
        int shakeThresold = PhoneData.getPhoneData(context, KeyConstant.SHAKE_THRESOLD_STR, 1000);
        if(shakeThresold < 500)
        {
            shakeThresold = 500;
        }

        Sensor sensor = event.sensor;
        float[] values = event.values;

        if (sensor.getType() == Sensor.TYPE_ACCELEROMETER)
        {
            long curTime = System.currentTimeMillis();
            // only allow one update every 100ms.
            if ((curTime - lastUpdate) > 100)
            {
                long diffTime = (curTime - lastUpdate);
                lastUpdate = curTime;

                x = values[SensorManager.DATA_X];
                y = values[SensorManager.DATA_Y];
                z = values[SensorManager.DATA_Z];

                if (Round(x, 4) > 10.0000)
                {
                    MyLogger.l(TAG, "X Right axis: " + x);

                    //VUtil.lockDevice();
                }
                else if (Round(x, 4) < -10.0000)
                {
                    MyLogger.l("sensor", "X Left axis: " + x);

                    //VUtil.lockDevice();
                }

                float speed = Math.abs(x + y + z - last_x - last_y - last_z) / diffTime * 10000;

                // Log.d("sensor", "diff: " + diffTime + " - speed: " + speed);
                if (speed > shakeThresold)
                {
                    MyLogger.l(TAG, "shake detected w/ speed: " + speed);
                    VUtil.lockDevice();
                }
                last_x = x;
                last_y = y;
                last_z = z;
            }
        }
    }

    public static float Round(float Rval, int Rpl)
    {
        float p = (float) Math.pow(10, Rpl);
        Rval = Rval * p;
        float tmp = Math.round(Rval);
        return tmp / p;
    }
}
