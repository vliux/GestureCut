package org.vliux.android.gesturecut.ui.floatwindow;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.vliux.android.gesturecut.R;

/**
 * Created by vliux on 4/17/14.
 */
public class TabLikeView extends LinearLayout implements View.OnClickListener {
    private TextView mTvUse;
    private View mUnderscoreUse;
    private TextView mTvAdd;
    private View mUnderscoreAdd;
    private TabType mCurrentType = TabType.USE;
    private OnTablikeChangedListener mTablikeChangedListener;

    public TabLikeView(Context context) {
        super(context);
        init();
    }

    public TabLikeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TabLikeView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init(){
        LayoutInflater.from(getContext()).inflate(R.layout.view_tablike, this, true);
        mTvUse = (TextView)findViewById(R.id.tablike_tv_use);
        mTvAdd = (TextView)findViewById(R.id.tablike_tv_add);
        mUnderscoreUse = findViewById(R.id.tablike_underscore_use);
        mUnderscoreAdd = findViewById(R.id.tablike_underscore_add);

        mTvUse.setOnClickListener(this);
        mTvAdd.setOnClickListener(this);
    }

    public void switchTab(TabType type){
        if(mCurrentType != type){
            switch (type){
                case USE:
                    mTvAdd.setTypeface(null, Typeface.NORMAL);
                    mTvUse.setTypeface(null, Typeface.BOLD);
                    mUnderscoreAdd.setVisibility(INVISIBLE);
                    mUnderscoreUse.setVisibility(VISIBLE);
                    mTvAdd.setTextColor(getContext().getResources().getColor(R.color.gesture_cur_grey));
                    mTvUse.setTextColor(getContext().getResources().getColor(R.color.gesture_cur_blue));
                    break;
                case ADD:
                    mTvAdd.setTypeface(null, Typeface.BOLD);
                    mTvUse.setTypeface(null, Typeface.NORMAL);
                    mUnderscoreAdd.setVisibility(VISIBLE);
                    mUnderscoreUse.setVisibility(INVISIBLE);
                    mTvAdd.setTextColor(getContext().getResources().getColor(R.color.gesture_cur_blue));
                    mTvUse.setTextColor(getContext().getResources().getColor(R.color.gesture_cur_grey));
                    break;
            }
            mCurrentType = type;
            if(null != mTablikeChangedListener){
                mTablikeChangedListener.onTabSwitched(type);
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.tablike_tv_add:
                switchTab(TabType.ADD);
                break;
            case R.id.tablike_tv_use:
                switchTab(TabType.USE);
                break;
        }
    }

    public void setOnTabChangedListener(OnTablikeChangedListener listener){
        mTablikeChangedListener = listener;
    }

    public enum TabType{
        USE, ADD
    }

    public interface OnTablikeChangedListener {
        public void onTabSwitched(TabType newType);
    }

    public TabType getType(){
        return mCurrentType;
    }
}
