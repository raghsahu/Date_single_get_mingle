package com.dating.datesinglegetmingle.utils;

import android.content.Context;
import android.os.CountDownTimer;
import android.view.Gravity;
import android.widget.Toast;

public class ToastClass {
    private static final int SHORT_TOAST_DURATION = 2000;

    //Toast message
    public static void showToast(Context mcontext, String message) {
        Toast.makeText(mcontext, message, Toast.LENGTH_SHORT).show();
    }

    public static void showLongToast(Context context ,String text, long durationInMillis) {
        final Toast t = Toast.makeText(context, text, Toast.LENGTH_SHORT);
        t.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);

        new CountDownTimer(Math.max(durationInMillis - SHORT_TOAST_DURATION, 1000), 1000) {
            @Override
            public void onFinish() {
                t.show();
            }

            @Override
            public void onTick(long millisUntilFinished) {
                t.show();
            }
        }.start();
    }

}
