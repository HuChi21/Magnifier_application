package com.example.baseproject.ui;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;

import com.example.baseproject.BuildConfig;
import com.example.baseproject.R;
import com.example.baseproject.base.BaseActivity;
import com.example.baseproject.databinding.ActivityAboutusBinding;
import com.lvt.ads.callback.AdCallback;
import com.lvt.ads.util.Admob;
import com.lvt.ads.util.AppOpenManager;

public class AboutUsActivity extends BaseActivity<ActivityAboutusBinding> {

    @Override
    protected ActivityAboutusBinding setViewBinding() {
        return ActivityAboutusBinding.inflate(LayoutInflater.from(this));
    }

    @Override
    protected void initView() {

        Admob.getInstance().loadBanner(this,getString(com.lvt.ads.R.string.ads_test_banner));
        initToolbar();

    }

    @Override
    protected void viewListener() {
        String versionName = BuildConfig.VERSION_NAME;
        binding.txtVersion.setText(getString(R.string.version)+" "+versionName);
    }

    @Override
    protected void dataObservable() {

    }
    private void initToolbar() {
        binding.toolbarAboutUs.txtToolbarTitle.setText(getString(R.string.about_us));
        binding.toolbarAboutUs.txtToolbarTitle.setTextColor(Color.parseColor("#4D4D4D"));
        binding.toolbarAboutUs.btnBack.setImageTintList(ColorStateList.valueOf(Color.parseColor("#4D4D4D")));
        binding.toolbarAboutUs.btnBack.setOnClickListener(view -> onBackPressed());
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(AboutUsActivity.this,MainActivity.class));
        finish();
    }

}