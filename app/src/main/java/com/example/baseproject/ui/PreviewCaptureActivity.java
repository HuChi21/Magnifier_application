package com.example.baseproject.ui;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.FileProvider;

import com.example.baseproject.R;
import com.example.baseproject.base.BaseActivity;
import com.example.baseproject.databinding.ActivityPreviewCaptureBinding;
import com.example.baseproject.model.MyPhotoModel;
import com.lvt.ads.callback.AdCallback;
import com.lvt.ads.util.Admob;
import com.lvt.ads.util.AppOpenManager;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class PreviewCaptureActivity extends BaseActivity<ActivityPreviewCaptureBinding> {
    String tempPath;
    private Bitmap rotatedBitmap;
    private File tempFile;

    @Override
    protected ActivityPreviewCaptureBinding setViewBinding() {
        return ActivityPreviewCaptureBinding.inflate(LayoutInflater.from(this));
    }

    @Override
    protected void initView() {
        initToolbar();
        loadBannerAd();
        getPreviewImage();
    }

    @Override
    protected void viewListener() {
        actionShareImage();
        binding.btnSave.setOnClickListener(view -> {
            openCreateNameDialog(Uri.fromFile(tempFile));
        });
        actionCrop();
    }


    @Override
    protected void dataObservable() {

    }

    private void initToolbar() {
        binding.btnBack.setOnClickListener(view -> {
            //delete temporary image
            File[] cacheFiles = getExternalCacheDir().listFiles();
            if (cacheFiles != null) {
                for (File file : cacheFiles) {
                    {
                        file.delete();
                    }
                }
            }
            onBackPressed();
        });

    }

    private void getPreviewImage() {
        tempPath = getIntent().getStringExtra("imagePath");
        tempFile = new File(tempPath);
        try {
            ExifInterface exif = new ExifInterface(tempPath);
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

            Bitmap bitmap = BitmapFactory.decodeFile(tempPath);
            rotatedBitmap = Bitmap.createBitmap(bitmap,
                    0, 0,
                    bitmap.getWidth(), bitmap.getHeight(),
                    matrix,
                    true);
            binding.imgPreview.setImageBitmap(rotatedBitmap);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void actionCrop() {
        Uri temp_image = Uri.fromFile(tempFile);
        Uri uCrop_image = Uri.fromFile(new File(getExternalCacheDir(), "uCrop_image.jpg"));
        binding.btnCrop.setOnClickListener(view -> {
            UCrop.of(temp_image, uCrop_image)
                    .withAspectRatio(9, 16)
                    .withMaxResultSize(1080, 1920)
                    .start(this);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == UCrop.REQUEST_CROP && resultCode == RESULT_OK) {

            binding.imgPreview.setImageURI(UCrop.getOutput(data));
            openCreateNameDialog(UCrop.getOutput(data));

        } else if (resultCode == UCrop.RESULT_ERROR) {
            Throwable error = UCrop.getError(data);
            Log.d(TAG, "onActivityResult: " + error);
        }
    }

    private void actionShareImage() {
        binding.btnShare.setOnClickListener(view -> {
            if (tempFile.exists()) {
                Intent shareIntent = new Intent(Intent.ACTION_SEND);

                Uri uri = FileProvider.getUriForFile(getApplicationContext(), getApplicationContext().getPackageName() + ".provider", tempFile);
                shareIntent.setType("application/image");
                shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
                startActivity(Intent.createChooser(shareIntent, "Share image"));

            }
        });

    }


    private void openCreateNameDialog(Uri uri) {
        final Dialog dialog = new Dialog(PreviewCaptureActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_create_name);

        Window window = dialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager.LayoutParams windowAttribute = window.getAttributes();
        windowAttribute.gravity = Gravity.CENTER;
        window.setAttributes(windowAttribute);

        TextView btnCreateNameAgree = dialog.findViewById(R.id.btnCreateNameAgree);
        EditText edtImageName = dialog.findViewById(R.id.edtImageName);
        edtImageName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() > 0) {
                    dialog.findViewById(R.id.btnClearText).setVisibility(View.VISIBLE);
                    dialog.findViewById(R.id.btnClearText).setOnClickListener(view -> {
                        edtImageName.setText("");
                    });
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        dialog.findViewById(R.id.btnDecline).setOnClickListener(view1 -> {
            dialog.dismiss();
            binding.imgPreview.setImageURI(Uri.fromFile(tempFile));
        });
        btnCreateNameAgree.setOnClickListener(view1 -> {
            closekeyboard();
            String pattern = "^[a-zA-Z0-9_-]{1,25}$";
            String name = edtImageName.getText().toString().trim();
            if (!name.matches(pattern)) {
                edtImageName.requestFocus();
                Toast.makeText(this, getString(R.string.invalid_image_name) + name, Toast.LENGTH_SHORT).show();
            } else if (checkImageName(name)) {
                edtImageName.requestFocus();
                Toast.makeText(this, name+" "+getString(R.string.is_already_taken), Toast.LENGTH_SHORT).show();
            } else {
                String fileName = name + ".jpg";
                File newImageFile = new File(getExternalFilesDir(null), fileName);
                try {
                    FileOutputStream fos = new FileOutputStream(newImageFile);
                    InputStream inputStream = getContentResolver().openInputStream(uri);

                    byte[] buffer = new byte[4096];
                    int bytesRead;
                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        fos.write(buffer, 0, bytesRead);
                    }
                    fos.close();
                    inputStream.close();

                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                dialog.dismiss();

                tempFile.delete();
                File a = new File(getExternalFilesDir(null), fileName);

                Intent intent = new Intent(PreviewCaptureActivity.this, PreviewPhotoActivity.class);
                intent.putExtra("imagePath", a.getPath());
                intent.putExtra("isPreview", true);

                startActivity(intent);
                finish();

            }
        });
        dialog.setCancelable(false);
        dialog.show();
    }

    private boolean checkImageName(String newName) {
        File directory = getExternalFilesDir(null);
        if (directory != null) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    String fileName = file.getName().replaceFirst("[.][^.]+$", "");
                    if (fileName.equalsIgnoreCase(newName)) {
                        Log.d(TAG, "checkImageName: "+true);
                        return true;

                    }
                }
            }
        }
        Log.d(TAG, "checkImageName: "+"false2");
        return false;
    }

    private void closekeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
    private void loadBannerAd() {
        Admob.getInstance().loadBanner(this,getString(com.lvt.ads.R.string.ads_test_banner));
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}