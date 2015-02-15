package org.vliux.android.gesturecut.ui.view;

import android.animation.ObjectAnimator;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.BaseAdapter;
import android.widget.ListView;

import org.vliux.android.gesturecut.AppConstant;
import org.vliux.android.gesturecut.R;
import org.vliux.android.gesturecut.control.PkgRemovedEventBus;
import org.vliux.android.gesturecut.model.ResolvedComponent;
import org.vliux.android.gesturecut.biz.db.DbManager;
import org.vliux.android.gesturecut.biz.db.GestureDbTable;
import org.vliux.android.gesturecut.util.ConcurrentManager;
import org.vliux.android.gesturecut.util.GestureUtil;
import org.vliux.android.gesturecut.util.ImageUtil;
import org.vliux.android.gesturecut.util.ScreenUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by vliux on 4/11/14.
 */
public class GestureListView extends ListView {
    /**
     * Click listener when an icon in the GestureListItem has been clicked, and the relevant
     * ResolvedComponent is not NULL.
     */
    public static interface OnGestureIconClickedListener {
        void onGestureIconClicked(ResolvedComponent rc);
    }

    private OnGestureIconClickedListener mOnGestureIconClickedListener;
    private GestureListViewAdapter mListViewAdapter;
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
        mListViewAdapter = new GestureListViewAdapter();
        setAdapter(mListViewAdapter);
    }

    public void setOnGestureIconClickedListener(OnGestureIconClickedListener listener){
        mOnGestureIconClickedListener = listener;
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
        public void onPostExecute(List<String> strings) {
            mListViewAdapter.mGestureNames = strings;
            mListViewAdapter.notifyDataSetChanged();
            if(null != mExternalUiCallback){
                mExternalUiCallback.onPostExecute(strings);
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

    /**
     * Adapter
     */
    private class GestureListViewAdapter extends BaseAdapter {
        private List<String> mGestureNames;
        private GestureDbTable mDbTable;
        private int iconDimen;

        public GestureListViewAdapter() {
            mDbTable = (GestureDbTable) DbManager.getInstance().getDbTable(GestureDbTable.class);
            iconDimen = (int)getResources().getDimension(R.dimen.gesture_list_item_icon_dimen);
        }

        public String getGestureName(int position) {
            if(position >= 0 && position < mGestureNames.size()) {
                return mGestureNames.get(position);
            }else{
                return null;
            }
        }

        @Override
        public int getCount() {
            return null != mGestureNames? mGestureNames.size() : 0;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            GestureListItem listItem = null;
            if (null == convertView) {
                listItem = new GestureListItem(getContext());
                // OnGestureItemClickedListener
                if (null != mOnGestureIconClickedListener) {
                    final GestureListItem finalListItem = listItem;
                    listItem.setOnGestureIconClicked(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (null != mOnGestureIconClickedListener) {
                                AppInfoView appInfoView = finalListItem.getAppInfoView();
                                if (null != appInfoView && null != appInfoView.getResolvedComponent()) {
                                    mOnGestureIconClickedListener.onGestureIconClicked(appInfoView.getResolvedComponent());
                                }
                            }
                        }
                    });
                }
            } else {
                listItem = (GestureListItem) convertView;
            }

            ConcurrentManager.submitRunnable(
                    new LoadGestureDataRunnable(mDbTable, mHandler, mGestureNames.get(position), listItem, iconDimen));
            //startItemAnim(listItem, position);
            return listItem;
        }
    }

    private int mScreenWidth = -1;
    private void startItemAnim(View itemView, int position){
        if(mScreenWidth <= 0){
            int[] screenSize = ScreenUtil.getScreenSize(getContext());
            mScreenWidth = screenSize[0];
        }
        ObjectAnimator animator = ObjectAnimator.ofFloat(itemView, "translationX", -mScreenWidth, 0);
        animator.setDuration(AppConstant.Anim.ANIM_DURATION_NORMAL);
        animator.setInterpolator(new DecelerateInterpolator());
        animator.setStartDelay(position * 100L);
        animator.start();
    }

    private class LoadGestureDataRunnable implements Runnable{
        private final Handler notifyHandler;
        private final String gestureName;
        private final GestureListItem listItemView;
        private final int iconDimen;
        private GestureDbTable gestureDbTable;

        public LoadGestureDataRunnable(GestureDbTable gestureDbTable, Handler handler, String gesName,
                                       GestureListItem itemView, int iconDimen){
            this.gestureDbTable = gestureDbTable;
            notifyHandler = handler;
            gestureName = gesName;
            listItemView = itemView;
            this.iconDimen = iconDimen;
        }

        @Override
        public void run() {
            GestureDbTable.DbData dbData = gestureDbTable.getGesture(gestureName);
            if(null != dbData && null != dbData.resolvedComponent){
                String iconPath = dbData.iconPath;
                Bitmap bmp = null;
                if(null != iconPath && iconPath.length() > 0){
                    bmp = ImageUtil.decodeSampledBitmap(iconPath, this.iconDimen, this.iconDimen, ImageUtil.optionSave());
                }

                if(null != bmp){
                    NotifyHandlerData notifyHandlerData = new NotifyHandlerData();
                    notifyHandlerData.listItemView = listItemView;
                    notifyHandlerData.bitmap = bmp;
                    notifyHandlerData.gestureName = gestureName;
                    notifyHandlerData.resolvedComponent = dbData.resolvedComponent;
                    Message message = notifyHandler.obtainMessage(WHAT_GESTURE_ICON_LOADED, notifyHandlerData);
                    mHandler.sendMessage(message);
                }
            }
        }
    }

    private class NotifyHandlerData{
        public String gestureName;
        public GestureListItem listItemView;
        public ResolvedComponent resolvedComponent;
        public Bitmap bitmap; // bitmap for gesture snapshot
    }

    public static final int WHAT_GESTURE_ICON_LOADED = 100;
    private final Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case WHAT_GESTURE_ICON_LOADED:
                    NotifyHandlerData notifyHandlerData = (NotifyHandlerData)msg.obj;
                    notifyHandlerData.listItemView.setGestureImage(notifyHandlerData.bitmap);
                    notifyHandlerData.listItemView.setAppInfoResolvedComponent(notifyHandlerData.resolvedComponent);
                    break;
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
