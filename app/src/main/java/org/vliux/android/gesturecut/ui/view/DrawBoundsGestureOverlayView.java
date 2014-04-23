package org.vliux.android.gesturecut.ui.view;

import android.content.Context;
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
    private Paint mPaint = new Paint();

    public DrawBoundsGestureOverlayView(Context context) {
        super(context);
    }

    public DrawBoundsGestureOverlayView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DrawBoundsGestureOverlayView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
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
    }

    private void resetPaint(){
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setAntiAlias(true);
        mPaint.setColor(getContext().getResources().getColor(R.color.gesture_cur_blue));
        // set stroke width
        float strokeWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1,
                getContext().getResources().getDisplayMetrics());
        mPaint.setStrokeWidth(strokeWidth);
    }
}
