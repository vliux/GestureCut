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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by vliux on 12/25/14.
 */
class FabPresenter {
    private final int COLOR_NORMAL_NORMAL = R.color.primary_color;
    private final int COLOR_NORMAL_PRESSED = R.color.primary_color_dark;
    private final int COLOR_DELETE_NORMAL = R.color.accent_color;
    private final int COLOR_DELETE_PRESSED = R.color.accent_color_dark;
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
            SparseBooleanArray booleanArray = mGestureListView.getCheckedItemPositions();
            // pick up checked items
            int boolArraySize = booleanArray.size();
            final Set<String> gestureNameSet = new HashSet<String>();
            final List<Integer> checkedPositions = new ArrayList<Integer>();

            for(int i = 0; i < boolArraySize; i++){
                if(booleanArray.valueAt(i)) {
                    int position = booleanArray.keyAt(i);
                    String gestureName = mGestureListView.getGestureName(position);
                    if (null != gestureName && gestureName.length() > 0) {
                        gestureNameSet.add(gestureName);
                        checkedPositions.add(position);
                    }
                }
            }

            int size = gestureNameSet.size();
            if(size > 0) {
                final GeneralDialog dialog = new GeneralDialog(mContext);
                dialog.setTitleContent(mContext.getText(R.string.del_gesture_confirm_title).toString(),
                        String.format(mContext.getText(R.string.del_gesture_confirm_content).toString(), size));

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
                        ConcurrentManager.submitJob(new DeleteGesturesBizCallback(gestureNameSet),
                                new DeleteGesturesUiCallback(checkedPositions));
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
        private Set<String> gestureNameSet;

        public DeleteGesturesBizCallback(Set<String> gestureNameSet){
            this.gestureNameSet = gestureNameSet;
        }

        @Override
        public Boolean onBusinessLogicAsync(ConcurrentManager.IJob job, Object... params) {
            int i = 1;
            int size = gestureNameSet.size();
            for (String gestureName : gestureNameSet) {
                try {
                    Thread.sleep(100L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    return false;
                }

                GesturePersistence.removeGesture(mContext, gestureName);
                job.publishJobProgress(100 * i/size);
            }
            return true;
        }
    }

    class DeleteGesturesUiCallback implements ConcurrentManager.IUiCallback<Boolean> {
        private ProgressDialog progressDialog;
        private List<Integer> checkedPositions;

        DeleteGesturesUiCallback(List<Integer> checkedPositions) {
            this.checkedPositions = checkedPositions;
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
            uncheckItems();
            mGestureListView.refresh();
            setNormalMode();
        }

        @Override
        public void onPregressUpdate(int percent) {
            progressDialog.setTitle(String.format("deleting %d%% ...", percent));
        }

        @Override
        public void onCancelled() {
            uncheckItems();
            mGestureListView.refresh();
            setNormalMode();
        }

        private void uncheckItems(){
            for(int position : checkedPositions){
                mGestureListView.setItemChecked(position, false);
            }
        }
    }
}
