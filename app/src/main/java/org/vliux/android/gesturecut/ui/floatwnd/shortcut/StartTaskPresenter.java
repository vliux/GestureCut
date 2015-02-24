package org.vliux.android.gesturecut.ui.floatwnd.shortcut;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.gesture.Gesture;
import android.gesture.GestureOverlayView;
import android.os.Vibrator;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.ImageView;

import org.vliux.android.gesturecut.AppConstant;
import org.vliux.android.gesturecut.R;
import org.vliux.android.gesturecut.biz.TaskManager;
import org.vliux.android.gesturecut.biz.gesture.GesturePersistence;
import org.vliux.android.gesturecut.model.ResolvedComponent;
import org.vliux.android.gesturecut.ui.view.glv.GestureListItem;
import org.vliux.android.gesturecut.ui.view.glv.GestureListView;
import org.vliux.android.gesturecut.util.PreferenceHelper;

/**
 * Created by vliux on 2/14/15.
 */
public class StartTaskPresenter implements GestureOverlayView.OnGesturePerformedListener,
        GestureListView.OnGestureIconClickedListener,
        AdapterView.OnItemClickListener {
    private IShortcutWindow mShortcutWindow;
    private ImageView mIvTargetApp;
    private int mTargetTranslationY;

    public StartTaskPresenter(ShortcutWindow sc, ImageView targetAppImageView, int targetTranslationY){
        this.mShortcutWindow = sc;
        this.mIvTargetApp = targetAppImageView;
        this.mTargetTranslationY = targetTranslationY;
    }

    private void startTask(final ResolvedComponent resolvedComponent, final Runnable animEndRunnable){
        final Context context = mShortcutWindow.getContext();
        ObjectAnimator transxObjAnimator = ObjectAnimator.ofFloat(mIvTargetApp, "translationY", 0.0f, mTargetTranslationY);
        ObjectAnimator alphaObjAnimator = ObjectAnimator.ofFloat(mIvTargetApp, "alpha", 0.0f, 1.0f);
        ObjectAnimator scalexAnimator = ObjectAnimator.ofFloat(mIvTargetApp, "scaleX", 1.0f, 3.0f);
        ObjectAnimator scaleyAnimator = ObjectAnimator.ofFloat(mIvTargetApp, "scaleY", 1.0f, 3.0f);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setDuration(AppConstant.Anim.ANIM_DURATION_NORMAL);
        animatorSet.setInterpolator(new DecelerateInterpolator());
        animatorSet.play(transxObjAnimator).with(alphaObjAnimator).with(scalexAnimator).with(scaleyAnimator);
        animatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                if(null != resolvedComponent){
                    mIvTargetApp.setImageDrawable(TaskManager.getIcon(context, resolvedComponent));
                }
                mIvTargetApp.setVisibility(View.VISIBLE);
                // vibrate if needed
                if(PreferenceHelper.getUserPref(context.getApplicationContext(), R.string.pref_key_vibrate, true)) {
                    Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
                    if (null != vibrator) {
                        vibrator.vibrate(AppConstant.VIBRATE_DURATION);
                    }
                }
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if(null != resolvedComponent){
                    TaskManager.startActivity(context.getApplicationContext(), resolvedComponent);
                }
                mIvTargetApp.setVisibility(View.GONE);
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
        animatorSet.start();
    }

    /**
     * For start task from drawing gesture.
     * @param overlay
     * @param gesture
     */
    @Override
    public void onGesturePerformed(GestureOverlayView overlay, Gesture gesture) {
        Context context = mShortcutWindow.getContext();
        ResolvedComponent rc = GesturePersistence.loadGesture(context, gesture);
        if(null != rc){
            startTask(rc, new Runnable() {
                @Override
                public void run() {
                    mShortcutWindow.hideOverlay(true);
                }
            });
        }
    }

    /**
     * For start task from clicking item in GestureListView.
     * @param rc
     */
    @Override
    public void onGestureIconClicked(ResolvedComponent rc) {
        if(null != rc){
            startTask(rc, new Runnable() {
                @Override
                public void run() {
                    mShortcutWindow.hideOverlay(true);
                }
            });
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if(view instanceof GestureListItem){
            GestureListItem gli = (GestureListItem)view;
            ResolvedComponent rc = gli.getAppInfoView().getResolvedComponent();
            if(null != rc){
                startTask(rc, new Runnable() {
                    @Override
                    public void run() {
                        mShortcutWindow.hideOverlay(true);
                    }
                });
            }
        }
    }
}
