package org.vliux.android.gesturecut.activity.add;

import android.content.Context;
import android.gesture.Gesture;
import android.view.View;
import android.view.WindowManager;

import org.vliux.android.gesturecut.R;
import org.vliux.android.gesturecut.biz.db.GestureDbTable;
import org.vliux.android.gesturecut.biz.gesture.GesturePersistence;
import org.vliux.android.gesturecut.model.ResolvedComponent;
import org.vliux.android.gesturecut.ui.floatwnd.GestureConfirmDialog;
import org.vliux.android.gesturecut.util.WindowManagerUtil;

/**
 * Created by vliux on 12/24/14.
 */
public class GesturePerformedPresenter {
    private ResolvedComponent mResolvedComponent;
    private Context mContext;
    private WindowManager mWindowMgr;

    public GesturePerformedPresenter(Context context, ResolvedComponent rc){
        mContext = context;
        mResolvedComponent = rc;
        mWindowMgr = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
    }

    public void addGesture(final Gesture gesture, final Runnable gestureAddedPostRunnable){
        if(null != mResolvedComponent){
            GestureDbTable.DbData dbData = GesturePersistence.loadGestureEx(mContext, gesture);
            final GestureConfirmDialog mFwDialog = new GestureConfirmDialog(mContext);

            if(null != dbData && null != dbData.resolvedComponent){
                mWindowMgr.addView(mFwDialog, WindowManagerUtil.dialogLayoutParams());
                mFwDialog.showAlert(mContext.getString(R.string.add_gesture_alert_duplicate),
                        mContext.getString(R.string.add_gesture_alert_duplicate_content),
                        gesture, dbData,
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mWindowMgr.removeView(mFwDialog);
                            }
                        });
            }else{
                mWindowMgr.addView(mFwDialog, WindowManagerUtil.dialogLayoutParams());
                mFwDialog.showConfirm(mContext.getString(R.string.add_gesture_confirm_title),
                        mContext.getString(R.string.add_gesture_confirm_content),
                        gesture, mResolvedComponent,
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                try {
                                    GesturePersistence.saveGesture(mContext, gesture, mResolvedComponent);
                                    mWindowMgr.removeView(mFwDialog);
                                    if(null != gestureAddedPostRunnable){
                                        gestureAddedPostRunnable.run();
                                    }
                                } catch (GesturePersistence.GestureLibraryException e) {
                                    e.printStackTrace();
                                } catch (GesturePersistence.GestureSaveIconException e) {
                                    e.printStackTrace();
                                } catch (GesturePersistence.GestureDbException e) {
                                    e.printStackTrace();
                                }
                            }
                        },
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mWindowMgr.removeView(mFwDialog);
                            }
                        }
                );
            }
        }
    }
}
