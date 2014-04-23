package org.vliux.android.gesturecut.ui.view;

import android.animation.Animator;
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
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import org.vliux.android.gesturecut.AppConstant;
import org.vliux.android.gesturecut.R;
import org.vliux.android.gesturecut.biz.ConcurrentControl;
import org.vliux.android.gesturecut.biz.ResolvedComponent;
import org.vliux.android.gesturecut.biz.db.DbManager;
import org.vliux.android.gesturecut.biz.db.GestureDbTable;
import org.vliux.android.gesturecut.biz.gesture.GesturePersistence;
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

    /**
     * Whether show() and hide() are required to make the view visible/invisible.
     * If it is faluse, then the view is by default visible.
     */
    private boolean mNeedShowHide = false;

    private ImageView mIvDel;
    private ListView mGestureListView;
    private int mScreenWidth;
    private boolean mIsShown = false;
    private GestureListViewAdapter mListViewAdapter;
    private boolean mIsDelMode = false;

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
        mIvDel = (ImageView)findViewById(R.id.gesture_list_del);
        mGestureListView = (ListView)findViewById(R.id.gesture_listview);

        mIvDel.setOnClickListener(this);
        mListViewAdapter = new GestureListViewAdapter();
        mGestureListView.setAdapter(mListViewAdapter);
        mScreenWidth = ScreenUtil.getScreenSize(getContext())[0];
        mGestureListView.setLayoutTransition(new LayoutTransition());

        if(null != attrs){
            TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.GestureList);
            mNeedShowHide = typedArray.getBoolean(R.styleable.GestureList_showHideRequired, false);
        }else{
            mNeedShowHide = false;
        }

        if(mNeedShowHide){
            setTranslationX(-mScreenWidth);
        }
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

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.gesture_list_del:
                mIsDelMode = !mIsDelMode;
                if(mIsDelMode){
                    mIvDel.setImageResource(R.drawable.ic_checkmark);
                }else{
                    mIvDel.setImageResource(R.drawable.ic_del);
                }
                mListViewAdapter.notifyDataSetChanged();
                break;
            case R.id.item_gesture_del:
                if(mIsDelMode){
                    String gestureName = (String)v.getTag();
                    if(null != gestureName && gestureName.length() > 0){
                        Toast.makeText(getContext(), "removing gesture " + gestureName, Toast.LENGTH_SHORT).show();
                        GesturePersistence.removeGesture(getContext(), gestureName);
                        mListViewAdapter.notifyDataSetChanged();
                    }
                }
        }
    }

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
                viewHolder.delIcon = (ImageView)convertView.findViewById(R.id.item_gesture_del);
                convertView.setTag(viewHolder);
            }else{
                viewHolder = (GestureListViewHolder)convertView.getTag();
            }

            if(mIsDelMode){
                viewHolder.delIcon.setOnClickListener(GestureList.this);
                getDeleteBtnAnimatorSet(viewHolder.delIcon, true).start(); // setVisibility() is in AnimatorListener
            }else {
                viewHolder.delIcon.setOnClickListener(null);
                getDeleteBtnAnimatorSet(viewHolder.delIcon, false).start();
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
        public ImageView delIcon;
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
                /*String componentStr = null;
                Drawable packageIcon = TaskManager.getIcon(getContext(), dbData.resolvedComponent);
                String[] descStrs = TaskManager.getDescription(getContext(), dbData.resolvedComponent);
                if(null != descStrs && descStrs.length >= 2){
                    componentStr = descStrs[0];
                }*/

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
                    // set tag, used in onClick()
                    notifyHandlerData.viewHolder.delIcon.setTag(notifyHandlerData.gestureName);
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
        animatorSet.setDuration(500L);
        animatorSet.setInterpolator(new OvershootInterpolator());
        animatorSet.play(transxObjAnimator).with(alphaObjAnimator);
        return animatorSet;
    }

    /**
     * Get the AnimatorSet for showing ImageView of delete button.
     * @return
     */
    private AnimatorSet getDeleteBtnAnimatorSet(final View deleteBtn, final boolean forShown){
        ObjectAnimator alphaObjectAnimator = null;
        if(forShown){
            alphaObjectAnimator = ObjectAnimator.ofFloat(deleteBtn, "alpha", 0.0f, 1.0f);
        }else{
            alphaObjectAnimator = ObjectAnimator.ofFloat(deleteBtn, "alpha", 1.0f, 0.0f);
        }
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setDuration(500L);
        animatorSet.setInterpolator(new OvershootInterpolator());
        animatorSet.play(alphaObjectAnimator);
        animatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                if(forShown){
                    deleteBtn.setVisibility(VISIBLE);
                }
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if(!forShown){
                    deleteBtn.setVisibility(GONE);
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                deleteBtn.setVisibility((forShown? VISIBLE : GONE));
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
        return animatorSet;
    }

    private GradientDrawable mShadowDrawable;
    /**
     * Draw a shadown on the right boundary.
     * @param canvas
     */
    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        if(null == mShadowDrawable){
            mShadowDrawable = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT,
                    new int[]{Color.argb(0x60, 0, 0, 0), Color.argb(0, 0 , 0, 0)});
        }
        mShadowDrawable.setBounds(0, 0, 4, getHeight());
        mShadowDrawable.draw(canvas);
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
}
