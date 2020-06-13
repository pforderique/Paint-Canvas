package com.example.paintcanvas;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View.OnTouchListener;
import android.view.View;

import java.util.ArrayList;

public class DrawingView extends View implements OnTouchListener {
    //setup canvas
    private Canvas mCanvas;
    // setup initial color
    private int paintColor = Color.BLACK;
    // defines paint and canvas
    private Paint mPaint;
    //Lets us draw in paths
    private Path mPath = new Path();
    //Stores the paths in an arraylist for undoing and redoing
    private ArrayList<Path> paths = new ArrayList<Path>();
    private ArrayList<Path> undonePaths = new ArrayList<Path>();
    //Bitmap for caching
    private Bitmap im;

    @SuppressLint("ClickableViewAccessibility")
    public DrawingView(Context context) {
        super(context);
        setFocusable(true);
        setFocusableInTouchMode(true);
        this.setOnTouchListener(this);
        mCanvas = new Canvas();
        mPath = new Path();
        paths.add(mPath);
        setupPaint();
//        im = BitmapFactory.decodeResource(context.getResources(),R.drawable.ic_launcher_background);
    }

    private void setupPaint() {
        mPaint = new Paint();
        mPaint.setColor(paintColor);
        mPaint.setAntiAlias(true); //rounds off drawings
        mPaint.setDither(true); //downsizes colors on devices that cant handle precision
        mPaint.setStrokeWidth(6);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }

    // Draw each circle onto the view
    @Override
    protected void onDraw(Canvas canvas) {
        for (Path p : paths){
            canvas.drawPath(p, mPaint);
        }
    }

    private float mX, mY;
    private static final float TOUCH_TOLERANCE = 4;


    private void touch_start(float x, float y) {
        mPath.reset();
        mPath.moveTo(x, y);
        mX = x;
        mY = y;
    }

    private void touch_move(float x, float y) {
        float dx = Math.abs(x - mX);
        float dy = Math.abs(y - mY);
        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
            mPath.quadTo(mX, mY, (x + mX)/2, (y + mY)/2);
            mX = x;
            mY = y;
        }
    }

    private void touch_up() {
        mPath.lineTo(mX, mY);
        // commit the path to our offscreen
        mCanvas.drawPath(mPath, mPaint);
        // kill this so we don't double draw
        mPath = new Path();
        paths.add(mPath);
    }

    public void onClickUndo(){
        if (paths.size()>0){
            undonePaths.add(paths.remove(paths.size()-1));
            invalidate(); //redraw on screen
        }else{

        }
        //toast the user
    }

    public void onClickRedo(){
        if (undonePaths.size()>0){
            paths.add(undonePaths.remove(undonePaths.size()-1));
            invalidate();
        }else{

        }
        //toast the user
    }

    public void onClickRestart(){
        paths.clear();
        undonePaths.clear();
        invalidate();
    }

    @Override
    public boolean onTouch(View arg0, MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                touch_start(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                touch_move(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                touch_up();
                invalidate();
                break;
        }
        return true;
    }
}