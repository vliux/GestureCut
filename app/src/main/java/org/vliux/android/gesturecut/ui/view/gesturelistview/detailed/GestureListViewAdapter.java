package org.vliux.android.gesturecut.ui.view.gesturelistview.detailed;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import org.vliux.android.gesturecut.R;
import org.vliux.android.gesturecut.biz.ConcurrentControl;
import org.vliux.android.gesturecut.biz.db.DbManager;
import org.vliux.android.gesturecut.biz.db.GestureDbTable;
import org.vliux.android.gesturecut.model.ResolvedComponent;
import org.vliux.android.gesturecut.ui.view.AppInfoView;
import org.vliux.android.gesturecut.util.GestureUtil;
import org.vliux.android.gesturecut.util.ImageUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by vliux on 12/9/14.
 */
public class GestureListViewAdapter extends BaseAdapter {
    private List<String> mGestureNames;
    private Context mContext;

    public GestureListViewAdapter(Context context){
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_gesture, parent, false);
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
                        mContext.getPackageManager()));
        return convertView;
    }

    private class GestureListViewHolder{
        public ImageView gestureIcon;
        //public ImageView appIcon;
        //public TextView textView;
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
            iconWidth = (int)mContext.getResources().getDimension(R.dimen.gesture_list_item_icon_dimen);
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
}
