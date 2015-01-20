package org.vliux.android.gesturecut.activity.main;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.melnykov.fab.FloatingActionButton;

import org.vliux.android.gesturecut.R;
import org.vliux.android.gesturecut.activity.SettingsActivity;
import org.vliux.android.gesturecut.activity.add.AddGestureActivity;
import org.vliux.android.gesturecut.biz.TaskManager;
import org.vliux.android.gesturecut.model.ResolvedComponent;
import org.vliux.android.gesturecut.ui.view.GestureListView;
import org.vliux.android.gesturecut.util.ConcurrentManager;

import java.util.List;

/**
 * Created by vliux on 4/21/14.
 */
public class GestureListActivity extends ActionBarActivity{
    private GestureListView mGestureList;
    private FloatingActionButton mFab;
    private FabPresenter mFabPresenter;
    private ProgressBar mProgressBar;
    private TextView mTvEmptyView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gesture_list);

        mProgressBar = (ProgressBar)findViewById(R.id.list_ges_prog_bar);

        mGestureList = (GestureListView)findViewById(R.id.actv_gesture_list);
        mTvEmptyView = (TextView)findViewById(R.id.actv_ges_list_empty_view);

        mGestureList.setOnGestureItemClickedListener(mOnGestureItemClicked);
        mGestureList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        mGestureList.setOnItemClickListener(mListItemClicked);
        mGestureList.setExternalUiCallback(mLoadGestureUiCallback);
        mGestureList.setEmptyView(mTvEmptyView);
        mTvEmptyView.setOnClickListener(mOnEmptyViewClicked);

        mFab = (FloatingActionButton)findViewById(R.id.fab);
        mFab.attachToListView(mGestureList);
        mFab.setOnClickListener(mFabOnClickListener);
        mFabPresenter = new FabPresenter(this, mFab, mGestureList);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mGestureList.refresh();
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

    private ConcurrentManager.IUiCallback<List<String>> mLoadGestureUiCallback = new ConcurrentManager.IUiCallback<List<String>>() {
        @Override
        public void onPreExecute() {
            mProgressBar.setVisibility(View.VISIBLE);
            mProgressBar.setProgress(0);
        }

        @Override
        public void onPostExecute(List<String> strings) {
            mProgressBar.setVisibility(View.GONE);
        }

        @Override
        public void onPregressUpdate(int percent) {
            mProgressBar.setProgress(percent);
        }

        @Override
        public void onCancelled() {

        }
    };

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
            mFabPresenter.onFabClicked();
        }
    };

    private final AdapterView.OnItemClickListener mListItemClicked = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if(mGestureList.getCheckedItemCount() > 0){
                mFabPresenter.setDeleteMode();
            }else{
                mFabPresenter.setNormalMode();
            }
        }
    };

    private final View.OnClickListener mOnEmptyViewClicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int id = v.getId();
            if(R.id.actv_ges_list_empty_view == id){
                mFabPresenter.onFabClicked();
            }
        }
    };
}
