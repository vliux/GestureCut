package org.vliux.android.gesturecut.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.TextView;

import org.vliux.android.gesturecut.R;

/**
 * Created by vliux on 12/29/14.
 */
public class AboutActivity extends Activity {
    private TextView mTvContent1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        mTvContent1 = (TextView)findViewById(R.id.about_content_1);

        String version = null;
        try {
            PackageInfo pkgInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            version = pkgInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        mTvContent1.setText(String.format("%s %s", getText(R.string.app_name),
                null != version? version : ""));

        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
    }
}
