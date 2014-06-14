package org.vliux.android.gesturecut.util;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;

import org.vliux.android.gesturecut.AppConstant;
import org.vliux.android.gesturecut.biz.ResolvedComponent;
import org.vliux.android.gesturecut.biz.TaskManager;

/**
 * Created by vliux on 4/19/14.
 */
public class AnimUtil {

    /**
     * The targetView should be initially placed at the bottom of the layout, and is GONE.
     * @param context
     * @param targetView
     * @param resolvedComponent
     * @param animEndRunnable called in UI thread, only used for carrying logic.
     * @return
     */
    public static AnimatorSet getStartActivityAnimatorSet(final Context context, final ImageView targetView,
                                                          final ResolvedComponent resolvedComponent, final Runnable animEndRunnable){
        ObjectAnimator transxObjAnimator = ObjectAnimator.ofFloat(targetView, "translationY", 0.0f, -ScreenUtil.getScreenSize(context)[1]/2);
        ObjectAnimator alphaObjAnimator = ObjectAnimator.ofFloat(targetView, "alpha", 0.0f, 1.0f, 0.5f);
        ObjectAnimator scalexAnimator = ObjectAnimator.ofFloat(targetView, "scaleX", 1.0f, 3.0f);
        ObjectAnimator scaleyAnimator = ObjectAnimator.ofFloat(targetView, "scaleY", 1.0f, 3.0f);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setDuration(AppConstant.Anim.ANIM_DURATION_NORMAL);
        animatorSet.setInterpolator(new OvershootInterpolator());
        animatorSet.play(transxObjAnimator).with(alphaObjAnimator).with(scalexAnimator).with(scaleyAnimator);
        animatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                if(null != resolvedComponent){
                    targetView.setImageDrawable(TaskManager.getIcon(context, resolvedComponent));
                }
                targetView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if(null != resolvedComponent){
                    resolvedComponent.startActivity(context.getApplicationContext());
                }
                targetView.setVisibility(View.GONE);
                if(null != animEndRunnable){
                    animEndRunnable.run();
                }
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
