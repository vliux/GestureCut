package org.vliux.android.gesturecut.activity.add;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.gesture.Gesture;
import android.gesture.GestureOverlayView;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
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
public class AddGestureActivity extends ActionBarActivity {
    private static final String TAG = AddGestureActivity.class.getSimpleName();

    private FrameLayout mLayout;
    private ListView mListView;
    private ProgressBar mProgressBar;
    private PkgListAdapter mListAdapter;
    private int mListItemPaddingHoriz = 0;

    //private AnimPresenter mAnimPresenter;
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
        // use EventBus to receive events from presenters.
        EventBus.getDefault().register(this);

        ActionBar actionBar = getSupportActionBar();
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
        MenuItemCompat.setOnActionExpandListener(mSearchMenu, mOnSearchExpandListener);
        //mSearchMenu.setOnActionExpandListener(mOnSearchExpandListener);

        SearchView searchView = (SearchView) MenuItemCompat.getActionView(mSearchMenu);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_refresh:
                collapseSearchView();
                scanUnGesturedPackagrsAsync(true);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        scanUnGesturedPackagrsAsync(true);
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

    private ConcurrentManager.IJob mScanJob = null;
    private void scanUnGesturedPackagrsAsync(String searchQuery){
        if(null != mScanJob && !mScanJob.isJobCancelled()){
            return;
        }

        mScanJob = ConcurrentManager.submitJob(mScanPkgsBizCallback, mScanPackagesUiCallback,
                mTabsPresenter.getSelectedTab(),
                searchQuery);
    }

    private void scanUnGesturedPackagrsAsync(boolean refreshInstalledAppInfos){
        if(null != mScanJob && !mScanJob.isJobCancelled()){
            return;
        }

        mScanJob = ConcurrentManager.submitJob(mScanPkgsBizCallback, mScanPackagesUiCallback,
                mTabsPresenter.getSelectedTab(),
                refreshInstalledAppInfos);
    }

    private final ConcurrentManager.IBizCallback<List<ResolvedComponent>> mScanPkgsBizCallback = new ConcurrentManager.IBizCallback<List<ResolvedComponent>>() {
        private List<ApplicationInfo> mApplicationInfoList;

        @Override
        public List<ResolvedComponent> onBusinessLogicAsync(ConcurrentManager.IJob job, Object... params) {
            TabsPresenter.TabTag tabTag = (TabsPresenter.TabTag)params[0];
            String searchQuery = null;
            boolean refreshAppInfos = false;
            if(params.length > 1){
                Object param1 = params[1];
                if(param1 instanceof String) {
                    searchQuery = (String) params[1];
                }else if(param1 instanceof Boolean){
                    refreshAppInfos = (Boolean)param1;
                }
            }

            List<ResolvedComponent> ungesturedRcList = new ArrayList<ResolvedComponent>();
            // already gestured package set
            GestureDbTable dbTable = (GestureDbTable)DbManager.getInstance().getDbTable(GestureDbTable.class);
            Set<String> packageNames = dbTable.getGesturedPackageNames();

            PackageManager packageManager = AddGestureActivity.this.getPackageManager();
            if(refreshAppInfos || null == mApplicationInfoList || mApplicationInfoList.size() <= 0) {
                mApplicationInfoList = packageManager.getInstalledApplications(PackageManager.GET_META_DATA);
            }

            int pkgInfoSize = mApplicationInfoList.size();
            for(int i = 0; i < pkgInfoSize; i++){
                job.publishJobProgress(100 * i/pkgInfoSize);
                ApplicationInfo applicationInfo = mApplicationInfoList.get(i);

                if(checkAppType(applicationInfo, tabTag)
                        && !packageNames.contains(applicationInfo.packageName)
                        && checkSearchQuery(applicationInfo, searchQuery, packageManager)) {
                    ResolvedComponent rc = new ResolvedComponent(applicationInfo.packageName);
                    ungesturedRcList.add(rc);
                }
            }
            return ungesturedRcList;
        }

        private boolean checkAppType(ApplicationInfo applicationInfo, TabsPresenter.TabTag tabTag){
            boolean isSystem = (applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 1;
            //AppLog.logd(TAG, String.format("%s is %s system app", applicationInfo.packageName,
            //        isSystem? "a" : "not a"));
            switch (tabTag){
                case SYSTEM_APP:
                    return isSystem;
                case USER_APP:
                    return !isSystem;
            }
            return false;
        }

        private boolean checkSearchQuery(ApplicationInfo applicationInfo, String searchQuery, PackageManager packageManager){
            if(null != searchQuery){
                String appLabel = packageManager.getApplicationLabel(applicationInfo).toString();
                if(null != appLabel && appLabel.toLowerCase().contains(searchQuery)){
                    return true;
                }else{
                    //AppLog.logd(TAG, String.format("%s NOT match search query %s",
                    //        null != appLabel? appLabel : applicationInfo.packageName,
                    //        searchQuery));
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
            mScanJob = null;
        }

        @Override
        public void onPregressUpdate(int percent) {
            mProgressBar.setProgress(percent);
        }

        @Override
        public void onCancelled() {
            mScanJob = null;
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

        /*public ResolvedComponent getResolvedComponent(int position){
            if(null != installedComponents &&
                    position >= 0 && position < installedComponents.size()){
                return installedComponents.get(position);
            }else{
                return null;
            }
        }*/

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
                appInfoView.setPadding(mListItemPaddingHoriz, 0, mListItemPaddingHoriz, 0);
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
            //if(null == mAnimPresenter){
            //    mAnimPresenter = new AnimPresenter(AddGestureActivity.this, view, mLayout, mAddGestureView);
            //    mAnimPresenter.show();
            //}
            if(view instanceof AppInfoView){
                int[] screenLoc = new int[2];
                view.getLocationOnScreen(screenLoc);
                int left = screenLoc[0];
                int top = screenLoc[1];
                int viewWidth = view.getWidth();
                int viewHeight = view.getHeight();
                int animStartX = left + viewWidth/2;
                int animStartY = top + viewHeight/2;

                AppInfoView appInfoView = (AppInfoView)view;
                AddGestureDrawActivity.startForResult(AddGestureActivity.this, appInfoView.getResolvedComponent(), animStartX, animStartY);
            }
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case AddGestureDrawActivity.REQUEST_CODE:
                if(resultCode == RESULT_OK){
                    scanUnGesturedPackagrsAsync(false);
                }
                break;
        }
    }

    private final MenuItemCompat.OnActionExpandListener mOnSearchExpandListener = new MenuItemCompat.OnActionExpandListener() {
        @Override
        public boolean onMenuItemActionExpand(MenuItem item) {
            return true;
        }

        @Override
        public boolean onMenuItemActionCollapse(MenuItem item) {
            scanUnGesturedPackagrsAsync(false);
            return true;
        }
    };

    /**
     * For event bus.
     * @param event
     */
    public void onEventMainThread(AddGestureEvent event){
        AddGestureEvent.EventType eventType = event.getType();
        if(eventType == AddGestureEvent.EventType.TAB_CHANGED){
            scanUnGesturedPackagrsAsync(false);
        }

        collapseSearchView();
    }

    private void collapseSearchView(){
        if(null != mSearchMenu && mSearchMenu.isActionViewExpanded()){
            mSearchMenu.collapseActionView();
        }
    }
}
