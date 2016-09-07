package com.prasilabs.screenlocker.components;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.TextView;

/**
 * Created by prasi on 25/2/16.
 */
public class FontAweSomeTextView extends TextView
{
    private static final String TAG = FontAweSomeTextView.class.getSimpleName();
    private Typeface font;

    public FontAweSomeTextView(Context context) {
        super(context);
        setFont(context);
    }

    public FontAweSomeTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setFont(context);
    }

    public FontAweSomeTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setFont(context);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public FontAweSomeTextView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        setFont(context);
    }

    private void setFont(Context context) {
        // prevent exception in Android Studio / ADT interface builder
        if (this.isInEditMode()) {
            return;
        }

        //Check for font is already loaded
        if(font == null)
        {
            try {
                font = Typeface.createFromAsset(context.getAssets(), "fonts/fontawesome-webfont.ttf");
                Log.i(TAG, "Font awesome loaded");
            } catch (RuntimeException e) {
                Log.i(TAG, "Font awesome not loaded");
            }
        }

        //Finally set the font
        setTypeface(font);
    }
}

