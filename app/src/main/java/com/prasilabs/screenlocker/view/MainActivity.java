package com.prasilabs.screenlocker.view;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.prasilabs.screenlocker.constants.KeyConstant;
import com.prasilabs.screenlocker.constants.RequestFor;
import com.prasilabs.screenlocker.notifications.ScreenLockNotification;
import com.prasilabs.screenlocker.utils.PhoneData;
import com.prasilabs.screenlocker.R;
import com.prasilabs.screenlocker.services.ScreenLockService;
import com.prasilabs.screenlocker.utils.VUtil;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private TextView statusText, shareBtn, pageBtn;
    private CheckBox checkBox;
    private Switch notificatinSwitch, volumeSwitch;
    private long prevTime;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkBox = (CheckBox) findViewById(R.id.check_box);
        statusText = (TextView) findViewById(R.id.status_text);
        shareBtn = (TextView) findViewById(R.id.share_btn);
        pageBtn = (TextView) findViewById(R.id.page_btn);
        notificatinSwitch = (Switch) findViewById(R.id.notification_switch);
        volumeSwitch = (Switch) findViewById(R.id.volume_key_switch);

        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                PhoneData.savePhoneData(MainActivity.this, KeyConstant.UNLOCK_STR, isChecked);

                renderView();
            }
        });

        shareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = "Check this app for power unlock via Volume keys.. :) ";
                Intent i = new Intent();
                i.setAction(Intent.ACTION_SEND);
                i.putExtra(Intent.EXTRA_TEXT, message);
                i.setType("text/plain");
                startActivity(i);
            }
        });

        pageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "http://www.facebook.com/prasilabs";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });

        volumeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                PhoneData.savePhoneData(MainActivity.this, KeyConstant.VOLUME_LOCK_ENABLE_STR, isChecked && VUtil.checkisDeviceAdminEnabled());

                if (isChecked && !VUtil.checkisDeviceAdminEnabled()) {
                    VUtil.openDeviceManagerEnableAction(MainActivity.this, RequestFor.REQUEST_VOLUME_ENABLE);
                }
            }
        });

        notificatinSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                PhoneData.savePhoneData(MainActivity.this, KeyConstant.NOTIF_LOCK_ENABLE_STR, isChecked && VUtil.checkisDeviceAdminEnabled());

                if (isChecked && !VUtil.checkisDeviceAdminEnabled()) {
                    VUtil.openDeviceManagerEnableAction(MainActivity.this, RequestFor.REQUEST_NOTIF_ENABLE);
                }

                manageNotificationLock();
            }
        });

        renderView();

        if(getIntent() != null && getIntent().getExtras() != null)
        {
            if(getIntent().getExtras().getInt(KeyConstant.REQUEST_FOR_STR) == RequestFor.ACTIVATE_DEVICE_ADMIN)
            {
                VUtil.openDeviceManagerEnableAction(this);
            }
        }
    }

    private void renderView()
    {
        boolean isChecked = PhoneData.getPhoneData(this, KeyConstant.UNLOCK_STR, false);

        String text = isChecked ? getString(R.string.enabled_str) : getString(R.string.disabled_str);
        int color = isChecked ? getResources().getColor(R.color.green) : getResources().getColor(R.color.red);

        checkBox.setChecked(isChecked);
        statusText.setText(text);
        statusText.setTextColor(color);

        if(isChecked)
        {
            ScreenLockService.startService(this);
        }
        else
        {
            ScreenLockService.stopService();
        }

        boolean isNotifEnabled = PhoneData.getPhoneData(this, KeyConstant.NOTIF_LOCK_ENABLE_STR, false);
        boolean isKeyEnabled = PhoneData.getPhoneData(this, KeyConstant.VOLUME_LOCK_ENABLE_STR, false);

        notificatinSwitch.setChecked(isNotifEnabled && VUtil.checkisDeviceAdminEnabled());
        volumeSwitch.setChecked(isKeyEnabled && VUtil.checkisDeviceAdminEnabled());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if(requestCode == RequestFor.REQUEST_VOLUME_ENABLE)
        {
            if(volumeSwitch != null)
            {
                volumeSwitch.setChecked(VUtil.checkisDeviceAdminEnabled());
                PhoneData.savePhoneData(this, KeyConstant.VOLUME_LOCK_ENABLE_STR, VUtil.checkisDeviceAdminEnabled());
            }
        }
        else if(requestCode == RequestFor.REQUEST_NOTIF_ENABLE)
        {
            if(notificatinSwitch != null)
            {
                notificatinSwitch.setChecked(VUtil.checkisDeviceAdminEnabled());
                PhoneData.savePhoneData(this, KeyConstant.NOTIF_LOCK_ENABLE_STR, VUtil.checkisDeviceAdminEnabled());
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void manageNotificationLock()
    {
        if(PhoneData.getPhoneData(this, KeyConstant.NOTIF_LOCK_ENABLE_STR, false) && VUtil.checkisDeviceAdminEnabled())
        {
            ScreenLockNotification.createNotification(this);
        }
        else
        {
            ScreenLockNotification.cancelNotification(this);
        }
    }

    @Override
    public void onBackPressed()
    {
        if(System.currentTimeMillis() - prevTime < 1000)
        {
            super.onBackPressed();

        }
        else
        {
            Toast.makeText(this, "Press Again to Exit ", Toast.LENGTH_SHORT).show();
        }

        prevTime = System.currentTimeMillis();
    }
}


