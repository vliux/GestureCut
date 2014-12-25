package org.vliux.android.gesturecut.activity.main;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.util.SparseBooleanArray;
import android.view.View;
import android.view.WindowManager;

import com.melnykov.fab.FloatingActionButton;

import org.vliux.android.gesturecut.R;
import org.vliux.android.gesturecut.activity.add.AddGestureActivity;
import org.vliux.android.gesturecut.biz.gesture.GesturePersistence;
import org.vliux.android.gesturecut.ui.view.GeneralDialog;
import org.vliux.android.gesturecut.ui.view.GestureListView;
import org.vliux.android.gesturecut.util.ConcurrentManager;
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
                        ConcurrentManager.submitJob(new DeleteGesturesBizCallback(booleanArray),
                                new DeleteGesturesUiCallback(booleanArray));
                    }
                });

                mWindowMgr.addView(dialog, WindowManagerUtil.dialogLayoutParams());
            }
        }else{
            Intent intent = new Intent(mContext, AddGestureActivity.class);
            mContext.startActivity(intent);
        }
    }

    class DeleteGesturesBizCallback implements ConcurrentManager.IBizCallback<Boolean> {
        private SparseBooleanArray sparseBooleanArray;

        public DeleteGesturesBizCallback(SparseBooleanArray booleanArray){
            sparseBooleanArray = booleanArray;
        }

        @Override
        public Boolean onBusinessLogicAsync(ConcurrentManager.IJob job, Object... params) {
            int size = sparseBooleanArray.size();
            for (int i = 0; i < size; i++) {
                try {
                    Thread.sleep(100L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    return false;
                }
                int position = sparseBooleanArray.keyAt(i);
                String gestureName = mGestureListView.getGestureName(position);
                GesturePersistence.removeGesture(mContext, gestureName);
                job.publishJobProgress(100 * (i+1)/size);
            }
            return true;
        }
    }

    class DeleteGesturesUiCallback extends ConcurrentManager.IUiCallback<Boolean> {
        private SparseBooleanArray sparseBooleanArray;
        private ProgressDialog progressDialog;

        public DeleteGesturesUiCallback(SparseBooleanArray booleanArray){
            sparseBooleanArray = booleanArray;
        }

        @Override
        public void onPreExecute() {
            progressDialog = new ProgressDialog(mContext);
            progressDialog.setIndeterminate(true);
            progressDialog.setTitle("deleting ...");
            progressDialog.show();
        }

        @Override
        public void onPostExecute(Boolean aBoolean) {
            progressDialog.dismiss();
            int size = sparseBooleanArray.size();
            for(int i = 0; i < size; i++){
                mGestureListView.setItemChecked(sparseBooleanArray.keyAt(i), false);
            }

            mGestureListView.refresh();
            setNormalMode();
        }

        @Override
        public void onPregressUpdate(int percent) {
            progressDialog.setTitle(String.format("deleting %d%% ...", percent));
        }

        @Override
        public void onCancelled() {
            mGestureListView.refresh();
            setNormalMode();
        }
    }
}
