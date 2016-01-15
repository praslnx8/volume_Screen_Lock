package com.prasilabs.screenlocker.components;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.SeekBar;

import com.prasilabs.screenlocker.R;
import com.prasilabs.screenlocker.constants.KeyConstant;
import com.prasilabs.screenlocker.utils.PhoneData;

/**
 * Created by prasi on 15/1/16.
 */
public class VDialog
{
    public static void showExperimentalDialog(Context context)
    {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);

        alertBuilder.setTitle("Experimental Feature");
        alertBuilder.setMessage("Double press volume key to lock screen. \n Use it with caution.");
        alertBuilder.setIcon(R.mipmap.ic_launcher);

        AlertDialog alertDialog = alertBuilder.create();
        alertDialog.show();
    }

    public static void setThresoldBar(final Context context)
    {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
        final int max = 3000;

        View view = LayoutInflater.from(context).inflate(R.layout.seek_bar_layout, null);
        int thresoldValue = PhoneData.getPhoneData(context, KeyConstant.SHAKE_THRESOLD_STR, 500);
        if(thresoldValue < 500)
        {
            thresoldValue = 500;
        }
        SeekBar seekBar = (SeekBar) view.findViewById(R.id.shake_thresold_seek_bar);
        seekBar.setMax(max);
        seekBar.setProgress(max - (thresoldValue - 500));

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
        {
            int seekValue = 0;

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
            {
                seekValue = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar)
            {
                PhoneData.savePhoneData(context, KeyConstant.SHAKE_THRESOLD_STR, max - (seekValue - 500));
            }
        });

        alertBuilder.setView(view);

        AlertDialog alertDialog = alertBuilder.create();
        alertDialog.show();
    }
}
