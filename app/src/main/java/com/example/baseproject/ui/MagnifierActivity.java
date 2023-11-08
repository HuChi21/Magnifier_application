package com.example.baseproject.ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Size;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.camera.core.AspectRatio;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.core.content.ContextCompat;

import com.example.baseproject.R;
import com.example.baseproject.base.BaseActivity;
import com.example.baseproject.databinding.ActivityMagnifierBinding;
import com.google.common.util.concurrent.ListenableFuture;
import com.lvt.ads.callback.AdCallback;
import com.lvt.ads.util.Admob;
import com.lvt.ads.util.AppOpenManager;

import java.io.File;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;

public class MagnifierActivity extends BaseActivity<ActivityMagnifierBinding> {
    private static final int REQUEST_CODE = 11234;
    private CameraSelector cameraSelector;
    private ProcessCameraProvider cameraProvider;
    private Preview preview;
    private Camera camera;
    private ImageCapture imageCapture;

    int cameraFacing = CameraSelector.LENS_FACING_BACK;
    int currentZoom = 1;
    private static final float defaultScale = 1.0f; //default scale of cameraX camera.getCameraInfo().getZoomState().getValue().getZoomRatio()
    //    String[] permissions;
//    PermissionDialog dialog;
    Boolean isFullScreen = false;
    private File tempImage;


    @Override
    protected ActivityMagnifierBinding setViewBinding() {
        return ActivityMagnifierBinding.inflate(LayoutInflater.from(this));
    }

    @Override
    protected void initView() {
        checkPermission();
        initToolbar();
        loadBannerAd();
    }



    @Override
    protected void viewListener() {
        actionView();
    }

    @Override
    protected void dataObservable() {

    }

    private void initToolbar() {
        binding.btnBack.setOnClickListener(view -> onBackPressed());
    }

    private void checkPermission() {
//        permissions = new String[]{
//                Manifest.permission.CAMERA,
//                Manifest.permission.READ_EXTERNAL_STORAGE,
//                Manifest.permission.WRITE_EXTERNAL_STORAGE,
//        };
        if (ContextCompat.checkSelfPermission(MagnifierActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, REQUEST_CODE);

        } else {
            initCam(cameraFacing);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initCam(cameraFacing);
            } else {
                startActivity(new Intent(getApplicationContext(), PermissionActivity.class));
                finish();
            }
        }
    }

    public void initCam(int cameraFacing) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int screenWidth = displayMetrics.widthPixels;
        int screenHeight = displayMetrics.heightPixels;

        ListenableFuture<ProcessCameraProvider> listenableFuture = ProcessCameraProvider.getInstance(this);
        listenableFuture.addListener(() -> {
            try {
                cameraProvider = (ProcessCameraProvider) listenableFuture.get();
                cameraSelector = new CameraSelector.Builder()
                        .requireLensFacing(cameraFacing)
                        .build();
                preview = new Preview.Builder()
                        .setTargetAspectRatio(AspectRatio.RATIO_16_9)
                        .build();
                imageCapture = new ImageCapture.Builder()
                        .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                        .setTargetRotation(getWindowManager().getDefaultDisplay().getRotation())
                        .setTargetResolution(new Size(screenWidth, screenHeight))
                        .build();

                cameraProvider.unbindAll();
                binding.previewView.setVisibility(View.INVISIBLE);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        binding.previewView.setVisibility(View.VISIBLE);
                    }
                },200);
                camera = cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture);

                actionCamFlip();
                actionFlashlight();
                actionZoom();
                actionBrightness();
                actionTakingPicture();
                preview.setSurfaceProvider(binding.previewView.getSurfaceProvider());

            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        }, ContextCompat.getMainExecutor(this));
    }

    private void actionCamFlip() {
        binding.btnCamFlip.setOnClickListener(view -> {
            cameraProvider.unbindAll();
            if (cameraFacing == CameraSelector.LENS_FACING_BACK) {
                cameraFacing = CameraSelector.LENS_FACING_FRONT;
                Drawable drawable = getResources().getDrawable(R.drawable.icon_flash_off);
                binding.btnFlashLight.setCompoundDrawablesWithIntrinsicBounds(null, drawable, null, null);
            } else {
                cameraFacing = CameraSelector.LENS_FACING_BACK;
            }
            binding.previewView.setVisibility(View.INVISIBLE);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    binding.previewView.setVisibility(View.VISIBLE);
                }
            },200);
            cameraSelector = new CameraSelector.Builder()
                    .requireLensFacing(cameraFacing)
                    .build();
            camera = cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture);
        });

    }

    private void actionFlashlight() {
        binding.btnFlashLight.setOnClickListener(view -> {
            if (camera.getCameraInfo().hasFlashUnit()) {
                if (camera.getCameraInfo().getTorchState().getValue() == 0) {
                    camera.getCameraControl().enableTorch(true);
                    Drawable drawable = getResources().getDrawable(R.drawable.icon_flash_on);
                    binding.btnFlashLight.setCompoundDrawablesWithIntrinsicBounds(null, drawable, null, null);
                } else {
                    camera.getCameraControl().enableTorch(false);
                    Drawable drawable = getResources().getDrawable(R.drawable.icon_flash_off);
                    binding.btnFlashLight.setCompoundDrawablesWithIntrinsicBounds(null, drawable, null, null);
                }
            } else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MagnifierActivity.this, getString(R.string.flash_is_not_available_currently), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void actionView() {
        binding.btnShow.setOnClickListener(view -> {
            if (!isFullScreen) {
                binding.layoutCamera.setVisibility(View.GONE);
                binding.btnShow.setImageResource(R.drawable.icon_view_less);
                isFullScreen = true;
            } else {
                binding.layoutCamera.setVisibility(View.VISIBLE);
                binding.btnShow.setImageResource(R.drawable.icon_view_more);
                isFullScreen = false;
            }
        });
    }

    private void actionBrightness() {
//        int currentEV = camera.getCameraInfo().getExposureState().getExposureCompensationIndex();
//        Log.d("aaa",currentEV+"");
        binding.seekBarBrightness.setProgress(50); // EV is currently equal 0
        binding.seekBarBrightness.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                float minValue = -10f; // min Ev
                float maxValue = 10f; // max Ev
                float value = minValue + (maxValue - minValue) * (progress / 100.0f);
                camera.getCameraControl().setExposureCompensationIndex((int) value);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }

    private void actionZoom() {
        binding.txtZoomScale.setOnClickListener(view -> {
            if (currentZoom == 4) {
                currentZoom = 1;
                binding.seekBarZoom.setProgress(0);
                camera.getCameraControl().setZoomRatio(defaultScale);
            } else {
                currentZoom++;
                float progress = (currentZoom - 1) * 100 / 3;
                binding.seekBarZoom.setProgress((int) progress);
                camera.getCameraControl().setZoomRatio(defaultScale * currentZoom);
            }

        });
        binding.seekBarZoom.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                int zoom = (progress - 1) / 25 + 1;
                binding.txtZoomScale.setText("x" + zoom);
                float scale = defaultScale * (progress * 3 + 100) / 100;
                camera.getCameraControl().setZoomRatio(scale);

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }

    public void actionTakingPicture() {
        binding.btnCamCapture.setOnClickListener(view -> {
            tempImage = new File(getExternalCacheDir(), "temp_image.jpg");
            ImageCapture.OutputFileOptions outputFileOptions = new ImageCapture.OutputFileOptions.Builder(tempImage).build();
            imageCapture.takePicture(outputFileOptions, Executors.newCachedThreadPool(), new ImageCapture.OnImageSavedCallback() {
                @Override
                public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Intent intent = new Intent(MagnifierActivity.this, PreviewCaptureActivity.class);
                            intent.putExtra("imagePath", tempImage.getPath());
                            startActivity(intent);
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    setDefault(camera);
                                }
                            }, 1000);

                        }
                    });
                }

                @Override
                public void onError(@NonNull ImageCaptureException exception) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MagnifierActivity.this, "Failed to save: " + exception.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                    initCam(cameraFacing);
                }
            });
        });
    }

    private void setDefault(Camera camera) {
        //turn off flash
        camera.getCameraControl().enableTorch(false);
        Drawable drawable = getResources().getDrawable(R.drawable.icon_flash_off);
        binding.btnFlashLight.setCompoundDrawablesWithIntrinsicBounds(null, drawable, null, null);
        //reset default zoom
        camera.getCameraControl().setZoomRatio(defaultScale);
        binding.seekBarZoom.setProgress(0);
        binding.txtZoomScale.setText("x1");
        //reset default brightness
        camera.getCameraControl().setExposureCompensationIndex((int) 0);
        binding.seekBarBrightness.setProgress(50);
    }
    private void loadBannerAd() {
        Admob.getInstance().loadBanner(this,getString(com.lvt.ads.R.string.ads_test_banner));
    }
    @Override
    protected void onResume() {
        super.onResume();
        binding.previewView.setVisibility(View.INVISIBLE);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                binding.previewView.setVisibility(View.VISIBLE);
            }
        },200);
    }

    @Override
    protected void onStop() {
        super.onStop();

    }
}