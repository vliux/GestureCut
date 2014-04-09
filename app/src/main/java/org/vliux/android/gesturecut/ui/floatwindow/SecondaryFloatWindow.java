package org.vliux.android.gesturecut.ui.floatwindow;

import android.content.Context;
import android.gesture.Gesture;
import android.gesture.GestureLibrary;
import android.gesture.GestureOverlayView;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.vliux.android.gesturecut.R;
import org.vliux.android.gesturecut.biz.db.DbManager;
import org.vliux.android.gesturecut.biz.db.DbTable;
import org.vliux.android.gesturecut.biz.db.GestureDbTable;
import org.vliux.android.gesturecut.biz.gesture.GesturePersistence;
import org.vliux.android.gesturecut.util.GestureUtil;

/**
 * Created by vliux on 4/9/14.
 */
public class SecondaryFloatWindow extends LinearLayout implements View.OnClickListener {
    private Button mRecordBtn;
    private GestureOverlayView mGestureOverlayView;

    public SecondaryFloatWindow(Context context) {
        super(context);
        init();
    }

    public SecondaryFloatWindow(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SecondaryFloatWindow(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init(){
        LayoutInflater.from(getContext()).inflate(R.layout.view_2nd_floatwindow, this, true);
        mRecordBtn = (Button)findViewById(R.id.btn_record);
        mGestureOverlayView = (GestureOverlayView)findViewById(R.id.gesture_overlay);

        mRecordBtn.setOnClickListener(this);
        mGestureOverlayView.setGestureColor(Color.RED);
        mGestureOverlayView.addOnGesturePerformedListener(mOnGesturePerformedListener);
    }

    public void onClick(View view){
        switch (view.getId()){
            case R.id.btn_record:

                break;
        }
    }

    private GestureOverlayView.OnGesturePerformedListener mOnGesturePerformedListener = new GestureOverlayView.OnGesturePerformedListener() {
        @Override
        public void onGesturePerformed(GestureOverlayView overlay, Gesture gesture) {
            Toast.makeText(getContext(), getContext().getString(R.string.saving_gesture), Toast.LENGTH_SHORT).show();
            try {
                GesturePersistence.saveGesture(getContext(), gesture, null);
            } catch (GesturePersistence.GestureLibraryException e) {
                e.printStackTrace();
            } catch (GesturePersistence.GestureSaveIconException e) {
                e.printStackTrace();
            } catch (GesturePersistence.GestureDbException e) {
                e.printStackTrace();
            }
        }
    };
}
