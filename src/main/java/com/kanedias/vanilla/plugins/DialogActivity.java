package com.kanedias.vanilla.plugins;

import android.app.Activity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Window;
import android.view.WindowManager;
import androidx.annotation.Nullable;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
import static android.view.WindowManager.LayoutParams.FLAG_DIM_BEHIND;

/**
 * Makeshift dialog activity. Workaround for having Action Bar in dialog.
 * You should set the style of its descendants to DialogStyle.
 *
 * @author Oleg Chernovskiy
 */
public abstract class DialogActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_ACTION_BAR);
        Window window = getWindow();
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        // clear all flags but dim behind
        window.setFlags(FLAG_DIM_BEHIND, Integer.MAX_VALUE);
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = (int) (metrics.widthPixels * 0.9f);
        params.alpha = 1.0f;
        params.dimAmount = 0.5f;
        params.height = WRAP_CONTENT;
        window.setAttributes(params);
    }
}
