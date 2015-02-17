package org.vliux.android.gesturecut.ui.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

import org.vliux.android.gesturecut.R;

/**
 * Created by vliux on 2/17/15.
 */
public class GestureOverlayBgView extends View {
    private static final int COLOR_DOT = R.color.overlay_bg_dot;
    private static float DOT_DIMEN_PX = 2f;
    private static float SPACE_DIMEN_PX = 48f;

    private Paint mPaintDot;
    private int mHorizontalGap;
    private int mVerticalGap;
    private int mNumOfDotsX;
    private int mNumOfDotsY;

    private Path mPathCached;

    public GestureOverlayBgView(Context context) {
        super(context);
        init(context);
    }

    public GestureOverlayBgView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public GestureOverlayBgView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context){
        mPaintDot = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintDot.setStyle(Paint.Style.STROKE);
        mPaintDot.setColor(context.getResources().getColor(COLOR_DOT));
        mPaintDot.setStrokeWidth(DOT_DIMEN_PX);
        DashPathEffect dashPathEffect = new DashPathEffect(new float[]{DOT_DIMEN_PX, SPACE_DIMEN_PX}, 0);
        mPaintDot.setPathEffect(dashPathEffect);
        mPathCached = new Path();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        int[] gapAndDotNum = calculateGapAndNumOfDots(w);
        mHorizontalGap = gapAndDotNum[0];
        mNumOfDotsX = gapAndDotNum[1];

        gapAndDotNum = calculateGapAndNumOfDots(h);
        mVerticalGap = gapAndDotNum[0];
        mNumOfDotsY = gapAndDotNum[1];
    }

    /**
     * calculate the vertical or horizontal gap as well as the number of dots accroding to the dimension lenght.
     * There are 2 gaps in one direction, this method returns the length of a single gap.
     * @param dimenLength
     * @return [0] is the length of a single gap; [1] is the number of dots.
     */
    private static int[] calculateGapAndNumOfDots(int dimenLength){
        int[] results = new int[2];
        int numOfDots = (int)((dimenLength)/(SPACE_DIMEN_PX + DOT_DIMEN_PX));
        //int high = (int)(dimenLength/(SPACE_DIMEN_PX + 1));
        results[0] = (int)((dimenLength + SPACE_DIMEN_PX - (SPACE_DIMEN_PX + DOT_DIMEN_PX) * numOfDots)/2);
        results[1] = numOfDots;
        return  results;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int width = getWidth();
        for(int i = 0; i < mNumOfDotsY; i++){
            float startX = mHorizontalGap;
            float startY = mVerticalGap + i * SPACE_DIMEN_PX - 1;
            float stopX = width - mHorizontalGap;
            float stopY = startY;
            //canvas.drawLine(startX, startY, stopX, stopY, mPaintDot);
            mPathCached.moveTo(startX, startY);
            mPathCached.lineTo(stopX, stopY);
            canvas.drawPath(mPathCached, mPaintDot);
            mPathCached.reset();
        }
    }
}
