package org.vliux.android.gesturecut.ui.view;

import android.content.Context;
import android.gesture.GestureOverlayView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

import org.vliux.android.gesturecut.R;

/**
 * Created by vliux on 12/23/14.
 */
public class AddGestureView extends FrameLayout {
    private GestureOverlayView mGestureOverlayView;
    private GestureConfirmDialog mFwDialog;

    public AddGestureView(Context context) {
        super(context);
        init(context);
    }

    public AddGestureView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public AddGestureView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context){
        LayoutInflater.from(context).inflate(R.layout.view_add_gesture, this, true);
        setBackgroundColor(getResources().getColor(R.color.gesture_create_bg_semi_transparent));
        mGestureOverlayView = (GestureOverlayView)findViewById(R.id.add_ges_overlay);
        mFwDialog = (GestureConfirmDialog)findViewById(R.id.add_gesture_fwdialog);
    }

    public GestureOverlayView getGestureOverlay(){
        return mGestureOverlayView;
    }

    public GestureConfirmDialog getFwDialog(){
        return mFwDialog;
    }
}
