package com.prasilabs.screenlocker.widgets;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.prasilabs.screenlocker.constants.Constant;
import com.prasilabs.screenlocker.constants.IntentConstant;
import com.prasilabs.screenlocker.utils.DeviceAdminUtil;

/**
 * Created by prasi on 10/1/16.
 * For doing lock now action
 */
public class LockNowActivity extends AppCompatActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        if(DeviceAdminUtil.checkisDeviceAdminEnabled())
        {
            DeviceAdminUtil.lockDevice();
        }
        else
        {
            DeviceAdminUtil.openDeviceManagerEnableAction(this);
        }

        finish();
    }
}
