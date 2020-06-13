package com.example.paintcanvas;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
//import android.graphics.Point;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

//import java.util.ArrayList;
//import java.util.List;

public class DrawingView extends View {
    // setup initial color
    private int paintColor = Color.BLACK;
    // defines paint and canvas
    private Paint drawPaint;
//    // Store circles to draw each time the user touches down
//    private List<Point> circlePoints;
    //Lets us draw in paths
    private Path path = new Path();
    //Bitmap for caching
//    Bitmap mField = null;

    public DrawingView(Context context, AttributeSet attrs) {
        super(context, attrs);
//        setFocusable(true);
//        setFocusableInTouchMode(true);
//        mField = new Bitmap();
        initializePaint();
//        circlePoints = new ArrayList<Point>();
    }

    private void initializePaint() {
        drawPaint = new Paint();
        drawPaint.setColor(paintColor);
        drawPaint.setAntiAlias(true);
        drawPaint.setStrokeWidth(5);
        drawPaint.setStyle(Paint.Style.STROKE);
        drawPaint.setStrokeJoin(Paint.Join.ROUND);
        drawPaint.setStrokeCap(Paint.Cap.ROUND);
    }

    // Draw each circle onto the view
    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawPath(path, drawPaint);
    }

    // Get x and y and append them to the path
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float touchX = event.getX();
        float touchY = event.getY();
        // Checks for the event that occurs
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // Starts a new line in the path
                path.moveTo(touchX, touchY);
                break;
            case MotionEvent.ACTION_MOVE:
                // Draws line between last point and this point
                path.lineTo(touchX, touchY);
                break;
            default:
                return false;
        }
        // indicate view should be redrawn
        postInvalidate();
        return true;
    }
}