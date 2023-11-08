package com.example.baseproject.utils;

import android.content.Context;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;

public class PermissionUtils {
    public static boolean checkPermission(String permission, Context context) {
        if (context != null) {
            return ActivityCompat.checkSelfPermission(
                    context,
                    permission
            ) == PackageManager.PERMISSION_GRANTED;
        }
        return false;
    }

}
