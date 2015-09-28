package com.example.parag.audetemi_android;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.nio.charset.Charset;

/**
 * Created by Parag on 9/24/15.
 */
public class ImageEditorActivity extends Activity {

    private String imagedata;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        Bundle extras = getIntent().getExtras();
        String photoPath = extras.getString("data");

        ImageView imageView = (ImageView)findViewById(R.id.imgView);

        Bitmap myBitmap = BitmapFactory.decodeFile(new File(photoPath).getAbsolutePath());

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        int width = size.x;
        int height = size.y;

        float scaleHt =(float) width/myBitmap.getWidth();

        Bitmap scaled = Bitmap.createScaledBitmap(myBitmap, width, (int) (myBitmap.getWidth() * scaleHt), true);

        imageView.setImageBitmap(scaled);
    }

}
