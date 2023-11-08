package com.example.baseproject.ui;

import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;

import com.example.baseproject.R;
import com.example.baseproject.adapter.LanguageAdapter;
import com.example.baseproject.base.BaseActivity;
import com.example.baseproject.databinding.ActivityLanguageBinding;
import com.example.baseproject.model.LanguageModel;
import com.example.baseproject.utils.SharePrefUtils;
import com.example.baseproject.utils.SystemUtils;
import com.lvt.ads.callback.AdCallback;
import com.lvt.ads.util.Admob;
import com.lvt.ads.util.AppOpenManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class LanguageActivity extends BaseActivity<ActivityLanguageBinding> {
    List<LanguageModel> listLanguage;
    String codeLang;

    @Override
    protected ActivityLanguageBinding setViewBinding() {
        return ActivityLanguageBinding.inflate(LayoutInflater.from(this));
    }

    @Override
    protected void initView() {

        SharedPreferences sharedPreferences = getSharedPreferences("data", Context.MODE_PRIVATE);
        boolean startFist = sharedPreferences.getBoolean(SharePrefUtils.KEY_STARTFIRST, true);
//        SharedPreferences.Editor editor = sharedPreferences.edit();
        boolean isEditLang = getIntent().getBooleanExtra("isEditLang",false);
        if (startFist) {
            binding.include.setVisibility(View.GONE);
            binding.nativeAds.setVisibility(View.VISIBLE);
            loadNativeAd();
            binding.btnDone.setOnClickListener(view -> {
//                editor.putBoolean(START_FIRST, false);
//                editor.apply();
                SystemUtils.setPreLanguage(LanguageActivity.this, codeLang);
                startActivity(new Intent(LanguageActivity.this, TutorialActivity.class));
                finishAffinity();
            });
        } else {
            if(!isEditLang){
                SystemUtils.setPreLanguage(LanguageActivity.this, codeLang);
                startActivity(new Intent(LanguageActivity.this, MainActivity.class));
                finishAffinity();
            }else{
                binding.include.setVisibility(View.VISIBLE);
                binding.nativeAds.setVisibility(View.GONE);
                loadBannerAd();
                binding.btnDone.setOnClickListener(view -> {
                    SystemUtils.setPreLanguage(LanguageActivity.this, codeLang);
                    startActivity(new Intent(LanguageActivity.this, MainActivity.class));
                    finishAffinity();
                });
            }
        }

        initListLanguage();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        LanguageAdapter languageAdapter = new LanguageAdapter(listLanguage, new LanguageAdapter.ILanguageItem() {
            @Override
            public void onClickItemLanguage(String code) {
                codeLang = code;
            }
        }, this);
        String codeLangDefault = SystemUtils.getPreLanguage(getBaseContext());
        String[] langDefault = {"en", "hi", "es", "fr", "pt", "id", "de"};
        if (!Arrays.asList(langDefault).contains(codeLangDefault)) codeLang = "en";

        languageAdapter.setCheck(codeLangDefault);
        binding.rvLanguageStart.setLayoutManager(linearLayoutManager);
        binding.rvLanguageStart.setAdapter(languageAdapter);

    }

    @Override
    protected void viewListener() {

    }

    @Override
    protected void dataObservable() {
    }

    private void initListLanguage() {
        codeLang = Locale.getDefault().getLanguage();
        listLanguage = new ArrayList<>();
        String lang = Locale.getDefault().getLanguage();
        listLanguage.add(new LanguageModel(getString(R.string.english), "en"));
        listLanguage.add(new LanguageModel(getString(R.string.hindi), "hi"));
        listLanguage.add(new LanguageModel(getString(R.string.spanish), "es"));
        listLanguage.add(new LanguageModel(getString(R.string.french), "fr"));
        listLanguage.add(new LanguageModel(getString(R.string.portuguese), "pt"));
        listLanguage.add(new LanguageModel(getString(R.string.indonesian), "id"));
        listLanguage.add(new LanguageModel(getString(R.string.german), "de"));

        for (int i = 0; i < listLanguage.size(); i++) {
            if (listLanguage.get(i).getCode().equals(lang)) {
                listLanguage.add(0, listLanguage.get(i));
                listLanguage.remove(i + 1);
            }
        }
    }

    private void loadNativeAd() {
        List<String> listID = new ArrayList<>();
        listID.add(getString(R.string.native_language_1));
        listID.add(getString(R.string.native_language_2));
        listID.add(getString(R.string.native_language_3));

        Admob.getInstance().loadNativeAd(this, getString(R.string.native_all), binding.nativeAds, com.lvt.ads.R.layout.ads_native_btn_ads_bot);
    }

    private void loadBannerAd() {
        Admob.getInstance().loadBanner(this,getString(R.string.banner_all));
    }



    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}