package com.example.baseproject.utils;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
public class SharePrefUtils {
    public static final String KEY_STARTFIRST = "isFirstTimeLaunch";
    public static final String KEY_RATED = "isRated";
    private static SharedPreferences mSharePref;

    public static void init(Context context) {
        if (mSharePref == null) {
            mSharePref = PreferenceManager.getDefaultSharedPreferences(context);
        }
    }
    public static void putString(String str, String str2) {
        SharedPreferences.Editor edit = mSharePref.edit();
        edit.putString(str, str2);
        edit.apply();
    }

    public static String getString(String str, String str2) {
        return mSharePref.getString(str, str2);
    }


    public static boolean isRated(Context context) {
        SharedPreferences pre = context.getSharedPreferences("data", Context.MODE_PRIVATE);
        return pre.getBoolean("rated", false);
    }
    public static void forceRated(Context context) {
        SharedPreferences pre = context.getSharedPreferences("data", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pre.edit();
        editor.putBoolean("rated", true);
        editor.apply();
    }
}
