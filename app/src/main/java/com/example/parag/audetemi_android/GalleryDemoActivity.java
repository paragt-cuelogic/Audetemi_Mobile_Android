package com.example.parag.audetemi_android;

import android.app.Activity;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;

/**
 * Created by Parag on 9/24/15.
 */
public class GalleryDemoActivity extends Activity {

    private String imagedata;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        Bundle bundle = getIntent().getExtras();
        imagedata = bundle.getString("data");
        ImageView imgView = (ImageView) findViewById(R.id.imgView);
        // Set the Image in ImageView after decoding the String
        imgView.setImageBitmap(BitmapFactory.decodeFile(imagedata));


    }

}
