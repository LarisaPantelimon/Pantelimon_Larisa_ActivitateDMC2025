package com.example.lab12;

import android.content.Context;
import android.graphics.*;
import android.view.View;
import java.util.ArrayList;

public class ChartView extends View {
    private final ArrayList<Integer> values;
    private final String type;
    private final Paint paint;
    private final Paint textPaint;

    public ChartView(Context context, ArrayList<Integer> values, String type) {
        super(context);
        this.values = values;
        this.type = type;
        this.paint = new Paint();
        this.textPaint = new Paint();
        textPaint.setColor(Color.BLACK);
        textPaint.setTextSize(36f);
        textPaint.setAntiAlias(true);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        switch (type) {
            case "PieChart":
                drawPieChart(canvas);
                break;
            case "ColumnChart":
                drawColumnChart(canvas);
                break;
            case "BarChart":
                drawBarChart(canvas);
                break;
        }
    }

    private void drawPieChart(Canvas canvas) {
        int width = getWidth();
        int height = getHeight();
        int size = Math.min(width, height) - 100;
        RectF rect = new RectF(50, 50, 50 + size, 50 + size);

        float total = 0;
        for (int val : values) total += val;

        float startAngle = 0;
        for (int i = 0; i < values.size(); i++) {
            float value = values.get(i);
            float sweep = (value / total) * 360;
            paint.setColor(Color.HSVToColor(new float[]{i * 360f / values.size(), 1, 1}));
            canvas.drawArc(rect, startAngle, sweep, true, paint);

            // draw label
            float angle = startAngle + sweep / 2;
            double rad = Math.toRadians(angle);
            float x = rect.centerX() + (float) (Math.cos(rad) * size / 3);
            float y = rect.centerY() + (float) (Math.sin(rad) * size / 3);
            canvas.drawText(String.valueOf(value), x, y, textPaint);

            startAngle += sweep;
        }
    }

    private void drawColumnChart(Canvas canvas) {
        int width = getWidth();
        int height = getHeight();
        int barWidth = width / (values.size() * 2);
        int maxVal = getMax(values);

        for (int i = 0; i < values.size(); i++) {
            int value = values.get(i);
            int left = i * 2 * barWidth + barWidth / 2;
            int top = height - (value * (height - 100) / maxVal);
            int right = left + barWidth;
            int bottom = height - 50;

            paint.setColor(Color.HSVToColor(new float[]{i * 360f / values.size(), 1, 1}));
            canvas.drawRect(left, top, right, bottom, paint);

            canvas.drawText(String.valueOf(value), left + 10, top - 10, textPaint);
        }
    }

    private void drawBarChart(Canvas canvas) {
        int width = getWidth();
        int height = getHeight();
        int barHeight = height / (values.size() * 2);
        int maxVal = getMax(values);

        for (int i = 0; i < values.size(); i++) {
            int value = values.get(i);
            int left = 50;
            int top = i * 2 * barHeight + barHeight / 2;
            int right = left + (value * (width - 100) / maxVal);
            int bottom = top + barHeight;

            paint.setColor(Color.HSVToColor(new float[]{i * 360f / values.size(), 1, 1}));
            canvas.drawRect(left, top, right, bottom, paint);

            canvas.drawText(String.valueOf(value), right + 10, top + barHeight / 2f + 10, textPaint);
        }
    }

    private int getMax(ArrayList<Integer> list) {
        int max = Integer.MIN_VALUE;
        for (int val : list) {
            if (val > max) max = val;
        }
        return max;
    }
}
