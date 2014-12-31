package org.vliux.android.gesturecut.activity.add;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.content.Context;

import org.vliux.android.gesturecut.R;

/**
 * Created by vliux on 12/31/14.
 */
public class TabsPresenter {
    private Context mContext;
    private ActionBar mActionBar;

    public TabsPresenter(Context context, ActionBar actionBar){
        mContext = context;
        mActionBar = actionBar;
    }

    public void initTabs(){
        mActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        ActionBar.Tab sysAppTab = mActionBar.newTab().setText(mContext.getText(R.string.add_ges_tab_sys_app)).setTabListener(mTabListener);
        mActionBar.addTab(sysAppTab);

        ActionBar.Tab userAppTab = mActionBar.newTab().setText(mContext.getText(R.string.add_ges_tab_usr_app)).setTabListener(mTabListener);
        mActionBar.addTab(userAppTab);

    }

    private final ActionBar.TabListener mTabListener = new ActionBar.TabListener() {
        @Override
        public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {

        }

        @Override
        public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {

        }

        @Override
        public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {

        }
    };
}
