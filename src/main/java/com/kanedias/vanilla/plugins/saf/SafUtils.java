package com.kanedias.vanilla.plugins.saf;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.provider.DocumentFile;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.kanedias.vanilla.plugins.PluginConstants.LOG_TAG;

/**
 * Utility class for SAF-related routines
 *
 * @author Oleg Chernovskiy
 */
public class SafUtils {

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

    /**
     * Check if Android Storage Access Framework routines apply here
     * @param file file to check permissions for
     * @param ctx context to use when resolving sdcard paths
     * @return true if document seems to be SAF-accessible only, false otherwise
     */
    public static boolean isSafNeeded(File file, Context ctx) {
        // on external SD card after KitKat this will return false
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && isOnExtSdCard(file, ctx);
    }

    /**
     * Determine if a file is on external sd card. (Kitkat or higher.)
     *
     * @param file The file
     * @param ctx context to use when resolving sdcard paths
     * @return true if on external sd card
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static boolean isOnExtSdCard(File file, Context ctx) {
        return getExtSdCardFolder(file, ctx) != null;
    }

    /**
     * Determine the main folder of the external SD card containing the given file.
     *
     * @param file the file.
     * @return The main folder of the external SD card containing this file, if the file is on an SD card. Otherwise,
     * null is returned.
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Nullable
    private static String getExtSdCardFolder(final File file, Context context) {
        String[] extSdPaths = getExtSdCardPaths(context);
        try {
            for (String extSdPath : extSdPaths) {
                if (file.getCanonicalPath().startsWith(extSdPath)) {
                    return extSdPath;
                }
            }
        } catch (IOException e) {
            return null;
        }
        return null;
    }

    /**
     * Get a list of external SD card paths. (Kitkat or higher)
     *
     * @return A list of external SD card paths.
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    @NonNull
    private static String[] getExtSdCardPaths(Context context) {
        List<String> paths = new ArrayList<>();
        for (File file : context.getExternalFilesDirs("external")) {
            if (file != null && !file.equals(context.getExternalFilesDir("external"))) {
                int index = file.getAbsolutePath().lastIndexOf("/Android/data");
                if (index < 0) {
                    Log.w(LOG_TAG, "Unexpected external file dir: " + file.getAbsolutePath());
                } else {
                    String path = file.getAbsolutePath().substring(0, index);
                    try {
                        path = new File(path).getCanonicalPath();
                    } catch (IOException e) {
                        // Keep non-canonical path.
                    }
                    paths.add(path);
                }
            }
        }
        if (paths.isEmpty()) {
            paths.add("/storage/sdcard1");
        }
        return paths.toArray(new String[0]);
    }
}
