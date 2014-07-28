package com.znupy.chattyowl.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * Created by samok on 27/07/14.
 */
public class SoundIndicator extends View {
    private static final String TAG = SoundIndicator.class.getSimpleName();
    private Point circlePosition;
    private Paint circlePaint;

    private float circleRadius;
    private float maxRadius;
    private float minRadius = 50;
    private float soundLevel = -1;

    public SoundIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        circlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        circlePaint.setStyle(Paint.Style.FILL);
        circlePaint.setColor(Color.LTGRAY);
    }

    @Override
    public void onSizeChanged (int w, int h, int oldw, int oldh){
        super.onSizeChanged(w, h, oldw, oldh);
        Log.d(TAG, "onSizeChanged: " + w + ", " + h);
        circlePosition = new Point(w / 2, h / 2);
        maxRadius = Math.max((float) w, (float) h) / 2.0f;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        circleRadius = minRadius + soundLevel*(maxRadius - minRadius);

        canvas.drawCircle( circlePosition.x, circlePosition.y, circleRadius, circlePaint);

    }

    public void setSoundLevel(float level) {
        level = Math.max(0, level) * 0.1f;
        this.soundLevel = (this.soundLevel*3.0f + level) * 0.25f;
        this.invalidate();
    }

    public float getSoundLevel() {
        return soundLevel;
    }
}
