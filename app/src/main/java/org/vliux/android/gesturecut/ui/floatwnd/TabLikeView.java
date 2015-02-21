/*package org.vliux.android.gesturecut.ui.floatwnd;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.vliux.android.gesturecut.R;

public class TabLikeView extends LinearLayout implements View.OnClickListener {
    private static final int COLOR_ACTIVE = R.color.yellow;
    private static final int COLOR_INACTIVE = R.color.text_general;

    private ImageView mIvUse;
    private ImageView mIvAdd;
    private View mUnderscoreUse;
    private View mUnderscoreAdd;

    private ImageView mIvList;
    private View mUnderscoreList;

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
        mIvUse = (ImageView)findViewById(R.id.tablike_use_iv);
        mIvAdd = (ImageView)findViewById(R.id.tablike_add_iv);
        mUnderscoreUse = findViewById(R.id.tablike_underscore_use);
        mUnderscoreAdd = findViewById(R.id.tablike_underscore_add);

        mIvList = (ImageView)findViewById(R.id.tablike_list);
        mUnderscoreList = findViewById(R.id.tablike_underscore_list);

        mIvUse.setOnClickListener(this);
        mIvAdd.setOnClickListener(this);
        mIvList.setOnClickListener(this);
    }

    public void switchTab(TabType type){
        if(mCurrentType != type){
            if(null != mTablikeChangedListener){
                if(!mTablikeChangedListener.onTabSwitched(type)){
                    return;
                }
            }

            switch (type){
                case USE:
                    //mTvAdd.setTypeface(null, Typeface.NORMAL);
                    //mTvUse.setTypeface(null, Typeface.BOLD);
                    mUnderscoreAdd.setVisibility(INVISIBLE);
                    mUnderscoreUse.setVisibility(VISIBLE);
                    //mTvAdd.setTextColor(getResources().getColor(COLOR_INACTIVE));
                    //mTvUse.setTextColor(getResources().getColor(COLOR_ACTIVE));
                    mUnderscoreList.setVisibility(INVISIBLE);
                    break;
                case ADD:
                    //mTvAdd.setTypeface(null, Typeface.BOLD);
                    //mTvUse.setTypeface(null, Typeface.NORMAL);
                    mUnderscoreAdd.setVisibility(VISIBLE);
                    mUnderscoreUse.setVisibility(INVISIBLE);
                    //mTvAdd.setTextColor(getResources().getColor(COLOR_ACTIVE));
                    //mTvUse.setTextColor(getResources().getColor(COLOR_INACTIVE));
                    mUnderscoreList.setVisibility(INVISIBLE);
                    break;
                case LIST:
                    //mTvAdd.setTypeface(null, Typeface.NORMAL);
                    //mTvUse.setTypeface(null, Typeface.NORMAL);
                    mUnderscoreAdd.setVisibility(INVISIBLE);
                    mUnderscoreUse.setVisibility(INVISIBLE);
                    //mTvAdd.setTextColor(getResources().getColor(COLOR_INACTIVE));
                    //mTvUse.setTextColor(getResources().getColor(COLOR_INACTIVE));

                    mUnderscoreList.setVisibility(VISIBLE);
            }
            mCurrentType = type;
        }
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.tablike_add_iv:
                switchTab(TabType.ADD);
                break;
            case R.id.tablike_use_iv:
                switchTab(TabType.USE);
                break;
            case R.id.tablike_list:
                switchTab(TabType.LIST);
                break;
        }
    }

    public void setOnTabChangedListener(OnTablikeChangedListener listener){
        mTablikeChangedListener = listener;
    }

    public enum TabType{
        USE, ADD, LIST
    }

    public interface OnTablikeChangedListener {
         //*
         //* @param newType
         //* @return True if the tab switch can be proceed, False if the switch should be stopped.

        public boolean onTabSwitched(TabType newType);
    }

    public TabType getType(){
        return mCurrentType;
    }
}*/
