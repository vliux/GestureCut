package org.vliux.android.gesturecut.activity.add;

import android.animation.Animator;
import android.app.Activity;
import android.content.Intent;
import android.gesture.Gesture;
import android.gesture.GestureOverlayView;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.vliux.android.gesturecut.AppConstant;
import org.vliux.android.gesturecut.R;
import org.vliux.android.gesturecut.biz.TaskManager;
import org.vliux.android.gesturecut.model.ResolvedComponent;
import org.vliux.android.gesturecut.util.SimpleAnimatorListener;

/**
 * Created by vliux on 2/15/15.
 */
public class AddGestureDrawActivity extends ActionBarActivity {
    private static final String INTENT_RESOLVED_COMPONENT = "rc";
    private static final String INTENT_ANIM_START_X = "x";
    private static final String INTENT_ANIM_START_Y = "y";

    public static final int REQUEST_CODE = 1;
    private static final int REQUEST_CODE_INVALID = -1;

    private ViewGroup mGestureOverlayViewParent;
    private GestureOverlayView mGestureOverlayView;
    private TextView mTvAppName;
    private ImageView mIvAppIcon;
    private ResolvedComponent mRc;
    private GesturePerformedPresenter mGesturePerformedPresenter;

    private int mAnimStartX;
    private int mAnimStartY;
    // for activity start anim
    private ViewGroup mLayoutTitleArea;

    private boolean mGestureAdded = false; // if the gesture has been added
    /**
     *
     * @param activity
     * @param rc
     * @param animStartPointX
     * @param animStartPointY
     * @return If succeded, return request code; else return value is negative.
     */
    public static int startForResult(Activity activity, ResolvedComponent rc, int animStartPointX, int animStartPointY){
        if(null != rc && null != rc.getType()) {
            Intent intent = new Intent(activity, AddGestureDrawActivity.class);
            intent.putExtra(INTENT_RESOLVED_COMPONENT, rc);
            intent.putExtra(INTENT_ANIM_START_X, animStartPointX);
            intent.putExtra(INTENT_ANIM_START_Y, animStartPointY);
            activity.startActivityForResult(intent, REQUEST_CODE);
            activity.overridePendingTransition(0, 0);
            return REQUEST_CODE;
        }else{
            Toast.makeText(activity, activity.getString(R.string.new_gesture_no_rc), Toast.LENGTH_SHORT).show();
            return REQUEST_CODE_INVALID;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_gesture_draw);
        mTvAppName = (TextView)findViewById(R.id.agda_app_name);
        mIvAppIcon = (ImageView)findViewById(R.id.agda_app_icon);
        mLayoutTitleArea = (ViewGroup)findViewById(R.id.agda_title_area);
        mGestureOverlayView = (GestureOverlayView)findViewById(R.id.agda_gesture_overlay);
        mGestureOverlayViewParent = (ViewGroup)findViewById(R.id.agda_gesture_parent);

        Intent intent = getIntent();
        if(intent.hasExtra(INTENT_RESOLVED_COMPONENT)){
            mRc = intent.getParcelableExtra(INTENT_RESOLVED_COMPONENT);
            if(null != mRc){
                mTvAppName.setText(TaskManager.getDescription(this, mRc, false)[0]);
                mIvAppIcon.setImageDrawable(TaskManager.getIcon(this, mRc));
            }
        }

        if(intent.hasExtra(INTENT_ANIM_START_X)){
            mAnimStartX = intent.getIntExtra(INTENT_ANIM_START_X, 0);
        }
        if(intent.hasExtra(INTENT_ANIM_START_Y)){
            mAnimStartY = intent.getIntExtra(INTENT_ANIM_START_Y, 0);
        }

        mLayoutTitleArea.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                mLayoutTitleArea.getViewTreeObserver().removeOnPreDrawListener(this);
                startActivityAnim();
                return true;
            }
        });

        mGestureOverlayView.addOnGesturePerformedListener(mOnGesturePerformedListener);
        mGesturePerformedPresenter = new GesturePerformedPresenter(this, mRc);
        // on Lollipop we have to disable ActionBar bottom shadow by code
        // on older versions, set it in styles.
        //if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            getSupportActionBar().setElevation(0f);
        //}
    }

    /**
     * Pressing the "back" btn in action bar should have the same quit transition.
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            closeActivityAnim();
            return true;
        }
        return false;
    }

    private void startActivityAnim(){
        mLayoutTitleArea.setScaleY(0.3f);
        mLayoutTitleArea.setScaleX(0.3f);
        mLayoutTitleArea.setAlpha(0f);

        mLayoutTitleArea.setPivotX(mAnimStartX);
        mLayoutTitleArea.setPivotY(mAnimStartY);

        mGestureOverlayViewParent.setTranslationX(mGestureOverlayViewParent.getWidth());
        mGestureOverlayViewParent.setAlpha(0f);
        mLayoutTitleArea.animate().scaleY(1).scaleX(1).alpha(1f)
                .setDuration(AppConstant.Anim.ANIM_DURATION_NORMAL).setInterpolator(new DecelerateInterpolator())
                .setListener(new SimpleAnimatorListener() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        mGestureOverlayViewParent.animate().translationX(0f)
                                .alpha(1f)
                                .setDuration(AppConstant.Anim.ANIM_DURATION_NORMAL)
                                .setInterpolator(new DecelerateInterpolator()).start();
                    }
                })
        .start();
    }

    private void closeActivityAnim(){
        mLayoutTitleArea.setPivotX(mAnimStartX);
        mLayoutTitleArea.setPivotY(mAnimStartY);


        mGestureOverlayViewParent.animate().translationX(mGestureOverlayViewParent.getWidth())
                .alpha(0f)
                .setDuration(AppConstant.Anim.ANIM_DURATION_NORMAL)
                .setInterpolator(new DecelerateInterpolator())
                .setListener(new SimpleAnimatorListener() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        mLayoutTitleArea.animate().scaleY(0.3f).scaleX(0.3f).alpha(0f)
                                .setDuration(AppConstant.Anim.ANIM_DURATION_NORMAL).setInterpolator(new DecelerateInterpolator())
                                .setListener(new SimpleAnimatorListener() {
                                    @Override
                                    public void onAnimationEnd(Animator animation) {
                                        setResult(mGestureAdded? RESULT_OK : RESULT_CANCELED);
                                        finish();
                                        overridePendingTransition(0, 0);
                                    }
                                }).start();
                    }
                })
                .start();
    }

    @Override
    public void onBackPressed() {
        closeActivityAnim();
    }

    private final GestureOverlayView.OnGesturePerformedListener mOnGesturePerformedListener = new GestureOverlayView.OnGesturePerformedListener() {
        @Override
        public void onGesturePerformed(GestureOverlayView overlay, Gesture gesture) {
            if(null != mRc){
                mGesturePerformedPresenter.addGesture(gesture,
                        new Runnable() {
                            @Override
                            public void run() {
                                mGestureAdded = true;
                                closeActivityAnim();
                            }
                        });
            }
        }
    };
}
