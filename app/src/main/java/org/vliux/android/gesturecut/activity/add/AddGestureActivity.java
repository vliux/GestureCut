package org.vliux.android.gesturecut.activity.add;

import android.app.ActionBar;
import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.gesture.Gesture;
import android.gesture.GestureOverlayView;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SearchView;

import org.vliux.android.gesturecut.R;
import org.vliux.android.gesturecut.biz.db.DbManager;
import org.vliux.android.gesturecut.biz.db.GestureDbTable;
import org.vliux.android.gesturecut.model.ResolvedComponent;
import org.vliux.android.gesturecut.ui.view.AddGestureView;
import org.vliux.android.gesturecut.ui.view.AppInfoView;
import org.vliux.android.gesturecut.util.AppLog;
import org.vliux.android.gesturecut.util.ConcurrentManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import de.greenrobot.event.EventBus;

/**
 * Created by vliux on 12/23/14.
 */
public class AddGestureActivity extends Activity {
    private static final String TAG = AddGestureActivity.class.getSimpleName();

    private FrameLayout mLayout;
    private ListView mListView;
    private ProgressBar mProgressBar;
    private PkgListAdapter mListAdapter;
    private int mListItemPaddingHoriz = 0;
    private int mListItemPaddingVerti = 0;

    private AddGestureView mAddGestureView;
    private AnimPresenter mAnimPresenter;
    private TabsPresenter mTabsPresenter;

    private MenuItem mSearchMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_gesture);

        mProgressBar = (ProgressBar)findViewById(R.id.add_ges_prog_bar);

        mLayout = (FrameLayout)findViewById(R.id.add_ges_layout_top);
        mListView = (ListView)findViewById(R.id.list_packges);
        mListAdapter = new PkgListAdapter(getPackageManager());
        mListView.setAdapter(mListAdapter);
        mListView.setOnItemClickListener(mOnItemClickListener);

        mListItemPaddingHoriz = (int)getResources().getDimension(R.dimen.gesture_list_outter_margin);
        mListItemPaddingVerti = (int)getResources().getDimension(R.dimen.gesture_list_item_padding_vertical);
        mAddGestureView = new AddGestureView(this);
        mAddGestureView.getGestureOverlay().addOnGesturePerformedListener(mOnGesturePerformed);
        // use EventBus to receive events from presenters.
        EventBus.getDefault().register(this);

        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        mTabsPresenter = new TabsPresenter(this, actionBar);
        mTabsPresenter.initTabs();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        if(Intent.ACTION_SEARCH.equals(intent.getAction())){
            String query = intent.getStringExtra(SearchManager.QUERY);
            scanUnGesturedPackagrsAsync(query);
        }else {
            super.onNewIntent(intent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.activity_add_gesture, menu);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        mSearchMenu = menu.findItem(R.id.action_search);
        mSearchMenu.setOnActionExpandListener(mOnSearchExpandListener);

        SearchView searchView = (SearchView) mSearchMenu.getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        return super.onCreateOptionsMenu(menu);
    }

    private ConcurrentManager.IJob mScanJob;
    @Override
    protected void onStart() {
        super.onStart();
        mScanJob = scanUnGesturedPackagrsAsync();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(null != mScanJob && !mScanJob.isJobCancelled()){
            mScanJob.cancelJob();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }


    private ConcurrentManager.IJob scanUnGesturedPackagrsAsync(String searchQuery){
        return ConcurrentManager.submitJob(mScanPkgsBizCallback, mScanPackagesUiCallback,
                mTabsPresenter.getSelectedTab(),
                searchQuery);
    }

    private ConcurrentManager.IJob scanUnGesturedPackagrsAsync(){
        return ConcurrentManager.submitJob(mScanPkgsBizCallback, mScanPackagesUiCallback,
                mTabsPresenter.getSelectedTab());
    }

    private final ConcurrentManager.IBizCallback<List<ResolvedComponent>> mScanPkgsBizCallback = new ConcurrentManager.IBizCallback<List<ResolvedComponent>>() {
        @Override
        public List<ResolvedComponent> onBusinessLogicAsync(ConcurrentManager.IJob job, Object... params) {
            TabsPresenter.TabTag tabTag = (TabsPresenter.TabTag)params[0];
            String searchQuery = null;
            if(params.length > 1){
                searchQuery = (String)params[1];
            }

            List<ResolvedComponent> ungesturedRcList = new ArrayList<ResolvedComponent>();
            // already gestured package set
            GestureDbTable dbTable = (GestureDbTable)DbManager.getInstance().getDbTable(GestureDbTable.class);
            Set<String> packageNames = dbTable.getGesturedPackageNames();

            PackageManager packageManager = AddGestureActivity.this.getPackageManager();
            List<PackageInfo> pkgInfoList = packageManager.getInstalledPackages(PackageManager.GET_META_DATA);

            int pkgInfoSize = pkgInfoList.size();
            for(int i = 0; i < pkgInfoSize; i++){
                job.publishJobProgress(100 * i/pkgInfoSize);
                PackageInfo pkgInfo = pkgInfoList.get(i);

                if(checkAppInfoExists(pkgInfo)
                        && checkAppType(pkgInfo, tabTag)
                        && checkSearchQuery(pkgInfo, searchQuery, packageManager)) {

                    if (!packageNames.contains(pkgInfo.packageName)) {
                        ResolvedComponent rc = new ResolvedComponent(pkgInfo.packageName);
                        ungesturedRcList.add(rc);
                    }
                }
            }
            return ungesturedRcList;
        }

        private boolean checkAppType(PackageInfo packageInfo, TabsPresenter.TabTag tabTag){
            boolean isSystem = (packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 1;
            AppLog.logd(TAG, String.format("%s is %s system app", packageInfo.packageName,
                    isSystem? "a" : "not a"));
            switch (tabTag){
                case SYSTEM_APP:
                    return isSystem;
                case USER_APP:
                    return !isSystem;
            }
            return false;
        }

        private boolean checkAppInfoExists(PackageInfo packageInfo){
            boolean condition = (null != packageInfo.applicationInfo);
            AppLog.logd(TAG, String.format("%s %s application",
                    packageInfo.packageName,
                    condition? "has" : "has NO"));
            return condition;
        }

        private boolean checkSearchQuery(PackageInfo packageInfo, String searchQuery, PackageManager packageManager){
            if(null != searchQuery){
                ApplicationInfo applicationInfo = packageInfo.applicationInfo;
                String appLabel = packageManager.getApplicationLabel(applicationInfo).toString();
                if(null != appLabel && !appLabel.toLowerCase().contains(searchQuery)){
                    return true;
                }else{
                    AppLog.logd(TAG, String.format("%s NOT match search query %s",
                            null != appLabel? appLabel : packageInfo.packageName,
                            searchQuery));
                    return false;
                }
            }else{
                return true;
            }
        }
    };

    private final ConcurrentManager.IUiCallback<List<ResolvedComponent>> mScanPackagesUiCallback = new ConcurrentManager.IUiCallback<List<ResolvedComponent>>() {
        @Override
        public void onPreExecute() {
            mProgressBar.setVisibility(View.VISIBLE);
            mProgressBar.setProgress(0);
        }

        @Override
        public void onPostExecute(List<ResolvedComponent> resolvedComponents) {
            mListAdapter.installedComponents = resolvedComponents;
            mListAdapter.notifyDataSetChanged();
            mProgressBar.setVisibility(View.GONE);
        }

        @Override
        public void onPregressUpdate(int percent) {
            mProgressBar.setProgress(percent);
        }

        @Override
        public void onCancelled() {

        }
    };

    /**
     * Adapter of ListView.
     */
    class PkgListAdapter extends BaseAdapter{
        private List<ResolvedComponent> installedComponents;

        public PkgListAdapter(PackageManager pkgMgr){
            installedComponents = new ArrayList<ResolvedComponent>();
        }

        @Override
        public int getCount() {
            return null != installedComponents? installedComponents.size() : 0;
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

    private final int WHAT_DATA_LOAD_COMPLETE = 100;
    private final Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case WHAT_DATA_LOAD_COMPLETE:
                    List<ResolvedComponent> rcList = (List<ResolvedComponent>)msg.obj;
                    mListAdapter.installedComponents = rcList;
                    mListAdapter.notifyDataSetChanged();
                    break;
            }
        }
    };

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

    private final MenuItem.OnActionExpandListener mOnSearchExpandListener = new MenuItem.OnActionExpandListener() {
        @Override
        public boolean onMenuItemActionExpand(MenuItem item) {
            return true;
        }

        @Override
        public boolean onMenuItemActionCollapse(MenuItem item) {
            if(null != mAnimPresenter) {
                mAnimPresenter.close();
                mAnimPresenter = null;
            }
            scanUnGesturedPackagrsAsync();
            return true;
        }
    };



    /**
     * For event bus.
     * @param event
     */
    public void onEventMainThread(AddGestureEvent event){
        AddGestureEvent.EventType eventType = event.getType();
        if(eventType == AddGestureEvent.EventType.GESTURE_ADDED){
            if(null != mAnimPresenter) {
                mAnimPresenter.close();
                mAnimPresenter = null;
            }
            scanUnGesturedPackagrsAsync();
        }else if(eventType == AddGestureEvent.EventType.TAB_CHANGED){
            scanUnGesturedPackagrsAsync();
        }

        // close SearchView if it's expended, for both events
        if(null != mSearchMenu && mSearchMenu.isActionViewExpanded()){
            mSearchMenu.collapseActionView();
        }
    }
}
