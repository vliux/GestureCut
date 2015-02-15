package org.vliux.android.gesturecut.activity.add;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.vliux.android.gesturecut.R;
import org.vliux.android.gesturecut.biz.TaskManager;
import org.vliux.android.gesturecut.model.ResolvedComponent;

/**
 * Created by vliux on 2/15/15.
 */
public class AddGestureDrawActivity extends ActionBarActivity {
    public static final String INTENT_RESOLVED_COMPONENT = "rc";

    private TextView mTvAppName;
    private ImageView mIvAppIcon;
    private ResolvedComponent mRc;

    public static void start(Context context, ResolvedComponent rc){
        if(null != rc && null != rc.getType()) {
            Intent intent = new Intent(context, AddGestureDrawActivity.class);
            intent.putExtra(INTENT_RESOLVED_COMPONENT, rc);
            context.startActivity(intent);
        }else{
            Toast.makeText(context, context.getString(R.string.new_gesture_no_rc), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_gesture_draw);
        mTvAppName = (TextView)findViewById(R.id.agda_app_name);
        mIvAppIcon = (ImageView)findViewById(R.id.agda_app_icon);

        Intent intent = getIntent();
        if(intent.hasExtra(INTENT_RESOLVED_COMPONENT)){
            mRc = intent.getParcelableExtra(INTENT_RESOLVED_COMPONENT);
            if(null != mRc){
                mTvAppName.setText(TaskManager.getDescription(this, mRc, false)[0]);
                mIvAppIcon.setImageDrawable(TaskManager.getIcon(this, mRc));
            }
        }
    }
}
