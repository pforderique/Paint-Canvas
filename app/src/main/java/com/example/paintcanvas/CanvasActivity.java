package com.example.paintcanvas;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Date;
import java.util.Random;

import yuku.ambilwarna.AmbilWarnaDialog;

public class CanvasActivity extends AppCompatActivity {
    FrameLayout frm_layout;
    DrawingView drawView;
    int pensizeVal = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_canvas);
        Log.i("Running", "Running...");
        //sets up color display text view next to palette - how to change element's background
        TextView color_txt_view = (TextView) findViewById(R.id.colorDispView);
        final GradientDrawable color_disp = (GradientDrawable) color_txt_view.getBackground();

        //Sets tool bar up without a title!
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        //drawing view and frame layout setup
        drawView = new DrawingView(this);
        frm_layout = (FrameLayout) findViewById(R.id.drawView_frame);
        frm_layout.addView(drawView);

        //PENSIZE BUTTON
        Button btn_pensize = (Button) findViewById(R.id.sizeChooserButton);
        btn_pensize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { initializePopUpWindow(); }
        });

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
                                ImageView imgView = (ImageView) findViewById(R.id.screenShotView);
//                                Bitmap b = Screenshot.takescreenshotOfRootView(imgView);
//                                imgView.setImageBitmap(b);

                                frm_layout.setBackgroundColor(Color.parseColor("#999999")); //added for screenshot
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
        final int[] backgroundColor = {Color.WHITE}; //keeps track of bgColor for when ambilwarnadialog appears
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
    public void initializePopUpWindow(){
        try {
            LayoutInflater inflater = (LayoutInflater) CanvasActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final View layout = inflater.inflate(R.layout.pensize_popup, (ViewGroup) findViewById(R.id.popupElement));
            PopupWindow pwindo = new PopupWindow(layout, 800, 300, true);
            pwindo.showAtLocation(layout, Gravity.CENTER, 0, 0);

            final TextView pensize_tv = (TextView) layout.findViewById(R.id.pensizeTxtView);
            SeekBar pensize_sb = (SeekBar) layout.findViewById(R.id.seekBar);

            pensize_tv.setText("Pen Size: " + pensizeVal + "px"); //changes text view to whatever last pensize val was on new popup instance
            pensize_sb.setProgress(pensizeVal); //changes progress value too to current pensize val

            pensize_sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    pensizeVal = seekBar.getProgress();
                    pensize_tv.setText("Pen Size: " + pensizeVal + "px");
                    drawView.setStrokeWidth(pensizeVal*5);
                    Log.i("being dragged", "dragging...");
                }
                @Override
                public void onStartTrackingTouch(SeekBar seekBar) { }
                @Override
                public void onStopTrackingTouch(SeekBar seekBar) { }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void saveImage(Bitmap finalBitmap, String image_name) {
        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root);
        myDir.mkdirs();
        String fname = "/Image-" + image_name+ ".jpg";
        File file = new File(myDir, fname);
        if (file.exists()) file.delete();
        Log.i("LOAD", root + fname);
        try {
            FileOutputStream out = new FileOutputStream(file);
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void saveImageToGallery( String title, String description){
        drawView.setDrawingCacheEnabled(true);
        Bitmap b = drawView.getDrawingCache();
        MediaStore.Images.Media.insertImage(getContentResolver(), b,title, description);
    }
    private void SaveImage(Bitmap finalBitmap) {
        String root = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES).toString();
        File myDir = new File(root + "/PaintCanvasDrawings");
        myDir.mkdirs();
        Random generator = new Random();

        int n = 10000;
        n = generator.nextInt(n);
        String fname = "Image-"+ n +".jpg";
        File file = new File (myDir, fname);
        if (file.exists ()) file.delete ();
        try {
            FileOutputStream out = new FileOutputStream(file);
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            // sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED,
            //     Uri.parse("file://"+ Environment.getExternalStorageDirectory())));
            out.flush();
            out.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        // Tell the media scanner about the new file so that it is
        // immediately available to the user.
        MediaScannerConnection.scanFile(this, new String[]{file.toString()}, null,
                new MediaScannerConnection.OnScanCompletedListener() {
                    public void onScanCompleted(String path, Uri uri) {
                        Log.i("ExternalStorage", "Scanned " + path + ":");
                        Log.i("ExternalStorage", "-> uri=" + uri);
                    }
                });
    }

    public void screenShot(View view) {
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(),
                view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
//        screenShot.setImageBitmap(bitmap);
//        textView.setText("click");
    }
}