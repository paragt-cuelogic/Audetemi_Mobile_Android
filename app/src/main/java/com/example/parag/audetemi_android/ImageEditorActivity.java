package com.example.parag.audetemi_android;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
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
import android.widget.Toast;

import com.soundcloud.android.crop.Crop;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;

import jp.co.cyberagent.android.gpuimage.GPUImage;
import jp.co.cyberagent.android.gpuimage.GPUImageBrightnessFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageContrastFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageGaussianBlurFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageSaturationFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageSepiaFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageVignetteFilter;

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
    private GPUImageVignetteFilter gpuImageVignettFilter;
    private GPUImageSepiaFilter gpuImageSepiaFilter;
    private GPUImageGaussianBlurFilter gpuImageGaussianBlurFilter;

    private float scale = 1f;
    private int oldX,oldY,originX,originY,diffX,diffY;
    final int CROP_PIC = 2;

    private Bitmap rotatedBitmap;
    private Bitmap mutableBitmap;


    private Button btn_circle, btn_rectangle, btn_pointer, btn_freehand, btn_crop;
    private CheckBox checkbox_brightness,checkbox_contrast,checkbox_saturation;

    private File file;
    private Uri uri;
    private String photoPath;
    private Matrix matrix = new Matrix();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        // Get the data from bundle object.
        Bundle extras = getIntent().getExtras();

        if(extras.getString("data_capture").equals("")) {
            // Gallery
            photoPath = extras.getString("data_gallery");
        } else {
            // Camera
            photoPath = extras.getString("data_capture");
        }

        initView();

        // Get Display height and width.
        Display display = getWindowManager().getDefaultDisplay();
        final int displayWidth = display.getWidth();
        final int displayHeight = display.getHeight();

        // To reduce the bitmap size.
        BitmapFactory.Options options = new BitmapFactory.Options();
        file = new File(photoPath);
        options.inJustDecodeBounds = true;
        uri = Uri.fromFile(file);
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

        if(extras.getString("data_gallery").equals("")) {
            // Gallery
            matrix.postRotate(90);
        }

        rotatedBitmap = Bitmap.createBitmap(mutableBitmap , 0, 0, mutableBitmap.getWidth(), mutableBitmap.getHeight(), matrix, true);

        mGPUImage.setImage(rotatedBitmap);

        getUri();
        initFilter();
        initGesture();
        handleSeekbar();

        btn_circle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (circleRelativeLayout == null) {
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
        btn_crop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(uri != null) {
                    //performCrop(uri);
                    beginCrop(uri);
                }
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
                matrix.setScale(scale, scale);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == CROP_PIC && data!=null) {
//            Bundle extras = data.getExtras();
//            if(extras != null) {
//                Bitmap thePic = extras.getParcelable("data");
//                if(thePic != null) {
//                    mGPUImage.deleteImage();
//                    mGPUImage.setImage(thePic);
//                    rotatedBitmap =thePic;
//                    getUri();
//                }
//            }
//
//        }
        if (requestCode == Crop.REQUEST_PICK && resultCode == RESULT_OK) {
            beginCrop(data.getData());
        } else if (requestCode == Crop.REQUEST_CROP) {
            handleCrop(resultCode, data);
//            Bundle extras = data.getExtras();
//            if(extras != null) {
//                Bitmap thePic = extras.getParcelable("data");
//                Log.i("thePic"," "+thePic);
//                if(thePic != null) {
//                    mGPUImage.deleteImage();
//                    mGPUImage.setImage(thePic);
//                    rotatedBitmap =thePic;
//                    getUri();
//                    Log.i("ifthePic", " " + thePic);
//                }
//
//            }
        }
    }

    private void initView(){

        // Get the RelativeLayout from XML to add the shape elements dynamically.
        relativeLayout = (RelativeLayout) findViewById(R.id.relativelayout);
        mGPUSurfaceview = (GLSurfaceView) findViewById(R.id.surfaceView);
        btn_circle = (Button) findViewById(R.id.circle);
        btn_rectangle = (Button) findViewById(R.id.rectangle);
        btn_pointer = (Button) findViewById(R.id.poiter);
        btn_freehand = (Button) findViewById(R.id.freehand);
        btn_crop = (Button) findViewById(R.id.crop);
        checkbox_brightness = (CheckBox) findViewById(R.id.brightness);
        checkbox_contrast = (CheckBox) findViewById(R.id.contrast);
        checkbox_saturation = (CheckBox) findViewById(R.id.saturation);

    }

    public void initFilter(){

        gpuImageBrightnessFilter = new GPUImageBrightnessFilter();
        gpuImageContrastFilter = new GPUImageContrastFilter();
        gpuImageSaturationFilter = new GPUImageSaturationFilter();
        gpuImageVignettFilter = new GPUImageVignetteFilter();
        gpuImageSepiaFilter = new GPUImageSepiaFilter();
        gpuImageGaussianBlurFilter = new GPUImageGaussianBlurFilter();

    }
    private void initGesture(){
        circleViewScaleGestureDetector =
                new ScaleGestureDetector(ImageEditorActivity.this,
                        new CircleViewScaleGestureListener());

        rectangleViewScaleGestureDetector =
                new ScaleGestureDetector(ImageEditorActivity.this,
                        new RectangleViewScaleGestureListener());
    }

    private void getUri(){
        file = new File(this.getExternalCacheDir(), file.getName()+System.currentTimeMillis());
        try {
            file.createNewFile();
            Bitmap actualBitmap = rotatedBitmap;
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            actualBitmap.compress(Bitmap.CompressFormat.PNG, 0 , bos);
            byte[] bitmapdata = bos.toByteArray();

            //write the bytes in file
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(bitmapdata);
            fos.flush();
            fos.close();
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        uri = Uri.fromFile(file);

    }


    private void performCrop(Uri uri) {
        try {
            if(uri != null) {
                Intent cropIntent = new Intent("com.android.camera.action.CROP");
                // <span id="IL_AD5" class="IL_AD">indicate</span> image type and Uri
                cropIntent.setDataAndType(uri, "image/*");
                // set crop properties
                cropIntent.putExtra("crop", "true");
                // indicate aspect of desired crop
                cropIntent.putExtra("aspectX", 1);
                cropIntent.putExtra("aspectY", 1);
                // indicate output X and Y
                cropIntent.putExtra("outputX", 256);
                cropIntent.putExtra("outputY", 256);
                // retrieve data on return
                cropIntent.putExtra("return-data", true);
                // start <span id="IL_AD6" class="IL_AD">the activity</span> - we handle returning in onActivityResult
                startActivityForResult(cropIntent, CROP_PIC);

            }
        }
        catch (ActivityNotFoundException ae){
            Toast toast = Toast
                    .makeText(ImageEditorActivity.this, "This device doesn't support the crop action!", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    private void beginCrop(Uri source) {
        Uri destination = Uri.fromFile(new File(getCacheDir(), "cropped"));
        Crop.of(source, destination).asSquare().start(this);
    }

    private void handleCrop(int resultCode, Intent result) {
        if (resultCode == RESULT_OK) {
            mGPUImage.setImage(Crop.getOutput(result));
            getUri();
//            if(result!= null){
//                startActivityForResult(result,Crop.REQUEST_CROP);
//            }
            ;
           // resultView.setImageURI(Crop.getOutput(result));

        } else if (resultCode == Crop.RESULT_ERROR) {
            Toast.makeText(this, Crop.getError(result).getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void handleSeekbar(){

        final SeekBar seekBar_contrast = (SeekBar) findViewById(R.id.seekBar);

        seekBar_contrast.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                float value = 0.1f * progress;
                if(checkbox_brightness.isChecked()) {
                    mGPUImage.setFilter(gpuImageBrightnessFilter);
                    gpuImageBrightnessFilter.setBrightness(value);
                    mGPUImage.setImage(mutableBitmap);

//                    mGPUImage.setFilter(gpuImageVignettFilter);
//                    gpuImageVignettFilter.setVignetteStart(value - 0.2f);
//                    gpuImageVignettFilter.setVignetteEnd(value);
//                    gpuImageVignettFilter.setVignetteCenter(new PointF(0.5f, 0.5f));
//                    mGPUImage.setImage(mutableBitmap);

//                    mGPUImage.setFilter(gpuImageSepiaFilter);
//                    gpuImageSepiaFilter.setIntensity(value);
//                    mGPUImage.setImage(mutableBitmap);
//                    mGPUImage.setFilter(gpuImageGaussianBlurFilter);
//                    gpuImageGaussianBlurFilter.setBlurSize(value);
//                    mGPUImage.setImage(mutableBitmap);
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
                            diffX = (int) circleRelativeLayout.getX() - oldX;
                            diffY = (int) circleRelativeLayout.getY() - oldY;
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
                            diffY = (int) rectangleRelativeLayout.getY() - oldY;
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