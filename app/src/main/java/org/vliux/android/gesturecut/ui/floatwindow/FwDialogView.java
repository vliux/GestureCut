package org.vliux.android.gesturecut.ui.floatwindow;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.gesture.Gesture;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import org.vliux.android.gesturecut.AppConstant;
import org.vliux.android.gesturecut.R;
import org.vliux.android.gesturecut.biz.ConcurrentControl;
import org.vliux.android.gesturecut.biz.ResolvedComponent;
import org.vliux.android.gesturecut.biz.TaskManager;
import org.vliux.android.gesturecut.biz.db.GestureDbTable;
import org.vliux.android.gesturecut.biz.gesture.GesturePersistence;
import org.vliux.android.gesturecut.util.ImageUtil;
import org.w3c.dom.Text;

/**
 * Created by vliux on 4/18/14.
 * The dialog view for confirmation of adding new gesture.
 */
public class FwDialogView extends FrameLayout implements View.OnClickListener {
    private TextView mTvTitle;
    private TextView mTvContent;

    private TextView mTvLeft;
    private TextView mTvRight;
    private ImageView mIvLeft;
    private ImageView mIvRight;

    private OnClickListener mSaveClicked;
    private OnClickListener mCancelClicked;
    private Button mBtnSave;
    private Button mBtnCancel;

    public FwDialogView(Context context) {
        super(context);
        init();
    }

    public FwDialogView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public FwDialogView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init(){
        LayoutInflater.from(getContext()).inflate(R.layout.view_fw_dialog, this, true);
        mTvTitle = (TextView)findViewById(R.id.fw2_layer2_title);
        mTvContent = (TextView)findViewById(R.id.fw2_layer2_content);
        mBtnSave = (Button)findViewById(R.id.fw2_layer2_btn_save);
        mBtnCancel = (Button)findViewById(R.id.fw2_layer2_btn_cancel);
        mIvLeft = (ImageView)findViewById(R.id.fw_layer2_iv1);
        mIvRight = (ImageView)findViewById(R.id.fw_layer2_iv2);
        mTvLeft = (TextView)findViewById(R.id.fw_layer2_tv_title1);
        mTvRight = (TextView)findViewById(R.id.fw_layer2_tv_title2);

        mBtnSave.setOnClickListener(this);
        mBtnCancel.setOnClickListener(this);
    }

    public boolean isShow(){
        return getVisibility() == VISIBLE;
    }

    public void showAlert(String title, String content,
                          Gesture newGesture, GestureDbTable.DbData dbData,
                          OnClickListener okClicked){
        mTvTitle.setText(title);
        mTvContent.setText(content);

        mBtnSave.setText(getContext().getString(R.string.get_it));
        mBtnSave.setBackgroundResource(R.drawable.btn_bg_warning);
        mBtnCancel.setVisibility(GONE);

        mTvLeft.setText(getContext().getString(R.string.add_gesture_subtitle_new_gesture));
        mTvRight.setText(getContext().getString(R.string.add_gesture_subtitle_similar_gesture));
        mIvLeft.setImageBitmap(GesturePersistence.toBitmap(getContext(), newGesture));
        mIvRight.setImageDrawable(null);
        if(null != dbData && null != dbData.iconPath){
            ConcurrentControl.submitTask(new LoadExistingGestureIconRunnable(dbData.iconPath));
        }

        mSaveClicked = okClicked;
        getShowHideAnimator(true).start();
    }

    public void showConfirm(String title, String content,
                            Gesture newGesture, ResolvedComponent resolvedComponent,
                            OnClickListener saveClicked, OnClickListener cancelClicked){
        mTvTitle.setText(title);
        mTvContent.setText(content);

        mBtnSave.setText(getContext().getString(R.string.save));
        mBtnSave.setBackgroundResource(R.drawable.btn_bg_ok);
        mBtnCancel.setVisibility(VISIBLE);

        mTvLeft.setText(getContext().getString(R.string.add_gesture_subtitle_new_gesture));
        mTvRight.setText(getContext().getString(R.string.add_gesture_subtitle_target_task));
        mIvLeft.setImageBitmap(GesturePersistence.toBitmap(getContext(), newGesture));
        mIvRight.setImageDrawable(TaskManager.getIcon(getContext(), resolvedComponent));

        mSaveClicked = saveClicked;
        mCancelClicked = cancelClicked;
        getShowHideAnimator(true).start();
    }

    public void hide(){
        mSaveClicked = null;
        mCancelClicked = null;
        getShowHideAnimator(false).start();
        mHandler.removeMessages(WHAT_GESTURE_ICON_LOADED);
    }

    /**
     * Load gesture icon from file system.
     */
    class LoadExistingGestureIconRunnable implements Runnable{
        private String mIconPath;

        public LoadExistingGestureIconRunnable(String iconPath){
            mIconPath = iconPath;
        }

        @Override
        public void run() {
            int iconWidth = (int)getContext().getResources().getDimension(R.dimen.gesture_list_item_icon_dimen);
            int iconHeight = iconWidth;
            Bitmap bmp = null;
            if (null != mIconPath && mIconPath.length() > 0) {
                bmp = ImageUtil.decodeSampledBitmap(mIconPath, iconWidth, iconHeight, ImageUtil.optionSave());
                if(null != bmp){
                    Message msg = mHandler.obtainMessage(WHAT_GESTURE_ICON_LOADED, bmp);
                    mHandler.sendMessage(msg);
                }
            }
        }
    }

    private AnimatorSet getShowHideAnimator(final boolean forShown){
        ObjectAnimator scaleXAnimator = null;
        ObjectAnimator scaleYAnimator = null;
        ObjectAnimator alphaAnimator = null;
        if(forShown){
            //scaleXAnimator = ObjectAnimator.ofFloat(this, "scaleX", 0.5f, 1.0f);
            //scaleYAnimator = ObjectAnimator.ofFloat(this, "scaleY", 0.5f, 1.0f);
            alphaAnimator = ObjectAnimator.ofFloat(this, "alpha", 0.0f, 1.0f);
        }else{
            //scaleXAnimator = ObjectAnimator.ofFloat(this, "scaleX", 1.0f, 0.0f);
            //scaleYAnimator = ObjectAnimator.ofFloat(this, "scaleY", 1.0f, 0.0f);
            alphaAnimator = ObjectAnimator.ofFloat(this, "alpha", 1.0f, 0.0f);
        }

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setDuration(AppConstant.Anim.ANIM_DURATION_NORMAL);
        //animatorSet.play(scaleXAnimator).with(scaleYAnimator).with(alphaAnimator);
        animatorSet.play(alphaAnimator);
        animatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                if(forShown){
                    setVisibility(VISIBLE);
                }
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if(!forShown){
                    setVisibility(GONE);
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                setVisibility((forShown? VISIBLE : GONE));
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        return animatorSet;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.fw2_layer2_btn_save:
                if(null != mSaveClicked){
                    mSaveClicked.onClick(this);
                }
                break;
            case R.id.fw2_layer2_btn_cancel:
                if(null != mCancelClicked){
                    mCancelClicked.onClick(this);
                }
                break;
        }
    }

    private static final int WHAT_GESTURE_ICON_LOADED = 100;
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case WHAT_GESTURE_ICON_LOADED:
                    Bitmap bitmap = (Bitmap)msg.obj;
                    if(null != bitmap){
                        mIvRight.setImageBitmap(bitmap);
                    }
            }
        }
    };
}
