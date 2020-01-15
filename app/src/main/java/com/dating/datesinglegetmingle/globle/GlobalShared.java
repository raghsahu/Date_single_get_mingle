package com.dating.datesinglegetmingle.globle;

import android.content.Context;
import android.content.SharedPreferences;

import com.dating.datesinglegetmingle.R;

/**
 * Created by Ravindra Birla on 13/07/2019.
 */
public class GlobalShared {

    public static String LoadPreferences(Context context, String key) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(context.getResources().getString(R.string.app_name), 0);
        String value = sharedPreferences.getString(key, "");
        return value;
    }

    public static void SavePreferences(Context context, String key, String value) {
        try
        {
            SharedPreferences sharedPreferences = context.getSharedPreferences(context.getResources().getString(R.string.app_name), 0);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(key, value);
            editor.commit();
        } catch (Throwable e)
        {
        }
    }
}
