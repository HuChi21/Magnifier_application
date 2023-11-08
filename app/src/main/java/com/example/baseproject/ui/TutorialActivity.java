package com.example.baseproject.ui;

import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;

import com.example.baseproject.R;
import com.example.baseproject.adapter.TutorialAdapter;
import com.example.baseproject.base.BaseActivity;
import com.example.baseproject.databinding.ActivityTutorialBinding;
import com.example.baseproject.model.TutorialModel;
import com.example.baseproject.utils.SharePrefUtils;
import com.lvt.ads.callback.AdCallback;
import com.lvt.ads.util.AppOpenManager;

import java.util.ArrayList;
import java.util.List;

public class TutorialActivity extends BaseActivity<ActivityTutorialBinding> {
    List<TutorialModel> tutorialModelList;
    @Override
    protected ActivityTutorialBinding setViewBinding() {
        return ActivityTutorialBinding.inflate(LayoutInflater.from(this));
    }

    @Override
    protected void initView() {
        SharedPreferences sharedPreferences = getSharedPreferences("data", Context.MODE_PRIVATE);
        boolean startFist = sharedPreferences.getBoolean(SharePrefUtils.KEY_STARTFIRST, true);
        if(!startFist){
            startActivity(new Intent(TutorialActivity.this, MainActivity.class));
            finish();
        }
        tutorialModelList = new ArrayList<>();
        tutorialModelList.add(new TutorialModel(R.drawable.image_tutor1,R.string.tutorial1));
        tutorialModelList.add(new TutorialModel(R.drawable.image_tutor2,R.string.tutorial2));
        tutorialModelList.add(new TutorialModel(R.drawable.image_tutor3,R.string.tutorial3));
        binding.viewPager.setAdapter(new TutorialAdapter(this,tutorialModelList));
        binding.viewPager.setClipToPadding(false);
        binding.viewPager.setClipChildren(false);
        binding.viewPager.setOffscreenPageLimit(3);
        binding.viewPager.getChildAt(0).setOverScrollMode(RecyclerView.OVER_SCROLL_ALWAYS);
        CompositePageTransformer compositePageTransformer = new CompositePageTransformer();
        compositePageTransformer.addTransformer(new MarginPageTransformer(100));
        compositePageTransformer.addTransformer((page, position) -> {
            float r = 1- Math.abs(position);
            page.setScaleY(0.8f+r*0.2f);
            float absPosition = Math.abs(position);
            page.setAlpha( 1.0f - ( 1.0f - 0.3f) * absPosition);
        });
        binding.pageIndicatorView.setCount(tutorialModelList.size());
        binding.viewPager.setPageTransformer(compositePageTransformer);
        binding.viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }

            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                binding.pageIndicatorView.setSelection(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);
            }
        });
    }

    @Override
    protected void viewListener() {
        binding.btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(binding.viewPager.getCurrentItem()<tutorialModelList.size()-1){
                    binding.viewPager.setCurrentItem(binding.viewPager.getCurrentItem()+1, true);
                }else{
                    Intent intent = new Intent(TutorialActivity.this,PermissionActivity.class);
                    startActivity(intent);
                    finish();
                }

            }
        });
    }

    @Override
    protected void dataObservable() {

    }

}