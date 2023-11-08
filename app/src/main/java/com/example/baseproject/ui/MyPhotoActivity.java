package com.example.baseproject.ui;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.app.Dialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.core.os.LocaleListCompat;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.baseproject.R;
import com.example.baseproject.adapter.MyPhotoAdapter;
import com.example.baseproject.base.BaseActivity;
import com.example.baseproject.databinding.ActivityMyPhotoBinding;
import com.example.baseproject.interfaces.PhotoClickListener;
import com.example.baseproject.model.MyPhotoModel;
import com.lvt.ads.callback.AdCallback;
import com.lvt.ads.util.Admob;
import com.lvt.ads.util.AppOpenManager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MyPhotoActivity extends BaseActivity<ActivityMyPhotoBinding> {
    final static int REQUEST_CODE = 123;
    List<MyPhotoModel> imagePathList;
    MyPhotoAdapter myPhotoAdapter;

    @Override
    protected ActivityMyPhotoBinding setViewBinding() {
        return ActivityMyPhotoBinding.inflate(LayoutInflater.from(this));
    }

    @Override
    protected void initView() {
        initToolbar();
        initMyPhoto();
        loadBannerAd();
    }


    @Override
    protected void viewListener() {

    }

    @Override
    protected void dataObservable() {

    }

    private void initToolbar() {
        binding.toolbarMyPhoto.txtToolbarTitle.setText(R.string.my_photo);
        binding.toolbarMyPhoto.txtToolbarTitle.setTextColor(Color.parseColor("#4D4D4D"));
        binding.toolbarMyPhoto.btnBack.setImageTintList(ColorStateList.valueOf(Color.parseColor("#4D4D4D")));
        binding.toolbarMyPhoto.btnBack.setOnClickListener(view -> {
            onBackPressed();
        });

    }

    private void initMyPhoto() {
        binding.recyclerMyPhoto.setLayoutManager(new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL));
        binding.recyclerMyPhoto.setHasFixedSize(true);

        imagePathList = new ArrayList<>();

        File directory = getExternalFilesDir(null);
        if (directory != null) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    String imagePath = file.getAbsolutePath();
                    MyPhotoModel model = new MyPhotoModel(imagePath, false);
                    imagePathList.add(model);
                }
            }
        }
        if(imagePathList.size()>0){
            binding.recyclerMyPhoto.setVisibility(View.VISIBLE);
            binding.txtNoPhoto.setVisibility(View.INVISIBLE);
        }else{
            binding.recyclerMyPhoto.setVisibility(View.INVISIBLE);
            binding.txtNoPhoto.setVisibility(View.VISIBLE);
        }
        myPhotoAdapter = new MyPhotoAdapter(MyPhotoActivity.this, imagePathList, new PhotoClickListener() {
            @Override
            public void isSelect(Boolean b) {
                if (b) {
                    binding.layoutPhoto.setVisibility(View.VISIBLE);
                    binding.btnSelectAll.setVisibility(View.VISIBLE);
                } else {
                    binding.layoutPhoto.setVisibility(View.GONE);
                    binding.btnSelectAll.setVisibility(View.GONE);
                }
            }

            @Override
            public void onImageClick(String imagePath) {
                Intent intent = new Intent(getApplicationContext(), PreviewPhotoActivity.class);
                intent.putExtra("imagePath",imagePath);
                intent.putExtra("isPreview",false);
                startActivityForResult(intent, REQUEST_CODE);
            }
        });
        binding.recyclerMyPhoto.setAdapter(myPhotoAdapter);
        binding.recyclerMyPhoto.setItemAnimator(null);

        binding.btnDelete.setOnClickListener(view -> deleteItem());
        binding.btnShare.setOnClickListener(view -> shareItem());
        binding.btnSelectAll.setOnClickListener(view -> myPhotoAdapter.selectAll());
    }

    private void deleteItem() {
        final Dialog dialog = new Dialog(MyPhotoActivity.this);
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
            myPhotoAdapter.selectList.clear();
            binding.layoutPhoto.setVisibility(View.GONE);
            binding.btnSelectAll.setVisibility(View.GONE);
            myPhotoAdapter.notifyDataSetChanged();
            dialog.dismiss();
        });
        dialog.findViewById(R.id.btnDeleteAgree).setOnClickListener(view1 -> {
            for (int i = 0; i < myPhotoAdapter.selectList.size(); i++) {
                new File(myPhotoAdapter.selectList.get(i).getPhotoPath()).delete();
                imagePathList.remove(myPhotoAdapter.selectList.get(i));
            }
            myPhotoAdapter.selectList.clear();
            binding.layoutPhoto.setVisibility(View.GONE);
            binding.btnSelectAll.setVisibility(View.GONE);
            myPhotoAdapter.notifyDataSetChanged();
            dialog.dismiss();
        });
        dialog.setCancelable(true);
        dialog.show();
    }

    private void shareItem() {
        if(myPhotoAdapter.selectList.size()>=2){
            Toast.makeText(this, getString(R.string.cant_share), Toast.LENGTH_SHORT).show();
        }else{
            Intent sharingIntent = new Intent(Intent.ACTION_SEND);
            File file = new File(myPhotoAdapter.selectList.get(0).getPhotoPath());
            Uri uri = FileProvider.getUriForFile(getApplicationContext(), getApplicationContext().getPackageName() + ".provider", file);
            Log.d(TAG, "shareItem: "+uri);
            sharingIntent.setType("application/image");
            sharingIntent.putExtra(Intent.EXTRA_STREAM, uri);
            sharingIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(Intent.createChooser(sharingIntent, "Share image using"));
            myPhotoAdapter.selectList.clear();
            binding.layoutPhoto.setVisibility(View.GONE);
            binding.btnSelectAll.setVisibility(View.GONE);
            myPhotoAdapter.notifyDataSetChanged();
        }

    }
    private void loadBannerAd() {
        Admob.getInstance().loadBanner(this,getString(com.lvt.ads.R.string.ads_test_banner));
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE && resultCode == RESULT_OK){
            myPhotoAdapter.notifyDataSetChanged();
        }
    }
}