package org.vliux.android.gesturecut.ui.floatwnd;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;

import org.vliux.android.gesturecut.R;
import org.vliux.android.gesturecut.ui.SizeCalculator;

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

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mPaint.setColor(context.getResources().getColor(R.color.gesture_create_bg_semi_transparent));

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
        canvas.drawArc(mBoundRectF, 90f, 180f, true, mPaint);
        mIconDrawable.draw(canvas);
    }

    public int getRadius(){
        return mRadius;
    }
}
