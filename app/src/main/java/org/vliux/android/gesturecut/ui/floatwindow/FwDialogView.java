package org.vliux.android.gesturecut.ui.floatwindow;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import org.vliux.android.gesturecut.R;

/**
 * Created by vliux on 4/18/14.
 */
public class FwDialogView extends FrameLayout implements View.OnClickListener {
    private TextView mTvTitle;
    private TextView mTvContent;
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

        mBtnSave.setOnClickListener(this);
        mBtnCancel.setOnClickListener(this);
    }

    public boolean isShow(){
        return getVisibility() == VISIBLE;
    }

    public void showAlert(String title, String content, OnClickListener okClicked){
        mTvTitle.setText(title);
        mTvContent.setText(content);

        mBtnSave.setText(getContext().getString(R.string.get_it));
        mBtnSave.setBackgroundResource(R.drawable.btn_bg_warning);
        mBtnCancel.setVisibility(GONE);

        mSaveClicked = okClicked;
        getShowHideAnimator(true).start();
    }

    public void showConfirm(String title, String content, OnClickListener saveClicked, OnClickListener cancelClicked){
        mTvTitle.setText(title);
        mTvContent.setText(content);

        mBtnSave.setText(getContext().getString(R.string.save));
        mBtnSave.setBackgroundResource(R.drawable.btn_bg_ok);
        mBtnCancel.setVisibility(VISIBLE);

        mSaveClicked = saveClicked;
        mCancelClicked = cancelClicked;
        getShowHideAnimator(true).start();
    }

    public void hide(){
        mSaveClicked = null;
        mCancelClicked = null;
        getShowHideAnimator(false).start();
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
        animatorSet.setDuration(500L);
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
}
