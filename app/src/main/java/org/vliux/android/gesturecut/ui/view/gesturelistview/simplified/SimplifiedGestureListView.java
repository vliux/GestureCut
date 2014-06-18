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

    // Mask view which shows when this listview is touched,
    // hidden when this listview is action up.
    private View mMaskLayer;

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

    public void setAutoRefresh(boolean autoRefresh){

    }

    private void init(){
        setVerticalScrollBarEnabled(false);
        setHorizontalScrollBarEnabled(false);
        setDivider(null);
        setAdapter(new SimplifiedGestureListAdapter(getContext()));
    }

    public void setMaskLayer(View maskLayer){
        mMaskLayer = maskLayer;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        boolean ret = super.onTouchEvent(ev);

        if(null != mMaskLayer) {
            switch (ev.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    showMaskLayer();
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    hideMaskLayer();
                    break;
            }
        }
        return ret;
    }

    private Animator mPrevShowAnimator;
    private Animator mPrevHideAnimator;

    private void showMaskLayer(){
        if(null != mPrevShowAnimator && mPrevShowAnimator.isStarted()){
            return;
        }else if(null != mPrevHideAnimator && mPrevHideAnimator.isStarted()){
            mPrevHideAnimator.cancel();
        }

        final Animator animator = ObjectAnimator.ofFloat(mMaskLayer, "alpha", 0.0f, 1.0f);
        animator.setDuration(AppConstant.Anim.ANIM_DURATION_NORMAL);
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                mPrevShowAnimator = animator;
                mMaskLayer.setVisibility(VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mPrevShowAnimator = null;
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                mPrevShowAnimator = null;
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
        animator.start();
    }

    private void hideMaskLayer(){
        if(null != mPrevHideAnimator && mPrevHideAnimator.isStarted()){
            return;
        }else if(null != mPrevShowAnimator && mPrevShowAnimator.isStarted()){
            mPrevShowAnimator.cancel();
        }

        final Animator animator = ObjectAnimator.ofFloat(mMaskLayer, "alpha", 1.0f, 0.0f);
        animator.setDuration(AppConstant.Anim.ANIM_DURATION_NORMAL);
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                mPrevHideAnimator = animator;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mPrevHideAnimator = null;
                mMaskLayer.setVisibility(GONE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                mPrevHideAnimator = null;
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
        animator.start();
    }
}
