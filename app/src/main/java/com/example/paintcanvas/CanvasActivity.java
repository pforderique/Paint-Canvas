package com.example.paintcanvas;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.widget.Toolbar;

import android.graphics.Canvas;
import android.os.Bundle;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.PopupMenu;
import android.widget.Toast;

public class CanvasActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_canvas);

        //Sets tool bar up without a title!
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        //drawing view setup
        final DrawingView drawView = new DrawingView(this);
        FrameLayout frm_layout = (FrameLayout) findViewById(R.id.drawView_frame);
        frm_layout.addView(drawView);

        //UNDO BUTTON
        Button btn_undo = (Button) findViewById(R.id.undoButton);
        btn_undo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawView.onClickUndo();
            }
        });
        //REDO BUTTON
        Button btn_redo = (Button) findViewById(R.id.redoButton);
        btn_redo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawView.onClickRedo();
            }
        });
        //MENU BUTTON
        Button btn_menu = (Button) findViewById(R.id.menuButton);
        btn_menu.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                PopupMenu popup = new PopupMenu(CanvasActivity.this, v);
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch(item.getItemId()){
                            case R.id.restartOption:
                                drawView.onClickRestart();
                                Toast.makeText(CanvasActivity.this,"New Canvas Started",Toast.LENGTH_SHORT).show();
                                return true;
                            case R.id.saveOption:
                                Toast.makeText(CanvasActivity.this,"save clicked",Toast.LENGTH_SHORT).show();
                                return true;
                            case R.id.shareOption:
                                Toast.makeText(CanvasActivity.this,"share clicked",Toast.LENGTH_SHORT).show();
                                return true;
                            default:
                                return false;
                        }
                    }
                });
                popup.inflate(R.menu.popupmenu);
                popup.show();
            }
        });
    }
}