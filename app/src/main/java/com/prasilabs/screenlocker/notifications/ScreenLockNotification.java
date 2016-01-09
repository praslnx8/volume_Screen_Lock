package com.prasilabs.screenlocker.notifications;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.prasilabs.screenlocker.R;
import com.prasilabs.screenlocker.constants.NotificationConstant;
import com.prasilabs.screenlocker.constants.IntentConstant;

/**
 * Created by prasi on 9/1/16.
 */
public class ScreenLockNotification
{
    public static void createNotification(Context context)
    {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);
        mBuilder.setSmallIcon(R.mipmap.ic_launcher);
        mBuilder.setContentTitle(context.getString(R.string.app_name));
        mBuilder.setContentText(context.getString(R.string.notification_screen_lock_message));

        Intent intent = new Intent();
        intent.setAction(IntentConstant.LOCK_SCREEN_ACTION_INTENT);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        mBuilder.setContentIntent(pendingIntent);
        mBuilder.setAutoCancel(false);

        Notification notification = mBuilder.build();
        notification.flags = Notification.FLAG_ONGOING_EVENT;

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(NotificationConstant.SCREEN_LOCK_NOTIFICATION);
        notificationManager.notify(NotificationConstant.SCREEN_LOCK_NOTIFICATION, notification);
    }

    public static void cancelNotification(Context context)
    {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(NotificationConstant.SCREEN_LOCK_NOTIFICATION);
    }
}
