package org.vliux.android.gesturecut.ui.floatwnd;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.os.Vibrator;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import org.vliux.android.gesturecut.AppConstant;
import org.vliux.android.gesturecut.R;
import org.vliux.android.gesturecut.util.ScreenUtil;
import org.vliux.android.gesturecut.util.WindowManagerUtil;

/**
 * Created by vliux on 4/3/14.
 */
public class FloatWindow extends View implements View.OnClickListener {
    private static final String TAG = FloatWindow.class.getSimpleName();
    private static final int OUTTER_STROKE_WIDTH_DP = 2;
    private static final int CHAR_STROKE_WIDTH_DP = 1;

    private static final int COLOR_OUTTER_CIRCLE_NORMAL = R.color.float_wnd_circle;
    private static final int COLOR_OUTTER_CIRCLE_PRESSED = R.color.gesture_cur_blue;

    private static final int COLOR_INNER_SPACE_NORMAL = R.color.gesture_cur_blue_light_semi_transparent;
    private static final int COLOR_INNER_SPACE_PRESSED = R.color.beige_light_semi_transparent;

    private static final int COLOR_TEXT_STROKE = R.color.beige_light_semi_transparent;

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
        mPaintText.setColor(getResources().getColor(COLOR_TEXT_STROKE));
        mPaintText.setTextSize(mDimenSize/2);
        mPaintText.setTextAlign(Paint.Align.CENTER);
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
        int width = getWidth();
        int height = getHeight();

        RectF oval = new RectF();
        oval.top = mOutterStrokeWidth;
        oval.bottom = height - mOutterStrokeWidth;
        oval.left = mOutterStrokeWidth;
        oval.right = width - mOutterStrokeWidth;

        // outter circle
        mPaintStroke.setStrokeWidth(mOutterStrokeWidth);
        if(!mIsPressed) {
            mPaintStroke.setColor(getResources().getColor(COLOR_OUTTER_CIRCLE_NORMAL));
        }else{
            mPaintStroke.setColor(getResources().getColor(COLOR_OUTTER_CIRCLE_PRESSED));
        }
        canvas.drawArc(oval, 0.0f, 360.0f, false, mPaintStroke);

        // semi-transparent background of circle
        if(!mIsPressed) {
            mPaintSlight.setColor(getResources().getColor(COLOR_INNER_SPACE_NORMAL));
        }else{
            mPaintSlight.setColor(getResources().getColor(COLOR_INNER_SPACE_PRESSED));
        }
        canvas.drawArc(oval, 0.0f, 360.0f, true, mPaintSlight);

        // center text
        float txtWidth = mPaintText.measureText("G");
        canvas.drawText("G", width/2, (height + txtWidth)/2, mPaintText);
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
        SecondaryFloatWindow expandedFloatWindow = new SecondaryFloatWindow(getContext().getApplicationContext());
        FloatWindowManager.showSecondaryFloatWindow(getContext(), expandedFloatWindow);
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
                Log.d(TAG, String.format("DOWN: %f, %f; %f, %f", mDownX, mDownY, event.getX(), event.getY()));
                if(null != mVibrator){
                    mVibrator.vibrate(AppConstant.FloatWindow.ACTION_DOWN_VIBRATE_DURATION);
                }
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                if(isMoving(rawX, rawY)){
                    mIsPrevMoved = true;
                    mLayoutParams.x = (int)(rawX - mDownInnerX);
                    mLayoutParams.y = (int)(rawY - mDownInnerY);
                    Log.d(TAG, "mIsPrevMoved = true");Log.d(TAG, String.format("MOVE: %d, %d; %f, %f", mLayoutParams.x, mLayoutParams.y, event.getX(), event.getY()));
                    FloatWindowManager.updateFloatWindow(getContext(), this, mLayoutParams, false);
                }
                break;
            case MotionEvent.ACTION_CANCEL:
                mIsPressed = false;
                mDownInnerX = 0;
                mDownInnerY = 0;
                mDownX = 0;
                mDownY = 0;
                Log.d(TAG, String.format("CANCEL: %f, %f; %f, %f", rawX, rawY, event.getX(), event.getY()));
                mIsPrevMoved = false;
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                mIsPressed = false;
                Log.d(TAG, String.format("UP: %d, %d; %f, %f", mLayoutParams.x, mLayoutParams.y, event.getX(), event.getY()));
                if(mIsPrevMoved) {
                    Log.d(TAG, "mIsPrevMoved = true");
                    mLayoutParams.x = (int)(rawX - mDownInnerX);
                    mLayoutParams.y = (int)(rawY - mDownInnerY);
                    FloatWindowManager.updateFloatWindow(getContext(), this, mLayoutParams, true);
                }else{
                    Log.d(TAG, "mIsPrevMoved = false");
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

    private boolean isMoving(float rawX, float rawY){
        float offsetX = Math.abs(rawX - mDownX);
        float offsetY = Math.abs(rawY - mDownY);
        Log.d(TAG, String.format("offsetX=%f, offsetY=%f, threshold=%f",
                offsetX, offsetY, mMoveDistantThreshold));
        if (offsetX > mMoveDistantThreshold || offsetY > mMoveDistantThreshold) {
            return true;
        }
        return false;
    }

}
