package org.vliux.android.gesturecut.ui.floatwnd.shortcut;

import android.animation.Animator;
import android.util.Log;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import org.vliux.android.gesturecut.AppConstant;

import de.greenrobot.event.EventBus;

/**
 * Created by vliux on 2/12/15.
 * Manager overlay movement by knob.
 */
class OverlayKnobPresenter {
    static final int EVENT_TYPE_KNOB_PRESSED = 1;
    static final int EVENT_TYPE_KNOB_UNPRESSED = 2;
    static final int EVENT_TYPE_KNOB_MOVE = 3;
    static class Event{
        public int eventType;
        public int rawX;
    }

    /**
     * Event send to OverlayKnob, notify it that the moving is stopped,
     * and the whether the knob stops at left/right side of the screen.
     */
    static class EventToKnob{
        public static final int END_STATE_LEFT = 1;
        public static final int END_STATE_RIGHT = 2;
        public static final int WND_CLOSING = 3;

        public int eventType;

        public EventToKnob(int eventType){
            this.eventType = eventType;
        }

    }

    private IShortcutWindow mShortcutWindow;
    private int mDownX;
    private int mDownTranslationX;

    public OverlayKnobPresenter(IShortcutWindow scw){
        mShortcutWindow = scw;
        EventBus.getDefault().register(this);
    }

    public void onShortcutWindowClosed(){
        EventBus eb = EventBus.getDefault();
        eb.unregister(this);
        eb.post(new EventToKnob(EventToKnob.WND_CLOSING));
    }

    /**
     * for EventBus.
     * @param event
     */
    public void onEventMainThread(Event event){
        switch (event.eventType) {
            case EVENT_TYPE_KNOB_PRESSED:
                Log.d(ShortcutWindow.TAG, "KNOB_PRESSED received");
                mShortcutWindow.setExclusiveMoveMode(IShortcutWindow.OverlayMoveMode.BY_KNOB);
                mDownX = event.rawX;
                mDownTranslationX = (int)mShortcutWindow.getOverlayView().getTranslationX();
                mShortcutWindow.setGestureOverlayViewVisible(View.VISIBLE);
                break;
            case EVENT_TYPE_KNOB_UNPRESSED:
                Log.d(ShortcutWindow.TAG, "KNOB_UNPRESSED received");
                mShortcutWindow.setExclusiveMoveMode(IShortcutWindow.OverlayMoveMode.UNKNOWN);
                animAfterUnpress();
                break;
            case EVENT_TYPE_KNOB_MOVE:
                Log.d(ShortcutWindow.TAG, "KNOB_MOVE received");
                moveOverlay(event.rawX);
                break;
        }
    }

    private void moveOverlay(int currentX){
        if(mShortcutWindow.getExclusiveMoveMode() == IShortcutWindow.OverlayMoveMode.BY_KNOB) {
            int initialTranslationX = mShortcutWindow.getInitialTranslationX();
            int targetTranslationX = mShortcutWindow.getTargetTranslationX();
            int xDelta = currentX - mDownX;
            int transX = mDownTranslationX + xDelta;

            Log.d(ShortcutWindow.TAG, "initTransX=" + mShortcutWindow.getInitialTranslationX() + ", tgtTransX=" + mShortcutWindow.getTargetTranslationX());
            Log.d(ShortcutWindow.TAG, "downX=" + mDownX + ", downTransX=" + mDownTranslationX +
                    ", currentX=" + currentX + ", xDelta=" + xDelta + ", transX=" + transX);
            if (targetTranslationX >= targetTranslationX && targetTranslationX <= initialTranslationX) {
                mShortcutWindow.getOverlayView().setTranslationX(transX);
            }
        }
    }

    private void animAfterUnpress(){
        View overlay = mShortcutWindow.getOverlayView();
        int translationX = (int)overlay.getTranslationX();
        int middleTranslationX = (mShortcutWindow.getInitialTranslationX() + mShortcutWindow.getTargetTranslationX())/2;
        int animTargetTranslationX = 0;
        boolean isRestore = false;
        if(translationX >= middleTranslationX){
            animTargetTranslationX = mShortcutWindow.getInitialTranslationX();
            isRestore = true;
        }else{
            animTargetTranslationX = mShortcutWindow.getTargetTranslationX();
            isRestore = false;
        }

        final boolean finalIsRestore = isRestore;
        overlay.animate().setDuration(AppConstant.Anim.ANIM_DURATION_NORMAL).translationX(animTargetTranslationX)
                .setInterpolator(new DecelerateInterpolator())
                .setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        mShortcutWindow.setExclusiveMoveMode(IShortcutWindow.OverlayMoveMode.UNKNOWN);
                        if (finalIsRestore) {
                            mShortcutWindow.setGestureOverlayViewVisible(View.GONE);
                            EventBus.getDefault().post(new EventToKnob(EventToKnob.END_STATE_RIGHT));
                        } else {
                            EventBus.getDefault().post(new EventToKnob(EventToKnob.END_STATE_LEFT));
                        }
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {
                    }
                }).start();
    }
}
