package org.vliux.android.gesturecut.ui.floatwindow;

import android.content.ComponentName;
import android.content.Context;
import android.gesture.Gesture;
import android.gesture.GestureOverlayView;
import android.graphics.Color;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import org.vliux.android.gesturecut.R;
import org.vliux.android.gesturecut.biz.ResolvedComponent;
import org.vliux.android.gesturecut.biz.TaskManager;
import org.vliux.android.gesturecut.biz.gesture.GesturePersistence;
import org.vliux.android.gesturecut.util.WindowManagerUtil;

/**
 * Created by vliux on 4/9/14.
 */
public class SecondaryFloatWindow extends LinearLayout
        implements View.OnClickListener,
        TabLikeView.OnTablikeChangedListener {

    private GestureOverlayView mGestureOverlayView;
    private TabLikeView mTabLikeView;
    private TextView mTvHint;
    private FwDialogView mFwDialog;

    public SecondaryFloatWindow(Context context) {
        super(context);
        init();
    }

    public SecondaryFloatWindow(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SecondaryFloatWindow(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init(){
        LayoutInflater.from(getContext()).inflate(R.layout.view_2nd_floatwindow, this, true);
        mGestureOverlayView = (GestureOverlayView)findViewById(R.id.gesture_overlay);
        mTabLikeView = (TabLikeView)findViewById(R.id.gesture_tablike);
        mTvHint = (TextView)findViewById(R.id.gesture_hint);
        mFwDialog = (FwDialogView)findViewById(R.id.gesture_fwdialog);

        mGestureOverlayView.setGestureColor(Color.RED);
        mGestureOverlayView.addOnGesturePerformedListener(mOnGesturePerformedListener);
        mTabLikeView.setOnTabChangedListener(this);
        refreshHint(mTabLikeView.getType());
    }

    public void onClick(View view){
        switch (view.getId()){
        }
    }

    private GestureOverlayView.OnGesturePerformedListener mOnGesturePerformedListener = new GestureOverlayView.OnGesturePerformedListener() {
        @Override
        public void onGesturePerformed(GestureOverlayView overlay, final Gesture gesture) {
            switch (mTabLikeView.getType()){
                case ADD:
                    addGesture(gesture);
                    break;
                case USE:
                    useGesture(gesture);
                    break;
            }
        }
    };

    private void useGesture(Gesture gesture){
        ResolvedComponent resolvedComponent = GesturePersistence.loadGesture(getContext(), gesture);
        if(null != resolvedComponent){
            Toast.makeText(getContext(),
                    getContext().getString(R.string.start_activity_from_gesture),
                    Toast.LENGTH_SHORT).show();
            resolvedComponent.startActivity(getContext());
            WindowManagerUtil.closeWindow(getContext().getApplicationContext(), SecondaryFloatWindow.this);
        }
    }

    private void addGesture(final Gesture gesture){
        final ResolvedComponent resolvedComponent = TaskManager.getTopComponent(getContext());
        if(null != resolvedComponent){
            //Toast.makeText(getContext(), getContext().getString(R.string.saving_gesture), Toast.LENGTH_SHORT).show();
            mFwDialog.show("Add new gesture",
                    "Are you sure to add this new gesture into your gesture store?",
                    new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            try {
                                GesturePersistence.saveGesture(getContext(), gesture, resolvedComponent);
                                WindowManagerUtil.closeWindow(getContext().getApplicationContext(), SecondaryFloatWindow.this);
                            } catch (GesturePersistence.GestureLibraryException e) {
                                e.printStackTrace();
                            } catch (GesturePersistence.GestureSaveIconException e) {
                                e.printStackTrace();
                            } catch (GesturePersistence.GestureDbException e) {
                                e.printStackTrace();
                            }
                        }
                    },
                    new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mFwDialog.hide();
                        }
                    });
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if(event.getKeyCode() == KeyEvent.KEYCODE_BACK){
            WindowManagerUtil.closeWindow(getContext().getApplicationContext(), this);
            return true;
        }else{
            return super.dispatchKeyEvent(event);
        }
    }

    @Override
    public void onTabSwitched(TabLikeView.TabType newType) {
        refreshHint(newType);
    }

    private void refreshHint(TabLikeView.TabType tabType){
        switch (tabType){
            case ADD:
                mTvHint.setText(getContext().getString(R.string.gesture_bg_title_record));
                break;
            case USE:
                mTvHint.setText(getContext().getString(R.string.gesture_bg_title_use));
                break;
        }
    }
}
