package org.alvarowau.apicontact.utils;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;

import androidx.activity.result.ActivityResultLauncher;
import androidx.core.content.ContextCompat;

public class PermissionUtils {


    public static boolean isReadContactsPermissionGranted(Context context) {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED;
    }

    public static boolean isWriteContactsPermissionGranted(Context context) {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_CONTACTS) == PackageManager.PERMISSION_GRANTED;
    }

    public static boolean isWriteExternalStoragePermissionGranted(Context context) {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    public static boolean isReadExternalStoragePermissionGranted(Context context) {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    // Métodos para solicitar permisos
    public static void requestReadContactsPermission(ActivityResultLauncher<String[]> launcher) {
        launcher.launch(new String[]{Manifest.permission.READ_CONTACTS});
    }

    public static void requestWriteContactsPermission(ActivityResultLauncher<String[]> launcher) {
        launcher.launch(new String[]{Manifest.permission.WRITE_CONTACTS});
    }

    public static void requestWriteExternalStoragePermission(ActivityResultLauncher<String[]> launcher) {
        launcher.launch(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE});
    }

    public static void requestReadExternalStoragePermission(ActivityResultLauncher<String[]> launcher) {
        launcher.launch(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE});
    }

    // Solicitar todos los permisos necesarios
    public static void requestAllPermissions(ActivityResultLauncher<String[]> launcher) {
        launcher.launch(new String[]{
                Manifest.permission.READ_CONTACTS,
                Manifest.permission.WRITE_CONTACTS,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
        });
    }
}
