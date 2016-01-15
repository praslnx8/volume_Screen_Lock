package com.prasilabs.screenlocker.utils;

import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Build;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.prasilabs.screenlocker.R;

/**
 * Created by prasi on 15/1/16.
 */
public class WindowManagerUtil
{
    private static final String TAG = WindowManagerUtil.class.getSimpleName();
    private static ImageView imageView;
    private static long startClickTime = -1;
    private static final int MAX_CLICK_DURATION = 200;

    public static void showFloatingButton(final Context context)
    {
        if(imageView == null)
        {
            imageView = new ImageView(context);
            imageView.setClickable(true);

            imageView.setMaxWidth(70);
            imageView.setMaxHeight(70);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            {
                imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_launcher_small, null));
            }
            else
            {
                //noinspection deprecation
                imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_launcher_small));
            }
        }


        if(!imageView.isShown())
        {
            final WindowManager.LayoutParams params = new WindowManager.LayoutParams(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.TYPE_PHONE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.TRANSLUCENT);
            params.x = 10;
            params.y = 10;
            params.setTitle("");
            final WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);

            wm.addView(imageView, params);

            imageView.setOnTouchListener(new View.OnTouchListener()
            {
                private int initialX;
                private int initialY;
                private float initialTouchX;
                private float initialTouchY;

                @Override
                public boolean onTouch(View v, MotionEvent event) {


                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN: {
                            MyLogger.l(TAG, "action down");
                            initialX = params.x;
                            initialY = params.y;
                            initialTouchX = event.getRawX();
                            initialTouchY = event.getRawY();

                            startClickTime = System.currentTimeMillis();

                            break;
                        }
                        case MotionEvent.ACTION_UP:
                        {
                            MyLogger.l(TAG, "action up");

                            long clickDuration = System.currentTimeMillis() - startClickTime;
                            if (clickDuration < MAX_CLICK_DURATION)
                            {
                                MyLogger.l(TAG, "clicked");
                                VUtil.lockDevice();
                            }

                            break;
                        }
                        case MotionEvent.ACTION_MOVE: {
                            MyLogger.l(TAG, "action move");

                            params.x = initialX
                                    + (int) (event.getRawX() - initialTouchX);
                            params.y = initialY
                                    + (int) (event.getRawY() - initialTouchY);
                            wm.updateViewLayout(imageView, params);

                            break;
                        }
                    }

                    return false;
                }
            });

        }
    }

    public static void removeFloatingButton()
    {
        if(imageView != null && imageView.getContext() != null)
        {
            final WindowManager wm = (WindowManager) imageView.getContext().getSystemService(Context.WINDOW_SERVICE);
            wm.removeView(imageView);
        }
    }
}
