package org.vliux.android.gesturecut.ui;

import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.vliux.android.gesturecut.R;

/**
 * Created by vliux on 4/26/14.
 */
public class WelcomeActivity extends BaseActivity {
    private static final int NUM_PAGES = 4;
    private ViewPager mViewPager;

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
                    break;
            }
            container.addView(pageView);
            return pageView;
        }
    };
}
