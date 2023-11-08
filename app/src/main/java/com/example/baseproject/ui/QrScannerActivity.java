package com.example.baseproject.ui;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.DecodeCallback;
import com.budiyev.android.codescanner.ScanMode;
import com.example.baseproject.R;
import com.example.baseproject.base.BaseActivity;
import com.example.baseproject.databinding.ActivityQrScannerBinding;
import com.google.zxing.Result;
import com.lvt.ads.callback.AdCallback;
import com.lvt.ads.util.AppOpenManager;

public class QrScannerActivity extends BaseActivity<ActivityQrScannerBinding> {
    private static final int REQUEST_CODE = 123;
    private CodeScanner mCodeScanner;

    @Override
    protected ActivityQrScannerBinding setViewBinding() {
        return ActivityQrScannerBinding.inflate(LayoutInflater.from(this));
    }

    @Override
    protected void initView() {
        checkPermission();
        initToolbar();

    }

    @Override
    protected void viewListener() {

    }

    @Override
    protected void dataObservable() {

    }

    private void initToolbar() {
        RelativeLayout toolbar = findViewById(R.id.toolbarQr);
        toolbar.setBackgroundColor(Color.parseColor("#70141414"));
        binding.toolbarQr.btnBack.setOnClickListener(view -> onBackPressed());
        binding.toolbarQr.btnBack.setImageTintList(ColorStateList.valueOf(Color.WHITE));
        binding.toolbarQr.txtToolbarTitle.setTextColor(Color.WHITE);
        binding.toolbarQr.txtToolbarTitle.setText(getString(R.string.qr_scan));
    }

    private void checkPermission() {
        if (ContextCompat.checkSelfPermission(QrScannerActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            initScanner();
        } else {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initScanner();
            } else {
                startActivity(new Intent(getApplicationContext(), PermissionActivity.class));
                finish();
            }
        }
    }

    private void initScanner() {
        mCodeScanner = new CodeScanner(this, binding.scannerView);
        mCodeScanner.setDecodeCallback(new DecodeCallback() {
            @Override
            public void onDecoded(@NonNull final Result result) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mCodeScanner.stopPreview();
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                    v.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
                                } else {
                                    // deprecated in API 26
                                    v.vibrate(500);
                                }
                                binding.scannerView.setMaskVisible(false);
                                binding.layoutQrInstruc.setVisibility(View.GONE);
                                showResultDialog(result.getText());
                            }
                        }, 500);
                    }

                });
            }
        });
        binding.scannerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                actionContinuePreview();
            }
        });
    }

    private void actionContinuePreview() {
        if (mCodeScanner != null) {
            mCodeScanner.startPreview();
            binding.scannerView.setMaskVisible(true);
            binding.layoutQrInstruc.setVisibility(View.VISIBLE);
        }
    }

    private void showResultDialog(String result) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_qr_result);

        Window window = dialog.getWindow();
        if (window == null) {
            return;
        }
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager.LayoutParams windowAttribute = window.getAttributes();
        windowAttribute.gravity = Gravity.CENTER;
        window.setAttributes(windowAttribute);

        TextView txtResult = dialog.findViewById(R.id.txtResult);
        ImageView btnCopy = dialog.findViewById(R.id.btnCopy);
        ImageView btnClose = dialog.findViewById(R.id.btnClose);

        btnClose.setOnClickListener(view1 -> {
            dialog.dismiss();
            actionContinuePreview();
        });

        txtResult.setText(result);
        btnCopy.setOnClickListener(view1 -> {
            ClipboardManager clipboard = (ClipboardManager)getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("label", result);
            if (clipboard != null) {
                clipboard.setPrimaryClip(clip);
                btnCopy.setImageResource(R.drawable.icon_check);
                btnCopy.setImageTintList(ColorStateList.valueOf(Color.parseColor("#1CCF7A")));
                btnCopy.setClickable(false);
            }
        });
        dialog.setCancelable(false);
        dialog.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mCodeScanner != null) {
            mCodeScanner.startPreview();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mCodeScanner != null) {
            mCodeScanner.releaseResources();
        }
    }
}