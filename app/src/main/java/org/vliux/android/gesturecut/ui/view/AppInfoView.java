package org.vliux.android.gesturecut.ui.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.vliux.android.gesturecut.R;
import org.vliux.android.gesturecut.biz.ResolvedComponent;
import org.vliux.android.gesturecut.biz.TaskManager;

/**
 * Created by vliux on 4/20/14.
 */
public class AppInfoView extends LinearLayout{
    private ImageView mIvAppIcon;
    private TextView mTvAppName;
    private TextView mTvAppDetail;
    private ResolvedComponent mResolvedComponent;
    private boolean mLongClzName = true;

    public AppInfoView(Context context) {
        super(context);
        init();
    }

    public AppInfoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        readAttrs(attrs);
        init();
    }

    public AppInfoView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        readAttrs(attrs);
        init();
    }

    private void readAttrs(AttributeSet attrs){
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.AppInfoView);
        mLongClzName = typedArray.getBoolean(R.styleable.AppInfoView_longClassName, true);
        typedArray.recycle();
    }

    private void init(){
        LayoutInflater.from(getContext()).inflate(R.layout.view_appinfo, this, true);
        mIvAppIcon = (ImageView)findViewById(R.id.appinfo_app_icon);
        mTvAppName = (TextView)findViewById(R.id.appinfo_appname);
        mTvAppDetail = (TextView)findViewById(R.id.appinfo_appdetail);
    }

    public void setResolvedComponent(ResolvedComponent resolvedComponent){
        mResolvedComponent = resolvedComponent;
        mIvAppIcon.setImageDrawable(TaskManager.getIcon(getContext(), resolvedComponent));
        String[] descStrings = TaskManager.getDescription(getContext(), resolvedComponent, mLongClzName);
        if(null != descStrings && descStrings.length >= 2){
            mTvAppName.setText(descStrings[0]);
            mTvAppDetail.setText(descStrings[1]);
        }
    }
}
