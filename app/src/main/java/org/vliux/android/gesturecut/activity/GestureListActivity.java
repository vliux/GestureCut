package org.vliux.android.gesturecut.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;

import com.melnykov.fab.FloatingActionButton;

import org.vliux.android.gesturecut.R;
import org.vliux.android.gesturecut.activity.add.AddGestureActivity;
import org.vliux.android.gesturecut.biz.TaskManager;
import org.vliux.android.gesturecut.model.ResolvedComponent;
import org.vliux.android.gesturecut.ui.view.GestureListView;

/**
 * Created by vliux on 4/21/14.
 */
public class GestureListActivity extends Activity{
    private GestureListView mGestureList;
    private FloatingActionButton mFab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gesture_list);

        mGestureList = (GestureListView)findViewById(R.id.actv_gesture_list);
        mGestureList.setOnGestureItemClickedListener(mOnGestureItemClicked);
        mGestureList.setOnItemClickListener(mOnListItemClickListener);
        mGestureList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        mFab = (FloatingActionButton)findViewById(R.id.fab);
        mFab.attachToListView(mGestureList);
        mFab.setOnClickListener(mFabOnClickListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mGestureList.setAutoRefresh(true);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mGestureList.setAutoRefresh(false);
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
            case R.id.action_refresh:
                mGestureList.refresh();
                return true;
            case R.id.action_settings:
                intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Gesture image in the item is clicked.
     */
    private final GestureListView.OnGestureItemClickedListener mOnGestureItemClicked = new GestureListView.OnGestureItemClickedListener() {
        @Override
        public void onGestureItemClicked(ResolvedComponent rc) {
            TaskManager.startActivity(GestureListActivity.this, rc);
        }
    };

    private final View.OnClickListener mFabOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(GestureListActivity.this, AddGestureActivity.class);
            startActivity(intent);
        }
    };

    /**
     * The whole item is clicked.
     */
    private final AdapterView.OnItemClickListener mOnListItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            boolean activated = view.isActivated();
            //Log.d("vliux", "actived before = " + activated);
            boolean checked = mGestureList.isItemChecked(position);
            Log.d("vliux", "actived before = " + checked);
            mGestureList.setItemChecked(position, !checked);
            checked = mGestureList.isItemChecked(position);
            Log.d("vliux", "actived after = " + checked);
        }
    };
}
