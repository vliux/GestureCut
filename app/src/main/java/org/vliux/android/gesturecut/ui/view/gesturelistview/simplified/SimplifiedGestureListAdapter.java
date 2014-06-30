package org.vliux.android.gesturecut.ui.view.gesturelistview.simplified;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import org.vliux.android.gesturecut.R;
import org.vliux.android.gesturecut.biz.ConcurrentControl;
import org.vliux.android.gesturecut.control.PkgRemovedEventBus;
import org.vliux.android.gesturecut.model.ResolvedComponent;
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
    private final Context mContext;

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
        SimpListViewItem listViewItem = null;

        if(null == convertView){
            listViewItem = new SimpListViewItem(mContext);
        }else{
            listViewItem = (SimpListViewItem)convertView;
        }

        ConcurrentControl.submitTask(
                new LoadGestureDataRunnable(mHandler,
                        mGestureNames.get(position),
                        listViewItem)
        );
        return listViewItem;
    }

    private class LoadGestureDataRunnable implements Runnable{
        private final Handler notifyHandler;
        private final String gestureName;
        private final SimpListViewItem listViewItem;
        private final int iconWidth;
        private final int iconHeight;

        public LoadGestureDataRunnable(Handler handler, String gesName,
                                       SimpListViewItem item){
            notifyHandler = handler;
            gestureName = gesName;
            listViewItem = item;
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
                    notifyHandlerData.listViewItem = listViewItem;
                    notifyHandlerData.appIconBitmap = TaskManager.getIcon(mContext, dbData.resolvedComponent);
                    notifyHandlerData.gestureIconBitmap = bmp;
                    notifyHandlerData.resolvedComponent = dbData.resolvedComponent;
                    Message message = notifyHandler.obtainMessage(WHAT_ITEM_LOADED, notifyHandlerData);
                    mHandler.sendMessage(message);
                }
            }
        }
    }

    class NotifyHandlerData{
        public SimpListViewItem listViewItem;
        public Drawable appIconBitmap;
        public Bitmap gestureIconBitmap;
        public ResolvedComponent resolvedComponent;
    }

    private final int WHAT_ITEM_LOADED = 100;

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case WHAT_ITEM_LOADED:
                    NotifyHandlerData handlerData = (NotifyHandlerData)msg.obj;
                    SimpListViewItem simpListViewItem = handlerData.listViewItem;
                    simpListViewItem.setAppIcon(handlerData.appIconBitmap);
                    simpListViewItem.setGestureIcon(handlerData.gestureIconBitmap);
                    simpListViewItem.setResolvedComponent(handlerData.resolvedComponent);
                    break;
            }
        }
    };
}
