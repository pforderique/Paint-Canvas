package com.paintcanvasapp.paintcanvas;

public class Stroke {
    private int color;
    private float strokeWidth;

    public Stroke(int col, float sw) {
        color = col;
        strokeWidth = sw;
    }
    //setters
    public void setStrokeColor(int col){ color = col;}
    public void setStrokeWidth(float sw){ strokeWidth = sw; }
    //getters
    public int getStrokeColor(){ return color; }
    public float getStrokeWidth(){ return strokeWidth; }
}
