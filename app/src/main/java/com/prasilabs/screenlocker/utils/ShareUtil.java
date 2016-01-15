package com.prasilabs.screenlocker.utils;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

/**
 * Created by prasi on 15/1/16.
 */
public class ShareUtil
{
    public static void shareApp(Context context)
    {
        String message = "Check this app for power unlock via Volume keys.. :) https://play.google.com/store/apps/details?id=com.prasilabs.screenlocker ";
        Intent i = new Intent();
        i.setAction(Intent.ACTION_SEND);
        i.putExtra(Intent.EXTRA_TEXT, message);
        i.setType("text/plain");
        context.startActivity(i);
    }

    public static void gotoPage(Context context)
    {
        String url = "http://www.facebook.com/prasilabs";
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        context.startActivity(i);
    }

    public static void rateApp(Context context)
    {
        Uri uri = Uri.parse("market://details?id=" + context.getPackageName());
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        try
        {
            context.startActivity(goToMarket);
        }
        catch (ActivityNotFoundException e)
        {
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + context.getPackageName())));
        }
    }
}
