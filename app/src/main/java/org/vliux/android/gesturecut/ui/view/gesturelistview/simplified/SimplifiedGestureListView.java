package org.vliux.android.gesturecut.ui.view.gesturelistview.simplified;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ListView;

import org.vliux.android.gesturecut.AppConstant;

/**
 * Created by vliux on 6/16/14.
 * Simplified ListView in which, for an item, there are only app icon with gesture icon shown at right corner.
 */
public class SimplifiedGestureListView extends ListView {

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

    private void init(){
        setVerticalScrollBarEnabled(false);
        setHorizontalScrollBarEnabled(false);
        setDivider(null);
        setAdapter(new SimplifiedGestureListAdapter(getContext()));
    }

}
