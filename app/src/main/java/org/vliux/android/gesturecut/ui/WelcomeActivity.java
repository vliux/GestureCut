package org.vliux.android.gesturecut.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import org.vliux.android.gesturecut.R;
import org.vliux.android.gesturecut.util.PreferenceHelper;

/**
 * Created by vliux on 4/26/14.
 */
public class WelcomeActivity extends BaseActivity implements View.OnClickListener{
    private static final int NUM_PAGES = 4;
    private ViewPager mViewPager;

    public static void startWelcomeIfNeeded(Context context){
        boolean isAleadyShown =
                PreferenceHelper.getUserPref(context.getApplicationContext(), R.string.pref_key_welcome_already_shown, false);
        if(!isAleadyShown){
            context.startActivity(new Intent(context, WelcomeActivity.class));
            PreferenceHelper.setUserPref(context.getApplicationContext(), R.string.pref_key_welcome_already_shown, true);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        mViewPager = (ViewPager)findViewById(R.id.welcome_viewpager);
        mViewPager.setAdapter(mViewPagerAdapter);
    }

    private PagerAdapter mViewPagerAdapter = new PagerAdapter() {
        @Override
        public int getCount() {
            return NUM_PAGES;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View)object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View pageView = null;
            switch (position){
                case 0:
                    pageView = LayoutInflater.from(WelcomeActivity.this).inflate(R.layout.view_welcome_01, null);
                    break;
                case 1:
                    pageView = LayoutInflater.from(WelcomeActivity.this).inflate(R.layout.view_welcome_02, null);
                    break;
                case 2:
                    pageView = LayoutInflater.from(WelcomeActivity.this).inflate(R.layout.view_welcome_03, null);
                    break;
                case 3:
                    pageView = LayoutInflater.from(WelcomeActivity.this).inflate(R.layout.view_welcome_04, null);
                    Button startBtn = (Button)pageView.findViewById(R.id.welcome_start_btn);
                    startBtn.setOnClickListener(WelcomeActivity.this);
                    break;
            }
            container.addView(pageView);
            return pageView;
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.welcome_start_btn:
                finish();
                break;
        }
    }
}
