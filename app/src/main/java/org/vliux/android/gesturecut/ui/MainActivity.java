package org.vliux.android.gesturecut.ui;

import android.app.Activity;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import org.vliux.android.gesturecut.R;
import org.vliux.android.gesturecut.biz.ConcurrentControl;
import org.vliux.android.gesturecut.biz.broadcast.AppBroadcastManager;
import org.vliux.android.gesturecut.ui.view.AppInfoView;
import org.vliux.android.gesturecut.ui.view.baoyz.SwipeMenu;
import org.vliux.android.gesturecut.ui.view.baoyz.SwipeMenuCreator;
import org.vliux.android.gesturecut.ui.view.baoyz.SwipeMenuItem;
import org.vliux.android.gesturecut.ui.view.baoyz.SwipeMenuListView;
import org.vliux.android.gesturecut.ui.view.gesturelistview.detailed.GestureListViewAdapter;
import org.vliux.android.gesturecut.util.GestureUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by vliux on 4/3/14.
 */
public class MainActivity extends Activity {
    SwipeMenuListView mSwipeListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSwipeListView = (SwipeMenuListView)findViewById(R.id.main_listview);
        mSwipeListView.setAdapter(new GestureListViewAdapter(this));
        mSwipeListView.setMenuCreator(mSwipeMenuCreator);
    }

    @Override
    protected void onStop() {
        super.onStop();
        AppBroadcastManager.sendLockerStoppedBroadcast(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    private final SwipeMenuCreator mSwipeMenuCreator = new SwipeMenuCreator() {
        @Override
        public void create(SwipeMenu menu) {
            SwipeMenuItem delete = new SwipeMenuItem(MainActivity.this);
            Resources res = getResources();
            delete.setBackground(R.color.smlv_del_bk);
            delete.setWidth((int)res.getDimension(R.dimen.smlv_del_width));
            delete.setIcon(R.drawable.ic_swipe_delete);
            menu.addMenuItem(delete);
        }
    };

    private final SwipeMenuListView.OnMenuItemClickListener mOnMenuItemClickListener = new SwipeMenuListView.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
            return false;
        }
    };
}
