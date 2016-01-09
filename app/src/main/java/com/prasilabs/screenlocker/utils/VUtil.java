package com.prasilabs.screenlocker.utils;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.Intent;
import android.util.Log;

import com.prasilabs.screenlocker.VApp;
import com.prasilabs.screenlocker.constants.Constant;

/**
 * Created by prasi on 9/1/16.
 */
public class VUtil
{
    private static final String TAG = VUtil.class.getSimpleName();

    public static boolean checkisDeviceAdminEnabled()
    {
        if(VApp.devicePolicyManager != null && VApp.mAdminName != null)
        {
            if (VApp.devicePolicyManager.isAdminActive(VApp.mAdminName))
            {
                MyLogger.l(TAG, "Permision is enabled");
                return true;
            }
            else
            {
                MyLogger.l(TAG, "No admin permision");
            }
        }
        else
        {
            MyLogger.l(TAG, "device managet is null");
        }

        return false;
    }

    public static boolean lockDevice()
    {
        boolean isLocked = false;
        if(VApp.devicePolicyManager != null && VApp.mAdminName != null)
        {
            if (VApp.devicePolicyManager.isAdminActive(VApp.mAdminName))
            {
                VApp.devicePolicyManager.lockNow();
                Log.d(TAG, "Device is locked");
                isLocked = true;
            }
            else
            {
                Log.d(TAG, "no permisioon unable to lock");
            }
        }
        else
        {
            Log.d(TAG, "device policy is null, cannot lock device");
        }

        return isLocked;
    }

    public static void openDeviceManagerEnableAction(Activity activity)
    {
        Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, VApp.mAdminName);
        intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "We need this permission to lock your phone.");
        activity.startActivityForResult(intent, 0);
    }

    public static void openDeviceManagerEnableAction(Activity activity, int requestCode)
    {
        Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, VApp.mAdminName);
        intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "We need this permission to lock your phone.");
        activity.startActivityForResult(intent, requestCode);
    }
}
