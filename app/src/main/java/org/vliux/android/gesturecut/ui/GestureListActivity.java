package org.vliux.android.gesturecut.ui;

import android.app.Activity;
import android.os.Bundle;

import org.vliux.android.gesturecut.R;
import org.vliux.android.gesturecut.ui.view.GestureList.GestureList;

/**
 * Created by vliux on 4/21/14.
 */
public class GestureListActivity extends Activity{
    private GestureList mGestureList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gesture_list);

        mGestureList = (GestureList)findViewById(R.id.actv_gesture_list);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mGestureList.setAutoRefresh(true);
        mGestureList.show();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mGestureList.setAutoRefresh(false);
        mGestureList.hide();
    }
}
