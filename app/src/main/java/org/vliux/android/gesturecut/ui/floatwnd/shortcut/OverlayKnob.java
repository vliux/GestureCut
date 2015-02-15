package org.vliux.android.gesturecut.ui.floatwnd.shortcut;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

import org.vliux.android.gesturecut.R;
import org.vliux.android.gesturecut.ui.SizeCalculator;

import de.greenrobot.event.EventBus;

/**
 * Created by vliux on 2/12/15.
 */
public class OverlayKnob extends View {
    private static final int COLOR_BG_PRESSED = R.color.accent_color_dark;
    private static final int COLOR_BG_UNPRESSED = R.color.accent_color;
    private static final int COLOR_STROKE = R.color.sc_knob_stroke;
    private static final float STROKE_PADDING_DP = 6f;
    private static final float STROKE_WIDTH_DP = 1f;

    private int mDiameter;
    private int mRadius;
    private Paint mPaint;
    private RectF mBoundRectF;

    private Drawable mIconDrawableBack;
    private Drawable mIconDrawableNext;
    private Rect mDrawableBounds;

    private int mColorNormal;
    private int mColorPressed;

    private Paint mStrokePaint;
    private RectF mBoundStroke;

    public OverlayKnob(Context context) {
        super(context);
        init(context);
    }

    public OverlayKnob(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public OverlayKnob(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context){
        mDiameter = SizeCalculator.gestureIconWidth(context.getResources());
        mRadius = mDiameter / 2;
        mBoundRectF = new RectF(0f, 0f, mDiameter, mDiameter);

        mColorNormal = context.getResources().getColor(COLOR_BG_UNPRESSED);
        mColorPressed = context.getResources().getColor(COLOR_BG_PRESSED);

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);

        int strokePadding = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, STROKE_PADDING_DP, context.getResources().getDisplayMetrics());
        mStrokePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mStrokePaint.setStyle(Paint.Style.STROKE);
        mStrokePaint.setColor(context.getResources().getColor(COLOR_STROKE));
        mStrokePaint.setStrokeWidth((int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, STROKE_WIDTH_DP, context.getResources().getDisplayMetrics()));
        mBoundStroke = new RectF(strokePadding, strokePadding, mDiameter - strokePadding, mDiameter - strokePadding);

        mIconDrawableBack = context.getResources().getDrawable(R.drawable.ic_back);
        mIconDrawableNext = context.getResources().getDrawable(R.drawable.ic_next);
        int left = mRadius / 4;
        int top = mRadius - left;
        int right = mRadius - left;
        int bottom = mDiameter - top;
        mDrawableBounds = new Rect(left, top, right, bottom);
        mIconDrawableBack.setBounds(mDrawableBounds);
        mIconDrawableNext.setBounds(mDrawableBounds);

        // register for eventbus
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(mDiameter, mDiameter);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if(mIsPressed){
            mPaint.setColor(mColorPressed);
            canvas.drawArc(mBoundRectF, 90f, 360f, true, mPaint);
        }else{
            mPaint.setColor(mColorNormal);
            canvas.drawArc(mBoundRectF, 90f, 360f, true, mPaint);
        }
        canvas.drawArc(mBoundStroke, 90f, 360f, false, mStrokePaint);

        if(!mNeedRotate) {
            mIconDrawableBack.draw(canvas);
        }else{
            mIconDrawableNext.draw(canvas);
        }
    }

    public int getRadius(){
        return mRadius;
    }

    private boolean mIsPressed = false;
    private int mDownX;
    private OverlayKnobPresenter.Event evCached = new OverlayKnobPresenter.Event();
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = MotionEventCompat.getActionMasked(event);
        switch (action){
            case MotionEvent.ACTION_DOWN:
                mDownX = (int)event.getRawX();
                mIsPressed = true;
                invalidate();
                evCached.eventType = OverlayKnobPresenter.EVENT_TYPE_KNOB_PRESSED;
                evCached.rawX = mDownX;
                EventBus.getDefault().post(evCached);
                break;
            case MotionEvent.ACTION_MOVE:
                evCached.eventType = OverlayKnobPresenter.EVENT_TYPE_KNOB_MOVE;
                evCached.rawX = (int)event.getRawX();
                //Log.d("ShortcutWindow", "=== KNOB_MOVE: xDelta = " + evCached.xDelta + ", mDownX = " + mDownX);
                EventBus.getDefault().post(evCached);
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                mIsPressed = false;
                invalidate();
                evCached.eventType = OverlayKnobPresenter.EVENT_TYPE_KNOB_UNPRESSED;
                evCached.rawX = (int)event.getRawX();
                EventBus.getDefault().post(evCached);
                break;
        }
        return true;
    }

    private boolean mNeedRotate = false;
    public void onEventMainThread(IShortcutWindow.EventToKnob event){
        switch (event.eventType){
            case IShortcutWindow.EventToKnob.END_STATE_LEFT:
                mNeedRotate = true;
                invalidate();
                break;
            case IShortcutWindow.EventToKnob.END_STATE_RIGHT:
                mNeedRotate = false;
                invalidate();
                break;
            case IShortcutWindow.EventToKnob.WND_CLOSING:
                EventBus.getDefault().unregister(this);
                break;
        }

    }
}
