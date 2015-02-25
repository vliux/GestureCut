package org.vliux.android.gesturecut.ui.floatwnd;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;

import org.vliux.android.gesturecut.AppConstant;
import org.vliux.android.gesturecut.R;
import org.vliux.android.gesturecut.ui.floatwnd.shortcut.ShortcutWindow;
import org.vliux.android.gesturecut.util.ScreenUtil;
import org.vliux.android.gesturecut.util.SimpleAnimatorListener;

/**
 * Created by vliux on 4/3/14.
 */
public class FloatWindow extends View implements View.OnClickListener {
    private static final String TAG = FloatWindow.class.getSimpleName();
    private static final int OUTTER_STROKE_WIDTH_DP = 2;
    private static final int CHAR_STROKE_WIDTH_DP = 2;
    private static final long RESTORE_LESS_OPAQUE_MILLIS = 4000L;

    //private static final int COLOR_OUTTER_CIRCLE_NORMAL = R.color.float_wnd_circle;
    //private static final int COLOR_OUTTER_CIRCLE_PRESSED = R.color.float_wnd_circle;

    private static final int COLOR_INNER_SPACE_NORMAL = R.color.floatwnd_out_rect_opaque;
    private static final int COLOR_INNER_SPACE_NORMAL_TRANSPARENT = R.color.floatwnd_out_rect_transparent;
    private static final int COLOR_INNER_SPACE_PRESSED = R.color.floatwnd_out_rect_pressed;
    private static final int COLOR_TEXT_STROKE = R.color.floatwnd_char_opaque;
    private static final int COLOR_TEXT_STROKE_TRANSPARENT = R.color.floatwnd_char_transparent;

    private int mColorChar = COLOR_TEXT_STROKE_TRANSPARENT;
    private int mColorOutterRectNormal = COLOR_INNER_SPACE_NORMAL_TRANSPARENT;
    private int mOutterStrokeWidth;
    private int mCharStrokeWidth;

    private Paint mPaintStroke;
    private Paint mPaintSlight;
    private Paint mPaintText;

    private float mSysBarHeight;
    private float mMoveDistantThreshold;
    private WindowManager.LayoutParams mLayoutParams;
    private Vibrator mVibrator;
    private int mDimenSize;
    // out boundary of rounded rectangle
    private RectF mOutRectF;
    // the radius of rounder corners in rectangle
    private float mRoundCornerRadius;
    // dimension of the char "G"
    private float mCharWidth;
    private float mCharHeight;

    private int mScreenWidth;
    private int mScreenHeight;

    public FloatWindow(Context context) {
        super(context);
        init(context);
    }

    public FloatWindow(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public FloatWindow(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context){
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();

        // system bar height
        int notifBarHeight = ScreenUtil.getStatusBarHeight(context);
        mSysBarHeight = (notifBarHeight > 0) ? notifBarHeight :
                TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                        AppConstant.DEFAULT_SYS_NOTIFICATION_BAR_HEIGHT,
                        displayMetrics);

        mMoveDistantThreshold = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                AppConstant.FloatWindow.THRESHOLD_MOVE_DISTANCE,
                displayMetrics);

        mDimenSize = (int)getResources().getDimension(R.dimen.float_wnd_dimen);
        mVibrator = (Vibrator)context.getSystemService(Context.VIBRATOR_SERVICE);

        // painters
        mOutterStrokeWidth = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, OUTTER_STROKE_WIDTH_DP, displayMetrics);
        mCharStrokeWidth = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, CHAR_STROKE_WIDTH_DP, displayMetrics);
        mPaintStroke = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintStroke.setStyle(Paint.Style.STROKE);
        //mPaintStroke.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 18f, displayMetrics));
        //mPaintStroke.setTextAlign(Paint.Align.CENTER);

        mPaintSlight = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintSlight.setStyle(Paint.Style.FILL);

        mPaintText = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintText.setStyle(Paint.Style.FILL_AND_STROKE);
        Typeface tf = Typeface.createFromAsset(context.getAssets(), "CodeLight.otf");
        mPaintText.setTypeface(tf);
        mPaintText.setStrokeWidth(mCharStrokeWidth);
        mPaintText.setColor(getResources().getColor(mColorChar));
        mPaintText.setTextSize(mDimenSize/2);
        mPaintText.setTextAlign(Paint.Align.CENTER);

        mOutRectF = new RectF();
        mRoundCornerRadius = mDimenSize/4;

        int[] screenSizes = ScreenUtil.getScreenSize(context);
        mScreenWidth = screenSizes[0];
        mScreenHeight = screenSizes[1];
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        // out bound rect
        mOutRectF.top = mOutterStrokeWidth;
        mOutRectF.bottom = h - mOutterStrokeWidth;
        mOutRectF.left = mOutterStrokeWidth;
        mOutRectF.right = w - mOutterStrokeWidth;
        // char "G"
        float txtWidth = mPaintText.measureText("G");
        mCharWidth = w/2;
        mCharHeight = (h + txtWidth)/2;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(mDimenSize, mDimenSize);
    }

    private boolean mIsPressed = false;
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // outter circle
        /*mPaintStroke.setStrokeWidth(mOutterStrokeWidth);
        if(!mIsPressed) {
            mPaintStroke.setColor(getResources().getColor(COLOR_OUTTER_CIRCLE_NORMAL));
        }else{
            mPaintStroke.setColor(getResources().getColor(COLOR_OUTTER_CIRCLE_PRESSED));
        }
        canvas.drawRoundRect(mOutRectF, mRoundCornerRadius, mRoundCornerRadius, mPaintStroke);*/

        // semi-transparent background of circle
        if(!mIsPressed) {
            mPaintSlight.setColor(getResources().getColor(mColorOutterRectNormal));
        }else{
            mPaintSlight.setColor(getResources().getColor(COLOR_INNER_SPACE_PRESSED));
        }
        canvas.drawRoundRect(mOutRectF, mRoundCornerRadius, mRoundCornerRadius, mPaintSlight);

        // center text
        mPaintText.setColor(getResources().getColor(mColorChar));
        canvas.drawText("G", mCharWidth, mCharHeight, mPaintText);
    }

    /**
     * When added to WindowManager, should set the layoutParams here.
     * @param layoutParams
     */
    public void setWindowLayoutParams(WindowManager.LayoutParams layoutParams){
        mLayoutParams = layoutParams;
    }

    @Override
    public void onClick(View view){
        moreOpaque();
        ShortcutWindow shortcutWindow = new ShortcutWindow(getContext());
        FloatWindowManager.showSecondaryFloatWindow(getContext(), shortcutWindow);
    }

    // raw location of ACTION_DOWN, which is screen-coordinator-based.
    private float mDownX;
    private float mDownY;
    // location of ACTION_DOWN inside this view
    private float mDownInnerX;
    private float mDownInnerY;
    // checked by ACTION_UP, if there was not any movement, won't update window.
    private boolean mIsPrevMoved = false;
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(mInPullBoundaryAnim){
            // currently in animation of pulling to boundary, not respond to events
            return true;
        }
        float rawX = event.getRawX();
        float rawY = event.getRawY() - mSysBarHeight;

        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                mIsPressed = true;
                mIsPrevMoved = false;
                mDownX = rawX;
                mDownY = rawY;
                mDownInnerX = event.getX();
                mDownInnerY = event.getY();
                //Log.d(TAG, String.format("DOWN: %f, %f; %f, %f", mDownX, mDownY, event.getX(), event.getY()));
                if(null != mVibrator){
                    mVibrator.vibrate(AppConstant.VIBRATE_DURATION);
                }
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                if(isMoving(rawX, rawY)){
                    mIsPrevMoved = true;
                    mLayoutParams.x = (int)(rawX - mDownInnerX);
                    mLayoutParams.y = (int)(rawY - mDownInnerY);
                    //Log.d(TAG, "mIsPrevMoved = true");Log.d(TAG, String.format("MOVE: %d, %d; %f, %f", mLayoutParams.x, mLayoutParams.y, event.getX(), event.getY()));
                    FloatWindowManager.updateFloatWindow(getContext(), this, mLayoutParams, false);
                }
                break;
            case MotionEvent.ACTION_CANCEL:
                mIsPressed = false;
                mDownInnerX = 0;
                mDownInnerY = 0;
                mDownX = 0;
                mDownY = 0;
                //Log.d(TAG, String.format("CANCEL: %f, %f; %f, %f", rawX, rawY, event.getX(), event.getY()));
                mIsPrevMoved = false;
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                mIsPressed = false;
                Log.d(TAG, String.format("UP: %d, %d; %f, %f", mLayoutParams.x, mLayoutParams.y, event.getX(), event.getY()));
                if(mIsPrevMoved) {
                    //Log.d(TAG, "mIsPrevMoved = true");
                    //mLayoutParams.x = (int)(rawX - mDownInnerX);
                    //mLayoutParams.y = (int)(rawY - mDownInnerY);
                    //FloatWindowManager.updateFloatWindow(getContext(), this, mLayoutParams, false);
                    pullToBoundary((int)(rawX - mDownInnerX),
                            (int)(rawY - mDownInnerY));
                }else{
                    //Log.d(TAG, "mIsPrevMoved = false");
                    onClick(this);
                }

                mIsPrevMoved = false;
                mDownInnerX = 0;
                mDownInnerY = 0;
                mDownX = 0;
                mDownY = 0;
                invalidate();
                break;
        }
        return true;
    }

    private boolean mInPullBoundaryAnim = false;
    private void pullToBoundary(int x, final int y){
        final int targetX = x >= mScreenWidth/2 ? mScreenWidth : 0;
        ValueAnimator va = ValueAnimator.ofInt(x, targetX);
        va.setDuration(AppConstant.Anim.ANIM_DURATION_NORMAL);
        va.setInterpolator(new AccelerateDecelerateInterpolator());
        va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int animatedX = (Integer)animation.getAnimatedValue();
                mLayoutParams.x = animatedX;
                mLayoutParams.y = y;
                FloatWindowManager.updateFloatWindow(getContext(), FloatWindow.this, mLayoutParams,
                        animatedX == targetX);
            }
        });
        va.addListener(new SimpleAnimatorListener(){
            @Override
            public void onAnimationStart(Animator animation) {
                mInPullBoundaryAnim = true;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mInPullBoundaryAnim = false;
            }
        });
        va.start();
    }

    private boolean isMoving(float rawX, float rawY){
        float offsetX = Math.abs(rawX - mDownX);
        float offsetY = Math.abs(rawY - mDownY);
        //Log.d(TAG, String.format("offsetX=%f, offsetY=%f, threshold=%f",
                //offsetX, offsetY, mMoveDistantThreshold));
        if (offsetX > mMoveDistantThreshold || offsetY > mMoveDistantThreshold) {
            return true;
        }
        return false;
    }

    private static final int WHAT_MORE_OPAQUE = 100;
    private static final int WHAT_LESS_OPAQUE = 101;
    private final Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case WHAT_LESS_OPAQUE:
                    mColorOutterRectNormal = COLOR_INNER_SPACE_NORMAL_TRANSPARENT;
                    mColorChar = COLOR_TEXT_STROKE_TRANSPARENT;
                    invalidate();
                    break;
                case WHAT_MORE_OPAQUE:
                    mColorOutterRectNormal = COLOR_INNER_SPACE_NORMAL;
                    mColorChar = COLOR_TEXT_STROKE;
                    invalidate();
                    break;
            }
        }
    };

    /**
     * Use to inform this view to be shown as more opaque.
     * It will be restore to less opaque state if serverl seconds no activity.
     */
    public void moreOpaque(){
        // remove previous LESS_OPAQUE messages
        mHandler.removeMessages(WHAT_LESS_OPAQUE);
        // send MORE_OPQAUE message
        Message msg = mHandler.obtainMessage(WHAT_MORE_OPAQUE);
        mHandler.sendMessage(msg);
        // schedule LESS_OPAQUE message for the coming future
        mHandler.sendEmptyMessageDelayed(WHAT_LESS_OPAQUE, RESTORE_LESS_OPAQUE_MILLIS);
    }
}
