package org.vliux.android.gesturecut.ui.view;

import android.animation.AnimatorSet;
import android.animation.LayoutTransition;
import android.animation.ObjectAnimator;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import org.vliux.android.gesturecut.AppConstant;
import org.vliux.android.gesturecut.R;
import org.vliux.android.gesturecut.biz.ConcurrentControl;
import org.vliux.android.gesturecut.control.PkgRemovedEventBus;
import org.vliux.android.gesturecut.model.ResolvedComponent;
import org.vliux.android.gesturecut.biz.db.DbManager;
import org.vliux.android.gesturecut.biz.db.GestureDbTable;
import org.vliux.android.gesturecut.biz.gesture.GesturePersistence;
import org.vliux.android.gesturecut.ui.view.AppInfoView;
import org.vliux.android.gesturecut.util.GestureUtil;
import org.vliux.android.gesturecut.util.ImageUtil;
import org.vliux.android.gesturecut.util.ScreenUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by vliux on 4/11/14.
 */
public class GestureListLayout extends FrameLayout {
    /**
     * Click listener when an item in the GestureListView has been clicked, and the relevant
     * ResolvedComponent is not NULL.
     */
    public static interface OnGestureItemClickedListener{
        void onGestureItemClicked(ResolvedComponent rc);
    }

    private OnGestureItemClickedListener mOnGestureItemClickedListener;

    private ListView mGestureListView;
    private GestureListViewAdapter mListViewAdapter;

    public GestureListLayout(Context context) {
        super(context);
        init(null);
    }

    public GestureListLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public GestureListLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs);
    }

    private void init(AttributeSet attrs){
        LayoutInflater.from(getContext()).inflate(R.layout.view_gesture_list, this, true);
        mGestureListView = (ListView)findViewById(R.id.gesture_listview);

        mListViewAdapter = new GestureListViewAdapter();
        mGestureListView.setAdapter(mListViewAdapter);
        setEmptyGestureView(mGestureListView);
    }

    public void setOnGestureItemClickedListener(OnGestureItemClickedListener listener){
        mOnGestureItemClickedListener = listener;
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
        mListViewAdapter.notifyDataSetChanged();
    }

    private void setEmptyGestureView(ListView gestureListView){
        TextView tvEmpty = (TextView)findViewById(R.id.gesture_empty_tv);
        gestureListView.setEmptyView(tvEmpty);
    }

    /**
     * Adapter
     */
    private class GestureListViewAdapter extends BaseAdapter{
        private List<String> mGestureNames;

        public GestureListViewAdapter(){
            loadData();
        }

        private void loadData(){
            if(null == mGestureNames){
                mGestureNames = new ArrayList<String>();
            }else{
                mGestureNames.clear();
            }
            mGestureNames.addAll(GestureUtil.getInstance().getGestureNames());
            Collections.sort(mGestureNames);
        }

        public String getGestureName(int position){
            return mGestureNames.get(position);
        }

        @Override
        public void notifyDataSetChanged() {
            loadData();
            super.notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return mGestureNames.size();
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
            GestureListViewHolder viewHolder = null;
            if(null == convertView){
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_gesture, parent, false);
                viewHolder = new GestureListViewHolder();
                viewHolder.gestureIcon = (ImageView)convertView.findViewById(R.id.item_gesture_icon);
                viewHolder.appInfoView = (AppInfoView)convertView.findViewById(R.id.item_gesture_appinfo);
                convertView.setTag(viewHolder);

                // OnGestureItemClickedListener
                if(null != mOnGestureItemClickedListener){
                    final GestureListViewHolder finalViewHolder = viewHolder;
                    viewHolder.gestureIcon.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(null != mOnGestureItemClickedListener){
                                AppInfoView appInfoView = finalViewHolder.appInfoView;
                                if(null != appInfoView && null != appInfoView.getResolvedComponent()){
                                    mOnGestureItemClickedListener.onGestureItemClicked(appInfoView.getResolvedComponent());
                                }
                            }
                        }
                    });
                }
            }else{
                viewHolder = (GestureListViewHolder)convertView.getTag();
            }

            ConcurrentControl.submitTask(
                    new LoadGestureDataRunnable(mHandler,
                            mGestureNames.get(position),
                            viewHolder,
                            getContext().getPackageManager()));
            return convertView;
        }
    }

    private class GestureListViewHolder{
        public ImageView gestureIcon;
        public AppInfoView appInfoView;
    }

    private class LoadGestureDataRunnable implements Runnable{
        private final Handler notifyHandler;
        private final String gestureName;
        private final GestureListViewHolder viewHolder;
        private final int iconWidth;
        private final int iconHeight;

        public LoadGestureDataRunnable(Handler handler, String gesName,
                                       GestureListViewHolder vh,
                                       PackageManager pm){
            notifyHandler = handler;
            gestureName = gesName;
            viewHolder = vh;
            iconWidth = (int)getResources().getDimension(R.dimen.gesture_list_item_icon_dimen);
            iconHeight = iconWidth;
        }

        @Override
        public void run() {
            GestureDbTable gestureDbTable = (GestureDbTable) DbManager.getInstance().getDbTable(GestureDbTable.class);
            GestureDbTable.DbData dbData = gestureDbTable.getGesture(gestureName);
            if(null != dbData && null != dbData.resolvedComponent){
                String iconPath = dbData.iconPath;
                Bitmap bmp = null;
                if(null != iconPath && iconPath.length() > 0){
                    bmp = ImageUtil.decodeSampledBitmap(iconPath, iconWidth, iconHeight, ImageUtil.optionSave());
                }

                if(null != bmp){
                    NotifyHandlerData notifyHandlerData = new NotifyHandlerData();
                    notifyHandlerData.viewHolder = viewHolder;
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
        public GestureListViewHolder viewHolder;
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
                    notifyHandlerData.viewHolder.gestureIcon.setImageBitmap(notifyHandlerData.bitmap);
                    notifyHandlerData.viewHolder.appInfoView.setResolvedComponent(notifyHandlerData.resolvedComponent);
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
                        mListViewAdapter.notifyDataSetChanged();
                    }
                }
            }
        }
    };

    private final PkgRemovedEventBus.PkgRemovedHandler mPkgRemovedEventHandler = new PkgRemovedEventBus.PkgRemovedHandler() {
        @Override
        public void onEventMainThread(PkgRemovedEventBus.PkgRemovedEvent event) {
            if(null != mListViewAdapter){
                mListViewAdapter.notifyDataSetChanged();
            }
        }
    };

    public ListView getListView(){
        return mGestureListView;
    }
}
