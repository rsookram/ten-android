package io.github.rsookram.ten;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.View;

public class VerticalProgressBar extends View {

    private final int totalTimeMillis = 90_000;
    private float progressPercentage = 1.0f;
    private final int progressBarColour = 0xFF_21_21_21;
    private final int backgroundColour = Color.LTGRAY;

    private final Paint progressBarPaint = new Paint();
    private final Paint backgroundPaint = new Paint();

    private final RectF backgroundRect = new RectF();
    private final RectF progressBarRect = new RectF();

    private long startTime = 0;

    private Runnable onComplete = () -> {};

    private final Runnable nextProgressStep = new Runnable() {
        @Override
        public void run() {
            invalidate();

            long elapsedMillis = SystemClock.uptimeMillis() - startTime;
            if (elapsedMillis >= totalTimeMillis) {
                progressPercentage = 0.0f;
                onComplete.run();
            } else {
                progressPercentage = 1.0f - ((float) elapsedMillis / totalTimeMillis);
                postOnAnimation(this);
            }
        }
    };

    public VerticalProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);

        progressBarPaint.setColor(progressBarColour);
        progressBarPaint.setStyle(Paint.Style.FILL);

        backgroundPaint.setColor(backgroundColour);
        backgroundPaint.setStyle(Paint.Style.FILL);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int width = getWidth();
        int height = getHeight();

        backgroundRect.set(0, 0, width, height);
        canvas.drawRect(backgroundRect, backgroundPaint);

        float progressHeight = height * progressPercentage;
        progressBarRect.set(0, height - progressHeight, width, height);
        canvas.drawRect(progressBarRect, progressBarPaint);
    }

    public void restart() {
        startTime = SystemClock.uptimeMillis();
        progressPercentage = 1.0f;
        invalidate();

        removeCallbacks(nextProgressStep);

        postOnAnimation(nextProgressStep);
    }

    public void setOnComplete(Runnable onComplete) {
        this.onComplete = onComplete;
    }
}
