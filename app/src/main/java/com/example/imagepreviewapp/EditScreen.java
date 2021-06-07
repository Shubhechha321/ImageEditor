package com.example.imagepreviewapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;

public class EditScreen extends AppCompatActivity {
    private static final int CROP_PIC_REQUEST_CODE =100 ;
    Bitmap editedBmp;
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_screen);
        Button saveButton = this.findViewById(R.id.button2);
        ImageButton cropButton = this.findViewById(R.id.crop);
        cropButton.setBackground(null);
        ImageButton rotateAnti = this.findViewById(R.id.rotateAntiClock);
        rotateAnti.setBackground(null);
        ImageButton rotateClock = this.findViewById(R.id.rotateClock);
        rotateClock.setBackground(null);
        ImageButton undoButton = this.findViewById(R.id.undo);
        undoButton.setBackground(null);
        Bitmap bmp = null;

        String filename = getIntent().getStringExtra("image");
        try {
            FileInputStream is = this.openFileInput(filename);
            bmp = BitmapFactory.decodeStream(is);
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        imageView = this.findViewById(R.id.imageView2);

        Bitmap finalBmp = bmp;
        editedBmp = bmp;

        String path = MediaStore.Images.Media.insertImage(getApplicationContext().getContentResolver(), bmp, "Title", null);
        Uri uri = Uri.parse(path);
        imageView.setImageBitmap(bmp);

        saveButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                imageView.invalidate();
                imageView.setDrawingCacheEnabled(true);
                Bitmap bitmap = imageView.getDrawingCache();
                sendEditedImage(bitmap);
            }
        });
        undoButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                imageView.setImageBitmap(finalBmp);
            }
        });
        cropButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cropIntent = new Intent("com.android.camera.action.CROP");
                cropIntent.setDataAndType(uri, "image/*");
                cropIntent.putExtra("crop", "true");
                cropIntent.putExtra("aspectX", 1);
                cropIntent.putExtra("aspectY", 1);
                cropIntent.putExtra("outputX", 128);
                cropIntent.putExtra("outputY", 128);
                cropIntent.putExtra("return-data", true);
                startActivityForResult(cropIntent, CROP_PIC_REQUEST_CODE);
            }
        });
        rotateAnti.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Matrix matrix = new Matrix();
                // setup rotation degree
                matrix.postRotate(270);
                Bitmap bmp = Bitmap.createBitmap(editedBmp, 0, 0, editedBmp.getWidth(), editedBmp.getHeight(), matrix, true);
                imageView.setImageBitmap(bmp);
                editedBmp = bmp;
            }
        });
        rotateClock.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Matrix matrix = new Matrix();
                // setup rotation degree
                matrix.postRotate(90);
                Bitmap bmp = Bitmap.createBitmap(editedBmp, 0, 0, editedBmp.getWidth(), editedBmp.getHeight(), matrix, true);
                imageView.setImageBitmap(bmp);
                editedBmp = bmp;
            }
        });

    }

    private void sendEditedImage(Bitmap finalBmp) {
        try {
            //Write file
            String filename = "bitmap.png";
            FileOutputStream stream = this.openFileOutput(filename, Context.MODE_PRIVATE);

            finalBmp.compress(Bitmap.CompressFormat.PNG, 100, stream);

            //Cleanup
            stream.close();
            finalBmp.recycle();

            //Pop intent
            Intent in1 = new Intent(this, HomeScreen.class);
            in1.putExtra("editedImage", filename);
            startActivity(in1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CROP_PIC_REQUEST_CODE) {
            if (data != null) {
                Bundle extras = data.getExtras();
                Bitmap bitmap= extras.getParcelable("data");
                imageView.setImageBitmap(bitmap);
            }
        }

    }
}