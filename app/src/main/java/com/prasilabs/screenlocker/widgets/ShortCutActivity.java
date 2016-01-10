package com.prasilabs.screenlocker.widgets;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.prasilabs.screenlocker.R;

/**
 * Created by prasi on 10/1/16.
 */
public class ShortCutActivity extends AppCompatActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        Intent i= new Intent();
        Intent shortcutActivity = new Intent(this, LockNowActivity.class);
        shortcutActivity.setAction(Intent.ACTION_MAIN);
        shortcutActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        i.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutActivity);
        i.putExtra(Intent.EXTRA_SHORTCUT_NAME, "Shortcut Title");
        i.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, Intent.ShortcutIconResource.fromContext(this, R.drawable.ic_launcher_small));
        setResult(RESULT_OK, i);
        finish();
    }
}
