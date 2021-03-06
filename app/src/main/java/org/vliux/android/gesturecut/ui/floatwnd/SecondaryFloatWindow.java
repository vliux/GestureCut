package org.vliux.android.gesturecut.ui.floatwnd;

/**
 * Created by vliux on 4/9/14.
 */
/*public class SecondaryFloatWindow extends LinearLayout implements TabLikeView.OnTablikeChangedListener {
    private TextView mTvBack;
    private GestureOverlayView mGestureOverlayView;
    private TabLikeView mTabLikeView;
    private TextView mTvHint;
    private GestureConfirmDialog mFwDialog;

    private AppInfoView mAppInfoView; // app info shown when adding new gesture
    private LinearLayout mAppInfoLayout; // layout containing AppInfoView
    private ImageView mIvAppIconUseAnim; // app icon for animator when using gesture
    private TextView mTvInvalidRc; // show warning for invalid ResolvedComponent
    private GestureListView mGestureListView;
    private View mGestureListEmptyView;

    // ResolvedComponent as a gesture target. This variable is is used for kepping the reference,
    // as far as, when actually saving the new gesture, the top-level component may be different than what is shown to user.
    //
    private ResolvedComponent mResolvedComponent;

    public SecondaryFloatWindow(Context context) {
        super(context);
        init();
    }

    public SecondaryFloatWindow(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SecondaryFloatWindow(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init(){
        LayoutInflater.from(getContext()).inflate(R.layout.view_2nd_floatwindow, this, true);
        setOrientation(VERTICAL);
        setBackgroundResource(R.drawable.float_wnd_2nd_bg);

        mTvBack = (TextView)findViewById(R.id.fw2_tv_back);
        mGestureOverlayView = (GestureOverlayView)findViewById(R.id.gesture_overlay);
        mTabLikeView = (TabLikeView)findViewById(R.id.gesture_tablike);
        mTvHint = (TextView)findViewById(R.id.gesture_hint);
        mFwDialog = (GestureConfirmDialog)findViewById(R.id.gesture_fwdialog);
        mAppInfoView = (AppInfoView)findViewById(R.id.gesture_appinfoview);
        mIvAppIconUseAnim = (ImageView)findViewById(R.id.gesture_appicon_startactiv);
        mAppInfoLayout = (LinearLayout)findViewById(R.id.gesture_appinfo_layout);
        mTvInvalidRc = (TextView)findViewById(R.id.gesture_tv_invalid_rc);
        mGestureListView = (GestureListView)findViewById(R.id.gesture_listview);
        mGestureListEmptyView = findViewById(R.id.ges_list_empty_view);

        mGestureListView.setOnItemClickListener(mOnGestureListItemClicked);
        mGestureListEmptyView.setOnClickListener(mOnEmptyViewClicked);

        mGestureOverlayView.setGestureColor(Color.RED);
        mGestureOverlayView.addOnGesturePerformedListener(mOnGesturePerformedListener);

        mTabLikeView.setOnTabChangedListener(this);
        refreshHint(mTabLikeView.getType());
        mTvBack.setOnClickListener(mOnBackClicked);
    }

    private final GestureOverlayView.OnGesturePerformedListener mOnGesturePerformedListener = new GestureOverlayView.OnGesturePerformedListener() {
        @Override
        public void onGesturePerformed(GestureOverlayView overlay, final Gesture gesture) {
            switch (mTabLikeView.getType()){
                case ADD:
                    addGesture(gesture);
                    break;
                case USE:
                    useGesture(gesture);
                    break;
            }
        }
    };

    private final AdapterView.OnItemClickListener mOnGestureListItemClicked = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            String gestureName = mGestureListView.getGestureName(position);
            if(null != gestureName && gestureName.length() > 0){
                GestureDbTable.DbData dbData = GesturePersistence.loadGestureEx(getContext(), gestureName);
                if(null != dbData && null != dbData.resolvedComponent){
                    AnimUtil.getStartActivityAnimatorSet(getContext(), mIvAppIconUseAnim, dbData.resolvedComponent, new Runnable(){
                        @Override
                        public void run() {
                            WindowManagerUtil.closeWindow(getContext(), SecondaryFloatWindow.this);
                        }
                    }).start();
                }
            }
        }
    };

    private void useGesture(Gesture gesture){
        ResolvedComponent resolvedComponent = GesturePersistence.loadGesture(getContext(), gesture);
        if(null != resolvedComponent){
            AnimUtil.getStartActivityAnimatorSet(getContext(), mIvAppIconUseAnim, resolvedComponent, new Runnable(){
                @Override
                public void run() {
                    WindowManagerUtil.closeWindow(getContext(), SecondaryFloatWindow.this);
                }
            }).start();
        }else{
            Toast.makeText(getContext(),
                    getContext().getString(R.string.no_gesture_match), Toast.LENGTH_SHORT).show();
        }
    }

    private void addGesture(final Gesture gesture){
        if(null != mResolvedComponent){
            //Toast.makeText(getContext(), getContext().getString(R.string.saving_gesture), Toast.LENGTH_SHORT).show();
            GestureDbTable.DbData dbData = GesturePersistence.loadGestureEx(getContext(), gesture);
            if(null != dbData && null != dbData.resolvedComponent){
                mFwDialog.showAlert(getContext().getString(R.string.add_gesture_alert_duplicate),
                        getContext().getString(R.string.add_gesture_alert_duplicate_content),
                        gesture, dbData,
                        new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mFwDialog.hide();
                            }
                        });
            }else{
                mFwDialog.showConfirm(getContext().getString(R.string.add_gesture_confirm_title),
                        getContext().getString(R.string.add_gesture_confirm_content),
                        gesture, mResolvedComponent,
                        new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                try {
                                    GesturePersistence.saveGesture(getContext(), gesture, mResolvedComponent);
                                    WindowManagerUtil.closeWindow(getContext().getApplicationContext(), SecondaryFloatWindow.this);
                                } catch (GesturePersistence.GestureLibraryException e) {
                                    e.printStackTrace();
                                } catch (GesturePersistence.GestureSaveIconException e) {
                                    e.printStackTrace();
                                } catch (GesturePersistence.GestureDbException e) {
                                    e.printStackTrace();
                                }
                            }
                        },
                        new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mFwDialog.hide();
                            }
                        }
                );
            }
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if(event.getKeyCode() == KeyEvent.KEYCODE_BACK
                && KeyEvent.ACTION_UP == event.getAction()){
            if(mFwDialog.isShow()){
                mFwDialog.hide();
            }else {
                quitAnim();
            }
            return true;
        }else{
            return super.dispatchKeyEvent(event);
        }
    }

    private boolean mQuitAnimLock = false;
    private void quitAnim(){
        if(mQuitAnimLock){
            return;
        }

        mQuitAnimLock = true;
        ObjectAnimator animator = ObjectAnimator.ofFloat(this, "alpha", 1.0f, 0.0f);
        animator.setDuration(AppConstant.Anim.ANIM_DURATION_NORMAL);
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                WindowManagerUtil.closeWindow(getContext().getApplicationContext(), SecondaryFloatWindow.this);
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

    @Override
    public boolean onTabSwitched(TabLikeView.TabType newType) {
        return refreshHint(newType);
    }

    private boolean refreshHint(TabLikeView.TabType tabType){
        if(mSwitchTabAnimLock){
            return false;
        }

        switch (tabType){
            case ADD:
                mTvHint.setText(getContext().getString(R.string.gesture_bg_title_record));
                mResolvedComponent = TaskManager.getTopComponent(getContext());
                try {
                    TaskFilterManager.getInstance().processAddFilters(getContext(), mResolvedComponent);
                    mTvInvalidRc.setVisibility(GONE);
                } catch (TaskFilterException e) {
                    e.printStackTrace();
                    mTvInvalidRc.setVisibility(VISIBLE);
                    mTvInvalidRc.setText(e.getMessage());
                }
                mAppInfoView.setResolvedComponent(mResolvedComponent);
                mGestureListView.setEmptyView(null);
                //mGestureOverlayView.setVisibility(VISIBLE);
                //mGestureListView.setVisibility(GONE);
                break;
            case USE:
                mTvHint.setText(getContext().getString(R.string.gesture_bg_title_use));
                mGestureListView.setEmptyView(null);
                //mGestureOverlayView.setVisibility(VISIBLE);
                //mGestureListView.setVisibility(GONE);
                break;
            case LIST:
                //mAppInfoLayout.setVisibility(INVISIBLE);
                //mGestureListView.setVisibility(VISIBLE);
                //mGestureOverlayView.setVisibility(GONE);
                mGestureListView.refresh();
                mGestureListView.setEmptyView(mGestureListEmptyView);
                break;
        }
        switchTabAnim(tabType);
        return true;
    }

    private boolean mSwitchTabAnimLock = false;
    private void switchTabAnim(final TabLikeView.TabType tabType){
        mSwitchTabAnimLock = true;
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setDuration(AppConstant.Anim.ANIM_DURATION_NORMAL);
        animatorSet.setInterpolator(new AccelerateDecelerateInterpolator());

        ObjectAnimator mainAnimator = null;
        switch (tabType) {
            case ADD:
            case USE:
                mainAnimator = ObjectAnimator.ofFloat(mGestureOverlayView, "alpha", 0.0f, 1.0f);
                mainAnimator.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        if(mGestureListView.getVisibility() == VISIBLE){
                            mGestureListView.setVisibility(GONE);
                            mGestureListEmptyView.setVisibility(GONE);
                        }
                        if(mGestureOverlayView.getVisibility() != VISIBLE){
                            mGestureOverlayView.setVisibility(VISIBLE);
                        }
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        mSwitchTabAnimLock = false;
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                        mSwitchTabAnimLock = false;
                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {
                    }
                });
                break;
            case LIST:
                mainAnimator = ObjectAnimator.ofFloat(mGestureListView, "alpha", 0.0f, 1.0f);
                mainAnimator.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        if(mGestureListView.getVisibility() != VISIBLE){
                            mGestureListView.setVisibility(VISIBLE);
                        }
                        if(mGestureOverlayView.getVisibility() == VISIBLE){
                            mGestureOverlayView.setVisibility(GONE);
                        }
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        mSwitchTabAnimLock = false;
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                        mSwitchTabAnimLock = false;
                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {
                    }
                });
                break;
        }

        Animator appLayoutAnimator = appInfoLayoutAnimator(tabType);
        if(null != appLayoutAnimator){
            animatorSet.play(mainAnimator).with(appLayoutAnimator);
        }else{
            animatorSet.play(mainAnimator);
        }
        animatorSet.start();
    }

    private Animator appInfoLayoutAnimator(TabLikeView.TabType type){
        ObjectAnimator translationYAnimator = null;
        switch (type){
            case ADD:
                translationYAnimator = ObjectAnimator.ofFloat(mAppInfoLayout, "translationY", -mAppInfoLayout.getHeight(), 0.0f);
                translationYAnimator.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        mAppInfoLayout.setVisibility(VISIBLE);
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
                break;
            case USE:
            case LIST:
                if(mAppInfoLayout.getVisibility() == View.VISIBLE){
                    translationYAnimator = ObjectAnimator.ofFloat(mAppInfoLayout, "translationY", 0.0f, -mAppInfoLayout.getHeight());
                    translationYAnimator.addListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {
                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            mAppInfoLayout.setVisibility(INVISIBLE);
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {
                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {
                        }
                    });
                }
                break;
        }
        return translationYAnimator;
    }

    private final OnClickListener mOnEmptyViewClicked = new OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(getContext(), AddGestureActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
            getContext().startActivity(intent);
            quitAnim();
        }
    };

    private final OnClickListener mOnBackClicked = new OnClickListener() {
        @Override
        public void onClick(View v) {
            quitAnim();
        }
    };
}*/
