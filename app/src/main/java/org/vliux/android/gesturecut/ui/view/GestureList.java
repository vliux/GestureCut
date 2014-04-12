package org.vliux.android.gesturecut.ui.view;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import org.vliux.android.gesturecut.R;
import org.vliux.android.gesturecut.biz.ConcurrentControl;
import org.vliux.android.gesturecut.biz.db.DbManager;
import org.vliux.android.gesturecut.biz.db.GestureDbTable;
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
    private ImageView mIvAdd;
    private ImageView mIvSettings;
    private ListView mGestureListView;
    private int mScreenWidth;
    private boolean mIsShown = false;

    public GestureList(Context context) {
        super(context);
        init();
    }

    public GestureList(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public GestureList(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init(){
        LayoutInflater.from(getContext()).inflate(R.layout.view_gesture_list, this, true);
        mIvAdd = (ImageView)findViewById(R.id.gesture_list_add);
        mIvSettings = (ImageView)findViewById(R.id.gesture_list_settings);
        mGestureListView = (ListView)findViewById(R.id.gesture_listview);

        mIvAdd.setOnClickListener(this);
        mIvSettings.setOnClickListener(this);
        mGestureListView.setAdapter(new GestureListViewAdapter());
        mScreenWidth = ScreenUtil.getScreenSize(getContext())[0];
        setTranslationX(-mScreenWidth);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.gesture_list_add:
                break;
            case R.id.gesture_list_settings:
                break;
        }
    }

    private class GestureListViewAdapter extends BaseAdapter{
        private List<String> mGestureNames;

        public GestureListViewAdapter(){
            mGestureNames = new ArrayList<String>();
            mGestureNames.addAll(GestureUtil.getInstance().getGestureNames());
            Collections.sort(mGestureNames);
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
                viewHolder.appIcon = (ImageView)convertView.findViewById(R.id.item_gesture_app_icon);
                viewHolder.textView = (TextView)convertView.findViewById(R.id.item_gesture_tv);
                convertView.setTag(viewHolder);
            }else{
                viewHolder = (GestureListViewHolder)convertView.getTag();
            }

            ConcurrentControl.submitTask(
                    new LoadGestureDataRunnable(mHandler, mGestureNames.get(position), viewHolder));
            return convertView;
        }
    };

    private class GestureListViewHolder{
        public ImageView gestureIcon;
        public ImageView appIcon;
        public TextView textView;
    }

    private class LoadGestureDataRunnable implements Runnable{
        private Handler notifyHandler;
        private String gestureName;
        private GestureListViewHolder viewHolder;
        private int iconWidth;
        private int iconHeight;

        public LoadGestureDataRunnable(Handler handler, String gesName, GestureListViewHolder vh){
            notifyHandler = handler;
            gestureName = gesName;
            viewHolder = vh;
            iconWidth = (int)getContext().getResources().getDimension(R.dimen.gesture_list_item_icon_dimen);
            iconHeight = iconWidth;
        }

        @Override
        public void run() {
            GestureDbTable gestureDbTable = (GestureDbTable) DbManager.getInstance().getDbTable(GestureDbTable.class);
            ContentValues gestureValues = gestureDbTable.getGesture(gestureName);
            String iconPath = gestureValues.getAsString(GestureDbTable.DB_COL_GESTURE_ICON_PATH_TEXT_1);
            Bitmap bmp = null;
            if(null != iconPath && iconPath.length() > 0){
                bmp = ImageUtil.decodeSampledBitmap(iconPath, iconWidth, iconHeight, ImageUtil.optionSave());
            }
            String componentStr = gestureValues.getAsString(GestureDbTable.DB_COL_COMPONENT_NAME_TEXT_1);

            if(null != bmp || null != componentStr){
                NotifyHandlerData notifyHandlerData = new NotifyHandlerData();
                notifyHandlerData.viewHolder = viewHolder;
                notifyHandlerData.bitmap = bmp;
                notifyHandlerData.componentStr = componentStr;
                Message message = notifyHandler.obtainMessage(WHAT_GESTURE_ICON_LOADED, notifyHandlerData);
                mHandler.sendMessage(message);
            }
        }
    }

    private class NotifyHandlerData{
        public GestureListViewHolder viewHolder;
        public Bitmap bitmap;
        public String componentStr;
    }

    public static final int WHAT_GESTURE_ICON_LOADED = 100;

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case WHAT_GESTURE_ICON_LOADED:
                    NotifyHandlerData notifyHandlerData = (NotifyHandlerData)msg.obj;
                    notifyHandlerData.viewHolder.gestureIcon.setImageBitmap(notifyHandlerData.bitmap);
                    notifyHandlerData.viewHolder.textView.setText(notifyHandlerData.componentStr);
                    break;
            }
        }
    };

    public boolean isShown(){
        return mIsShown;
    }

    public void show(){
        mIsShown = true;
        getAnimatorSet(true).start();
    }

    public void hide(){
        mIsShown = false;
        getAnimatorSet(false).start();
    }

    private AnimatorSet getAnimatorSet(boolean forShown){
        ObjectAnimator transxObjAnimator = null;
        ObjectAnimator alphaObjAnimator = null;
        if(forShown) {
            // animators for showing
            transxObjAnimator = ObjectAnimator.ofFloat(this, "translationX", -mScreenWidth, 0);
            alphaObjAnimator = ObjectAnimator.ofFloat(this, "alpha", 0.8f, 1.0f);
        }else{
            // animators for hiding
            transxObjAnimator = ObjectAnimator.ofFloat(this, "translationX", 0, -mScreenWidth);
            alphaObjAnimator = ObjectAnimator.ofFloat(this, "alpha", 1.0f, 0.8f);
        }

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setDuration(500L);
        animatorSet.setInterpolator(new OvershootInterpolator());
        animatorSet.play(transxObjAnimator).with(alphaObjAnimator);
        return animatorSet;
    }
}
