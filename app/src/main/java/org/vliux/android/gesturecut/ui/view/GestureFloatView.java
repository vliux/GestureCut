package org.vliux.android.gesturecut.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

import org.vliux.android.gesturecut.R;

/**
 * Created by vliux on 12/11/14.
 */
public class GestureFloatView extends FrameLayout {
    public GestureFloatView(Context context) {
        super(context);
        init(context);
    }

    public GestureFloatView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public GestureFloatView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    private void init(Context context){
        LayoutInflater.from(context).inflate(R.layout.view_gesture_float_view, this, true);
    }
}
