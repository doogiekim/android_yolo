package com.samsung.android.app.yolo;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import java.util.List;

public class YoloBoxView extends View implements OnObjectDetectedListener {

    private Paint mPaint = new Paint();
    private List<Box> mBoxes = null;

    public YoloBoxView(Context context) {
        super(context);
    }

    public YoloBoxView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public YoloBoxView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (mBoxes != null) {
            int width = getWidth();
            int height = getHeight();

            for (int i = 0, mBoxesSize = mBoxes.size(); i < mBoxesSize; i++) {
                Box b = mBoxes.get(i);
                b.bx *= width;
                b.bw *= width;
                b.by *= height;
                b.bh *= height;

                mPaint.setColor(COLORS[i % COLORS.length]);
                mPaint.setStyle(Paint.Style.STROKE);
                mPaint.setStrokeWidth(4.0f);
                canvas.drawRect(b.getRectF(), mPaint);

                mPaint.setStyle(Paint.Style.FILL);
                mPaint.setTextSize(50.f);
                canvas.drawText(YoloConstants.OBJECT_TEXT[b.box_class], b.bx - b.bw / 2, b.by, mPaint);
            }
        }
    }

    private static int[] COLORS = {
        Color.RED,
        Color.GREEN,
        Color.BLUE,
        Color.YELLOW,
        Color.CYAN,
        Color.MAGENTA,
    };

    @Override
    public void OnObjectDetected(List<Box> boxes) {
        mBoxes = boxes;
        invalidate();
    }
}
