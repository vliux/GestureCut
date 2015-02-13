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
    private static final int COLOR_BG_PRESSED = R.color.yellow;
    private static final int COLOR_BG_UNPRESSED = R.color.gesture_create_bg_semi_transparent;
    private static final int COLOR_STROKE = R.color.sc_knob_stroke;

    private int mDiameter;
    private int mRadius;
    private Paint mPaint;
    private RectF mBoundRectF;

    private Drawable mIconDrawable;
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

        int strokePadding = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4f, context.getResources().getDisplayMetrics());
        mStrokePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mStrokePaint.setStyle(Paint.Style.STROKE);
        mStrokePaint.setColor(context.getResources().getColor(R.color.sc_knob_stroke));
        mStrokePaint.setStrokeWidth(strokePadding/2);
        mBoundStroke = new RectF(strokePadding, strokePadding, mDiameter - strokePadding, mDiameter - strokePadding);

        mIconDrawable = getContext().getResources().getDrawable(R.drawable.ic_back);
        int left = mRadius / 4;
        int top = mRadius - left;
        int right = mRadius - left;
        int bottom = mDiameter - top;
        mDrawableBounds = new Rect(left, top, right, bottom);
        mIconDrawable.setBounds(mDrawableBounds);
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
            canvas.drawArc(mBoundStroke, 90f, 360f, false, mStrokePaint);
        }else{
            mPaint.setColor(mColorNormal);
            canvas.drawArc(mBoundRectF, 90f, 180f, true, mPaint);
            canvas.drawArc(mBoundStroke, 90f, 180f, true, mStrokePaint);
        }

        mIconDrawable.draw(canvas);
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
}
