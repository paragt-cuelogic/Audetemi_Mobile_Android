package com.example.parag.audetemi_android;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.io.File;

/**
 * Created by Parag on 9/24/15.
 */
public class ImageEditorActivity extends Activity {

    private RelativeLayout relativeLayout;
    private ImageView capturedImageView;
    private Button btn_circle, btn_rectangle, btn_pointer, btn_freehand;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        // Get the data from bundle object.
        Bundle extras = getIntent().getExtras();
        String photoPath = extras.getString("data");

        // Get the RelativeLayout from XML to add the shape elements dynamically.
        relativeLayout = (RelativeLayout) findViewById(R.id.relativelayout);
        btn_circle = (Button) findViewById(R.id.circle);
        btn_rectangle = (Button) findViewById(R.id.rectangle);
        btn_pointer = (Button) findViewById(R.id.poiter);
        btn_freehand = (Button) findViewById(R.id.freehand);

        // Get Display height and width.
        Display display = getWindowManager().getDefaultDisplay();
        final int displayWidth = display.getWidth();
        final int displayHeight = display.getHeight();


        // To reduce the bitmap size.
        BitmapFactory.Options options = new BitmapFactory.Options();
        File file = new File(photoPath);
        options.inJustDecodeBounds = true;
        Bitmap bitmap =  BitmapFactory.decodeFile(file.getAbsolutePath(), options);
        int width = options.outWidth;
        if (width > displayWidth) {
            int widthRatio = Math.round((float) width / (float) displayWidth);
            options.inSampleSize = widthRatio;
        }
        options.inJustDecodeBounds = false;
        Bitmap scaledBitmap =  BitmapFactory.decodeFile(file.getAbsolutePath(), options);

        // Get the mutable bitmap.
        final Bitmap mutableBitmap = scaledBitmap.copy(Bitmap.Config.ARGB_8888, true);

        // To draw the pointer on the captured image.
        capturedImageView = (ImageView) findViewById(R.id.imageView);
        capturedImageView.setRotation(90);
        capturedImageView.setImageBitmap(mutableBitmap);
        btn_circle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawCircleView();
            }
        });

        btn_rectangle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawRectangleView();
            }
        });

        btn_pointer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayPointerView();;
            }
        });

        btn_freehand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayFreeHandView();
            }
        });
    }

    private void displayPointerView() {

        final RelativeLayout relative  = new RelativeLayout(ImageEditorActivity.this);

        final ImageView poniterImageView = new ImageView(ImageEditorActivity.this);
        poniterImageView.setImageResource(R.drawable.pointer);

        relative.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int X = (int) event.getRawX();
                int Y = (int) event.getRawY();

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        break;
                    case MotionEvent.ACTION_POINTER_UP:
                        break;
                    case MotionEvent.ACTION_POINTER_DOWN:
                        break;
                    case MotionEvent.ACTION_MOVE:
                        relative.setX(X);
                        relative.setY(Y);
                        break;
                }
                return true;
            }
        });

        // TODO: Set this X and Y values dynamically.
        relative.setX(100);
        relative.setY(200);

        relative.addView(poniterImageView);
        relativeLayout.addView(relative);
    }

    private void drawCircleView() {

        View circleView = new View(this);
        circleView.setBackgroundResource(R.drawable.circle);

        final RelativeLayout relative  = new RelativeLayout(ImageEditorActivity.this);
        relative.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int X = (int) event.getRawX();
                int Y = (int) event.getRawY();

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        break;
                    case MotionEvent.ACTION_POINTER_UP:
                        break;
                    case MotionEvent.ACTION_POINTER_DOWN:
                        break;
                    case MotionEvent.ACTION_MOVE:
                        relative.setX(X);
                        relative.setY(Y);
                        break;

                }
                return true;
            }
        });

        // TODO: Set this X and Y values dynamically.
        relative.setX(200);
        relative.setY(300);

        // TODO: Make this value dynamic based on the DPI values of the device.
        relative.addView(circleView, 100, 100);
        relativeLayout.addView(relative);
    }

    private void drawRectangleView() {

        View rectangleView = new View(this);
        rectangleView.setBackgroundResource(R.drawable.rectangle);

        final RelativeLayout relative  = new RelativeLayout(ImageEditorActivity.this);
        relative.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int X = (int) event.getRawX();
                int Y = (int) event.getRawY();

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        break;
                    case MotionEvent.ACTION_POINTER_UP:
                        break;
                    case MotionEvent.ACTION_POINTER_DOWN:
                        break;
                    case MotionEvent.ACTION_MOVE:
                        relative.setX(X);
                        relative.setY(Y);
                        break;
                }
                return true;
            }
        });

        // TODO: Set this X and Y values dynamically.
        relative.setX(200);
        relative.setY(300);

        // TODO: Make this value dynamic based on the DPI values of the device.
        relative.addView(rectangleView, 100, 100);
        relativeLayout.addView(relative);
    }

    private void displayFreeHandView() {
        FreeHandView freeHandView = new FreeHandView(ImageEditorActivity.this);
        relativeLayout.addView(freeHandView);
    }
}
