package com.prasilabs.screenlocker.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.prasilabs.screenlocker.constants.KeyConstant;

/**
 * Created by prasi on 8/1/16.
 * PhoneCacheData
 */
public class PhoneData
{
    private static final String TAG = PhoneData.class.getSimpleName();

    public static void savePhoneData(Context context, String field, String values)
    {
        try
        {
            SharedPreferences sp = context.getSharedPreferences(KeyConstant.PHONE_DATA_STR, Context.MODE_PRIVATE);
            SharedPreferences.Editor edit = sp.edit();
            edit.putString(field, values);
            MyLogger.l(TAG + " saving", field + " = " + values);
            edit.apply();
        }
        catch (Exception e)
        {
            MyLogger.e(e);
        }
    }

    public static String getPhoneData(Context context, String field, String defaultValue)
    {
        try
        {
            SharedPreferences sp = context.getSharedPreferences(KeyConstant.PHONE_DATA_STR, Context.MODE_PRIVATE);
            MyLogger.l(TAG + " reading", field + " " + sp.getString(field, defaultValue));
            return sp.getString(field, defaultValue);
        }
        catch (Exception e)
        {
            MyLogger.e(e);
            return "";
        }
    }

    public static void savePhoneData(Context context, String field, boolean values)
    {
        savePhoneData(context, field, String.valueOf(values));
    }

    public static void savePhoneData(Context context, String field, int values)
    {
        savePhoneData(context, field, String.valueOf(values));
    }

    public static void savePhoneData(Context context, String field, long values)
    {
        savePhoneData(context, field, String.valueOf(values));
    }

    public static boolean getPhoneData(Context context, String field, boolean defaultValue)
    {
        String value = getPhoneData(context, field, "");

        if(!TextUtils.isEmpty(value)) {
            try {
                defaultValue = Boolean.parseBoolean(value);
            } catch (Exception e) {}
        }

        return defaultValue;
    }

    public static long getPhoneData(Context context, String field, long defaultValue)
    {
        String value = getPhoneData(context, field, "");
        if(!TextUtils.isEmpty(value)) {
            try {
                defaultValue = Long.parseLong(value);
            } catch (Exception e) {}
        }

        return defaultValue;
    }

    public static int getPhoneData(Context context, String field, int defaultValue)
    {
        String value = getPhoneData(context, field, "");
        if(!TextUtils.isEmpty(value)) {
            try {
                defaultValue = Integer.parseInt(value);
            } catch (Exception e) {}
        }

        return defaultValue;
    }
}
