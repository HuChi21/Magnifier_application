package com.example.baseproject.ui;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.view.LayoutInflater;
import android.Manifest;

import com.example.baseproject.R;
import com.example.baseproject.base.BaseActivity;
import com.example.baseproject.databinding.ActivityPermissionBinding;
import com.example.baseproject.utils.PermissionUtils;
import com.example.baseproject.utils.SharePrefUtils;
import com.lvt.ads.callback.AdCallback;
import com.lvt.ads.util.AppOpenManager;

public class PermissionActivity extends BaseActivity<ActivityPermissionBinding> {
    String[] STORAGE_PERMISSION;
    String[] CAMERA_PERMISSION;
    private static final int STORAGE_REQUEST_CODE = 1;
    private static final int CAMERA_REQUEST_CODE = 2;

    @Override
    protected ActivityPermissionBinding setViewBinding() {
        return ActivityPermissionBinding.inflate(LayoutInflater.from(this));
    }

    @Override
    protected void initView() {
        binding.txtPermissonContent.setText("\"Magnifier - Magnifying Glass\" "+getString(R.string.permission_content));
        SharedPreferences sharedPreferences = getSharedPreferences("data", Context.MODE_PRIVATE);
        boolean startFist = sharedPreferences.getBoolean(SharePrefUtils.KEY_STARTFIRST, true);
        if(!startFist){
            startActivity(new Intent(PermissionActivity.this, MainActivity.class));
            finish();
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            STORAGE_PERMISSION = new String[]{
                    Manifest.permission.READ_MEDIA_IMAGES,
                    Manifest.permission.READ_MEDIA_VIDEO,
                    Manifest.permission.READ_MEDIA_AUDIO
            };
        } else {
            STORAGE_PERMISSION = new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            };
        }
        CAMERA_PERMISSION = new String[]{Manifest.permission.CAMERA};

        if (checkPermission(STORAGE_PERMISSION)) {
            binding.swPer.setImageResource(R.drawable.switch_on);
            binding.swPer.setClickable(false);
        }

        if (checkPermission(CAMERA_PERMISSION)) {
            binding.swPer2.setImageResource(R.drawable.switch_on);
            binding.swPer2.setClickable(false);
        }
//        if (checkPermission(CAMERA_PERMISSION) && checkPermission(STORAGE_PERMISSION)) {
//
//            startActivity(new Intent(getApplicationContext(), MainActivity.class));
//            finish();
//        }

    }

    @Override
    protected void viewListener() {
        binding.txtGo.setOnClickListener(v -> startActivity(new Intent(PermissionActivity.this, MainActivity.class)));
        binding.swPer.setOnClickListener(v -> {
            for (String per : STORAGE_PERMISSION) {
                if (!PermissionUtils.checkPermission(per, this)) {
                    ActivityCompat.requestPermissions(
                            this, STORAGE_PERMISSION, STORAGE_REQUEST_CODE);
                }
            }
        });
        binding.swPer2.setOnClickListener(v -> {
            for (String per : CAMERA_PERMISSION) {
                if (!PermissionUtils.checkPermission(per, this)) {
                    ActivityCompat.requestPermissions(
                            this, CAMERA_PERMISSION, CAMERA_REQUEST_CODE);
                }
            }
        });
//
    }

    @Override
    protected void dataObservable() {

    }

    private boolean checkPermission(String[] listPermission) {
        for (String per : listPermission) {
            boolean allow = ActivityCompat.checkSelfPermission(this, per) == PackageManager.PERMISSION_GRANTED;
            if (!allow) return false;
        }
        return true;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case STORAGE_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    binding.swPer.setImageResource(R.drawable.switch_on);
                } else {
                    binding.swPer.setImageResource(R.drawable.switch_off);
                }
                break;
            case CAMERA_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    binding.swPer2.setImageResource(R.drawable.switch_on);
                } else {
                    binding.swPer2.setImageResource(R.drawable.switch_off);
                }
                break;
        }
    }

}