package com.prasilabs.screenlocker.view;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.prasilabs.screenlocker.R;
import com.prasilabs.screenlocker.components.VDialog;
import com.prasilabs.screenlocker.constants.KeyConstant;
import com.prasilabs.screenlocker.constants.RequestFor;
import com.prasilabs.screenlocker.notifications.ScreenLockNotification;
import com.prasilabs.screenlocker.services.ScreenLockService;
import com.prasilabs.screenlocker.utils.DeviceAdminUtil;
import com.prasilabs.screenlocker.utils.MyLogger;
import com.prasilabs.screenlocker.utils.PhoneData;
import com.prasilabs.screenlocker.utils.ShareUtil;

public class MainActivity extends AppCompatActivity
{
    private static final String TAG = MainActivity.class.getSimpleName();

    private TextView statusText, pageBtn,gitHubBtn,  deviceAdminBtn;
    private CheckBox checkBox;
    private Switch notificatinSwitch, volumeSwitch, shakeSwitch, floatingSwitch;
    private long prevTime;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkBox = (CheckBox) findViewById(R.id.check_box);
        statusText = (TextView) findViewById(R.id.status_text);
        pageBtn = (TextView) findViewById(R.id.page_btn);
        gitHubBtn = (TextView) findViewById(R.id.github_btn);
        deviceAdminBtn = (TextView) findViewById(R.id.device_admin_btn);
        notificatinSwitch = (Switch) findViewById(R.id.notification_switch);
        volumeSwitch = (Switch) findViewById(R.id.volume_key_switch);
        shakeSwitch = (Switch) findViewById(R.id.shake_switch);
        floatingSwitch = (Switch) findViewById(R.id.floating_switch);

        final LinearLayout splashLayout = (LinearLayout) findViewById(R.id.splash_layout);
        final RelativeLayout contentLayout = (RelativeLayout) findViewById(R.id.content_view);
        LinearLayout bikeRLayout = (LinearLayout) findViewById(R.id.bikr_layout);
        final Button bikRinstallBtn = (Button) findViewById(R.id.bikr_install_btn);

        bikeRLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                bikRinstallBtn.performClick();
            }
        });

        bikRinstallBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                final String appPackageName = "com.prasilabs.ffloc";
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName +"&rdid=" + appPackageName)));
                } catch (android.content.ActivityNotFoundException anfe) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + appPackageName + "rdid=" + appPackageName)));
                }
            }
        });

        splashLayout.setVisibility(View.VISIBLE);
        contentLayout.setVisibility(View.GONE);

        //for letting the ads to load
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run()
            {
                contentLayout.setVisibility(View.VISIBLE);
                splashLayout.setVisibility(View.GONE);
            }
        }, 3000);

        renderView(); //for not making too many popup on setChecked

        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                PhoneData.savePhoneData(MainActivity.this, KeyConstant.UNLOCK_STR, isChecked);
                ScreenLockService.manageService(MainActivity.this);

                String text = isChecked ? getString(R.string.enabled_str) : getString(R.string.disabled_str);
                int color = isChecked ? ContextCompat.getColor(MainActivity.this, R.color.green) : ContextCompat.getColor(MainActivity.this, R.color.red);
                statusText.setText(text);
                statusText.setTextColor(color);
            }
        });

        pageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShareUtil.gotoPage(MainActivity.this);
            }
        });

        gitHubBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.github_src_url)));
                startActivity(browserIntent);
            }
        });


        volumeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {

                PhoneData.savePhoneData(MainActivity.this, KeyConstant.VOLUME_LOCK_ENABLE_STR, isChecked && DeviceAdminUtil.checkisDeviceAdminEnabled());

                if (isChecked) {
                    VDialog.showExperimentalDialog(MainActivity.this);
                }

                if (isChecked && !DeviceAdminUtil.checkisDeviceAdminEnabled()) {
                    DeviceAdminUtil.openDeviceManagerEnableAction(MainActivity.this, RequestFor.REQUEST_VOLUME_ENABLE);
                }
            }
        });

        notificatinSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {

                PhoneData.savePhoneData(MainActivity.this, KeyConstant.NOTIF_LOCK_ENABLE_STR, isChecked && DeviceAdminUtil.checkisDeviceAdminEnabled());

                if (isChecked && !DeviceAdminUtil.checkisDeviceAdminEnabled()) {
                    DeviceAdminUtil.openDeviceManagerEnableAction(MainActivity.this, RequestFor.REQUEST_NOTIF_ENABLE);
                }

                ScreenLockNotification.manageNotification(MainActivity.this);
            }
        });

        floatingSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {

                PhoneData.savePhoneData(MainActivity.this, KeyConstant.FLOATING_LOCK_STR, isChecked && DeviceAdminUtil.checkisDeviceAdminEnabled());

                if (isChecked && !DeviceAdminUtil.checkisDeviceAdminEnabled()) {
                    DeviceAdminUtil.openDeviceManagerEnableAction(MainActivity.this, RequestFor.REQUEST_FLOATING_ENABLE);
                }

                ScreenLockService.manageService(MainActivity.this);
            }
        });

        shakeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {

                PhoneData.savePhoneData(MainActivity.this, KeyConstant.SHAKE_LOCK_STR, isChecked);

                if (isChecked && !DeviceAdminUtil.checkisDeviceAdminEnabled()) {
                    DeviceAdminUtil.openDeviceManagerEnableAction(MainActivity.this, RequestFor.REQUEST_SHAKE_ENABLE);
                } else if (isChecked) {
                    VDialog.setThresoldBar(MainActivity.this);
                }

                ScreenLockService.manageService(MainActivity.this);
            }
        });

        deviceAdminBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DeviceAdminUtil.removeAdminAndUninstall(MainActivity.this);
            }
        });

        if(getIntent() != null && getIntent().getExtras() != null)
        {
            MyLogger.l(TAG, "Intent came");
            if(getIntent().getExtras().getInt(KeyConstant.REQUEST_FOR_STR) == RequestFor.ACTIVATE_DEVICE_ADMIN)
            {
                DeviceAdminUtil.openDeviceManagerEnableAction(this);
            }
        }
    }

    private void renderView()
    {
        boolean isChecked = PhoneData.getPhoneData(this, KeyConstant.UNLOCK_STR, false);

        String text = isChecked ? getString(R.string.enabled_str) : getString(R.string.disabled_str);
        int color = isChecked ? ContextCompat.getColor(this, R.color.green) : ContextCompat.getColor(this, R.color.red);

        checkBox.setChecked(isChecked);
        statusText.setText(text);
        statusText.setTextColor(color);

        boolean isNotifEnabled = PhoneData.getPhoneData(this, KeyConstant.NOTIF_LOCK_ENABLE_STR, false);
        boolean isKeyEnabled = PhoneData.getPhoneData(this, KeyConstant.VOLUME_LOCK_ENABLE_STR, false);
        boolean isShakeEnabled = PhoneData.getPhoneData(this, KeyConstant.SHAKE_LOCK_STR, false);
        boolean isFloatingEnabed = PhoneData.getPhoneData(this, KeyConstant.FLOATING_LOCK_STR, false);

        notificatinSwitch.setChecked(isNotifEnabled && DeviceAdminUtil.checkisDeviceAdminEnabled());
        volumeSwitch.setChecked(isKeyEnabled && DeviceAdminUtil.checkisDeviceAdminEnabled());
        shakeSwitch.setChecked(isShakeEnabled && DeviceAdminUtil.checkisDeviceAdminEnabled());
        floatingSwitch.setChecked(isFloatingEnabed && DeviceAdminUtil.checkisDeviceAdminEnabled());

        if(DeviceAdminUtil.checkisDeviceAdminEnabled())
        {
            deviceAdminBtn.setVisibility(View.VISIBLE);
        }
        else
        {
            deviceAdminBtn.setVisibility(View.GONE);
        }

        ScreenLockService.manageService(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);

        return true;
    }

    public void menuClick(MenuItem item)
    {
        if(item.getItemId() == R.id.share_btn)
        {
            ShareUtil.shareApp(this);
        }
        else if(item.getItemId() == R.id.rate_btn)
        {
            ShareUtil.rateApp(this);
        }
        else if(item.getItemId() == R.id.about_btn)
        {
            ShareUtil.gotoPage(this);
        }
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        renderView();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if(requestCode == RequestFor.REQUEST_VOLUME_ENABLE)
        {
            if(volumeSwitch != null)
            {
                volumeSwitch.setChecked(DeviceAdminUtil.checkisDeviceAdminEnabled());
                PhoneData.savePhoneData(this, KeyConstant.VOLUME_LOCK_ENABLE_STR, DeviceAdminUtil.checkisDeviceAdminEnabled());
            }
        }
        else if(requestCode == RequestFor.REQUEST_NOTIF_ENABLE)
        {
            if(notificatinSwitch != null)
            {
                notificatinSwitch.setChecked(DeviceAdminUtil.checkisDeviceAdminEnabled());
                PhoneData.savePhoneData(this, KeyConstant.NOTIF_LOCK_ENABLE_STR, DeviceAdminUtil.checkisDeviceAdminEnabled());
                ScreenLockNotification.manageNotification(this);
            }
        }
        else if(requestCode == RequestFor.REQUEST_SHAKE_ENABLE)
        {
            if(shakeSwitch != null)
            {
                notificatinSwitch.setChecked(DeviceAdminUtil.checkisDeviceAdminEnabled());
                PhoneData.savePhoneData(this, KeyConstant.SHAKE_LOCK_STR, DeviceAdminUtil.checkisDeviceAdminEnabled());
                ScreenLockService.manageService(this);

                if(DeviceAdminUtil.checkisDeviceAdminEnabled())
                {
                    VDialog.setThresoldBar(this);
                }
            }
        }
        else if(requestCode == RequestFor.REQUEST_FLOATING_ENABLE)
        {
            if(floatingSwitch != null)
            {
                floatingSwitch.setChecked(DeviceAdminUtil.checkisDeviceAdminEnabled());
                PhoneData.savePhoneData(this, KeyConstant.FLOATING_LOCK_STR, DeviceAdminUtil.checkisDeviceAdminEnabled());
                ScreenLockService.manageService(this);
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }


    @Override
    public void onBackPressed()
    {
        if(System.currentTimeMillis() - prevTime < 2000)
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


