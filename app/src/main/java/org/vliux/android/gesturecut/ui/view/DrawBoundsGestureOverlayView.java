package org.vliux.android.gesturecut.ui.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.gesture.GestureOverlayView;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.TypedValue;

import org.vliux.android.gesturecut.R;

/**
 * Created by vliux on 4/23/14.
 */
public class DrawBoundsGestureOverlayView extends GestureOverlayView {
    private final Paint mPaint = new Paint();
    private int mBoundrayColor;
    private float mBoundaryWidth;

    public DrawBoundsGestureOverlayView(Context context) {
        super(context);
        init(context, null);
    }

    public DrawBoundsGestureOverlayView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public DrawBoundsGestureOverlayView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs){
        if(null != attrs) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.DrawBoundsGestureOverlayView);
            try {
                mBoundrayColor = typedArray.getColor(R.styleable.DrawBoundsGestureOverlayView_boundColor, context.getResources().getColor(R.color.gesture_cur_blue));
                mBoundaryWidth = typedArray.getFloat(R.styleable.DrawBoundsGestureOverlayView_boundWidth, 2.0f);
            } finally {
                typedArray.recycle();
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        resetPaint();
        int width = getWidth();
        int height = getHeight();
        canvas.drawLine(0.0f, 0.0f, width, 0.0f, mPaint);
        canvas.drawLine(0.0f, 0.0f, 0.0f, height, mPaint);
        canvas.drawLine(0.0f, height, width, height, mPaint);
        canvas.drawLine(width, height, width, 0.0f, mPaint);
        /*setPaintStrokeWidth(0.5f);
        mPaint.setStyle(Paint.Style.STROKE);
        canvas.drawLine(0.0f, 0.0f, width, height, mPaint);
        canvas.drawLine(width, 0.0f, 0.0f, height, mPaint);*/
    }

    private void resetPaint(){
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setAntiAlias(true);
        mPaint.setColor(mBoundrayColor);
        setPaintStrokeWidth(mBoundaryWidth);
    }

    private void setPaintStrokeWidth(float dp){
        float strokeWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                getResources().getDisplayMetrics());
        mPaint.setStrokeWidth(strokeWidth);
    }
}
