package com.kanedias.vanilla.plugins.saf;

import android.os.Build;
import android.support.annotation.Nullable;
import android.support.v4.provider.DocumentFile;

import java.io.File;
import java.util.List;

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

    /**
     * Finds needed file through Document API for SAF. It's not optimized yet - you can still gain wrong URI on
     * files such as "/a/b/c.mp3" and "/b/a/c.mp3", but I consider it complete enough to be usable.
     * @param currentDir - document file representing current dir of search
     * @param remainingPathSegments - path segments that are left to find
     * @return Document file representing a target
     */
    @Nullable
    public static DocumentFile findInDocumentTree(DocumentFile currentDir, List<String> remainingPathSegments) {
        for (DocumentFile file : currentDir.listFiles()) {
            int index = remainingPathSegments.indexOf(file.getName());
            if (index == -1) {
                continue;
            }

            if (file.isDirectory()) {
                remainingPathSegments.remove(file.getName());
                return findInDocumentTree(file, remainingPathSegments);
            }

            if (file.isFile() && index == remainingPathSegments.size() - 1) {
                // got to the last part
                return file;
            }
        }

        return null;
    }
}
