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
import android.view.MotionEvent;
import android.view.View;

import org.vliux.android.gesturecut.R;
import org.vliux.android.gesturecut.ui.SizeCalculator;

import de.greenrobot.event.EventBus;

/**
 * Created by vliux on 2/12/15.
 */
public class OverlayKnob extends View {
    private int mDiameter;
    private int mRadius;
    private Paint mPaint;
    private RectF mBoundRectF;

    private Drawable mIconDrawable;
    private Rect mDrawableBounds;

    private int mColorNormal;
    private int mColorPressed;

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

        mColorNormal = context.getResources().getColor(R.color.gesture_create_bg_semi_transparent);
        mColorPressed = context.getResources().getColor(R.color.red_warning);

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);

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
        setMeasuredDimension(mRadius, mDiameter);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if(mIsPressed){
            mPaint.setColor(mColorPressed);
        }else{
            mPaint.setColor(mColorNormal);
        }

        canvas.drawArc(mBoundRectF, 90f, 180f, true, mPaint);
        mIconDrawable.draw(canvas);
    }

    public int getRadius(){
        return mRadius;
    }

    private boolean mIsPressed = false;
    private int mDownX;
    private ShortcutWindow.Event evCached = new ShortcutWindow.Event();
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = MotionEventCompat.getActionMasked(event);
        switch (action){
            case MotionEvent.ACTION_DOWN:
                mDownX = (int)event.getRawX();
                mIsPressed = true;
                invalidate();
                evCached.eventType = ShortcutWindow.EVENT_TYPE_KNOB_PRESSED;
                EventBus.getDefault().post(evCached);
                break;
            case MotionEvent.ACTION_MOVE:
                evCached.eventType = ShortcutWindow.EVENT_TYPE_KNOB_MOVE;
                evCached.xDelta = (int)event.getRawX() - mDownX;
                Log.d("ShortcutWindow", "=== KNOB_MOVE: xDelta = " + evCached.xDelta + ", mDownX = " + mDownX);
                EventBus.getDefault().post(evCached);
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                mIsPressed = false;
                invalidate();
                evCached.eventType = ShortcutWindow.EVENT_TYPE_KNOB_UNPRESSED;
                EventBus.getDefault().post(evCached);
                break;
        }
        return true;
    }
}
