package com.kanedias.vanilla.plugins;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

/**
 * Common routines for all plugins
 *
 * @author Oleg `Kanedias` Chernovskiy
 */
public class PluginUtils {

    public static final int PERMISSIONS_REQUEST_CODE = 0;

    /**
     * Checks for permission and requests it if needed.
     * You should catch answer back in {@link Activity#onRequestPermissionsResult(int, String[], int[])}
     * <br/>
     * (Or don't. This way request will appear forever as {@link Activity#onResume()} will never end)
     *
     * @param perm permission to request
     * @return true if this app had this permission prior to check, false otherwise.
     */
    public static boolean checkAndRequestPermissions(Activity ctx, String perm) {
        if (!havePermissions(ctx, perm) && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ctx.requestPermissions(new String[]{perm}, PERMISSIONS_REQUEST_CODE);
            return false;
        }
        return true;
    }

    /**
     * Checks if all required permissions have been granted
     *
     * @param context The context to use
     * @return boolean true if all permissions have been granted
     */
    public static boolean havePermissions(Context context, String perm) {
        // else: granted during installation
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                context.checkSelfPermission(perm) == PackageManager.PERMISSION_GRANTED;
    }
}
