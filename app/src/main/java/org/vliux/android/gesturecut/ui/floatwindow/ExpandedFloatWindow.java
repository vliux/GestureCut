package org.vliux.android.gesturecut.ui.floatwindow;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TabHost;

import org.vliux.android.gesturecut.R;

/**
 * Created by vliux on 4/15/14.
 */
public class ExpandedFloatWindow extends LinearLayout {
    private TabHost mTabHost;

    public ExpandedFloatWindow(Context context) {
        super(context);
        init();
    }

    public ExpandedFloatWindow(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ExpandedFloatWindow(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init(){
        LayoutInflater.from(getContext()).inflate(R.layout.view_expnd_floatwindow, this, true);
        mTabHost = (TabHost)findViewById(R.id.tabhost);
        mTabHost.setup();
        mTabHost.setOnTabChangedListener(mOnTabChangeListener);
        mTabHost.addTab(mTabHost.newTabSpec("tab_add").setIndicator("Add gesture").setContent(new DummyTabContent(getContext())));
        mTabHost.addTab(mTabHost.newTabSpec("tab_use").setIndicator("Use gesture").setContent(new DummyTabContent(getContext())));
    }

    private TabHost.OnTabChangeListener mOnTabChangeListener = new TabHost.OnTabChangeListener() {
        @Override
        public void onTabChanged(String tabId) {

        }
    };

    public class DummyTabContent implements TabHost.TabContentFactory {
        private Context mContext;

        public DummyTabContent(Context context){
            mContext = context;
        }

        @Override
        public View createTabContent(String tag) {
            View v = new View(mContext);
            return v;
        }
    }
}
