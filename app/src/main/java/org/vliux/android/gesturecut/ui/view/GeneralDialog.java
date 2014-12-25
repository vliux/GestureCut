package org.vliux.android.gesturecut.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import org.vliux.android.gesturecut.R;

/**
 * Created by vliux on 12/25/14.
 */
public class GeneralDialog extends FrameLayout {
    private TextView mTvTitle;
    private TextView mTvContent;
    private Button mBtnCancel;
    private Button mBtnOk;

    public GeneralDialog(Context context) {
        super(context);
        init(context);
    }

    public GeneralDialog(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public GeneralDialog(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context){
        LayoutInflater.from(context).inflate(R.layout.dialog_general, this, true);
        mTvTitle = (TextView)findViewById(R.id.dlg_title);
        mTvContent = (TextView)findViewById(R.id.dlg_content);
        mBtnCancel = (Button)findViewById(R.id.dlg_btn_cancel);
        mBtnOk = (Button)findViewById(R.id.dlg_btn_ok);
    }

    public void setTitleContent(String title, String content){
        mTvTitle.setText(title);
        mTvContent.setText(content);
    }

    public void setOnCancelClicked(OnClickListener cancelClicked){
        mBtnCancel.setOnClickListener(cancelClicked);
    }

    public void setOnOkClicked(OnClickListener okClicked){
        mBtnOk.setOnClickListener(okClicked);
    }
}
