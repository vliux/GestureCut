package org.vliux.android.gesturecut.ui.view.glv;

import android.animation.ObjectAnimator;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.ListView;

import org.vliux.android.gesturecut.AppConstant;
import org.vliux.android.gesturecut.control.PkgRemovedEventBus;
import org.vliux.android.gesturecut.model.ResolvedComponent;
import org.vliux.android.gesturecut.util.ConcurrentManager;
import org.vliux.android.gesturecut.util.GestureUtil;
import org.vliux.android.gesturecut.util.ScreenUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by vliux on 4/11/14.
 * GestureListView handles empty view automatically,
 * by managing a single item view holding the full size of the ListView.
 */
public class GestureListView extends ListView {
    private boolean mIsRefreshed = false;
    private OnClickListener mEmptyViewClicked;

    static enum Status{
        NOT_LOADED, // the listview has not been loaded yet
        EMPTY_VIEW, // the listview has been loaded, but not data inside it
        NORMAL // the listview has been loaded with data set inside
    }

    /**
     * Click listener when an icon in the GestureListItem has been clicked, and the relevant
     * ResolvedComponent is not NULL.
     */
    public static interface OnGestureIconClickedListener {
        void onGestureIconClicked(ResolvedComponent rc);
    }

    private GestureListAdapter mListViewAdapter;
    private ConcurrentManager.IUiCallback<List<String>> mExternalUiCallback;

    public GestureListView(Context context) {
        super(context);
        init(null);
    }

    public GestureListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public GestureListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs);
    }

    private void init(AttributeSet attrs){
        mListViewAdapter = new GestureListAdapter(this);
        setAdapter(mListViewAdapter);
    }

    Status getStatus(){
        if(!mIsRefreshed){
            return Status.NOT_LOADED;
        }else if(null == mListViewAdapter.getGestureNames() || mListViewAdapter.getGestureNames().size() <= 0){
            return Status.EMPTY_VIEW;
        }else{
            return Status.NORMAL;
        }
    }

    public void setOnEmptyViewClickedListener(OnClickListener emptyViewClicked){
        this.mEmptyViewClicked = emptyViewClicked;
    }

    @Override
    public boolean performItemClick(View view, int position, long id) {
        Status status = getStatus();
        switch (status){
            case NORMAL:
                return super.performItemClick(view, position, id);
            case EMPTY_VIEW:
                if(null != mEmptyViewClicked){
                    mEmptyViewClicked.onClick(view);
                    return true;
                }
        }
        return false;
    }

    public void setOnGestureIconClickedListener(OnGestureIconClickedListener listener){
        mListViewAdapter.setOnGestureIconClickedListener(listener);
    }

    public void setExternalUiCallback(ConcurrentManager.IUiCallback<List<String>> uiCallback){
        mExternalUiCallback = uiCallback;
    }

    /**
     * Whether this view can automatically refresh for the change of gesture library.
     * @param autoRefresh
     */
    public void setAutoRefresh(boolean autoRefresh){
        if(autoRefresh){
            LocalBroadcastManager.getInstance(getContext()).registerReceiver(mGestureAddedBroadcastReceiver,
                    new IntentFilter(AppConstant.LocalBroadcasts.BROADCAST_GESTURE_ADDED));
        }else{
            LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(mGestureAddedBroadcastReceiver);
        }
    }

    public void registerPkgRemovedEventHandler(){
        PkgRemovedEventBus.getInstance().register(mPkgRemovedEventHandler);
    }

    public void unregisterPkgRemovedEventHandler(){
        PkgRemovedEventBus.getInstance().unregister(mPkgRemovedEventHandler);
    }

    public void refresh(){
        mIsRefreshed = true;
        ConcurrentManager.submitJob(mLoadGestureBizCallback, mLoadGestureUiCallback);
    }

    public String getGestureName(int position){
        return mListViewAdapter.getGestureName(position);
    }

    private final ConcurrentManager.IBizCallback<List<String>> mLoadGestureBizCallback = new ConcurrentManager.IBizCallback<List<String>>() {
        @Override
        public List<String> onBusinessLogicAsync(ConcurrentManager.IJob job, Object... params) {
            List<String> gestureNames = new ArrayList<String>();
            gestureNames.addAll(GestureUtil.getInstance().getGestureNames());
            job.publishJobProgress(50);
            Collections.sort(gestureNames);
            job.publishJobProgress(90);
            return gestureNames;
        }
    };

    private final ConcurrentManager.IUiCallback<List<String>> mLoadGestureUiCallback = new ConcurrentManager.IUiCallback<List<String>>() {
        @Override
        public void onPreExecute() {
            if(null != mExternalUiCallback){
                mExternalUiCallback.onPreExecute();
            }
        }

        @Override
        public void onPostExecute(List<String> gestureNames) {
            mListViewAdapter.setGestureNames(gestureNames);
            mListViewAdapter.notifyDataSetChanged();
            if(null != mExternalUiCallback){
                mExternalUiCallback.onPostExecute(gestureNames);
            }
        }

        @Override
        public void onPregressUpdate(int percent) {
            if(null != mExternalUiCallback){
                mExternalUiCallback.onPregressUpdate(percent);
            }
        }

        @Override
        public void onCancelled() {
            if(null != mExternalUiCallback){
                mExternalUiCallback.onCancelled();
            }
        }
    };

    private final BroadcastReceiver mGestureAddedBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(null != intent){
                String action = intent.getAction();
                if(AppConstant.LocalBroadcasts.BROADCAST_GESTURE_ADDED.equals(action)){
                    if(null != mListViewAdapter){
                        refresh();
                    }
                }
            }
        }
    };

    private final PkgRemovedEventBus.PkgRemovedHandler mPkgRemovedEventHandler = new PkgRemovedEventBus.PkgRemovedHandler() {
        @Override
        public void onEventMainThread(PkgRemovedEventBus.PkgRemovedEvent event) {
            if(null != mListViewAdapter){
                refresh();
            }
        }
    };
}
