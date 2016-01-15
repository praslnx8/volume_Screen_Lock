package com.prasilabs.screenlocker.utils;

import android.app.Activity;
import android.content.Intent;

import com.prasilabs.screenlocker.R;
import com.prasilabs.screenlocker.widgets.LockNowActivity;

/**
 * Created by prasi on 9/1/16.
 * Common Util for VAPP
 */
public class VUtil
{
    private static final String TAG = VUtil.class.getSimpleName();

    public static void createShortCut(Activity activity)
    {
        MyLogger.l(TAG, "shortcut is creating...");

        Intent i= new Intent();
        Intent shortcutActivity = new Intent(activity, LockNowActivity.class);
        shortcutActivity.setAction(Intent.ACTION_MAIN);
        shortcutActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        i.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutActivity);
        i.putExtra(Intent.EXTRA_SHORTCUT_NAME, "Lock Now");
        i.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, Intent.ShortcutIconResource.fromContext(activity, R.mipmap.ic_launcher));
        activity.setResult(Activity.RESULT_OK, i);
    }
}
