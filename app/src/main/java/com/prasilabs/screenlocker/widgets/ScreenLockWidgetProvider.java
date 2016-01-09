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

/**
 * Created by prasi on 9/1/16.
 */
public class ScreenLockWidgetProvider extends AppWidgetProvider
{

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        // initializing widget layout
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_screen_lock);

        // register for button event
        remoteViews.setOnClickPendingIntent(R.id.screen_lock_btn, buildButtonPendingIntent(context));

        // updating view with initial data
        /*remoteViews.setTextViewText(R.id.title, getTitle());
        remoteViews.setTextViewText(R.id.desc, getDesc());
*/
        // request for widget update
        pushWidgetUpdate(context, remoteViews);
    }

    public static PendingIntent buildButtonPendingIntent(Context context)
    {

        // initiate widget update request
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
}
