package com.prasilabs.screenlocker.widgets;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.prasilabs.screenlocker.utils.VUtil;

/**
 * Created by prasi on 10/1/16.
 * for shortcut activity that behaves as widget
 */
public class ShortCutActivity extends AppCompatActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        VUtil.createShortCut(this);
        finish();
    }
}
