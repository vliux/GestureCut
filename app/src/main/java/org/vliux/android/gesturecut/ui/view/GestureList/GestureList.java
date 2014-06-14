package org.vliux.android.gesturecut.ui.view.GestureList;

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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import org.vliux.android.gesturecut.AppConstant;
import org.vliux.android.gesturecut.R;
import org.vliux.android.gesturecut.biz.ConcurrentControl;
import org.vliux.android.gesturecut.biz.ResolvedComponent;
import org.vliux.android.gesturecut.biz.db.DbManager;
import org.vliux.android.gesturecut.biz.db.GestureDbTable;
import org.vliux.android.gesturecut.biz.gesture.GesturePersistence;
import org.vliux.android.gesturecut.ui.SettingsActivity;
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
public class GestureList extends LinearLayout implements View.OnClickListener {
    private static final String TAG = GestureList.class.getSimpleName();
    /**
     * Whether show() and hide() are required to make the view visible/invisible.
     * If it is faluse, then the view is by default visible.
     */
    private boolean mNeedShowHide = false;

    private ImageView mIvSetting;
    private BottomBarAwaredListView mGestureListView;
    private int mScreenWidth;
    private boolean mIsShown = false;
    private GestureListViewAdapter mListViewAdapter;
    private BottomBar mBottomBar;

    public GestureList(Context context) {
        super(context);
        init(null);
    }

    public GestureList(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public GestureList(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs);
    }

    private void init(AttributeSet attrs){
        LayoutInflater.from(getContext()).inflate(R.layout.view_gesture_list, this, true);
        mIvSetting = (ImageView)findViewById(R.id.gesture_list_settings);
        mGestureListView = (BottomBarAwaredListView)findViewById(R.id.gesture_listview);
        mBottomBar = (BottomBar)findViewById(R.id.gesture_bottom_bar);

        mBottomBar.setOnDeleteClicked(mOnBottomBarClickedListener);
        mIvSetting.setOnClickListener(this);
        mGestureListView.setBottomBar(mBottomBar);
        mListViewAdapter = new GestureListViewAdapter();
        mGestureListView.setAdapter(mListViewAdapter);
        mScreenWidth = ScreenUtil.getScreenSize(getContext())[0];
        mGestureListView.setLayoutTransition(new LayoutTransition());
        mGestureListView.setOnItemLongClickListener(mOnItemLongClickListener);

        if(null != attrs){
            TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.GestureList);
            mNeedShowHide = typedArray.getBoolean(R.styleable.GestureList_showHideRequired, false);
        }else{
            mNeedShowHide = false;
        }

        if(mNeedShowHide){
            setTranslationX(-mScreenWidth);
        }

        setEmptyGestureView(mGestureListView);
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

    private void setEmptyGestureView(ListView gestureListView){
        TextView tvEmpty = (TextView)findViewById(R.id.gesture_empty_tv);
        gestureListView.setEmptyView(tvEmpty);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.gesture_list_settings:
                getContext().startActivity(new Intent(getContext(), SettingsActivity.class));
                break;
        }
    }

    private OnClickListener mOnBottomBarClickedListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if(mLastLongClickPosition >= 0 && mLastLongClickPosition < mListViewAdapter.getCount()){
                GesturePersistence.removeGesture(getContext(), mListViewAdapter.getGestureName(mLastLongClickPosition));
                mListViewAdapter.notifyDataSetChanged();
            }
            mLastLongClickPosition = -1;
        }
    };

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
    };

    private class GestureListViewHolder{
        public ImageView gestureIcon;
        //public ImageView appIcon;
        //public TextView textView;
        public AppInfoView appInfoView;
    }

    private class LoadGestureDataRunnable implements Runnable{
        private Handler notifyHandler;
        private String gestureName;
        private GestureListViewHolder viewHolder;
        private int iconWidth;
        private int iconHeight;

        public LoadGestureDataRunnable(Handler handler, String gesName,
                                       GestureListViewHolder vh,
                                       PackageManager pm){
            notifyHandler = handler;
            gestureName = gesName;
            viewHolder = vh;
            iconWidth = (int)getContext().getResources().getDimension(R.dimen.gesture_list_item_icon_dimen);
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
    private Handler mHandler = new Handler(){
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

    public boolean isShown(){
        return mIsShown;
    }

    public void show(){
        if(mNeedShowHide){
            mIsShown = true;
            getShowHideAnimatorSet(true).start();
            mListViewAdapter.notifyDataSetChanged();
        }
    }

    public void hide(){
        if(mNeedShowHide){
            mIsShown = false;
            getShowHideAnimatorSet(false).start();
        }
    }

    /**
     * Get the AnimatorSet for showing or hiding this GestureList.
     * @param forShown
     * @return
     */
    private AnimatorSet getShowHideAnimatorSet(boolean forShown){
        ObjectAnimator transxObjAnimator = null;
        ObjectAnimator alphaObjAnimator = null;
        if(forShown) {
            // animators for showing
            transxObjAnimator = ObjectAnimator.ofFloat(this, "translationX", -mScreenWidth, 0);
            alphaObjAnimator = ObjectAnimator.ofFloat(this, "alpha", 0.0f, 1.0f);
        }else{
            // animators for hiding
            transxObjAnimator = ObjectAnimator.ofFloat(this, "translationX", 0, -mScreenWidth);
            alphaObjAnimator = ObjectAnimator.ofFloat(this, "alpha", 1.0f, 0.0f);
        }

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setDuration(AppConstant.Anim.ANIM_DURATION_NORMAL);
        animatorSet.setInterpolator(new OvershootInterpolator());
        animatorSet.play(transxObjAnimator).with(alphaObjAnimator);
        return animatorSet;
    }

    private BroadcastReceiver mGestureAddedBroadcastReceiver = new BroadcastReceiver() {
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

    private int mLastLongClickPosition = -1;
    private AdapterView.OnItemLongClickListener mOnItemLongClickListener = new AdapterView.OnItemLongClickListener() {
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
            mBottomBar.showBottomBar();
            mLastLongClickPosition = position;
            return true;
        }
    };

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if(event.getKeyCode() == KeyEvent.KEYCODE_BACK
                && KeyEvent.ACTION_UP == event.getAction()){
            if(mBottomBar.isShown()){
                mBottomBar.hideBottomBar();
                return true;
            }
            return false;
        }else{
            return super.dispatchKeyEvent(event);
        }
    }
}
