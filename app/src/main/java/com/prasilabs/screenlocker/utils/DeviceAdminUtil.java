package com.prasilabs.screenlocker.utils;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.prasilabs.screenlocker.VApp;
import com.prasilabs.screenlocker.constants.RequestFor;

/**
 * Created by prasi on 15/1/16.
 * utils to control for device admin
 */
public class DeviceAdminUtil
{
    private static final String TAG = DeviceAdminUtil.class.getSimpleName();


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
                MyLogger.l(TAG, "Device is locked");
                isLocked = true;
            }
            else
            {
                MyLogger.l(TAG, "no permisioon unable to lock");
            }
        }
        else
        {
            MyLogger.l(TAG, "device policy is null, cannot lock device");
        }

        return isLocked;
    }

    public static void removeAdminAndUninstall(Context context)
    {
        if(VApp.devicePolicyManager != null && VApp.mAdminName != null)
        {
            if (VApp.devicePolicyManager.isAdminActive(VApp.mAdminName))
            {
                VApp.devicePolicyManager.removeActiveAdmin(VApp.mAdminName);
            }
        }

        Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse("package:" + context.getPackageName()));
        context.startActivity(intent);
        //intentionally dont direclty uninstall
        /*String packageName = context.getPackageName();
        Intent intent = new Intent(Intent.ACTION_DELETE);
        intent.setData(Uri.parse("package:" + packageName));
        context.startActivity(intent);*/
    }

    public static void openDeviceManagerEnableAction(Activity activity)
    {
        openDeviceManagerEnableAction(activity, RequestFor.DEFAULT);
    }

    public static void openDeviceManagerEnableAction(Activity activity, int requestCode)
    {
        if(!checkisDeviceAdminEnabled())
        {
            Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
            intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, VApp.mAdminName);
            intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "We need this permission to lock your phone.");
            activity.startActivityForResult(intent, requestCode);
        }
        else
        {
            //Not safe,, May vary depends on ROM
            try
            {
                final Intent intent=new Intent();
                intent.setComponent(new ComponentName("com.android.settings","com.android.settings.DeviceAdminAdd"));
                intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN,VApp.mAdminName);
                activity.startActivity(intent);
            }
            catch (Exception e)
            {
                MyLogger.e(e);
            }
        }
    }
}
