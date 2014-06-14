package org.vliux.android.gesturecut.ui.view.GestureList;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;

import org.vliux.android.gesturecut.AppConstant;
import org.vliux.android.gesturecut.R;

/**
 * Created by vliux on 4/29/14.
 */
public class BottomBar extends FrameLayout {

    private AnimatorSet mAnimatorShow;
    private AnimatorSet mAnimatorHide;
    private boolean mIsShowingBottomBar = false;
    private boolean mIsHidingBottomBar = false;
    private boolean mIsBottomBarShown = false;
    private ImageView mIvDelete;
    private BottomBarAwaredListView mBottomBarAwaredListView;

    public BottomBar(Context context) {
        super(context);
        init();
    }

    public BottomBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BottomBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init(){
        LayoutInflater.from(getContext()).inflate(R.layout.view_gl_bottom_bar, this, true);
        mIvDelete = (ImageView)findViewById(R.id.gesture_bottom_bar_del);
    }

    /**
     * BottomBarAwaredListView.setBottomBar() will call this method.
     * @param listView
     */
    void setBottomBarAwaredListView(BottomBarAwaredListView listView){
        mBottomBarAwaredListView = listView;
    }

    public void setOnDeleteClicked(final OnClickListener onDeleteClicked){
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(null != onDeleteClicked){
                    onDeleteClicked.onClick(BottomBar.this);
                }
                hideBottomBar();
            }
        });
    }

    public boolean isShown(){
        return mIsBottomBarShown;
    }

    public void showBottomBar() {
        if (null != mAnimatorShow && mIsShowingBottomBar) {
            return;
        } else if (mIsBottomBarShown) {
            return;
        }

        // if currently is hiding, cancel the hiding animation
        if (null != mAnimatorHide && mIsHidingBottomBar) {
            mAnimatorHide.cancel();
        }
        if (null == mAnimatorShow) {
            mAnimatorShow = new AnimatorSet();
            mAnimatorShow.setDuration(AppConstant.Anim.ANIM_DURATION_NORMAL);
            mAnimatorShow.setInterpolator(new AccelerateDecelerateInterpolator());
            ObjectAnimator translationYAnimator = ObjectAnimator.ofFloat(this, "translationY", getHeight(), 0.0f);
            mAnimatorShow.play(translationYAnimator);
            mAnimatorShow.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                    setVisibility(VISIBLE);
                    mIsShowingBottomBar = true;
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    mIsShowingBottomBar = false;
                    mIsBottomBarShown = true;
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                    mIsShowingBottomBar = false;
                }

                @Override
                public void onAnimationRepeat(Animator animation) {
                    mIsShowingBottomBar = true;
                }
            });
        }
        mAnimatorShow.start();
    }

    public void hideBottomBar() {
        if (null != mAnimatorHide && mIsHidingBottomBar) {
            return;
        }else if (!mIsBottomBarShown) {
            return;
        }

        // if currently is showing, cancel the showing animation
        if (null != mAnimatorShow && mIsShowingBottomBar) {
            mAnimatorShow.cancel();
        }
        if (null == mAnimatorHide) {
            mAnimatorHide = new AnimatorSet();
            mAnimatorHide.setDuration(AppConstant.Anim.ANIM_DURATION_NORMAL);
            mAnimatorHide.setInterpolator(new AccelerateDecelerateInterpolator());
            ObjectAnimator translationYAnimator = ObjectAnimator.ofFloat(this, "translationY", 0.0f, getHeight());
            mAnimatorHide.play(translationYAnimator);
            mAnimatorHide.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                    mIsHidingBottomBar = true;
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    mIsHidingBottomBar = false;
                    mIsBottomBarShown = false;
                    setVisibility(GONE);
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                    mIsHidingBottomBar = false;
                }

                @Override
                public void onAnimationRepeat(Animator animation) {
                    mIsHidingBottomBar = true;
                }
            });
        }
        mAnimatorHide.start();

        if(null != mBottomBarAwaredListView){
            mBottomBarAwaredListView.resetActivatedState();
        }
    }

}
