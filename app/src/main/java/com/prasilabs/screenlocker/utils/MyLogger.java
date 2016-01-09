package com.prasilabs.screenlocker.utils;

import android.util.Log;

import com.prasilabs.screenlocker.VApp;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by prasi on 8/1/16.
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
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZZZZZ", Locale.ENGLISH);
            String cDateTime = dateFormat.format(new Date());
            writeToFile(cDateTime + " :: " + from + " :: " + message, "myappdebug");
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
            BufferedWriter bos = new BufferedWriter(new FileWriter("sdcard/" + filename, true));
            bos.newLine();
            bos.write(stacktrace);
            bos.flush();
            bos.close();
        }
        catch (Exception e)
        {

        }
    }
}
