package org.vliux.android.gesturecut.ui.floatwindow;

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

    public void show(String title, String content, OnClickListener saveClicked, OnClickListener cancelClicked){
        mTvTitle.setText(title);
        mTvContent.setText(content);
        mSaveClicked = saveClicked;
        mCancelClicked = cancelClicked;
        setVisibility(VISIBLE);
    }

    public void hide(){
        mSaveClicked = null;
        mCancelClicked = null;
        setVisibility(GONE);
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
