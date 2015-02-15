package org.vliux.android.gesturecut.activity.add;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
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

/**
 * Created by vliux on 2/15/15.
 */
public class AddGestureDrawActivity extends ActionBarActivity {
    public static final String INTENT_RESOLVED_COMPONENT = "rc";
    public static final String INTENT_ANIM_START_X = "x";
    public static final String INTENT_ANIM_START_Y = "y";

    private TextView mTvAppName;
    private ImageView mIvAppIcon;
    private ResolvedComponent mRc;

    private int mAnimStartX;
    private int mAnimStartY;

    // for activity start anim
    private ViewGroup mLayoutTitleArea;

    public static void start(Activity activity, ResolvedComponent rc, int animStartPointX, int animStartPointY){
        if(null != rc && null != rc.getType()) {
            Intent intent = new Intent(activity, AddGestureDrawActivity.class);
            intent.putExtra(INTENT_RESOLVED_COMPONENT, rc);
            intent.putExtra(INTENT_ANIM_START_X, animStartPointX);
            intent.putExtra(INTENT_ANIM_START_Y, animStartPointY);
            activity.startActivity(intent);
            activity.overridePendingTransition(0, 0);
        }else{
            Toast.makeText(activity, activity.getString(R.string.new_gesture_no_rc), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_gesture_draw);
        mTvAppName = (TextView)findViewById(R.id.agda_app_name);
        mIvAppIcon = (ImageView)findViewById(R.id.agda_app_icon);
        mLayoutTitleArea = (ViewGroup)findViewById(R.id.agda_title_area);

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
    }

    private void startActivityAnim(){
        mLayoutTitleArea.setScaleY(0.3f);
        mLayoutTitleArea.setScaleX(0.3f);

        mLayoutTitleArea.setPivotX(mAnimStartX);
        mLayoutTitleArea.setPivotY(mAnimStartY);
        mLayoutTitleArea.animate().scaleY(1).scaleX(1)
                .setDuration(AppConstant.Anim.ANIM_DURATION_NORMAL).setInterpolator(new DecelerateInterpolator()).start();
    }
}