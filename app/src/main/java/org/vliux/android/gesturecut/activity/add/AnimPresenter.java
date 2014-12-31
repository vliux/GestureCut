package org.vliux.android.gesturecut.activity.add;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import org.vliux.android.gesturecut.AppConstant;
import org.vliux.android.gesturecut.R;
import org.vliux.android.gesturecut.model.ResolvedComponent;
import org.vliux.android.gesturecut.ui.view.AddGestureView;
import org.vliux.android.gesturecut.ui.view.AppInfoView;
import org.vliux.android.gesturecut.util.ScreenUtil;

/**
 * Created by vliux on 12/24/14.
 */
class AnimPresenter {
    private Context mContext;
    private View mListItemTranslated;
    private Drawable mListItemOrgBg;
    private FrameLayout mParentLayout;
    private AddGestureView mAddGestureView;

    public AnimPresenter(Context context, View listItemView, FrameLayout parentLayout, AddGestureView addGestureView){
        mContext = context;
        mListItemTranslated = listItemView;
        mListItemOrgBg = mListItemTranslated.getBackground();
        mParentLayout = parentLayout;
        mAddGestureView = addGestureView;
    }

    public ResolvedComponent getRelatedResolvedComponent(){
        if(null != mListItemTranslated && mListItemTranslated instanceof AppInfoView){
            AppInfoView appInfoView = (AppInfoView)mListItemTranslated;
            return appInfoView.getResolvedComponent();
        }

        return null;
    }

    public void show(){
        Animator animator = ObjectAnimator.ofFloat(mListItemTranslated, "translationY", 0f, -mListItemTranslated.getY());
        animator.setDuration(AppConstant.Anim.ANIM_DURATION_NORMAL);
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                mListItemTranslated.setBackgroundColor(mContext.getResources().getColor(R.color.yellow));
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                showAddGestureView(mParentLayout.getHeight() - mListItemTranslated.getHeight());
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

    public void close(){
        if(null != mAddGestureView){
            int[] screenSize = ScreenUtil.getScreenSize(mContext);
            Animator animator = ObjectAnimator.ofFloat(mAddGestureView, "translationX", 0f, -screenSize[0]);
            animator.setDuration(AppConstant.Anim.ANIM_DURATION_NORMAL);
            animator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    if(null != mAddGestureView) {
                        mParentLayout.removeView(mAddGestureView);
                    }
                    revertListItem();
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
    }

    private void revertListItem(){
        if(null != mListItemTranslated){
            Animator animator = ObjectAnimator.ofFloat(mListItemTranslated, "translationY", mListItemTranslated.getTranslationY(), 0f);
            animator.setDuration(AppConstant.Anim.ANIM_DURATION_NORMAL);
            animator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    mListItemTranslated.setBackgroundDrawable(mListItemOrgBg);
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
    }

    /*
     Show it at the bottom.
     */
    private void showAddGestureView(int height){
        final FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                height,
                Gravity.BOTTOM);
        int[] screenSize = ScreenUtil.getScreenSize(mContext);
        Animator animator = ObjectAnimator.ofFloat(mAddGestureView, "translationX", -screenSize[0], 0f);
        animator.setDuration(AppConstant.Anim.ANIM_DURATION_NORMAL);
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                mParentLayout.addView(mAddGestureView, params);
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
        animator.start();
    }

}
