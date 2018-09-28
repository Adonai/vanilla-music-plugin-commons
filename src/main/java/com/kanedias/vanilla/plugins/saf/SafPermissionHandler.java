package com.kanedias.vanilla.plugins.saf;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.kanedias.vanilla.plugins.R;

import java.io.File;

import static com.kanedias.vanilla.plugins.PluginConstants.LOG_TAG;
import static com.kanedias.vanilla.plugins.PluginConstants.PREF_SDCARD_URI;

/**
 * Handler that is needed solely for the purpose of requesting SAF permissions for external SD cards.
 * Mostly this is needed for external media support.
 * If the file is located on external SD card then android provides
 * only Storage Access Framework to be able to write anything.
 *
 * @author  Kanedias on 17.02.17.
 */
public class SafPermissionHandler {

    private static final int SAF_FILE_REQUEST_CODE = 1;
    private static final int SAF_TREE_REQUEST_CODE = 2;

    /**
     * File to search access for
     */
    private Activity ctx;
    private SharedPreferences mPrefs;

    public SafPermissionHandler(Activity ctx) {
        this.ctx = ctx;
        mPrefs = PreferenceManager.getDefaultSharedPreferences(ctx);
    }

    public void handleFile(File file) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // it's Lollipop - let's request tree URI instead of nitpicking with specific files...
            // deal with file passed after request is fulfilled
            callSafRequestTree();
            return;
        }

        // it's Kitkat - we're doomed to request each file one by one
        // this is very unlikely actually - external card is still R/W for KitKat, so
        // plugin should be able to persist everything through normal File API
        callSafFilePicker(file);
    }

    /**
     * Call tree-picker to select root of SD card.
     * Shows a hint how to do this, continues if "ok" is clicked.
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void callSafRequestTree() {
        new AlertDialog.Builder(ctx)
                .setTitle(R.string.need_sd_card_access)
                .setView(R.layout.sd_operate_instructions)
                .setNegativeButton(android.R.string.cancel, null)
                .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                    Intent selectFile = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
                    ctx.startActivityForResult(selectFile, SAF_TREE_REQUEST_CODE);
                })
                .create()
                .show();
    }

    /*
     * Call this method from your activity to get results once SAF file picker finishes.
     * @param requestCode our sent code, see {@link SafUtils#isSafNeeded(File)}
     * @param resultCode success or error
     * @param data URI-containing intent
     */
    public boolean onActivityResult(int requestCode, int resultCode, Intent clearance) {
        if (requestCode == SAF_FILE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            // access granted, write through
            return true;
        }

        if (requestCode == SAF_TREE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            saveTreeAccessForever(clearance);
            return true;
        }

        // canceled or denied
        Log.e(LOG_TAG, "Saf access request was denied" + clearance);
        return false;
    }

    /**
     * Saves SAF-provided tree URI forever
     * @param clearance intent containing tree URI in data
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void saveTreeAccessForever(Intent clearance) {
        if (clearance.getData() == null) {
            Log.e(LOG_TAG, "Access to sdcard was granted but no access uri given: " + clearance);
        }

        Uri treeAccessUri = clearance.getData();
        ctx.getContentResolver().takePersistableUriPermission(treeAccessUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
        mPrefs.edit().putString(PREF_SDCARD_URI, treeAccessUri.toString()).apply();
    }

    /**
     * Opens SAF file pick dialog to allow you to select specific file to write to
     * @param file file to request picker for
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void callSafFilePicker(File file) {
        Toast.makeText(ctx, R.string.file_on_external_sd_warning, Toast.LENGTH_LONG).show();
        Toast.makeText(ctx, R.string.file_on_external_sd_workaround, Toast.LENGTH_LONG).show();
        Toast.makeText(ctx, String.format(ctx.getString(R.string.file_on_external_sd_hint), file.getPath()), Toast.LENGTH_LONG).show();

        Intent selectFile = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        selectFile.addCategory(Intent.CATEGORY_OPENABLE);
        selectFile.setType("audio/*");
        ctx.startActivityForResult(selectFile, SAF_FILE_REQUEST_CODE);
    }
}
