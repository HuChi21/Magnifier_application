package com.example.baseproject.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;

import androidx.annotation.Nullable;

import com.example.baseproject.R;
import com.example.baseproject.base.BaseActivity;
import com.example.baseproject.databinding.ActivitySplashBinding;
import com.example.baseproject.model.LanguageModel;
import com.example.baseproject.utils.SharePrefUtils;
import com.lvt.ads.callback.AdCallback;
import com.lvt.ads.util.AppOpenManager;

import java.util.ArrayList;
import java.util.List;

public class SplashActivity extends BaseActivity<ActivitySplashBinding> {
    List<LanguageModel> listLanguage;
    String codeLang;
    AdCallback adCallback;

    @Override
    protected ActivitySplashBinding setViewBinding() {
        return ActivitySplashBinding.inflate(LayoutInflater.from(this));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
          }

    @Override
    protected void initView() {
        List<String> list = new ArrayList<>();
        list.add(getString(R.string.open_splash1));
        list.add(getString(R.string.open_splash2));
        list.add(getString(R.string.open_splash3));
        adCallback = new AdCallback(){
            @Override
            public void onNextAction() {
                super.onNextAction();
                //start activity
                start();
            }
        };
        AppOpenManager.getInstance().loadOpenAppAdSplashFloor(this,list,true,adCallback);
    }

    @Override
    protected void viewListener() {

    }

    @Override
    protected void dataObservable() {

    }

    @Override
    public void onBackPressed() {

    }
    private void start(){
//                new Handler().postDelayed(() -> {
//
//                }, 3000);
        SharedPreferences sharedPreferences = getSharedPreferences("data", Context.MODE_PRIVATE);
        boolean startFirst = sharedPreferences.getBoolean(SharePrefUtils.KEY_STARTFIRST, true);
//            SharedPreferences.Editor editor = sharedPreferences.edit();
        if (startFirst) {
//                editor.putBoolean(PREF_KEY_FIRST_TIME_LAUNCH, false);
//                editor.apply();
            startActivity(new Intent(SplashActivity.this, LanguageActivity.class).putExtra("isEdit",false));
        } else {
            startActivity(new Intent(SplashActivity.this, MainActivity.class));
        }
        finish();

    }
    @Override
    protected void onResume() {
        super.onResume();
        AppOpenManager.getInstance().onCheckShowSplashWhenFail(this,adCallback,1000);

    }
}