package org.vliux.android.gesturecut.ui.floatwnd;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;
import android.widget.FrameLayout;

import org.vliux.android.gesturecut.R;
import org.vliux.android.gesturecut.ui.view.GestureListView;

/**
 * Created by vliux on 1/23/15.
 */
public class ShortcutWindow extends FrameLayout {
    private GestureListView mGestureListView;

    public ShortcutWindow(Context context) {
        super(context);
        init(context);
    }

    public ShortcutWindow(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ShortcutWindow(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context){
        LayoutInflater.from(context).inflate(R.layout.view_shortcut, this, true);
        mGestureListView = (GestureListView)findViewById(R.id.sc_gesture_list);
        mGestureListView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                mGestureListView.getViewTreeObserver().removeOnPreDrawListener(this);
                startShowAnim();
                return true;
            }
        });
        mGestureListView.refresh();
    }

    private void startShowAnim(){
        int[] wndLoc = FloatWindowManager.parseLocationFromPrefs(getContext());
        mGestureListView.setScaleY(0.3f);
        mGestureListView.setScaleX(0.3f);

        mGestureListView.setPivotX(wndLoc[0]);
        mGestureListView.setPivotY(wndLoc[1]);
        mGestureListView.animate().scaleY(1).scaleX(1).setDuration(300L).setInterpolator(new AccelerateInterpolator()).start();
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if(event.getKeyCode() == KeyEvent.KEYCODE_BACK
                && KeyEvent.ACTION_UP == event.getAction()){
            FloatWindowManager.closeWindow(getContext(), this);
            return true;
        }else{
            return super.dispatchKeyEvent(event);
        }
    }

}
