package com.prasilabs.screenlocker.utils;

import android.os.Environment;
import android.util.Log;

import com.prasilabs.screenlocker.VApp;
import com.prasilabs.screenlocker.constants.Constant;
import com.prasilabs.screenlocker.constants.KeyConstant;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by prasi on 8/1/16.
 * This method is for logging in both console and print to file
 * VApp.Debug should be true to enable logging
 */
public class MyLogger
{
    public static void l(String from, String message)
    {
        if(VApp.appDebug)
        {
            Log.d(from, message);
        }
    }


    public static void lw(String from, String message)
    {
        if(VApp.appDebug)
        {
            SimpleDateFormat dateFormat = new SimpleDateFormat(Constant.DATE_FORMAT_STR, Locale.ENGLISH);
            String cDateTime = dateFormat.format(new Date());
            writeToFile(cDateTime + " :: " + from + " :: " + message, KeyConstant.LOGFILENAME_STR);
        }
    }

    public static void e(Exception e)
    {
        if(VApp.appDebug)
        {
            e.printStackTrace();
        }
    }


    private static void writeToFile(String stacktrace, String filename) {
        try
        {
            BufferedWriter bos = new BufferedWriter(new FileWriter(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath() + "/" + filename, true));
            bos.newLine();
            bos.write(stacktrace);
            bos.flush();
            bos.close();
        }
        catch (Exception e)
        {
            MyLogger.e(e);
        }
    }
}
