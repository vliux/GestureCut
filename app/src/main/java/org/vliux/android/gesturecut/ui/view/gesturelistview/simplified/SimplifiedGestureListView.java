package org.vliux.android.gesturecut.ui.view.gesturelistview.simplified;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

import org.vliux.android.gesturecut.control.PkgRemovedEventBus;

/**
 * Created by vliux on 6/16/14.
 * Simplified ListView in which, for an item, there are only app icon with gesture icon shown at right corner.
 */
public class SimplifiedGestureListView extends ListView {
    private SimplifiedGestureListAdapter mAdapter;

    public SimplifiedGestureListView(Context context) {
        super(context);
        init();
    }

    public SimplifiedGestureListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SimplifiedGestureListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public void registerPkgRemoveEventHandler(){
        PkgRemovedEventBus.getInstance().register(mPkgRemovedHandler);
    }

    public void unregisterPkgRemoveEventHandler(){
        PkgRemovedEventBus.getInstance().unregister(mPkgRemovedHandler);
    }

    private void init(){
        setVerticalScrollBarEnabled(false);
        setHorizontalScrollBarEnabled(false);
        setDivider(null);
        mAdapter = new SimplifiedGestureListAdapter(getContext());
        setAdapter(mAdapter);
    }

    private final PkgRemovedEventBus.PkgRemovedHandler mPkgRemovedHandler = new PkgRemovedEventBus.PkgRemovedHandler() {
        @Override
        public void onEventMainThread(PkgRemovedEventBus.PkgRemovedEvent event) {
            if(null != mAdapter){
                mAdapter.notifyDataSetChanged();
            }
        }
    };

}
