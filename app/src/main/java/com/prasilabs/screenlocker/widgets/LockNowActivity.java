package com.prasilabs.screenlocker.widgets;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.prasilabs.screenlocker.constants.Constant;
import com.prasilabs.screenlocker.constants.IntentConstant;

/**
 * Created by prasi on 10/1/16.
 */
public class LockNowActivity extends AppCompatActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        Intent intent = new Intent();
        intent.setAction(IntentConstant.LOCK_SCREEN_ACTION_INTENT);
        sendBroadcast(intent);

        finish();
    }
}
