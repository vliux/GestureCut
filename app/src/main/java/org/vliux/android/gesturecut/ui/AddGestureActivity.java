package org.vliux.android.gesturecut.ui;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;

import com.melnykov.fab.FloatingActionButton;

import org.vliux.android.gesturecut.AppConstant;
import org.vliux.android.gesturecut.R;
import org.vliux.android.gesturecut.model.ResolvedComponent;
import org.vliux.android.gesturecut.ui.view.AddGestureView;
import org.vliux.android.gesturecut.ui.view.AppInfoView;
import org.vliux.android.gesturecut.util.ScreenUtil;
import org.vliux.android.gesturecut.util.WindowManagerUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vliux on 12/23/14.
 */
public class AddGestureActivity extends Activity {
    private FrameLayout mLayout;
    private ListView mListView;
    private PkgListAdapter mListAdapter;
    private int mListItemPaddingHoriz = 0;
    private int mListItemPaddingVerti = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_gesture);

        mLayout = (FrameLayout)findViewById(R.id.add_ges_layout_top);
        mListView = (ListView)findViewById(R.id.list_packges);
        mListAdapter = new PkgListAdapter(getPackageManager());
        mListView.setAdapter(mListAdapter);
        mListView.setOnItemClickListener(mOnItemClickListener);

        mListItemPaddingHoriz = (int)getResources().getDimension(R.dimen.gesture_list_outter_margin);
        mListItemPaddingVerti = (int)getResources().getDimension(R.dimen.gesture_list_item_padding_vertical);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mListAdapter.scanInstalledPackages();
    }

    class PkgListAdapter extends BaseAdapter{
        private List<ResolvedComponent> installedComponents;
        private PackageManager packageManager;

        public PkgListAdapter(PackageManager pkgMgr){
            packageManager = pkgMgr;
            installedComponents = new ArrayList<ResolvedComponent>();
        }

        private void scanInstalledPackages(){
            installedComponents.clear();
            List<PackageInfo> pkgInfoList = packageManager.getInstalledPackages(PackageManager.GET_META_DATA);
            for(PackageInfo pkgInfo : pkgInfoList){
                ResolvedComponent rc = new ResolvedComponent(pkgInfo.packageName);
                installedComponents.add(rc);
            }
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return installedComponents.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            AppInfoView appInfoView = null;
            if(null != convertView){
                appInfoView = (AppInfoView)convertView;
            }else{
                appInfoView = new AppInfoView(AddGestureActivity.this);
                appInfoView.setPadding(mListItemPaddingHoriz, mListItemPaddingVerti, mListItemPaddingHoriz, mListItemPaddingVerti);
            }

            appInfoView.setResolvedComponent(installedComponents.get(position));
            return appInfoView;
        }
    }

    private AddGestureView mAddGestureView;
    private final AdapterView.OnItemClickListener mOnItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
            translateListItem(view);
        }
    };

    private View mListItemTranslated;
    private Drawable mListItemOrgBg;
    private void translateListItem(final View view){
        if(null != mListItemTranslated){
            return;
        }

        mListItemTranslated = view;
        mListItemOrgBg = mListItemTranslated.getBackground();
        Animator animator = ObjectAnimator.ofFloat(view, "translationY", 0f, -view.getY());
        animator.setDuration(AppConstant.Anim.ANIM_DURATION_NORMAL);
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                view.setBackgroundColor(getResources().getColor(R.color.beige_light_semi_transparent));
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                showAddGestureView(mLayout.getHeight() - view.getHeight());
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
        animator.start();
    }

    private void revertListItem(){
        if(null != mListItemTranslated){
            Animator animator = ObjectAnimator.ofFloat(mListItemTranslated, "translationY", mListItemTranslated.getTranslationY(), 0f);
            animator.setDuration(AppConstant.Anim.ANIM_DURATION_NORMAL);
            animator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    mListItemTranslated.setBackgroundDrawable(mListItemOrgBg);
                    mListItemTranslated = null;
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                }

                @Override
                public void onAnimationRepeat(Animator animation) {
                }
            });
            animator.start();
        }
    }

    /*
     Show it at the bottom.
     */
    private void showAddGestureView(int height){
        if(null != mAddGestureView){
            return;
        }
        mAddGestureView = new AddGestureView(AddGestureActivity.this);
        final FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                height,
                Gravity.BOTTOM);
        int[] screenSize = ScreenUtil.getScreenSize(this);
        Animator animator = ObjectAnimator.ofFloat(mAddGestureView, "translationX", -screenSize[0], 0f);
        animator.setDuration(AppConstant.Anim.ANIM_DURATION_NORMAL);
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                mLayout.addView(mAddGestureView, params);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
        animator.start();
    }

    private void closeAddGestureView(){
        if(null != mAddGestureView){
            int[] screenSize = ScreenUtil.getScreenSize(this);
            Animator animator = ObjectAnimator.ofFloat(mAddGestureView, "translationX", 0f, -screenSize[0]);
            animator.setDuration(AppConstant.Anim.ANIM_DURATION_NORMAL);
            animator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    if(null != mAddGestureView) {
                        mLayout.removeView(mAddGestureView);
                        mAddGestureView = null;
                    }
                    revertListItem();
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                }

                @Override
                public void onAnimationRepeat(Animator animation) {
                }
            });
            animator.start();
        }
    }

    @Override
    public void onBackPressed() {
        if(null != mAddGestureView){
            closeAddGestureView();
        }else {
            super.onBackPressed();
        }
    }
}
