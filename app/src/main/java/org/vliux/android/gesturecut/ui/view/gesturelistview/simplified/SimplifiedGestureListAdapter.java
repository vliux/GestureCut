package org.vliux.android.gesturecut.ui.view.gesturelistview.simplified;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import org.vliux.android.gesturecut.R;
import org.vliux.android.gesturecut.biz.ConcurrentControl;
import org.vliux.android.gesturecut.biz.TaskManager;
import org.vliux.android.gesturecut.biz.db.DbManager;
import org.vliux.android.gesturecut.biz.db.GestureDbTable;
import org.vliux.android.gesturecut.util.GestureUtil;
import org.vliux.android.gesturecut.util.ImageUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by vliux on 6/16/14.
 */
class SimplifiedGestureListAdapter extends BaseAdapter {

    private List<String> mGestureNames;
    private Context mContext;

    public SimplifiedGestureListAdapter(Context context){
        mContext = context;
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
        ViewHolder viewHolder = null;
        if(null == convertView){
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_gesture_simplified, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.mAppIcon = (ImageView)convertView.findViewById(R.id.item_simple_app_icon);
            viewHolder.mGestureIcon = (ImageView)convertView.findViewById(R.id.item_simple_ges_icon);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder)convertView.getTag();
        }

        ConcurrentControl.submitTask(
                new LoadGestureDataRunnable(mHandler,
                        mGestureNames.get(position),
                        viewHolder,
                        mContext.getPackageManager())
        );
        return convertView;
    }

    class ViewHolder{
        public ImageView mAppIcon;
        public ImageView mGestureIcon;
    }

    private class LoadGestureDataRunnable implements Runnable{
        private Handler notifyHandler;
        private String gestureName;
        private ViewHolder viewHolder;
        private int iconWidth;
        private int iconHeight;

        public LoadGestureDataRunnable(Handler handler, String gesName,
                                       ViewHolder vh,
                                       PackageManager pm){
            notifyHandler = handler;
            gestureName = gesName;
            viewHolder = vh;
            iconWidth = (int)mContext.getResources().getDimension(R.dimen.gesture_simple_list_small_icon_dimen);
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
                    notifyHandlerData.appIconBitmap = TaskManager.getIcon(mContext, dbData.resolvedComponent);
                    notifyHandlerData.gestureIconBitmap = bmp;
                    Message message = notifyHandler.obtainMessage(WHAT_ITEM_LOADED, notifyHandlerData);
                    mHandler.sendMessage(message);
                }
            }
        }
    }

    class NotifyHandlerData{
        public ViewHolder viewHolder;
        public Drawable appIconBitmap;
        public Bitmap gestureIconBitmap;
    }

    private final int WHAT_ITEM_LOADED = 100;

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case WHAT_ITEM_LOADED:
                    NotifyHandlerData handlerData = (NotifyHandlerData)msg.obj;
                    handlerData.viewHolder.mAppIcon.setImageDrawable(handlerData.appIconBitmap);
                    handlerData.viewHolder.mGestureIcon.setImageBitmap(handlerData.gestureIconBitmap);
                    break;
            }
        }
    };
}
