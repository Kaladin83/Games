package com.example.maratbe.customViews;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;

import com.example.maratbe.domain.Sum;
import com.example.maratbe.other.Constants;

import java.util.ArrayList;

public class TrianglesView extends View implements Constants {
    private final Paint topTrianglePaint = new Paint();
    private final Paint bottomTrianglePaint = new Paint();
    private final Paint topTextPaint = new Paint();
    private final Paint bottomTextPaint = new Paint();
    private final Paint verticalLinePaint = new Paint();
    private final Paint horizontalLinePaint = new Paint();

    private final Path topPath = new Path();
    private final Path bottomPath = new Path();

    private Context context;
    private int size, textHeight = 0, textWidth = 0;
    private boolean isToDrawVerticalLine = false;
    private ArrayList<Sum> sums;
    private String bottomText = "", topText = "";

    public TrianglesView(Context context) {
        super(context);
        this.context = context;
    }

    public TrianglesView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TrianglesView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setSize(int size)
    {
        this.size = size;
    }

    public void setDirections(ArrayList<Sum> sums)
    {
        this.sums = sums;
    }

    public void isToDrawVerticalLine(boolean isToDrawVerticalLine) {
        this.isToDrawVerticalLine = isToDrawVerticalLine;
    }

    public void buildCell()
    {
        sums.forEach(s-> {
            if (s.getDirection().equals(UPPER)) {
                buildUpperTriangle();
                drawUpperText(s.getValue());
            } else {
                buildLowerTriangle();
                drawHorizontalLine();
                drawLowerText(s.getValue());
            }
        });
    }

    private void drawLowerText(int value) {
        bottomText = String.valueOf(value);
        setTextBounds(bottomText, bottomTextPaint);

        bottomTextPaint.setColor(BLACK_1);
        bottomTextPaint.setAntiAlias(true);
    }

    private void drawUpperText(int value) {
        topText = String.valueOf(value);
        setTextBounds(topText, topTextPaint);

        topTextPaint.setColor(BLACK_1);
        topTextPaint.setAntiAlias(true);
    }

    private void setTextBounds(String text,Paint paint) {
        Rect bounds = new Rect();
        paint.setTypeface(Typeface.DEFAULT);
        paint.setTextSize(30);
        paint.getTextBounds(text, 0, text.length(), bounds);

        textHeight = bounds.height();
        textWidth =  bounds.width();
    }

    private void buildLowerTriangle() {
        bottomTrianglePaint.setColor(BLUE_6);
        bottomTrianglePaint.setAntiAlias(true);

        bottomPath.moveTo(0, size);
        bottomPath.lineTo(0, 0);
        bottomPath.lineTo(size, size);
        bottomPath.lineTo(0, size);
    }

    private void drawVerticalLine() {
        verticalLinePaint.setColor(Color.BLACK);
        verticalLinePaint.setAntiAlias(false);
        verticalLinePaint.setStyle(Paint.Style.STROKE );
        verticalLinePaint.setStrokeWidth(4);
    }

    private void drawHorizontalLine() {
        horizontalLinePaint.setColor(Color.BLACK);
        horizontalLinePaint.setAntiAlias(false);
        horizontalLinePaint.setStyle(Paint.Style.STROKE );
        horizontalLinePaint.setStrokeWidth(4);
    }

    private void buildUpperTriangle() {
        topTrianglePaint.setColor(BLUE_4);
        topTrianglePaint.setAntiAlias(true);

        topPath.moveTo(size, 0);
        topPath.lineTo(0, 0);
        topPath.lineTo(size, size);
        topPath.lineTo(size, 0);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawPath(topPath, topTrianglePaint);
        canvas.drawPath(bottomPath, bottomTrianglePaint);

        canvas.drawText(topText, size - textWidth - 7, size/2 + textHeight/2, topTextPaint);
        canvas.drawText(bottomText, (size/2 - textWidth/2) - 3, size - 7, bottomTextPaint);

        if (isToDrawVerticalLine)
        {
            drawVerticalLine();
            canvas.drawLine(0, 0, 0, size, verticalLinePaint);
        }

        canvas.drawLine(0, size, size, size, horizontalLinePaint);
    }
}
