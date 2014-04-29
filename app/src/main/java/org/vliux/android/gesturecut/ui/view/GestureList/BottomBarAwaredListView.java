package org.vliux.android.gesturecut.ui.view.GestureList;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ListView;

/**
 * Created by vliux on 4/29/14.
 * When this ListView is touched, the bottom bar will be hidden.
 */
public class BottomBarAwaredListView extends ListView {

    private BottomBar mBottomBar;

    public BottomBarAwaredListView(Context context) {
        super(context);
    }

    public BottomBarAwaredListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BottomBarAwaredListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    /**
     * Need to set ASAP it's initialized.
     * @param bottomBar
     */
    public void setBottomBar(BottomBar bottomBar){
        mBottomBar = bottomBar;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getAction()){
            case MotionEvent.ACTION_DOWN:
                Message msg = mHandler.obtainMessage(WHAT_HIDE_BOTTOM_BAR);
                mHandler.sendMessage(msg);
                break;
        }
        return super.onInterceptTouchEvent(ev);
    }

    private static final int WHAT_HIDE_BOTTOM_BAR = 100;

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case WHAT_HIDE_BOTTOM_BAR:
                    if(null != mBottomBar){
                        mBottomBar.hideBottomBar();
                    }
                    break;
            }
        }
    };
}
