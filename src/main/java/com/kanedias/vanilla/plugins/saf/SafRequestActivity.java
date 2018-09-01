package com.kanedias.vanilla.plugins.saf;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.kanedias.vanilla.plugins.R;

import java.io.File;

import static com.kanedias.vanilla.plugins.PluginConstants.ACTION_LAUNCH_PLUGIN;
import static com.kanedias.vanilla.plugins.PluginConstants.ACTION_WAKE_PLUGIN;
import static com.kanedias.vanilla.plugins.PluginConstants.EXTRA_PARAM_PLUGIN_APP;
import static com.kanedias.vanilla.plugins.PluginConstants.EXTRA_PARAM_SAF_P2P;
import static com.kanedias.vanilla.plugins.PluginConstants.EXTRA_PARAM_URI;
import static com.kanedias.vanilla.plugins.PluginConstants.PREF_SDCARD_URI;

/**
 * Activity that is needed solely for requesting SAF permissions for external SD cards.
 *
 * @author  Kanedias on 17.02.17.
 */
public class SafRequestActivity extends Activity {

    private static final int SAF_FILE_REQUEST_CODE = 1;
    private static final int SAF_TREE_REQUEST_CODE = 2;

    /**
     * File to search access for
     */
    private File mFile;

    private SharedPreferences mPrefs;

    private ServiceConnection mServiceConn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }
    };


    @Override
    protected void onResume() {
        super.onResume();

        mPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        // need to bind the service or it will stop itself after TagEditActivity is closed
        ApplicationInfo info = getIntent().getParcelableExtra(EXTRA_PARAM_PLUGIN_APP);
        Intent bind = new Intent(ACTION_WAKE_PLUGIN);
        bind.setPackage(info.packageName);
        bindService(bind, mServiceConn, 0);

        Uri fileUri = getIntent().getParcelableExtra(EXTRA_PARAM_URI);
        mFile = new File(fileUri.getPath());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // it's Lollipop - let's request tree URI instead of nitpicking with specific files...
            // deal with file passed after request is fulfilled
            callSafRequestTree();
            return;
        }

        // it's Kitkat - we're doomed to request each file one by one
        // this is very unlikely actually - external card is still R/W for KitKat, so
        // service should be able to persist everything through normal File API
        callSafFilePicker();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mServiceConn);
    }

    /**
     * Call tree-picker to select root of SD card.
     * Shows a hint how to do this, continues if "ok" is clicked.
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void callSafRequestTree() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.need_sd_card_access)
                .setView(R.layout.sd_operate_instructions)
                .setNegativeButton(android.R.string.cancel, (dialog, which) -> finish())
                .setPositiveButton(android.R.string.ok, (dialog, which) -> {
					Intent selectFile = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
					startActivityForResult(selectFile, SAF_TREE_REQUEST_CODE);
				})
                .create()
                .show();
    }

    /*
     * Mostly this is needed for SAF support. If the file is located on external SD card then android provides
     * only Storage Access Framework to be able to write anything.
     * @param requestCode our sent code, see {@link SafUtils#isSafNeeded(File)}
     * @param resultCode success or error
     * @param data URI-containing intent
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Intent serviceStart = new Intent(ACTION_LAUNCH_PLUGIN);
        ApplicationInfo info = getIntent().getParcelableExtra(EXTRA_PARAM_PLUGIN_APP);
        serviceStart.setPackage(info.packageName);
        serviceStart.putExtras(getIntent());
        serviceStart.putExtra(EXTRA_PARAM_SAF_P2P, data);

        if (requestCode == SAF_FILE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            // access granted, write through
            serviceStart.putExtra(EXTRA_PARAM_SAF_P2P, data);
            startService(serviceStart); // pass intent back to the service
        }

        if (requestCode == SAF_TREE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            saveTreeAccessForever(data);
            startService(serviceStart); // pass intent back to the service
        }

        // canceled or denied
        finish();
    }

    /**
     * Saves SAF-provided tree URI forever
     * @param data intent containing tree URI in data
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void saveTreeAccessForever(Intent data) {
        Uri treeAccessUri = data.getData();
        getContentResolver().takePersistableUriPermission(treeAccessUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
        mPrefs.edit().putString(PREF_SDCARD_URI, treeAccessUri.toString()).apply();
    }

    /**
     * Opens SAF file pick dialog to allow you to select specific file to write to
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void callSafFilePicker() {
        Toast.makeText(this, R.string.file_on_external_sd_warning, Toast.LENGTH_LONG).show();
        Toast.makeText(this, R.string.file_on_external_sd_workaround, Toast.LENGTH_LONG).show();
        Toast.makeText(this, String.format(getString(R.string.file_on_external_sd_hint), mFile.getPath()), Toast.LENGTH_LONG).show();

        Intent selectFile = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        selectFile.addCategory(Intent.CATEGORY_OPENABLE);
        selectFile.setType("audio/*");
        startActivityForResult(selectFile, SAF_FILE_REQUEST_CODE);
    }
}
