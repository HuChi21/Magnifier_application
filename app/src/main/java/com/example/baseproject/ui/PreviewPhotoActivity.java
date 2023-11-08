package com.example.baseproject.ui;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.core.content.FileProvider;

import com.bumptech.glide.Glide;
import com.example.baseproject.R;
import com.example.baseproject.base.BaseActivity;
import com.example.baseproject.databinding.ActivityPreviewPhotoBinding;
import com.example.baseproject.dialog.PermissionDialog;
import com.lvt.ads.callback.AdCallback;
import com.lvt.ads.util.Admob;
import com.lvt.ads.util.AppOpenManager;

import java.io.File;
import java.io.IOException;

public class PreviewPhotoActivity extends BaseActivity<ActivityPreviewPhotoBinding> {
    private String previewPath;
    private File imageFile;

    @Override
    protected ActivityPreviewPhotoBinding setViewBinding() {
        return ActivityPreviewPhotoBinding.inflate(LayoutInflater.from(this));
    }

    @Override
    protected void initView() {
        loadBannerAd();
        getPreviewImage();
        initToolbar();

    }

    @Override
    protected void viewListener() {
        actionDeleteImage();
        actionShareImage();
    }

    @Override
    protected void dataObservable() {

    }

    private void initToolbar() {
        imageFile = new File(previewPath);
        String imageName = imageFile.getName().replaceFirst("[.][^.]+$", "");
        binding.toolbarPreview.btnBack.setColorFilter(Color.parseColor("#4D4D4D"));
        binding.toolbarPreview.txtToolbarTitle.setText(imageName);
        binding.toolbarPreview.txtToolbarTitle.setTextColor(Color.parseColor("#4D4D4D"));
        binding.toolbarPreview.btnBack.setOnClickListener(view -> {
            onBackPressed();
        });
    }

    private void getPreviewImage() {
        previewPath = getIntent().getStringExtra("imagePath");
        Log.d("TAG", "getPreviewImage: "+previewPath);
        try {
            ExifInterface exif = new ExifInterface(previewPath);
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

            Matrix matrix = new Matrix();
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    matrix.postRotate(90);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    matrix.postRotate(180);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    matrix.postRotate(270);
                    break;
                case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
                    matrix.preScale(-1, 1);
                    break;
                case ExifInterface.ORIENTATION_FLIP_VERTICAL:
                    matrix.preScale(1, -1);
                    break;
            }

            Bitmap originalBitmap = BitmapFactory.decodeFile(previewPath);
            Bitmap rotatedBitmap = Bitmap.createBitmap(originalBitmap,
                    0, 0,
                    originalBitmap.getWidth(), originalBitmap.getHeight(),
                    matrix,
                    true);
            binding.imgPreview.setImageBitmap(rotatedBitmap);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void actionDeleteImage() {
        binding.btnDelete.setOnClickListener(view -> {
            final Dialog dialog = new Dialog(PreviewPhotoActivity.this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.dialog_delete_image);

            Window window = dialog.getWindow();
            if (window == null) {
                return;
            }
            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            WindowManager.LayoutParams windowAttribute = window.getAttributes();
            windowAttribute.gravity = Gravity.CENTER;
            window.setAttributes(windowAttribute);

            dialog.findViewById(R.id.btnDecline).setOnClickListener(view1 -> {
                dialog.dismiss();
            });
            dialog.findViewById(R.id.btnDeleteAgree).setOnClickListener(view1 -> {
                dialog.dismiss();
                imageFile.delete();
                boolean isPreview = getIntent().getBooleanExtra("isPreview",true);
                if(isPreview)onBackPressed();
                else{
                    Intent resultIntent = new Intent();
                    setResult(RESULT_OK, resultIntent);
                    finish();

                }
            });
            dialog.setCancelable(true);
            dialog.show();

        });
    }
    private void loadBannerAd() {
        Admob.getInstance().loadBanner(this,getString(com.lvt.ads.R.string.ads_test_banner));
    }
    private void actionShareImage() {
        binding.btnShare.setOnClickListener(view -> {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("application/image");
            File file = new File(previewPath);
            Uri uri = FileProvider.getUriForFile(getApplicationContext(), getApplicationContext().getPackageName() + ".provider", file);
            shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
            startActivity(Intent.createChooser(shareIntent, "Share image"));
        });
    }

}