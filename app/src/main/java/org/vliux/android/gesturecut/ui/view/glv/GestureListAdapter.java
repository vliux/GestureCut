package org.vliux.android.gesturecut.ui.view.glv;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import org.vliux.android.gesturecut.R;
import org.vliux.android.gesturecut.biz.db.DbManager;
import org.vliux.android.gesturecut.biz.db.GestureDbTable;
import org.vliux.android.gesturecut.model.ResolvedComponent;
import org.vliux.android.gesturecut.ui.view.AppInfoView;
import org.vliux.android.gesturecut.util.ConcurrentManager;
import org.vliux.android.gesturecut.util.ImageUtil;

import java.util.List;

/**
 * Created by vliux on 2/22/15.
 */
class GestureListAdapter extends BaseAdapter {
    static final int ITEM_TYPE_NUM = 3;
    static final int ITEM_TYPE_EMPTY_VIEW = 0;
    static final int ITEM_TYPE_NORMAL = 1;
    static final int ITEM_TYPE_NOT_LOADED = 2;

    private GestureListView mGestureListView;
    private GestureListView.OnGestureIconClickedListener mOnGestureIconClickedListener;
    private List<String> mGestureNames;
    private GestureDbTable mDbTable;
    private int iconDimen;

    public GestureListAdapter(GestureListView glv) {
        mGestureListView = glv;
        mDbTable = (GestureDbTable) DbManager.getInstance().getDbTable(GestureDbTable.class);
        iconDimen = (int)mGestureListView.getResources().getDimension(R.dimen.gesture_list_item_icon_dimen);
    }

    public void setOnGestureIconClickedListener(GestureListView.OnGestureIconClickedListener gicl){
        mOnGestureIconClickedListener = gicl;
    }

    public String getGestureName(int position) {
        if(position >= 0 && position < mGestureNames.size()) {
            return mGestureNames.get(position);
        }else{
            return null;
        }
    }

    public List<String> getGestureNames(){
        return mGestureNames;
    }

    public void setGestureNames(List<String> gestureNames){
        mGestureNames = gestureNames;
    }

    @Override
    public int getCount() {
        if(null == mGestureNames || mGestureNames.size() <= 0){
            return 1; // empty view
        }else{
            return mGestureNames.size();
        }
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
    public int getItemViewType(int position) {
        switch (mGestureListView.getStatus()){
            case NORMAL:
                return ITEM_TYPE_NORMAL;
            case EMPTY_VIEW:
                return ITEM_TYPE_EMPTY_VIEW;
            case NOT_LOADED:
                return ITEM_TYPE_NOT_LOADED;
        }
        return ITEM_TYPE_NORMAL;
    }

    @Override
    public int getViewTypeCount() {
        return ITEM_TYPE_NUM;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        GestureListView.Status status = mGestureListView.getStatus();
        switch (status){
            case EMPTY_VIEW:
                return getEmptyView(convertView);
            case NOT_LOADED:
                return getNotLoadedView(convertView);
        }

        GestureListItem listItem = null;
        if (null == convertView) {
            listItem = new GestureListItem(mGestureListView.getContext());
            // OnGestureItemClickedListener
            if (null != mOnGestureIconClickedListener) {
                final GestureListItem finalListItem = listItem;
                listItem.setOnGestureIconClicked(new View.OnClickListener() {
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
        return listItem;
    }

    private View getEmptyView(View convertView){
        if(null == convertView){
            convertView = LayoutInflater.from(mGestureListView.getContext()).inflate(R.layout.item_gesture_list_empty, null, false);
            //Log.d("vliux", "height of GestureListView=" + GestureListView.this.getHeight());
            convertView.setMinimumHeight(mGestureListView.getHeight());
        }
        return convertView;
    }

    private View getNotLoadedView(View convertView){
        if(null == convertView){
            convertView = LayoutInflater.from(mGestureListView.getContext()).inflate(R.layout.item_gesture_list_not_loaded, null, false);
            convertView.setMinimumHeight(mGestureListView.getHeight());
        }
        return convertView;
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
}
