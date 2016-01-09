package com.prasilabs.screenlocker.widgets;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.prasilabs.screenlocker.R;
import com.prasilabs.screenlocker.constants.IntentConstant;
import com.prasilabs.screenlocker.utils.MyLogger;

/**
 * Created by prasi on 9/1/16.
 * For screen lock widget
 */
public class ScreenLockWidgetProvider extends AppWidgetProvider
{

    private static final String TAG = ScreenLockWidgetProvider.class.getSimpleName();


    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds)
    {
        for (int widgetId : appWidgetIds)
        {
            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_screen_lock);
            remoteViews.setOnClickPendingIntent(R.id.screen_lock_btn, buildButtonPendingIntent(context));

            pushWidgetUpdate(context, remoteViews);

            appWidgetManager.updateAppWidget(widgetId, remoteViews);
        }
    }


    public static PendingIntent buildButtonPendingIntent(Context context)
    {

        Intent intent = new Intent();
        intent.setAction(IntentConstant.LOCK_SCREEN_ACTION_INTENT);
        return PendingIntent.getBroadcast(context, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    public static void pushWidgetUpdate(Context context, RemoteViews remoteViews)
    {
        ComponentName myWidget = new ComponentName(context, ScreenLockWidgetProvider.class);
        AppWidgetManager manager = AppWidgetManager.getInstance(context);
        manager.updateAppWidget(myWidget, remoteViews);
    }

    @Override
    public void onReceive(Context context, Intent intent)
    {
        if(intent != null)
        {
            MyLogger.l(TAG, "intent is: "+ intent.getAction());
        }

        super.onReceive(context, intent);
    }
}
