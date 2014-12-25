package org.vliux.android.gesturecut.activity.main;

import android.content.Context;
import android.content.Intent;
import android.util.SparseBooleanArray;
import android.view.View;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.Toast;

import com.melnykov.fab.FloatingActionButton;

import org.vliux.android.gesturecut.R;
import org.vliux.android.gesturecut.activity.add.AddGestureActivity;
import org.vliux.android.gesturecut.biz.gesture.GesturePersistence;
import org.vliux.android.gesturecut.ui.view.GeneralDialog;
import org.vliux.android.gesturecut.ui.view.GestureListView;
import org.vliux.android.gesturecut.util.WindowManagerUtil;

/**
 * Created by vliux on 12/25/14.
 */
class FabPresenter {
    private final int COLOR_NORMAL_NORMAL = R.color.gesture_cur_blue;
    private final int COLOR_NORMAL_PRESSED = R.color.gesture_cur_blue_light;
    private final int COLOR_DELETE_NORMAL = R.color.gesture_cur_red;
    private final int COLOR_DELETE_PRESSED = R.color.beige_light_semi_transparent;
    private final int NORMAL_BG = R.drawable.ic_add;
    private final int DELETE_BG = R.drawable.ic_del;

    private boolean mCurrentModeDelete = false;
    private FloatingActionButton mFab;
    private GestureListView mGestureListView;
    private Context mContext;
    private WindowManager mWindowMgr;

    public FabPresenter(Context context, FloatingActionButton fab, GestureListView gestureListView){
        mContext = context;
        mFab = fab;
        mGestureListView = gestureListView;
        mWindowMgr = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
    }

    public void setDeleteMode(){
        mCurrentModeDelete = true;
        mFab.setImageResource(DELETE_BG);
        mFab.setColorNormalResId(COLOR_DELETE_NORMAL);
        mFab.setColorPressedResId(COLOR_DELETE_PRESSED);
    }

    public void setNormalMode(){
        mCurrentModeDelete = false;
        mFab.setImageResource(NORMAL_BG);
        mFab.setColorNormalResId(COLOR_NORMAL_NORMAL);
        mFab.setColorPressedResId(COLOR_NORMAL_PRESSED);
    }

    public void onFabClicked(){
        if(mCurrentModeDelete){
            final SparseBooleanArray booleanArray = mGestureListView.getCheckedItemPositions();
            final int size = booleanArray.size();
            if(size > 0) {
                final GeneralDialog dialog = new GeneralDialog(mContext);
                dialog.setTitleContent("Deleting ...", String.format("Delete the %d gestures?", size));
                dialog.setOnCancelClicked(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mWindowMgr.removeView(dialog);
                    }
                });

                dialog.setOnOkClicked(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mWindowMgr.removeView(dialog);
                        for (int i = 0; i < size; i++) {
                            int position = booleanArray.keyAt(i);
                            String gestureName = mGestureListView.getGestureName(position);
                            Toast.makeText(mContext, "deleting gestures " + gestureName, Toast.LENGTH_SHORT).show();
                            GesturePersistence.removeGesture(mContext, gestureName);
                            mGestureListView.setItemChecked(position, false);
                        }
                        mGestureListView.refresh();
                        setNormalMode();
                    }
                });

                mWindowMgr.addView(dialog, WindowManagerUtil.dialogLayoutParams());
            }
        }else{
            Intent intent = new Intent(mContext, AddGestureActivity.class);
            mContext.startActivity(intent);
        }
    }
}
