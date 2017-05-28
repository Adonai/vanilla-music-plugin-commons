package com.kanedias.vanilla.plugins.saf;

import android.os.Build;

import java.io.File;

/**
 * Utility class for SAF-related routines
 *
 * @author Oleg Chernovskiy
 */

public class SafUtils {

    /**
     * Check if Android Storage Access Framework routines apply here
     * @return true if document seems to be SAF-accessible only, false otherwise
     */
    public static boolean isSafNeeded(File file) {
        // on external SD card after KitKat this will return false
        return !file.canWrite() && Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
    }
}
