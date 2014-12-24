package org.vliux.android.gesturecut.activity.add;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.gesture.Gesture;
import android.gesture.GestureOverlayView;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;

import org.vliux.android.gesturecut.R;
import org.vliux.android.gesturecut.model.ResolvedComponent;
import org.vliux.android.gesturecut.ui.view.AddGestureView;
import org.vliux.android.gesturecut.ui.view.AppInfoView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vliux on 12/23/14.
 */
public class AddGestureActivity extends Activity {
    private FrameLayout mLayout;
    private ListView mListView;
    private PkgListAdapter mListAdapter;
    private int mListItemPaddingHoriz = 0;
    private int mListItemPaddingVerti = 0;

    private AddGestureView mAddGestureView;
    private AnimPresenter mAnimPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_gesture);

        mLayout = (FrameLayout)findViewById(R.id.add_ges_layout_top);
        mListView = (ListView)findViewById(R.id.list_packges);
        mListAdapter = new PkgListAdapter(getPackageManager());
        mListView.setAdapter(mListAdapter);
        mListView.setOnItemClickListener(mOnItemClickListener);

        mListItemPaddingHoriz = (int)getResources().getDimension(R.dimen.gesture_list_outter_margin);
        mListItemPaddingVerti = (int)getResources().getDimension(R.dimen.gesture_list_item_padding_vertical);
        mAddGestureView = new AddGestureView(this);
        mAddGestureView.getGestureOverlay().addOnGesturePerformedListener(mOnGesturePerformed);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mListAdapter.scanInstalledPackages();
    }

    class PkgListAdapter extends BaseAdapter{
        private List<ResolvedComponent> installedComponents;
        private PackageManager packageManager;

        public PkgListAdapter(PackageManager pkgMgr){
            packageManager = pkgMgr;
            installedComponents = new ArrayList<ResolvedComponent>();
        }

        private void scanInstalledPackages(){
            installedComponents.clear();
            List<PackageInfo> pkgInfoList = packageManager.getInstalledPackages(PackageManager.GET_META_DATA);
            for(PackageInfo pkgInfo : pkgInfoList){
                ResolvedComponent rc = new ResolvedComponent(pkgInfo.packageName);
                installedComponents.add(rc);
            }
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return installedComponents.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            AppInfoView appInfoView = null;
            if(null != convertView){
                appInfoView = (AppInfoView)convertView;
            }else{
                appInfoView = new AppInfoView(AddGestureActivity.this);
                appInfoView.setPadding(mListItemPaddingHoriz, mListItemPaddingVerti, mListItemPaddingHoriz, mListItemPaddingVerti);
            }

            appInfoView.setResolvedComponent(installedComponents.get(position));
            return appInfoView;
        }
    } // end of adapter

    private final AdapterView.OnItemClickListener mOnItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
            if(null == mAnimPresenter){
                mAnimPresenter = new AnimPresenter(AddGestureActivity.this, view, mLayout, mAddGestureView);
                mAnimPresenter.show();
            }
        }
    };

    @Override
    public void onBackPressed() {
        if(null != mAnimPresenter){
            mAnimPresenter.close();
            mAnimPresenter = null;
        }else {
            super.onBackPressed();
        }
    }

    private final GestureOverlayView.OnGesturePerformedListener mOnGesturePerformed = new GestureOverlayView.OnGesturePerformedListener() {
        @Override
        public void onGesturePerformed(GestureOverlayView overlay, Gesture gesture) {
            if(null != mAnimPresenter){
                ResolvedComponent rc = mAnimPresenter.getRelatedResolvedComponent();
                if(null != rc){
                    GesturePerformedPresenter presenter = new GesturePerformedPresenter(AddGestureActivity.this, rc);
                    presenter.addGesture(gesture);
                }
            }
        }
    };
}
