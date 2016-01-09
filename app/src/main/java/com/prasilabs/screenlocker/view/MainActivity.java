package com.prasilabs.screenlocker.view;

import android.app.admin.DevicePolicyManager;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.prasilabs.screenlocker.VApp;
import com.prasilabs.screenlocker.constants.Constant;
import com.prasilabs.screenlocker.utils.PhoneData;
import com.prasilabs.screenlocker.R;
import com.prasilabs.screenlocker.services.ScreenLockService;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private TextView statusText, shareBtn, pageBtn;
    private CheckBox checkBox;
    private Switch notificatinSwitch, volumeSwitch;

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
                PhoneData.savePhoneData(MainActivity.this, Constant.UNLOCK_STR, isChecked);

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
            public void onClick(View v)
            {
                String url = "http://www.facebook.com/prasilabs";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });

        volumeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                if(isChecked)
                {
                    if (VApp.devicePolicyManager != null && VApp.mAdminName != null)
                    {
                        if (!VApp.devicePolicyManager.isAdminActive(VApp.mAdminName))
                        {

                            volumeSwitch.setChecked(false);
                            Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
                            intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, VApp.mAdminName);
                            intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "Additional text explaining why this needs to be added.");
                            startActivityForResult(intent, Constant.REQUEST_ENABLE);
                        }
                        else
                        {

                        }
                    }
                }
                else
                {

                }
            }
        });

        notificatinSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {

            }
        });

        renderView();
    }

    private void renderView()
    {
        boolean isChecked = PhoneData.getPhoneData(this, Constant.UNLOCK_STR, false);

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
    }
}


