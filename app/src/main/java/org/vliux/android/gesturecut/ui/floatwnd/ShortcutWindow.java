package org.vliux.android.gesturecut.ui.floatwnd;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import org.vliux.android.gesturecut.R;
import org.vliux.android.gesturecut.biz.TaskManager;
import org.vliux.android.gesturecut.biz.db.DbManager;
import org.vliux.android.gesturecut.biz.db.GestureDbTable;
import org.vliux.android.gesturecut.util.ConcurrentManager;
import org.vliux.android.gesturecut.util.GestureUtil;
import org.vliux.android.gesturecut.util.ImageUtil;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by vliux on 1/23/15.
 */
public class ShortcutWindow extends LinearLayout {
    private RecyclerView mRecyclerView;
    private GestureListAdapter mAdapter;

    public ShortcutWindow(Context context) {
        super(context);
        init(context);
    }

    public ShortcutWindow(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ShortcutWindow(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context){
        LayoutInflater.from(context).inflate(R.layout.view_shortcut_wnd, this, true);
        mRecyclerView = (RecyclerView)findViewById(R.id.scw_list_horiz);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        mAdapter = new GestureListAdapter();
    }

    private void refresh(){
        ConcurrentManager.submitJob(mRefreshGesturesBizCallback, mRefreshGesturesUiCallback);
    }

    private ConcurrentManager.IBizCallback<List<String>> mRefreshGesturesBizCallback = new ConcurrentManager.IBizCallback<List<String>>() {
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

    private ConcurrentManager.IUiCallback<List<String>> mRefreshGesturesUiCallback = new ConcurrentManager.IUiCallback<List<String>>() {
        @Override
        public void onPreExecute() {
            mAdapter.gestureNames = null;
        }

        @Override
        public void onPostExecute(List<String> gestureNames) {
            mAdapter.gestureNames = gestureNames;
        }

        @Override
        public void onPregressUpdate(int percent) {
        }

        @Override
        public void onCancelled() {
        }
    };

    /**
     * ViewHolder
     */
    static class GestureListVH extends RecyclerView.ViewHolder{
        ImageView gestureIv;
        ImageView appIv;

        Bitmap gestureBmp;
        Drawable appIconDrawable;

        public GestureListVH(View itemView) {
            super(itemView);
            gestureIv = (ImageView)itemView.findViewById(R.id.scw_li_gesture);
            appIv = (ImageView)itemView.findViewById(R.id.scw_li_app);
        }

        public void setIconsAsync(Bitmap gestureBmp, Drawable appIcon){
            this.gestureBmp = gestureBmp;
            this.appIconDrawable = appIcon;
        }

        public void applyIconsSync(){
            this.gestureIv.setImageBitmap(this.gestureBmp);
            this.appIv.setImageDrawable(this.appIconDrawable);
        }

        public void resetIcons(){
            this.gestureBmp = null;
            this.appIconDrawable = null;
            this.gestureIv.setImageBitmap(null);
            this.appIv.setImageDrawable(null);
        }
    }

    /**
     * Adapter
     */
    class GestureListAdapter extends RecyclerView.Adapter<GestureListVH>{
        private List<String> gestureNames;
        private GestureDbTable dbTable;
        private int gestureIconDimen;

        public GestureListAdapter(){
            dbTable = (GestureDbTable) DbManager.getInstance().getDbTable(GestureDbTable.class);
            gestureIconDimen = (int)getContext().getResources().getDimension(R.dimen.icon_dimen_global_large);
        }

        public void refresh(){
            List<String> gestureNames = new ArrayList<String>();
            gestureNames.addAll(GestureUtil.getInstance().getGestureNames());
            Collections.sort(gestureNames);
        }

        @Override
        public GestureListVH onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(getContext()).inflate(R.layout.item_scw_gesture, null, false);
            return new GestureListVH(view);
        }

        @Override
        public void onBindViewHolder(GestureListVH holder, int position) {
            String gestureName = gestureNames.get(position);
            LoadIconsBizCallback bizCallback = new LoadIconsBizCallback(getContext(), gestureName, holder, dbTable, gestureIconDimen);
            LoadIconsUiCallback uiCallback = new LoadIconsUiCallback(holder);
            ConcurrentManager.submitJob(bizCallback, uiCallback);
        }

        @Override
        public int getItemCount() {
            return null != gestureNames? 0 : gestureNames.size();
        }
    };

    /**
     * Load the gesture & app icons in background.
     */
    static class LoadIconsBizCallback implements ConcurrentManager.IBizCallback<Boolean>{
        private WeakReference<Context> activityContext;
        private GestureListVH viewHolder;
        private String gestureName;
        private GestureDbTable dbTable;
        private int gestureIconDimen;

        public LoadIconsBizCallback(Context context, String gestureName,
                                    GestureListVH viewHolder,
                                    GestureDbTable dbTable,
                                    int gestureIconDimen){
            this.activityContext = new WeakReference<Context>(context);
            this.viewHolder = viewHolder;
            this.gestureName = gestureName;
            this.dbTable = dbTable;
            this.gestureIconDimen = gestureIconDimen;
        }

        @Override
        public Boolean onBusinessLogicAsync(ConcurrentManager.IJob job, Object... params) {
            GestureDbTable.DbData dbData = dbTable.getGesture(gestureName);
            if(null != dbData && null != dbData.resolvedComponent){
                String iconPath = dbData.iconPath;
                Bitmap gestureBmp = null;
                if(null != iconPath && iconPath.length() > 0){
                    gestureBmp = ImageUtil.decodeSampledBitmap(iconPath, gestureIconDimen, gestureIconDimen, ImageUtil.optionSave());
                }

                if(null != gestureBmp){
                    Context context = activityContext.get();
                    if(null != context) {
                        Drawable appIconDrawable = TaskManager.getIcon(context, dbData.resolvedComponent);
                        this.viewHolder.setIconsAsync(gestureBmp, appIconDrawable);
                        return true;
                    }
                }
            }
            return false;
        }
    }

    static class LoadIconsUiCallback implements ConcurrentManager.IUiCallback<Boolean>{
        private GestureListVH viewHolder;

        public LoadIconsUiCallback(GestureListVH viewHolder){
            this.viewHolder = viewHolder;
        }

        @Override
        public void onPreExecute() {
            viewHolder.resetIcons();
        }

        @Override
        public void onPostExecute(Boolean succeeded) {
            if(succeeded){
                viewHolder.applyIconsSync();
            }
        }

        @Override
        public void onPregressUpdate(int percent) {
        }

        @Override
        public void onCancelled() {
        }
    }
}
