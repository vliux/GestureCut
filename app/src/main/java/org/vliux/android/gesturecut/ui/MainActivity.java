package org.vliux.android.gesturecut.ui;

import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import org.vliux.android.gesturecut.GuestCutApplication;
import org.vliux.android.gesturecut.R;

/**
 * Created by vliux on 4/3/14.
 */
public class MainActivity extends BaseActivity {
    private Button mBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setKeyGuardFlags();
        setContentView(R.layout.activity_main);
        mBtn = (Button)findViewById(R.id.main_btn);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public void onClick(View view){
        switch (view.getId()){
            case R.id.main_btn:
                GuestCutApplication.startTargetActivity(getApplicationContext());
                break;
        }
    }

    private void setKeyGuardFlags(){
        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        /**
         * @author haihong.xiahh add flags to show activity before key guard
         *         FLAG_SHOW_WHEN_LOCKED : special flag to let windows be shown
         *         when the screen is locked. FLAG_DISMISS_KEYGUARD : when set
         *         the window will cause the keyguard to be dismissed, only if
         *         it is not a secure lock keyguard.
         */
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
    }
}
