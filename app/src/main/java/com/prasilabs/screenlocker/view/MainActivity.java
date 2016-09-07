package com.prasilabs.screenlocker.view;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.prasilabs.screenlocker.R;
import com.prasilabs.screenlocker.VApp;
import com.prasilabs.screenlocker.components.VDialog;
import com.prasilabs.screenlocker.constants.KeyConstant;
import com.prasilabs.screenlocker.constants.RequestFor;
import com.prasilabs.screenlocker.notifications.ScreenLockNotification;
import com.prasilabs.screenlocker.services.ScreenLockService;
import com.prasilabs.screenlocker.utils.DeviceAdminUtil;
import com.prasilabs.screenlocker.utils.MyLogger;
import com.prasilabs.screenlocker.utils.PhoneData;
import com.prasilabs.screenlocker.utils.ShareUtil;
import com.purplebrain.adbuddiz.sdk.AdBuddiz;
import com.purplebrain.adbuddiz.sdk.AdBuddizDelegate;
import com.purplebrain.adbuddiz.sdk.AdBuddizError;
import com.purplebrain.adbuddiz.sdk.AdBuddizLogLevel;
import com.purplebrain.adbuddiz.sdk.AdBuddizRewardedVideoDelegate;
import com.purplebrain.adbuddiz.sdk.AdBuddizRewardedVideoError;

public class MainActivity extends AppCompatActivity implements AdBuddizRewardedVideoDelegate, AdBuddizDelegate
{
    private static final String TAG = MainActivity.class.getSimpleName();

    private TextView statusText, pageBtn,gitHubBtn,  deviceAdminBtn;
    private CheckBox checkBox;
    private Switch notificatinSwitch, volumeSwitch, shakeSwitch, floatingSwitch;
    private long prevTime;
    private long lastAdTime;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MobileAds.initialize(VApp.getAppContext(), "ca-app-pub-9278716690121470~1246954143");
        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest.Builder adBuilder = new AdRequest.Builder();
        if(VApp.appDebug)
        {
            adBuilder.addTestDevice("794C17700249AE1D7CE8F8A532B090B6");
        }
        AdRequest adRequest = adBuilder.build();
        mAdView.loadAd(adRequest);

        AdBuddiz.setPublisherKey("b770e723-ed1b-4903-86cc-7e019dc3a41a");

        if(VApp.appDebug)
        {
            AdBuddiz.setTestModeActive();
            AdBuddiz.setLogLevel(AdBuddizLogLevel.Info);    // or Error, Silent
        }
        AdBuddiz.cacheAds(this);
        AdBuddiz.setDelegate(this);
        AdBuddiz.RewardedVideo.fetch(this);
        AdBuddiz.setDelegate(this);
        AdBuddiz.RewardedVideo.setDelegate(this);
        if (AdBuddiz.isReadyToShowAd(this)) { // this = current Activity
            /*if(isShowAdByTime()) {
                AdBuddiz.showAd(this); // showAd will always display an ad
            }*/

            //Disabled
        } else {
            Log.d(TAG, "no ads to display now");
        }

        checkBox = (CheckBox) findViewById(R.id.check_box);
        statusText = (TextView) findViewById(R.id.status_text);
        pageBtn = (TextView) findViewById(R.id.page_btn);
        gitHubBtn = (TextView) findViewById(R.id.github_btn);
        deviceAdminBtn = (TextView) findViewById(R.id.device_admin_btn);
        notificatinSwitch = (Switch) findViewById(R.id.notification_switch);
        volumeSwitch = (Switch) findViewById(R.id.volume_key_switch);
        shakeSwitch = (Switch) findViewById(R.id.shake_switch);
        floatingSwitch = (Switch) findViewById(R.id.floating_switch);

        renderView(); //for not making too many popup on setChecked

        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                if(isChecked && isShowAdByTime())
                {
                    AdBuddiz.RewardedVideo.show(MainActivity.this);
                }

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
                if(isChecked && isShowAdByTime())
                {
                    AdBuddiz.showAd(MainActivity.this);
                }

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
                if(isChecked && isShowAdByTime())
                {
                    AdBuddiz.showAd(MainActivity.this);
                }

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
                if(isChecked && isShowAdByTime())
                {
                    AdBuddiz.showAd(MainActivity.this);
                }

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
                if(isChecked && isShowAdByTime())
                {
                    AdBuddiz.showAd(MainActivity.this);
                }

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

    private boolean isShowAdByTime()
    {
        boolean status =  System.currentTimeMillis() - lastAdTime > 10000;

        return status;
    }


    @Override
    public void didFetch()
    {

    }

    @Override
    public void didFail(AdBuddizRewardedVideoError adBuddizRewardedVideoError) {

    }

    @Override
    public void didComplete()
    {
        lastAdTime = System.currentTimeMillis();
    }

    @Override
    public void didNotComplete()
    {

    }

    @Override
    public void didCacheAd() {

    }

    @Override
    public void didShowAd()
    {
        lastAdTime = System.currentTimeMillis();
    }

    @Override
    public void didFailToShowAd(AdBuddizError adBuddizError) {

    }

    @Override
    public void didClick() {

    }

    @Override
    public void didHideAd() {

    }
}


