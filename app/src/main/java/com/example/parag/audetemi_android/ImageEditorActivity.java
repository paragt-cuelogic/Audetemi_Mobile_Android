package com.example.parag.audetemi_android;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.Display;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;

import java.io.File;

import jp.co.cyberagent.android.gpuimage.GPUImage;
import jp.co.cyberagent.android.gpuimage.GPUImageBrightnessFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageContrastFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageSaturationFilter;

/**
 * Created by Parag on 9/24/15.
 */
public class ImageEditorActivity extends Activity {

    private RelativeLayout relativeLayout;
    private RelativeLayout circleRelativeLayout, rectangleRelativeLayout;

    private ScaleGestureDetector circleViewScaleGestureDetector;
    private ScaleGestureDetector rectangleViewScaleGestureDetector;

    private GLSurfaceView mGPUSurfaceview;
    private GPUImage mGPUImage;
    private GPUImageBrightnessFilter gpuImageBrightnessFilter;
    private GPUImageContrastFilter gpuImageContrastFilter;
    private GPUImageSaturationFilter gpuImageSaturationFilter;

    private float scale = 1f;

    private Bitmap rotatedBitmap;

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

        mGPUSurfaceview = (GLSurfaceView) findViewById(R.id.surfaceView);
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


        mGPUImage = new GPUImage(this);
        mGPUImage.setGLSurfaceView((GLSurfaceView) findViewById(R.id.surfaceView));

        Matrix matrix = new Matrix();

        matrix.postRotate(90);

        rotatedBitmap = Bitmap.createBitmap(mutableBitmap , 0, 0, mutableBitmap .getWidth(), mutableBitmap .getHeight(), matrix, true);

        mGPUImage.setImage(rotatedBitmap);

        // TODO: Add options for Contrast & Brightness.
//        gpuImageBrightnessFilter = new GPUImageBrightnessFilter(0.5f);
//        mGPUImage.setFilter(gpuImageBrightnessFilter);

//        gpuImageContrastFilter = new GPUImageContrastFilter(0.5f);
//        mGPUImage.setFilter(gpuImageContrastFilter);

        gpuImageSaturationFilter = new GPUImageSaturationFilter(0.5f);
        mGPUImage.setFilter(gpuImageSaturationFilter);

        circleViewScaleGestureDetector =
                new ScaleGestureDetector(ImageEditorActivity.this,
                        new CircleViewScaleGestureListener());

        rectangleViewScaleGestureDetector =
                new ScaleGestureDetector(ImageEditorActivity.this,
                        new RectangleViewScaleGestureListener());

        final SeekBar seekBar_contrast = (SeekBar) findViewById(R.id.seekBar);
        seekBar_contrast.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                float brightnessValue = 0.1f * progress;
                // TODO: Add options for Contrast & Brightness.
//                gpuImageBrightnessFilter.setBrightness(brightnessValue);
//                mGPUImage.setImage(mutableBitmap);

//                gpuImageContrastFilter.setContrast(brightnessValue);
//                mGPUImage.setImage(mutableBitmap);

                gpuImageSaturationFilter.setSaturation(brightnessValue);
                mGPUImage.setImage(rotatedBitmap);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

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


    public class CircleViewScaleGestureListener extends
            ScaleGestureDetector.SimpleOnScaleGestureListener {

        @Override
        public boolean onScale(ScaleGestureDetector detector) {

            scale *= detector.getScaleFactor();
            scale = Math.max(0.1f, Math.min(scale, 5.0f));

            if(circleRelativeLayout != null) {
                circleRelativeLayout.setScaleX(scale);
                circleRelativeLayout.setScaleY(scale);
            }

            return true;
        }

        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector) {
            return true;
        }

        @Override
        public void onScaleEnd(ScaleGestureDetector detector) {

        }
    }

    public class RectangleViewScaleGestureListener extends
            ScaleGestureDetector.SimpleOnScaleGestureListener {

        @Override
        public boolean onScale(ScaleGestureDetector detector) {

            scale *= detector.getScaleFactor();
            scale = Math.max(0.1f, Math.min(scale, 5.0f));

            if(rectangleRelativeLayout != null) {
                rectangleRelativeLayout.setScaleX(scale);
                rectangleRelativeLayout.setScaleY(scale);
            }

            return true;
        }

        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector) {
            return true;
        }

        @Override
        public void onScaleEnd(ScaleGestureDetector detector) {

        }
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



        circleRelativeLayout  = new RelativeLayout(ImageEditorActivity.this);
        circleRelativeLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                circleViewScaleGestureDetector.onTouchEvent(event);
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
                        circleRelativeLayout.setX(X);
                        circleRelativeLayout.setY(Y);
                        break;

                }
                return true;
            }
        });

        // TODO: Set this X and Y values dynamically.
        circleRelativeLayout.setX(200);
        circleRelativeLayout.setY(300);

        // TODO: Make this value dynamic based on the DPI values of the device.
        circleRelativeLayout.addView(circleView, 200, 200);
        relativeLayout.addView(circleRelativeLayout);
    }

    private void drawRectangleView() {

        View rectangleView = new View(this);
        rectangleView.setBackgroundResource(R.drawable.rectangle);

        rectangleRelativeLayout  = new RelativeLayout(ImageEditorActivity.this);
        rectangleRelativeLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                rectangleViewScaleGestureDetector.onTouchEvent(event);
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
                        rectangleRelativeLayout.setX(X);
                        rectangleRelativeLayout.setY(Y);
                        break;
                }
                return true;
            }
        });

        // TODO: Set this X and Y values dynamically.
        rectangleRelativeLayout.setX(200);
        rectangleRelativeLayout.setY(300);

        // TODO: Make this value dynamic based on the DPI values of the device.
        rectangleRelativeLayout.addView(rectangleView, 200, 200);
        relativeLayout.addView(rectangleRelativeLayout);
    }

    private void displayFreeHandView() {
        FreeHandView freeHandView = new FreeHandView(ImageEditorActivity.this);
        relativeLayout.addView(freeHandView, mGPUSurfaceview.getWidth(), mGPUSurfaceview.getHeight());
    }
}
