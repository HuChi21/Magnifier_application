package com.example.baseproject.ui;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatRatingBar;

import com.example.baseproject.R;
import com.example.baseproject.base.BaseActivity;
import com.example.baseproject.databinding.ActivityMainBinding;
import com.example.baseproject.model.RatingModel;
import com.example.baseproject.utils.SharePrefUtils;
import com.example.baseproject.utils.adUtil;
import com.lvt.ads.callback.InterCallback;
import com.lvt.ads.util.Admob;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity<ActivityMainBinding>{

    private float ratingValue = 0;
    SharedPreferences sharedPreferences ;
    boolean isRated,startFirst;


    @Override
    protected ActivityMainBinding setViewBinding() {
        return ActivityMainBinding.inflate(LayoutInflater.from(this));
    }

    @Override
    protected void initView() {
        if(adUtil.interAd ==null){
            adUtil.loadInterAll(MainActivity.this);
        }

        sharedPreferences = getSharedPreferences("data", Context.MODE_PRIVATE);
        startFirst = sharedPreferences.getBoolean(SharePrefUtils.KEY_STARTFIRST,true);
        isRated = sharedPreferences.getBoolean(SharePrefUtils.KEY_RATED, false);
        if(startFirst){
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(SharePrefUtils.KEY_STARTFIRST,false);
            editor.apply();
        }
        initToolbar();
        loadNativeAd();
    }


    @Override
    protected void viewListener() {
        binding.layoutMagnifier.setOnClickListener(view -> openActivity(MagnifierActivity.class));
        binding.layoutQrscan.setOnClickListener(view -> openActivity(QrScannerActivity.class));
        binding.layoutMyPhoto.setOnClickListener(view -> openActivity(MyPhotoActivity.class));
    }

    @Override
    protected void dataObservable() {

    }

    private void initToolbar() {
        binding.imgAboutUs.setOnClickListener(view -> {
            startActivity(new Intent(MainActivity.this, AboutUsActivity.class));
            finish();
        });
        binding.btnSettings.setOnClickListener(view -> initSettings());
    }

    private void initSettings() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_settings);

        Window window = dialog.getWindow();
        if (window == null) {
            return;
        }

        window.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        WindowManager.LayoutParams windowAttributes = window.getAttributes();
        windowAttributes.gravity = Gravity.TOP | Gravity.RIGHT;

        windowAttributes.x = getResources().getDimensionPixelOffset(com.intuit.sdp.R.dimen._30sdp);
        windowAttributes.y = getResources().getDimensionPixelOffset(com.intuit.sdp.R.dimen._70sdp);
        window.setAttributes(windowAttributes);

        dialog.findViewById(R.id.btnLang).setOnClickListener(view -> startActivity(new Intent(getApplicationContext(), LanguageActivity.class).putExtra("isEditLang", true)));
        dialog.findViewById(R.id.btnRateUs).setOnClickListener(view -> {
            if(isRated){
                Toast.makeText(MainActivity.this,getString(R.string.you_have_already_vote),Toast.LENGTH_SHORT).show();
            }else{
                initRateus();
            }
            dialog.dismiss();
        });
        dialog.findViewById(R.id.btnShareApp).setOnClickListener(view -> {
            actionShareApp();
            dialog.dismiss();
        });
        dialog.findViewById(R.id.btnPrivacyPolicy).setOnClickListener(view -> {
            actionPrivacy();
            dialog.dismiss();
        });
        dialog.setCancelable(true);

        dialog.show();
    }

    private void initRateus() {
        final Dialog dialog = new Dialog(MainActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_rateus);

        Window window = dialog.getWindow();
        if (window == null) {
            return;
        }
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager.LayoutParams windowAttribute = window.getAttributes();
        windowAttribute.gravity = Gravity.CENTER;
        window.setAttributes(windowAttribute);

        AppCompatRatingBar ratingBar = dialog.findViewById(R.id.ratingBar);
        ImageView ratingIcon = dialog.findViewById(R.id.imgRateIcon);
        TextView ratingText = dialog.findViewById(R.id.txtRating);
        TextView ratingText2 = dialog.findViewById(R.id.txtRating2);
        List<RatingModel> list = new ArrayList<>();
        list.add(new RatingModel(R.drawable.icon_rate_default, getString(R.string.do_you_like_the_app), getString(R.string.let_us_know_your_experience)));
        list.add(new RatingModel(R.drawable.icon_rate_1, getString(R.string.oh_no), getString(R.string.please_give_us_some_feedback)));
        list.add(new RatingModel(R.drawable.icon_rate_2, getString(R.string.oh_no), getString(R.string.please_give_us_some_feedback)));
        list.add(new RatingModel(R.drawable.icon_rate_3, getString(R.string.oh_no), getString(R.string.please_give_us_some_feedback)));
        list.add(new RatingModel(R.drawable.icon_rate_4, getString(R.string.we_love_you_to0), getString(R.string.thanks_for_your_feedback)));
        list.add(new RatingModel(R.drawable.icon_rate_5, getString(R.string.we_love_you_to0), getString(R.string.thanks_for_your_feedback)));
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                ratingValue = v;

                RatingModel model = list.get((int) ratingValue);
                ratingIcon.setImageResource(model.getIcon());
                ratingText.setText(model.getTxt1());
                ratingText2.setText(model.getTxt2());

            }

        });
        dialog.findViewById(R.id.btnVote).setOnClickListener(view -> {
            if ((int) ratingValue != 0) {
                Toast.makeText(dialog.getContext(), getString(R.string.thanks_for_voting_us) + ":" + (int) ratingValue, Toast.LENGTH_SHORT).show();
                isRated = true;
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean(SharePrefUtils.KEY_RATED,isRated);
                editor.apply();
                dialog.dismiss();
//                        try {
//                            Intent rateIntent = new Intent(Intent.ACTION_VIEW);
//                            rateIntent.setData(Uri.parse("market://details?id=" + getPackageName()));
//                            startActivity(rateIntent);
//                        } catch (ActivityNotFoundException e) {
//                            Intent webRateIntent = new Intent(Intent.ACTION_VIEW);
//                            webRateIntent.setData(Uri.parse("https://play.google.com/store/apps/details?id=" + getPackageName()));
//                            startActivity(webRateIntent);
//                        }
            } else {
                Toast.makeText(dialog.getContext(), getString(R.string.please_select_your_voting), Toast.LENGTH_SHORT).show();
            }
        });
        dialog.findViewById(R.id.btnClose).setOnClickListener(view -> dialog.dismiss());

        dialog.setCancelable(false);
        dialog.show();
    }

    private void actionPrivacy() {
        Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.link_privacy)));
        startActivity(i);
    }

    private void actionShareApp() {
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("text/plain");
        i.putExtra(Intent.EXTRA_TEXT, getString(R.string.try_this_app) + " " + getString(R.string.link_share_app));
        startActivity(Intent.createChooser(i, "Share by"));
    }

    private void loadNativeAd(){
        List<String> listID = new ArrayList<>();
        listID.add(getString(com.lvt.ads.R.string.ads_test_native));
        Admob.getInstance().loadNativeAd(this, listID, binding.nativeAds, com.lvt.ads.R.layout.ads_native_btn_ads_bot);

        //Admob.getInstance().loadNativeAd(this, "id native", native_ads,R.layout.ads_native);
    }

    private void openActivity(Class<?> cls){
        Admob.getInstance().showInterAds(MainActivity.this, adUtil.interAd, new InterCallback() {
            @Override
            public void onNextAction() {
                startActivity(new Intent(MainActivity.this,cls));
                adUtil.loadInterAll(MainActivity.this);
            }});
    }


    @Override
    protected void onStop() {
        super.onStop();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
    }
}