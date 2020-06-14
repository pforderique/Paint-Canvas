package com.example.paintcanvas;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import yuku.ambilwarna.AmbilWarnaDialog;

public class CanvasActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_canvas);

        //sets up color display text view next to palette - how to change element's background
        TextView color_txt_view = (TextView) findViewById(R.id.colorDispView);
        final GradientDrawable color_disp = (GradientDrawable) color_txt_view.getBackground();

        //Sets tool bar up without a title!
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        //drawing view setup
        final DrawingView drawView = new DrawingView(this);
        final FrameLayout frm_layout = (FrameLayout) findViewById(R.id.drawView_frame);
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
        btn_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(CanvasActivity.this, v);
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.restartOption:
                                drawView.onClickRestart();
                                Toast.makeText(CanvasActivity.this, "New Canvas Started", Toast.LENGTH_SHORT).show();
                                return true;
                            case R.id.saveOption:
                                Toast.makeText(CanvasActivity.this, "save clicked", Toast.LENGTH_SHORT).show();
                                return true;
                            case R.id.shareOption:
                                Toast.makeText(CanvasActivity.this, "share clicked", Toast.LENGTH_SHORT).show();
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
        //COLOR BUTTON
        ImageButton btn_palette = (ImageButton) findViewById(R.id.paletteButton);
        btn_palette.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AmbilWarnaDialog dialog = new AmbilWarnaDialog(CanvasActivity.this, drawView.getPaintColor(), new AmbilWarnaDialog.OnAmbilWarnaListener() {
                    @Override
                    public void onOk(AmbilWarnaDialog dialog, int color) {
                        // color is the color selected by the user.
                        drawView.setPaintColor(color);
                        color_disp.setColor(color);
                    }
                    @Override
                    public void onCancel(AmbilWarnaDialog dialog) { }
                });
                dialog.show();
            }
        });
        //BACKGROUND COLOR BUTTON
        final int[] backgroundColor = {Color.BLACK}; //keeps track of bgColor for when ambilwarnadialog appears
        Button btn_bgcolor = (Button) findViewById(R.id.backgroundChooserButton);
        btn_bgcolor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AmbilWarnaDialog dialog = new AmbilWarnaDialog(CanvasActivity.this, backgroundColor[0], new AmbilWarnaDialog.OnAmbilWarnaListener() {
                        @Override
                    public void onOk(AmbilWarnaDialog dialog, int color) {
                        backgroundColor[0] = color;
                        frm_layout.setBackgroundColor(color);
                    }
                    @Override
                    public void onCancel(AmbilWarnaDialog dialog) { }
                });
                dialog.show();
            }
        });
    }
}