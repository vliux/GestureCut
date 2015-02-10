package org.vliux.android.gesturecut.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;

import org.vliux.android.gesturecut.R;
import org.vliux.android.gesturecut.ui.floatwnd.FloatWindowManager;
import org.vliux.android.gesturecut.ui.view.GestureListView;

/**
 * Created by vliux on 1/28/15.
 */
public class ShortcutActivity extends Activity {
    private GestureListView mGestureListView;

    public static void show(Context context){
        Intent intent = new Intent(context, ShortcutActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
        if(Build.VERSION.SDK_INT >= 16) {
            ActivityOptionsCompat activityOptions = ActivityOptionsCompat.makeCustomAnimation(context, 0, 0);
            context.startActivity(intent, activityOptions.toBundle());
        }else{
            context.startActivity(intent);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);

        setContentView(R.layout.activity_shortcut);
        mGestureListView = (GestureListView)findViewById(R.id.sc_gesture_list);
        if(null == savedInstanceState){
            mGestureListView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    mGestureListView.getViewTreeObserver().removeOnPreDrawListener(this);
                    startShowAnim();
                    return true;
                }
            });
        }
    }

    private void startShowAnim(){
        int[] wndLoc = FloatWindowManager.parseLocationFromPrefs(this);
        mGestureListView.setScaleY(0.3f);
        mGestureListView.setScaleX(0.3f);

        mGestureListView.setPivotX(wndLoc[0]);
        mGestureListView.setPivotY(wndLoc[1]);
        mGestureListView.animate().scaleY(1).scaleX(1).setDuration(300L).setInterpolator(new AccelerateInterpolator()).start();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGestureListView.refresh();
    }
}
