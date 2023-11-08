package com.example.baseproject.base;

import android.app.Activity;

import com.lvt.ads.util.AppOpenManager;


public class BaseAds {
    public static void enableAdsResume(Activity context) {
        AppOpenManager.getInstance().enableAppResumeWithActivity(context.getClass());
    }

    public static void disableAdsResume(Activity context) {
        AppOpenManager.getInstance().disableAppResumeWithActivity(context.getClass());
    }
}
