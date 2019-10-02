package com.example.maratbe.games;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class Triangles extends View implements Constants {

    public enum Triangle {
        RIGHT, LEFT, TOP, BOTTOM
    }

    private final Paint rightPaint = new Paint();
    private final Paint leftPaint = new Paint();
    private final Paint topPaint = new Paint();
    private final Paint bottomPaint = new Paint();

    private final Path rightPath = new Path();
    private final Path leftPath = new Path();
    private final Path topPath = new Path();
    private final Path bottomPath = new Path();

    private Context context;
    private int size;

    private OnTriangleTouchedListener triangleTouchedListener;

    public Triangles(Context context) {
        super(context);
        this.context = context;
        init();
    }

    public Triangles(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public Triangles(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void setSize(int size)
    {
        this.size = size;
        init();
    }

    public void setOnTriangleTouchedListener(OnTriangleTouchedListener triangleTouchedListener) {
        this.triangleTouchedListener = triangleTouchedListener;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawPath(rightPath, rightPaint);
        canvas.drawPath(leftPath, leftPaint);
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

    private void init() {
        rightPaint.setColor(0xffff0000);
        leftPaint.setColor(0xff00ff00);
        topPaint.setColor(0xff0000ff);
        bottomPaint.setColor(0xffffff00);


        rightPaint.setAntiAlias(true);
        leftPaint.setAntiAlias(true);
        topPaint.setAntiAlias(true);
        bottomPaint.setAntiAlias(true);

        rightPath.moveTo(size, 0);
        rightPath.lineTo(size, size);
        rightPath.lineTo(size/2, size/2);
        rightPath.lineTo(size, 0);

        leftPath.moveTo(0, 0);
        leftPath.lineTo(size/2, size/2);
        leftPath.lineTo(0, size);
        leftPath.lineTo(0, 0);

        topPath.moveTo(0, 0);
        topPath.lineTo(size, 0);
        topPath.lineTo(size/2, size/2);
        topPath.lineTo(0, 0);

        bottomPath.moveTo(size, size);
        bottomPath.lineTo(0, size);
        bottomPath.lineTo(size/2, size/2);
        bottomPath.lineTo(size, size);
    }

    public interface OnTriangleTouchedListener {
        void onTriangleTouched(Triangle triangle);
    }
}
