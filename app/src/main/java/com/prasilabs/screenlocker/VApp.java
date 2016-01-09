package com.prasilabs.screenlocker;

import android.app.Application;
import android.app.admin.DeviceAdminReceiver;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

/**
 * Created by prasi on 8/1/16.
 */
public class VApp extends Application
{
    public static DevicePolicyManager devicePolicyManager;
    public static ComponentName mAdminName;

    public static boolean appDebug = false;

    private static VApp sInstance;

    public static VApp getsInstance() {
        return sInstance;
    }

    public static Context getAppContext() {
        return sInstance.getApplicationContext();
    }


    @Override
    public void onCreate()
    {
        super.onCreate();

        sInstance = this;

        devicePolicyManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
        mAdminName = new ComponentName(this, VAdmin.class);
    }

    public class VAdmin extends DeviceAdminReceiver
    {
        public VAdmin()
        {

        }

        @Override
        public void onEnabled(Context context, Intent intent) {
            super.onEnabled(context, intent);
        }

        @Override
        public void onDisabled(Context context, Intent intent) {
            super.onDisabled(context, intent);
        }
    }
}
