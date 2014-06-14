package org.vliux.android.gesturecut.ui.view.GestureList;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

/**
 * Created by vliux on 4/29/14.
 * When this ListView is touched, the bottom bar will be hidden.
 */
public class BottomBarAwaredListView extends ListView {
    private BottomBar mBottomBar;

    private static final int LAST_LONG_CLICK_POS_INVALID = -1;
    private int mLastLongClickPos = LAST_LONG_CLICK_POS_INVALID;

    public BottomBarAwaredListView(Context context) {
        super(context);
        init();
    }

    public BottomBarAwaredListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BottomBarAwaredListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init(){
        setChoiceMode(CHOICE_MODE_SINGLE);
        setOnItemClickListener(mRmActivationItemClickedListener);
    }

    /**
     * Need to set ASAP it's initialized.
     * @param bottomBar
     */
    public void setBottomBar(BottomBar bottomBar){
        mBottomBar = bottomBar;
        bottomBar.setBottomBarAwaredListView(this);
    }

    public void resetActivatedState(){
        if(mLastLongClickPos != LAST_LONG_CLICK_POS_INVALID){
            setItemChecked(mLastLongClickPos, false);
            mLastLongClickPos = LAST_LONG_CLICK_POS_INVALID;
        }
    }

    @Override
    public void setOnItemLongClickListener(final OnItemLongClickListener listener) {
        super.setOnItemLongClickListener(new OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                boolean ret = listener.onItemLongClick(parent, view, position, id);
                mActivateItemLongClickedListener.onItemLongClick(parent, view, position, id);
                return ret;
            }
        });
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

    /**
     * when an item is single clicked, its activated state is persisted.
     * we don't want this state to be kept in single click.
     */
    private OnItemClickListener mRmActivationItemClickedListener = new OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            setItemChecked(position, false);
        }
    };

    /**
     * when an item is long clicked, we want to keep the activated state.
     */
    private OnItemLongClickListener mActivateItemLongClickedListener = new OnItemLongClickListener() {
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
            setItemChecked(position, true);
            mLastLongClickPos = position;
            return false;
        }
    };
}
