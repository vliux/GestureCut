package org.vliux.android.gesturecut.ui.view.glv;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;

import org.vliux.android.gesturecut.R;
import org.vliux.android.gesturecut.model.ResolvedComponent;
import org.vliux.android.gesturecut.ui.view.AppInfoView;

/**
 * Created by vliux on 12/25/14.
 */
public class GestureListItem extends LinearLayout {
    private ImageView mImageView;
    private AppInfoView mAppInfoView;

    public GestureListItem(Context context) {
        super(context);
        init(context);
    }

    public GestureListItem(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public GestureListItem(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context){
        LayoutInflater.from(context).inflate(R.layout.item_gesture, this, true);
        mImageView = (ImageView)findViewById(R.id.item_gesture_icon);
        mAppInfoView = (AppInfoView)findViewById(R.id.item_gesture_appinfo);
    }

    public AppInfoView getAppInfoView(){
        return mAppInfoView;
    }

    public void setOnGestureIconClicked(OnClickListener listener){
        mImageView.setOnClickListener(listener);
    }

    public void setGestureImage(Bitmap bmp){
        mImageView.setImageBitmap(bmp);
    }

    public void setAppInfoResolvedComponent(ResolvedComponent rc){
        mAppInfoView.setResolvedComponent(rc);
    }
}
