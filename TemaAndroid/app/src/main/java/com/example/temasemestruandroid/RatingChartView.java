package com.example.temasemestruandroid;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import java.util.ArrayList;

public class RatingChartView extends View {
    private ArrayList<Integer> ratingCounts; // Counts for each rating
    private ArrayList<String> ratingLabels; // Labels for ratings (e.g., "0", "1")
    private final Paint piePaint;
    private final Paint textPaint;

    public RatingChartView(Context context) {
        super(context);
        this.ratingCounts = new ArrayList<>();
        this.ratingLabels = new ArrayList<>();
        this.piePaint = new Paint();
        this.piePaint.setAntiAlias(true);
        this.textPaint = new Paint();
        this.textPaint.setColor(Color.BLACK);
        this.textPaint.setTextSize(36f);
        this.textPaint.setAntiAlias(true);
    }

    public RatingChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.ratingCounts = new ArrayList<>();
        this.ratingLabels = new ArrayList<>();
        this.piePaint = new Paint();
        this.piePaint.setAntiAlias(true);
        this.textPaint = new Paint();
        this.textPaint.setColor(Color.BLACK);
        this.textPaint.setTextSize(36f);
        this.textPaint.setAntiAlias(true);
    }

    public RatingChartView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.ratingCounts = new ArrayList<>();
        this.ratingLabels = new ArrayList<>();
        this.piePaint = new Paint();
        this.piePaint.setAntiAlias(true);
        this.textPaint = new Paint();
        this.textPaint.setColor(Color.BLACK);
        this.textPaint.setTextSize(36f);
        this.textPaint.setAntiAlias(true);
    }

    public void setRatingData(ArrayList<Integer> ratingCounts, ArrayList<String> ratingLabels) {
        this.ratingCounts = ratingCounts;
        this.ratingLabels = ratingLabels;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (ratingCounts.isEmpty()) return;

        // Draw pie chart
        int width = getWidth();
        int height = getHeight();
        int size = Math.min(width, height) - 200; // Reserve space for margins
        RectF rect = new RectF(150, 100, 150 + size, 100 + size); // Shifted 100px right

        float total = 0;
        for (int count : ratingCounts) total += count;

        float startAngle = 0;
        for (int i = 0; i < ratingCounts.size(); i++) {
            int count = ratingCounts.get(i);
            float sweep = (count / total) * 360;
            piePaint.setColor(Color.HSVToColor(new float[]{i * 360f / ratingCounts.size(), 1, 1}));
            canvas.drawArc(rect, startAngle, sweep, true, piePaint);

            // Draw label (rating and count)
            float angle = startAngle + sweep / 2;
            double rad = Math.toRadians(angle);
            float x = rect.centerX() + (float) (Math.cos(rad) * size / 3);
            float y = rect.centerY() + (float) (Math.sin(rad) * size / 3);
            canvas.drawText(ratingLabels.get(i) + ": " + count, x, y, textPaint);

            startAngle += sweep;
        }

        // Draw title
        textPaint.setTextSize(48f);
        canvas.drawText("Distribuție Rating Email", width / 2f - 150, 50, textPaint);
        textPaint.setTextSize(36f);
    }
}