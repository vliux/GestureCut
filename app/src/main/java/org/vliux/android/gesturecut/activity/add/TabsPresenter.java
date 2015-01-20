package org.vliux.android.gesturecut.activity.add;

import android.content.Context;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;

import org.vliux.android.gesturecut.R;

import de.greenrobot.event.EventBus;

/**
 * Created by vliux on 12/31/14.
 */
class TabsPresenter {
    private Context mContext;
    private ActionBar mActionBar;

    static enum TabTag{
        SYSTEM_APP,
        USER_APP
    }

    public TabsPresenter(Context context, ActionBar actionBar){
        mContext = context;
        mActionBar = actionBar;
    }

    public void initTabs(){
        mActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        ActionBar.Tab sysAppTab = mActionBar.newTab().setText(mContext.getText(R.string.add_ges_tab_sys_app))
                .setTabListener(mTabListener)
                .setTag(TabTag.SYSTEM_APP);
        mActionBar.addTab(sysAppTab);

        ActionBar.Tab userAppTab = mActionBar.newTab()
                .setText(mContext.getText(R.string.add_ges_tab_usr_app))
                .setTabListener(mTabListener)
                .setTag(TabTag.USER_APP);
        mActionBar.addTab(userAppTab);
    }

    private TabTag mCurrentTabTag = TabTag.SYSTEM_APP;
    public TabTag getSelectedTab(){
        return mCurrentTabTag;
    }

    private final ActionBar.TabListener mTabListener = new ActionBar.TabListener() {
        @Override
        public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
            mCurrentTabTag = (TabTag)tab.getTag();
            EventBus.getDefault().post(new AddGestureEvent(AddGestureEvent.EventType.TAB_CHANGED));
        }

        @Override
        public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {

        }

        @Override
        public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {
            mCurrentTabTag = (TabTag)tab.getTag();
            EventBus.getDefault().post(new AddGestureEvent(AddGestureEvent.EventType.TAB_CHANGED));
        }
    };
}
