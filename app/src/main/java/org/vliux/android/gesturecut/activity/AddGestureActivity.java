package org.vliux.android.gesturecut.activity;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;

import org.vliux.android.gesturecut.R;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by vliux on 12/12/14.
 */
public class AddGestureActivity extends Activity {
    @InjectView(R.id.add_ges_listview)
    ListView mAppsListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_gesture);
        ButterKnife.inject(this);
    }


}
