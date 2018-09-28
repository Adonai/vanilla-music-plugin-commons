package com.kanedias.vanilla.plugins;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (context.checkSelfPermission(perm) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        } // else: granted during installation
        return true;
    }

    /**
     * Reads an InputStream fully to byte array
     * @param stream stream to read from
     * @return resulting byte array, never null
     * @throws IOException if any error happens during read
     */
    public static byte[] readFully(InputStream stream) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[4096];
        int count;
        while ((count = stream.read(buffer)) != -1) {
            baos.write(buffer, 0, count);
        }

        stream.close();
        return baos.toByteArray();
    }
}
