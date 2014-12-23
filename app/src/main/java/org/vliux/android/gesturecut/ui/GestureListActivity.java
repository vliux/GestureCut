package org.vliux.android.gesturecut.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import org.vliux.android.gesturecut.R;
import org.vliux.android.gesturecut.biz.TaskManager;
import org.vliux.android.gesturecut.model.ResolvedComponent;
import org.vliux.android.gesturecut.ui.view.gesturelist.GestureListView;

/**
 * Created by vliux on 4/21/14.
 */
public class GestureListActivity extends Activity{
    private GestureListView mGestureList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gesture_list);

        mGestureList = (GestureListView)findViewById(R.id.actv_gesture_list);
        mGestureList.setOnGestureItemClickedListener(mOnGestureItemClicked);
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

    @Override
    protected void onStart() {
        super.onStart();
        mGestureList.registerPkgRemovedEventHandler();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mGestureList.unregisterPkgRemovedEventHandler();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent = null;
        switch (item.getItemId()) {
            case R.id.action_add:
                intent = new Intent(this, AddGestureActivity.class);
                startActivity(intent);
                return true;
            case R.id.action_settings:
                intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private final GestureListView.OnGestureItemClickedListener mOnGestureItemClicked = new GestureListView.OnGestureItemClickedListener() {
        @Override
        public void onGestureItemClicked(ResolvedComponent rc) {
            TaskManager.startActivity(GestureListActivity.this, rc);
        }
    };
}
