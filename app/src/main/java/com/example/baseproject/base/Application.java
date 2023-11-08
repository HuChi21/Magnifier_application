package com.example.baseproject.base;

import androidx.appcompat.app.AppCompatActivity;


import com.example.baseproject.R;
import com.example.baseproject.ui.SplashActivity;
import com.lvt.ads.util.AdsApplication;
import com.lvt.ads.util.AppOpenManager;

import java.util.ArrayList;
import java.util.List;

public class Application extends AdsApplication {

    @Override
    public void onCreate() {
        super.onCreate();
        AppOpenManager.getInstance().disableAppResumeWithActivity(SplashActivity.class);
    }

    @Override
    public boolean enableAdsResume() {
        return true;
    }

    @Override
    public List<String> getListTestDeviceId() {
        return new ArrayList<>();
    }

    @Override
    public String getResumeAdId() {
        return getString(R.string.appopen_resume);
    }

    @Override
    public Boolean buildDebug() {
        return true;
    }
}
