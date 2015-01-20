package org.vliux.android.gesturecut.activity;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.widget.TextView;

import org.vliux.android.gesturecut.R;

/**
 * Created by vliux on 12/29/14.
 */
public class AboutActivity extends ActionBarActivity{
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

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
    }
}
