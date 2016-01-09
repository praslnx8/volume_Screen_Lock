package com.prasilabs.screenlocker.widgets;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.prasilabs.screenlocker.R;
import com.prasilabs.screenlocker.constants.Constant;
import com.prasilabs.screenlocker.services.MediaButtonIntentReciever;
import com.prasilabs.screenlocker.utils.MyLogger;
import com.prasilabs.screenlocker.utils.VUtil;

import java.util.Random;

/**
 * Created by prasi on 9/1/16.
 */
public class ScreenLockWidgetProvider extends AppWidgetProvider
{

    private static final String TAG = ScreenLockWidgetProvider.class.getSimpleName();


    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        final int count = appWidgetIds.length;

        for (int i = 0; i < count; i++)
        {
            int widgetId = appWidgetIds[i];
            // initializing widget layout
            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_screen_lock);

            // register for button event
            remoteViews.setOnClickPendingIntent(R.id.screen_lock_btn, buildButtonPendingIntent(context));

            // request for widget update
            pushWidgetUpdate(context, remoteViews);



            appWidgetManager.updateAppWidget(widgetId, remoteViews);
        }
    }


    public static PendingIntent buildButtonPendingIntent(Context context)
    {

        Intent intent = new Intent();
        intent.setAction(Constant.LOCK_SCREEN_ACTION_INTENT);
        return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    public static void pushWidgetUpdate(Context context, RemoteViews remoteViews)
    {
        ComponentName myWidget = new ComponentName(context, MediaButtonIntentReciever.class);
        AppWidgetManager manager = AppWidgetManager.getInstance(context);
        manager.updateAppWidget(myWidget, remoteViews);
    }

    @Override
    public void onReceive(Context context, Intent intent)
    {
        if(intent != null)
        {
            if(intent.getAction().equals(Constant.LOCK_SCREEN_ACTION_INTENT))
            {
                MyLogger.l(TAG, "lock screen action intent arrived");

                if(VUtil.checkisDeviceAdminEnabled())
                {
                    VUtil.lockDevice();
                }
            }
            else
            {
                MyLogger.l(TAG, "uncatched intent");
            }
        }


        super.onReceive(context, intent);
    }
}
