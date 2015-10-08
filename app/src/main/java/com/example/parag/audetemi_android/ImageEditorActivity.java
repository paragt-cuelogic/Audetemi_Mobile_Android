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
import android.widget.CheckBox;
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
    private int oldX,oldY,originX,originY,diffX,diffY;

    private Bitmap rotatedBitmap;
    private Bitmap mutableBitmap;

    private Button btn_circle, btn_rectangle, btn_pointer, btn_freehand;
    private CheckBox checkbox_brightness,checkbox_contrast,checkbox_saturation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        // Get the data from bundle object.
        Bundle extras = getIntent().getExtras();
        String photoPath = extras.getString("data");
        initView();
        //initFilter();

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
        mutableBitmap = scaledBitmap.copy(Bitmap.Config.ARGB_8888, true);

        mGPUImage = new GPUImage(this);
        mGPUImage.setGLSurfaceView(mGPUSurfaceview);

        Matrix matrix = new Matrix();

        matrix.postRotate(90);

        rotatedBitmap = Bitmap.createBitmap(mutableBitmap , 0, 0, mutableBitmap.getWidth(), mutableBitmap.getHeight(), matrix, true);

        mGPUImage.setImage(rotatedBitmap);

        gpuImageBrightnessFilter = new GPUImageBrightnessFilter();

        gpuImageContrastFilter = new GPUImageContrastFilter();

        gpuImageSaturationFilter = new GPUImageSaturationFilter();

        circleViewScaleGestureDetector =
                new ScaleGestureDetector(ImageEditorActivity.this,
                        new CircleViewScaleGestureListener());

        rectangleViewScaleGestureDetector =
                new ScaleGestureDetector(ImageEditorActivity.this,
                        new RectangleViewScaleGestureListener());

        handleSeekbar();
        
        btn_circle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(circleRelativeLayout == null){
                    drawCircleView();
                }
            }
        });

        btn_rectangle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (rectangleRelativeLayout == null )
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

    public void initView(){

        // Get the RelativeLayout from XML to add the shape elements dynamically.
        relativeLayout = (RelativeLayout) findViewById(R.id.relativelayout);
        mGPUSurfaceview = (GLSurfaceView) findViewById(R.id.surfaceView);
        btn_circle = (Button) findViewById(R.id.circle);
        btn_rectangle = (Button) findViewById(R.id.rectangle);
        btn_pointer = (Button) findViewById(R.id.poiter);
        btn_freehand = (Button) findViewById(R.id.freehand);
        checkbox_brightness = (CheckBox) findViewById(R.id.brightness);
        checkbox_contrast = (CheckBox) findViewById(R.id.contrast);
        checkbox_saturation = (CheckBox) findViewById(R.id.saturation);

    }

//    public void initFilter(){
//
//        mGPUImage = new GPUImage(this);
//        mGPUImage.setGLSurfaceView(mGPUSurfaceview);
//        mGPUImage.setImage(rotatedBitmap);
//        gpuImageBrightnessFilter = new GPUImageBrightnessFilter();
//        gpuImageContrastFilter = new GPUImageContrastFilter();
//        gpuImageSaturationFilter = new GPUImageSaturationFilter();
//
//
//    }

    public void handleSeekbar(){

        final SeekBar seekBar_contrast = (SeekBar) findViewById(R.id.seekBar);

        seekBar_contrast.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                float value = 0.1f * progress;
                if(checkbox_brightness.isChecked()) {
                    mGPUImage.setFilter(gpuImageBrightnessFilter);
                    gpuImageBrightnessFilter.setBrightness(value);
                    mGPUImage.setImage(mutableBitmap);
                }
                if(checkbox_saturation.isChecked()) {
                    mGPUImage.setFilter(gpuImageSaturationFilter);
                    gpuImageSaturationFilter.setSaturation(value);
                    mGPUImage.setImage(mutableBitmap);
                }
                if(checkbox_contrast.isChecked()) {
                    mGPUImage.setFilter(gpuImageContrastFilter);
                    gpuImageContrastFilter.setContrast(value);
                    mGPUImage.setImage(rotatedBitmap);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

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

        circleRelativeLayout  = new RelativeLayout(ImageEditorActivity.this);
        circleRelativeLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                circleViewScaleGestureDetector.onTouchEvent(event);
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                            oldX = (int) event.getRawX();
                            oldY = (int) event.getRawY();
                            diffX = (int) rectangleRelativeLayout.getX() - oldX;
                            diffY = (int)rectangleRelativeLayout.getY() - oldY;
                        break;
                    case MotionEvent.ACTION_POINTER_UP:
                        break;
                    case MotionEvent.ACTION_POINTER_DOWN:
                        break;
                    case MotionEvent.ACTION_MOVE:
                            originX = (int)event.getRawX()+diffX;
                            originY = (int)event.getRawY()+diffY;
                            circleRelativeLayout.setTranslationX(originX);
                            circleRelativeLayout.setTranslationY(originY);
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


        final View rectangleView = new View(this);
        rectangleView.setBackgroundResource(R.drawable.rectangle);


        rectangleRelativeLayout  = new RelativeLayout(ImageEditorActivity.this);
        rectangleRelativeLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                rectangleViewScaleGestureDetector.onTouchEvent(event);
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                            oldX = (int) event.getRawX();
                            oldY = (int) event.getRawY();
                            diffX = (int) rectangleRelativeLayout.getX() - oldX;
                            diffY = (int)rectangleRelativeLayout.getY() - oldY;
                        break;
                    case MotionEvent.ACTION_POINTER_UP:
                        break;
                    case MotionEvent.ACTION_POINTER_DOWN:
                        break;
                    case MotionEvent.ACTION_MOVE:
                            originX = (int)event.getRawX()+diffX;
                            originY = (int)event.getRawY()+diffY;
                            rectangleRelativeLayout.setTranslationX(originX);
                            rectangleRelativeLayout.setTranslationY(originY);
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
