package org.vliux.android.gesturecut.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import org.vliux.android.gesturecut.R;

/**
 * Created by vliux on 6/20/14.
 * Framelayout whose height exceeds the screen at the bottom,
 * so the UnlockBar will show entirely when this layout is moved up.
 */
public class ExceededFrameLayout extends FrameLayout {
    public ExceededFrameLayout(Context context) {
        super(context);
    }

    public ExceededFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ExceededFrameLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int halfUnlockBarHeight = (int)getResources().getDimension(R.dimen.gesture_list_item_height) / 2;
        setMeasuredDimension(getMeasuredWidth(), getMeasuredHeight() + halfUnlockBarHeight);
    }
}
