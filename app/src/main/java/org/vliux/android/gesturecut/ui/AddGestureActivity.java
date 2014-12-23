package org.vliux.android.gesturecut.ui;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;

import org.vliux.android.gesturecut.R;
import org.vliux.android.gesturecut.model.ResolvedComponent;
import org.vliux.android.gesturecut.ui.view.AppInfoView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vliux on 12/23/14.
 */
public class AddGestureActivity extends Activity {
    private ListView mListView;
    private PkgListAdapter mListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_gesture);

        mListView = (ListView)findViewById(R.id.list_packges);
        mListAdapter = new PkgListAdapter(getPackageManager());
        mListView.setAdapter(mListAdapter);
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
            }

            appInfoView.setResolvedComponent(installedComponents.get(position));
            return appInfoView;
        }
    }
}