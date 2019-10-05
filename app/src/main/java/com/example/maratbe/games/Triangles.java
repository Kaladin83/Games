package com.example.maratbe.games;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;

public class Triangles extends View implements Constants {

    public enum Triangle {
        RIGHT, LEFT, TOP, BOTTOM
    }

    private final Paint topPaint = new Paint();
    private final Paint bottomPaint = new Paint();

    private final Path topPath = new Path();
    private final Path bottomPath = new Path();

    private Context context;
    private int size;
    private ArrayList<Sum> sums;

    private OnTriangleTouchedListener triangleTouchedListener;

    public Triangles(Context context) {
        super(context);
        this.context = context;
    }

    public Triangles(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public Triangles(Context context, AttributeSet attrs, int defStyleAttr) {
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
            } else {
                buildLowerTriangle();
            }
        });
    }

    private void buildLowerTriangle() {
        bottomPaint.setColor(BLUE_6);
        bottomPaint.setAntiAlias(true);

        bottomPath.moveTo(0, size);
        bottomPath.lineTo(0, 0);
        bottomPath.lineTo(size, size);
        bottomPath.lineTo(0, size);
    }

    private void buildUpperTriangle() {
        topPaint.setColor(BLUE_4);
        topPaint.setAntiAlias(true);

        topPath.moveTo(size, 0);
        topPath.lineTo(0, 0);
        topPath.lineTo(size, size);
        topPath.lineTo(size, 0);

    }

    public void setOnTriangleTouchedListener(OnTriangleTouchedListener triangleTouchedListener) {
        this.triangleTouchedListener = triangleTouchedListener;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawPath(topPath, topPaint);
        canvas.drawPath(bottomPath, bottomPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (event.getX() > size/2 && event.getX() <= (size - event.getY()) ||
                    event.getX() <= size/2 && event.getX() >= event.getY()) {
                callTriangleTouch(Triangle.TOP);
            } else if (event.getY() > size/2 && event.getY() <= (size - event.getX()) ||
                    event.getY() <= size/2  && event.getY() >= event.getX()) {
                callTriangleTouch(Triangle.LEFT);
            } else if (event.getY() <= size/2 && event.getY() >= (size - event.getX()) ||
                    event.getY() > size/2 && event.getY() <= event.getX()) {
                callTriangleTouch(Triangle.RIGHT);
            } else if (event.getX() <= size/2 && event.getX() >= (size - event.getY()) ||
                    event.getX() > size/2 && event.getX() < event.getY()) {
                callTriangleTouch(Triangle.BOTTOM);
            }
        }

        return true;
    }

    private void callTriangleTouch(Triangle triangle) {
        if (triangleTouchedListener != null) {
            triangleTouchedListener.onTriangleTouched(triangle);
        }
    }

    public interface OnTriangleTouchedListener {
        void onTriangleTouched(Triangle triangle);
    }
}
