package com.example.paintcanvas;
/**TODO:
 * Find why one path is being drawn at the start - this might be the reason for undo button taking one more initially to actually start undoing
 * Fix color not changing until after path is drawn when a new color has just been selected (works fine after initial path after color change is drawn)*/
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View.OnTouchListener;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

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
    private Bitmap bitmap;
    //HashMap for storing Path colors
    private HashMap<Path,Integer> colorMap;

    @SuppressLint("ClickableViewAccessibility")
    public DrawingView(Context context) {
        super(context);
        setFocusable(true);
        setFocusableInTouchMode(true);
        this.setOnTouchListener(this);
        mCanvas = new Canvas();
        mPath = new Path();
        paths.add(mPath);
        colorMap = new HashMap<>();
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
        Log.i("OnDRAWING",""+paths.size());
        if(paths.size()>1) {
            for (Path p : paths) {
                mPaint.setColor(colorMap.get(p));
                canvas.drawPath(p, mPaint);
            }
        }
        canvas.drawPath(mPath, mPaint); //added
        mPaint.setColor(paintColor);
    }

    private float mX, mY;
    private static final float TOUCH_TOLERANCE = 4;

    private void touch_start(float x, float y) {
        mPaint.setColor(paintColor);
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
//        mPath = new Path();
        paths.add(mPath);
        colorMap.put(mPath,mPaint.getColor());
        Log.i("hashmap",colorMap.toString());
        mPath = new Path();
        mPath.reset();
    }

    public void onClickUndo(){
        if (paths.size()>0){
            undonePaths.add(paths.remove(paths.size()-1));
            invalidate(); //redraw on screen
        }else{ Toast.makeText(getContext(), "no more undos", Toast.LENGTH_SHORT).show(); }
    }

    public void onClickRedo(){
        if (undonePaths.size()>0){
            paths.add(undonePaths.remove(undonePaths.size()-1));
            invalidate();
        }else{ Toast.makeText(getContext(), "no more redos", Toast.LENGTH_SHORT).show(); }
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
    public int getPaintColor(){
        return mPaint.getColor();
    }
    public void setPaintColor(int color){
        paintColor = color;
        mPaint.setColor(color);
    }
}