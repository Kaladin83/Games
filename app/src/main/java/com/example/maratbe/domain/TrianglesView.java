package com.example.maratbe.domain;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.example.maratbe.other.Constants;

import java.util.ArrayList;

public class TrianglesView extends View implements Constants {
    private final Paint topTrianglePaint = new Paint();
    private final Paint bottomTrianglePaint = new Paint();
    private final Paint topTextPaint = new Paint();
    private final Paint bottomTextPaint = new Paint();

    private final Path topPath = new Path();
    private final Path bottomPath = new Path();

    private Context context;
    private int size, textHeight = 0, textWidth = 0;
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

    public void buildCell()
    {
        sums.forEach(s-> {
            if (s.getDirection().equals(UPPER)) {
                buildUpperTriangle();
                drawUpperText(s.getValue());
            } else {
                buildLowerTriangle();
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
        paint.setTypeface(Typeface.DEFAULT);// your preference here
        paint.setTextSize(30);// have this the same as your text size
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
    }
}
