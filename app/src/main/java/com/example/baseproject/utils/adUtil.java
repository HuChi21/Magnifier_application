package com.example.baseproject.utils;

import android.content.Context;

import com.example.baseproject.R;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.lvt.ads.callback.InterCallback;
import com.lvt.ads.util.Admob;

public class adUtil {
    public static InterstitialAd interAd =null;
    public static void loadInterAll(Context context){
        Admob.getInstance().loadInterAds(context, context.getString(R.string.inter_all), new InterCallback() {
            @Override
            public void onAdLoadSuccess(InterstitialAd interstitialAd) {
                super.onAdLoadSuccess(interstitialAd);
                adUtil.interAd = interstitialAd;
            }
        });
    }

}
