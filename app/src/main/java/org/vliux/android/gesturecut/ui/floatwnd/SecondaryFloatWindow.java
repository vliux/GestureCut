package org.vliux.android.gesturecut.ui.floatwnd;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.gesture.Gesture;
import android.gesture.GestureOverlayView;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.vliux.android.gesturecut.AppConstant;
import org.vliux.android.gesturecut.R;
import org.vliux.android.gesturecut.model.ResolvedComponent;
import org.vliux.android.gesturecut.biz.TaskFilterManager;
import org.vliux.android.gesturecut.biz.TaskManager;
import org.vliux.android.gesturecut.biz.db.GestureDbTable;
import org.vliux.android.gesturecut.biz.gesture.GesturePersistence;
import org.vliux.android.gesturecut.biz.taskfilters.TaskFilterException;
import org.vliux.android.gesturecut.ui.view.AppInfoView;
import org.vliux.android.gesturecut.ui.view.DrawBoundsGestureOverlayView;
import org.vliux.android.gesturecut.ui.view.GestureListView;
import org.vliux.android.gesturecut.util.AnimUtil;
import org.vliux.android.gesturecut.util.WindowManagerUtil;

/**
 * Created by vliux on 4/9/14.
 */
public class SecondaryFloatWindow extends LinearLayout implements TabLikeView.OnTablikeChangedListener {

    private DrawBoundsGestureOverlayView mGestureOverlayView;
    private TabLikeView mTabLikeView;
    private TextView mTvHint;
    private GestureConfirmDialog mFwDialog;

    private AppInfoView mAppInfoView; // app info shown when adding new gesture
    private LinearLayout mAppInfoLayout; // layout containing AppInfoView
    private ImageView mIvAppIconUseAnim; // app icon for animator when using gesture
    private TextView mTvInvalidRc; // show warning for invalid ResolvedComponent
    private GestureListView mGestureListView;

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
        setOrientation(VERTICAL);
        setBackgroundResource(R.color.gesture_create_bg_semi_transparent);

        mGestureOverlayView = (DrawBoundsGestureOverlayView)findViewById(R.id.gesture_overlay);
        mTabLikeView = (TabLikeView)findViewById(R.id.gesture_tablike);
        mTvHint = (TextView)findViewById(R.id.gesture_hint);
        mFwDialog = (GestureConfirmDialog)findViewById(R.id.gesture_fwdialog);
        mAppInfoView = (AppInfoView)findViewById(R.id.gesture_appinfoview);
        mIvAppIconUseAnim = (ImageView)findViewById(R.id.gesture_appicon_startactiv);
        mAppInfoLayout = (LinearLayout)findViewById(R.id.gesture_appinfo_layout);
        mTvInvalidRc = (TextView)findViewById(R.id.gesture_tv_invalid_rc);
        mGestureListView = (GestureListView)findViewById(R.id.gesture_listview);

        mGestureOverlayView.setGestureColor(Color.RED);
        mGestureOverlayView.addOnGesturePerformedListener(mOnGesturePerformedListener);
        mTabLikeView.setOnTabChangedListener(this);
        refreshHint(mTabLikeView.getType());
    }

    private final GestureOverlayView.OnGesturePerformedListener mOnGesturePerformedListener = new GestureOverlayView.OnGesturePerformedListener() {
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
                quitAnim();
            }
            return true;
        }else{
            return super.dispatchKeyEvent(event);
        }
    }

    private boolean mQuitAnimLock = false;
    private void quitAnim(){
        if(mQuitAnimLock){
            return;
        }

        mQuitAnimLock = true;
        ObjectAnimator animator = ObjectAnimator.ofFloat(this, "alpha", 1.0f, 0.0f);
        animator.setDuration(AppConstant.Anim.ANIM_DURATION_NORMAL);
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                WindowManagerUtil.closeWindow(getContext().getApplicationContext(), SecondaryFloatWindow.this);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
        animator.start();
    }

    @Override
    public boolean onTabSwitched(TabLikeView.TabType newType) {
        return refreshHint(newType);
    }

    private boolean refreshHint(TabLikeView.TabType tabType){
        if(mSwitchTabAnimLock){
            return false;
        }

        switch (tabType){
            case ADD:
                mGestureOverlayView.setBoundayColor(getResources().getColor(R.color.beige_light_semi_transparent));
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
                //mGestureOverlayView.setVisibility(VISIBLE);
                //mGestureListView.setVisibility(GONE);
                break;
            case USE:
                mGestureOverlayView.setBoundayColor(getResources().getColor(R.color.gesture_cur_blue));
                mTvHint.setText(getContext().getString(R.string.gesture_bg_title_use));
                //mGestureOverlayView.setVisibility(VISIBLE);
                //mGestureListView.setVisibility(GONE);
                break;
            case LIST:
                //mAppInfoLayout.setVisibility(INVISIBLE);
                //mGestureListView.setVisibility(VISIBLE);
                //mGestureOverlayView.setVisibility(GONE);
                mGestureListView.refresh();
                break;
        }
        switchTabAnim(tabType);
        return true;
    }

    private boolean mSwitchTabAnimLock = false;
    private void switchTabAnim(final TabLikeView.TabType tabType){
        mSwitchTabAnimLock = true;
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setDuration(AppConstant.Anim.ANIM_DURATION_NORMAL);
        animatorSet.setInterpolator(new AccelerateDecelerateInterpolator());

        ObjectAnimator mainAnimator = null;
        switch (tabType) {
            case ADD:
            case USE:
                mainAnimator = ObjectAnimator.ofFloat(mGestureOverlayView, "alpha", 0.0f, 1.0f);
                mainAnimator.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        if(mGestureListView.getVisibility() == VISIBLE){
                            mGestureListView.setVisibility(GONE);
                        }
                        if(mGestureOverlayView.getVisibility() != VISIBLE){
                            mGestureOverlayView.setVisibility(VISIBLE);
                        }
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        mSwitchTabAnimLock = false;
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                        mSwitchTabAnimLock = false;
                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {
                    }
                });
                break;
            case LIST:
                mainAnimator = ObjectAnimator.ofFloat(mGestureListView, "alpha", 0.0f, 1.0f);
                mainAnimator.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        if(mGestureListView.getVisibility() != VISIBLE){
                            mGestureListView.setVisibility(VISIBLE);
                        }
                        if(mGestureOverlayView.getVisibility() == VISIBLE){
                            mGestureOverlayView.setVisibility(GONE);
                        }
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        mSwitchTabAnimLock = false;
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                        mSwitchTabAnimLock = false;
                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {
                    }
                });
                break;
        }

        Animator appLayoutAnimator = appInfoLayoutAnimator(tabType);
        if(null != appLayoutAnimator){
            animatorSet.play(mainAnimator).with(appLayoutAnimator);
        }else{
            animatorSet.play(mainAnimator);
        }
        animatorSet.start();
    }

    private Animator appInfoLayoutAnimator(TabLikeView.TabType type){
        ObjectAnimator translationYAnimator = null;
        switch (type){
            case ADD:
                translationYAnimator = ObjectAnimator.ofFloat(mAppInfoLayout, "translationY", -mAppInfoLayout.getHeight(), 0.0f);
                translationYAnimator.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        mAppInfoLayout.setVisibility(VISIBLE);
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
                break;
            case USE:
            case LIST:
                if(mAppInfoLayout.getVisibility() == View.VISIBLE){
                    translationYAnimator = ObjectAnimator.ofFloat(mAppInfoLayout, "translationY", 0.0f, -mAppInfoLayout.getHeight());
                    translationYAnimator.addListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {
                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            mAppInfoLayout.setVisibility(INVISIBLE);
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {
                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {
                        }
                    });
                }
                break;
        }
        return translationYAnimator;
    }
}
