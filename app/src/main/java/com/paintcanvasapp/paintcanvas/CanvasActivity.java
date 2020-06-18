package com.paintcanvasapp.paintcanvas;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import yuku.ambilwarna.AmbilWarnaDialog;

public class CanvasActivity extends AppCompatActivity {
    private AdView mAdView; // for ads
    FrameLayout frm_layout;
    DrawingView drawView;
    FileOutputStream out;
    String menuOptionSelected;
    int STORAGE_PERMISSION_CODE = 1; //to identify our request
    int pensizeVal = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_canvas);

        //sets up color display text view next to palette - how to change element's background
        TextView color_txt_view = (TextView) findViewById(R.id.colorDispView);
        final GradientDrawable color_disp = (GradientDrawable) color_txt_view.getBackground();

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
                                menuOptionSelected = "saveOption";
                                //check if permission has already been granted. If not, request permission
                                if (ContextCompat.checkSelfPermission(CanvasActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                        == PackageManager.PERMISSION_GRANTED) { saveImage(screenshotAsBitmap(frm_layout)); }
                                else { requestStorageAccessPermission();}
                                Toast.makeText(CanvasActivity.this, "Canvas Saved", Toast.LENGTH_SHORT).show();
                                return true;
                            case R.id.shareOption:
                                menuOptionSelected = "shareOption";
                                //check if permission has already been granted. If not, request permission
                                if (ContextCompat.checkSelfPermission(CanvasActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                        == PackageManager.PERMISSION_GRANTED) { shareImageFile(saveImage(screenshotAsBitmap(frm_layout))); }
                                else { requestStorageAccessPermission();}
                                Toast.makeText(CanvasActivity.this, "Share Options", Toast.LENGTH_SHORT).show();
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

        //Google Admob
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) { }
        });
        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }

    //method for changing pen size from popup and displaying correspondingly
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

    //requests permission to write and read to external storage
    private void requestStorageAccessPermission(){
        if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.WRITE_EXTERNAL_STORAGE)){
            new AlertDialog.Builder(this)
                    .setTitle("Permission Needed")
                    .setMessage("Permission is needed to save drawing to storage")
                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(CanvasActivity.this, new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE},STORAGE_PERMISSION_CODE);
                        }
                    })
                    .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create().show();
        } else {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE},STORAGE_PERMISSION_CODE);
        }
    }

    @Override //what to do based on user input
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == STORAGE_PERMISSION_CODE){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                //if user says yes to request, act appropriately based on if they are sharing or only saving image.
                if(menuOptionSelected.equals("saveOption")) { saveImage(screenshotAsBitmap(frm_layout)); }
                else if (menuOptionSelected.equals("shareOption")){ shareImageFile(saveImage(screenshotAsBitmap(frm_layout))); }
                else { Toast.makeText(this, "Error Sharing/Saving Drawing",Toast.LENGTH_SHORT); }
                Toast.makeText(this, "Permission GRANTED",Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Permission NOT GRANTED",Toast.LENGTH_SHORT).show();
            }
        }
    }

    //saves view as a bitmap based on view's dimensions
    public Bitmap screenshotAsBitmap(View view){
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        return bitmap;
    }

    //saves image to gallery
    private File saveImage(Bitmap finalBitmap) {
        //Establishes the path to image and makes the directories needed
        String rootPath = Environment.getExternalStorageDirectory().getAbsolutePath()+"/Pictures/PaintCanvas/";
        File root = new File(rootPath);
        root.mkdirs();
        //Creates file location and compresses image there
        String fileName = "PaintCanvasDrawing"+System.currentTimeMillis()+".jpg";
        File imgFile = new File(root, fileName).getAbsoluteFile();
//        Log.i("Loaded", root.getAbsolutePath()+"/"+fileName);
        try { out = new FileOutputStream(imgFile); }
        catch (Exception e) { e.printStackTrace(); }
        finalBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
        try { out.flush(); out.close(); } catch (IOException e) { e.printStackTrace(); }
        //Needed so that it is shown in Gallery
        MediaScannerConnection.scanFile(this, new String[] { imgFile.getPath() }, new String[] { "image/jpeg" }, null);
        return imgFile;
    }

    //saves image from file and shares image
    private void shareImageFile(File file){
        Uri uri = FileProvider.getUriForFile(this,this.getApplicationContext().getPackageName() + ".provider", file);
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.setType("image/*");

        intent.putExtra(android.content.Intent.EXTRA_SUBJECT, "");
        intent.putExtra(android.content.Intent.EXTRA_TEXT, "");

        intent.putExtra(Intent.EXTRA_STREAM, uri);
        startActivity(Intent.createChooser(intent, "Share Cover Image"));
    }
}