package org.vliux.android.gesturecut.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import org.vliux.android.gesturecut.R;
import org.vliux.android.gesturecut.ui.view.GestureListView;

/**
 * Created by vliux on 1/28/15.
 */
public class ShortcutActivity extends Activity {
    private GestureListView mGestureListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        window.addFlags(
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        setContentView(R.layout.activity_shortcut);

        mGestureListView = (GestureListView)findViewById(R.id.sc_gesture_list);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGestureListView.refresh();
    }
}
