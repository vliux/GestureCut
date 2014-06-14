package org.vliux.android.gesturecut.ui.floatwindow;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.gesture.Gesture;
import android.gesture.GestureOverlayView;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.vliux.android.gesturecut.AppConstant;
import org.vliux.android.gesturecut.R;
import org.vliux.android.gesturecut.biz.ResolvedComponent;
import org.vliux.android.gesturecut.biz.TaskFilterManager;
import org.vliux.android.gesturecut.biz.TaskManager;
import org.vliux.android.gesturecut.biz.db.GestureDbTable;
import org.vliux.android.gesturecut.biz.gesture.GesturePersistence;
import org.vliux.android.gesturecut.biz.taskfilters.TaskFilterException;
import org.vliux.android.gesturecut.ui.GestureListActivity;
import org.vliux.android.gesturecut.ui.view.AppInfoView;
import org.vliux.android.gesturecut.util.AnimUtil;
import org.vliux.android.gesturecut.util.ScreenUtil;
import org.vliux.android.gesturecut.util.WindowManagerUtil;

/**
 * Created by vliux on 4/9/14.
 */
public class SecondaryFloatWindow extends LinearLayout
        implements View.OnClickListener,
        TabLikeView.OnTablikeChangedListener {

    private ImageView mIvMore; // button at right-top corner of action bar
    private GestureOverlayView mGestureOverlayView;
    private TabLikeView mTabLikeView;
    private TextView mTvHint;
    private FwDialogView mFwDialog;

    private AppInfoView mAppInfoView; // app info shown when adding new gesture
    private LinearLayout mAppInfoLayout; // layout containing AppInfoView
    private ImageView mIvAppIconUseAnim; // app icon for animator when using gesture
    private TextView mTvInvalidRc; // show warning for invalid ResolvedComponent

    /* ResolvedComponent as a gesture target. This variable is is used for kepping the reference,
     * as far as, when actually saving the new gesture, the top-level component may be different than what is shown to user.
     */
    private ResolvedComponent mResolvedComponent;

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
        mAppInfoView = (AppInfoView)findViewById(R.id.gesture_appinfoview);
        mIvAppIconUseAnim = (ImageView)findViewById(R.id.gesture_appicon_startactiv);
        mAppInfoLayout = (LinearLayout)findViewById(R.id.gesture_appinfo_layout);
        mIvMore = (ImageView)findViewById(R.id.gesture_more);
        mTvInvalidRc = (TextView)findViewById(R.id.gesture_tv_invalid_rc);

        mGestureOverlayView.setGestureColor(Color.RED);
        mGestureOverlayView.addOnGesturePerformedListener(mOnGesturePerformedListener);
        mTabLikeView.setOnTabChangedListener(this);
        refreshHint(mTabLikeView.getType());
        mIvMore.setOnClickListener(this);
    }

    public void onClick(View view){
        switch (view.getId()){
            case R.id.gesture_more:
                Intent intent = new Intent(getContext(), GestureListActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                getContext().startActivity(intent);
                WindowManagerUtil.closeWindow(getContext(), this);
                break;
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
            AnimUtil.getStartActivityAnimatorSet(getContext(), mIvAppIconUseAnim, resolvedComponent, new Runnable(){
                @Override
                public void run() {
                    WindowManagerUtil.closeWindow(getContext(), SecondaryFloatWindow.this);
                }
            }).start();
        }else{
            Toast.makeText(getContext(),
                    getContext().getString(R.string.no_gesture_match), Toast.LENGTH_SHORT).show();
        }
    }

    private void addGesture(final Gesture gesture){
        if(null != mResolvedComponent){
            //Toast.makeText(getContext(), getContext().getString(R.string.saving_gesture), Toast.LENGTH_SHORT).show();
            GestureDbTable.DbData dbData = GesturePersistence.loadGestureEx(getContext(), gesture);
            if(null != dbData && null != dbData.resolvedComponent){
                mFwDialog.showAlert(getContext().getString(R.string.add_gesture_alert_duplicate),
                        getContext().getString(R.string.add_gesture_alert_duplicate_content),
                        gesture, dbData,
                        new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mFwDialog.hide();
                            }
                        });
            }else{
                mFwDialog.showConfirm(getContext().getString(R.string.add_gesture_confirm_title),
                        getContext().getString(R.string.add_gesture_confirm_content),
                        gesture, mResolvedComponent,
                        new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                try {
                                    GesturePersistence.saveGesture(getContext(), gesture, mResolvedComponent);
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
                        }
                );
            }
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if(event.getKeyCode() == KeyEvent.KEYCODE_BACK
                && KeyEvent.ACTION_UP == event.getAction()){
            if(mFwDialog.isShow()){
                mFwDialog.hide();
            }else {
                WindowManagerUtil.closeWindow(getContext().getApplicationContext(), this);
            }
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
                mResolvedComponent = TaskManager.getTopComponent(getContext());
                try {
                    TaskFilterManager.getInstance().processAddFilters(getContext(), mResolvedComponent);
                    mTvInvalidRc.setVisibility(GONE);
                } catch (TaskFilterException e) {
                    e.printStackTrace();
                    mTvInvalidRc.setVisibility(VISIBLE);
                    mTvInvalidRc.setText(e.getMessage());
                }
                mAppInfoView.setResolvedComponent(mResolvedComponent);
                break;
            case USE:
                mTvHint.setText(getContext().getString(R.string.gesture_bg_title_use));
                //mAppInfoLayout.setVisibility(GONE);
                break;
        }
        getAnimatorSetOnSwitchTab(tabType).start();
    }

    private AnimatorSet getAnimatorSetOnSwitchTab(final TabLikeView.TabType tabType){
        ObjectAnimator translationXAnimator = ObjectAnimator.ofFloat(mGestureOverlayView, "translationX", ScreenUtil.getScreenSize(getContext())[0], 0.0f);
        ObjectAnimator alphaAnimator = ObjectAnimator.ofFloat(mGestureOverlayView, "alpha", 0.0f, 1.0f);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setDuration(AppConstant.Anim.ANIM_DURATION_NORMAL);
        animatorSet.setInterpolator(new OvershootInterpolator());
        animatorSet.play(translationXAnimator).with(alphaAnimator);

        animatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                switch (tabType){
                    case ADD:
                        mAppInfoLayout.setVisibility(VISIBLE);
                        break;
                    case USE:
                        mAppInfoLayout.setVisibility(GONE);
                }
            }

            @Override
            public void onAnimationEnd(Animator animation) {
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
        return animatorSet;
    }
}
