package org.vliux.android.gesturecut.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import org.vliux.android.gesturecut.R;

/**
 * Created by vliux on 4/15/14.
 */
public class UnlockBar extends LinearLayout {
    public UnlockBar(Context context) {
        super(context);
        init();
    }

    public UnlockBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public UnlockBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init(){
        LayoutInflater.from(getContext()).inflate(R.layout.view_unlock_bar, this, true);
    }
}
