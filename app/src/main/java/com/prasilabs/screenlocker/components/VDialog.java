package com.prasilabs.screenlocker.components;

import android.content.Context;
import android.support.v7.app.AlertDialog;

import com.prasilabs.screenlocker.R;

/**
 * Created by prasi on 15/1/16.
 */
public class VDialog
{
    public static void showExperimentalDialog(Context context)
    {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);

        alertBuilder.setTitle("Hey . :) ");
        alertBuilder.setMessage("This Feature is Experimental, Use it with caution. If not working uncheck it.. :) Have a happy day");
        alertBuilder.setIcon(R.drawable.ic_launcher);

        AlertDialog alertDialog = alertBuilder.create();
        alertDialog.show();
    }
}
