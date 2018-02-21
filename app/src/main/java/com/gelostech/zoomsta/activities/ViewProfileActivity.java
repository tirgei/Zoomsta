package com.gelostech.zoomsta.activities;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.gelostech.zoomsta.R;
import com.github.chrisbanes.photoview.PhotoView;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.DialogOnDeniedPermissionListener;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

public class ViewProfileActivity extends AppCompatActivity {
    private PhotoView imageView;
    private AdView adView;
    private ImageButton save, share, delete;
    private Bitmap bitmap;
    private RelativeLayout buttons;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_profile);

        adView = findViewById(R.id.prof_banner_ad);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);

        imageView = findViewById(R.id.main_imageview);
        save = findViewById(R.id.button_save);
        share = findViewById(R.id.button_share);
        delete = findViewById(R.id.button_delete);
        buttons = findViewById(R.id.button_options);

        save.setVisibility(View.VISIBLE);

        try {
            bitmap = BitmapFactory.decodeStream(ViewProfileActivity.this.openFileInput("myImage"));
            imageView.setImageBitmap(bitmap);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }


        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(buttons.isShown())
                    buttons.setVisibility(View.GONE);
                else
                    buttons.setVisibility(View.VISIBLE);
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dexter.withActivity(ViewProfileActivity.this)
                        .withPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        .withListener(new PermissionListener() {
                            @Override
                            public void onPermissionGranted(PermissionGrantedResponse response) {
                                saveImage(bitmap);
                            }

                            @Override
                            public void onPermissionDenied(PermissionDeniedResponse response) {
                                PermissionListener dialogPermissionListener = DialogOnDeniedPermissionListener.Builder
                                        .withContext(ViewProfileActivity.this)
                                        .withTitle("Storage permission")
                                        .withMessage("Storage permission is needed to save pictures")
                                        .withButtonText(android.R.string.ok)
                                        .build();
                            }

                            @Override
                            public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                                token.continuePermissionRequest();
                            }
                        }).check();
            }
        });

        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String bitmapPath = MediaStore.Images.Media.insertImage(getContentResolver(), bitmap,"title", null);
                Uri bitmapUri = Uri.parse(bitmapPath);

                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("image/png");
                intent.putExtra(Intent.EXTRA_STREAM, bitmapUri);
                startActivity(Intent.createChooser(intent, "Share via..."));
            }
        });


    }

    private void saveImage(Bitmap bitmap){
        File file = new File(Environment.getExternalStorageDirectory().toString() + File.separator + "Zoomsta");
        if(!file.exists())
            file.mkdirs();

        String fileName = "Zoomsta-" + System.currentTimeMillis() + ".jpg";

        File newImage = new File(file, fileName);
        if(newImage.exists()) file.delete();
        try {
            FileOutputStream out = new FileOutputStream(newImage);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();

            Toast.makeText(this, "Saving image...", Toast.LENGTH_SHORT).show();

        } catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
