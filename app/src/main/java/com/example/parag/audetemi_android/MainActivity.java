package com.example.parag.audetemi_android;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.media.CamcorderProfile;
import android.media.MediaMetadataRetriever;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends Activity implements View.OnClickListener, SurfaceHolder.Callback {

    private Button btn_gallery,btn_camera;
    private ToggleButton btn_flash,btn_video;
    private InputStream inputStream;
    private BufferedInputStream buf;
    static android.hardware.Camera camera = null;
    private Bitmap bitmap;

    private LinearLayout linearLayout;
    private SurfaceView surfaceView;
    private SurfaceHolder surfaceHolder;
    private HorizontalScrollView horizontalScrollView;
    private PictureCallback cameraCallback;
    private MediaRecorder recorder;

    private String imgDecodableString;
    long currenttime;
    private String PATH ="/sdcard/%d.jpg";
    private String mediaFilePath;

    private final String DATA_KEY_GALLERY = "data_gallery";
    private final String DATA_KEY_CAPTURE = "data_capture";
    ArrayList<Bitmap> frameList;


    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn_gallery = (Button) findViewById(R.id.gallery);
        btn_video = (ToggleButton) findViewById(R.id.video);
        btn_flash = (ToggleButton) findViewById(R.id.flash);
        btn_camera = (Button) findViewById(R.id.camera);

        btn_gallery.setOnClickListener(this);
        btn_video.setOnClickListener(this);
        btn_flash.setOnClickListener(this);
        btn_camera.setOnClickListener(this);

        surfaceView = (SurfaceView) findViewById(R.id.surfaceView);
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(this);
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        //imageView = (ImageView) findViewById(R.id.imagView);
        horizontalScrollView = (HorizontalScrollView) findViewById(R.id.horizontalScrollview);
        linearLayout = (LinearLayout) findViewById(R.id.linearlayout);

        // Install a SurfaceHolder.Callback so we get notified when the
        // underlying surface is created and destroyed.

        cameraCallback = new PictureCallback() {
            public void onPictureTaken(byte[] data, android.hardware.Camera camera) {

                currenttime=System.currentTimeMillis();
                String filepath = null;

                filepath ="/sdcard/"+currenttime+".jpg";

                try {
                    FileOutputStream fileOutStream = new FileOutputStream(String.format(PATH, currenttime));
                    fileOutStream.write(data);
                    fileOutStream.close();
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                }

                imgDecodableString = new String(data);

                Intent intent =new Intent(MainActivity.this,ImageEditorActivity.class);
                intent.putExtra(DATA_KEY_CAPTURE, filepath);
                intent.putExtra(DATA_KEY_GALLERY, "");
                startActivity(intent);

                btn_camera.setClickable(true);

            }
        };
    }

    public void captureImage() throws IOException {
        //take the
        setFlashButtonState();
        camera.takePicture(null, null, cameraCallback);
        btn_camera.setClickable(false);
    }

    public void refreshCamera() {

        if (surfaceHolder.getSurface() == null) {
            // preview surface does not exist
            return;
        }
        // stop preview before making changes
        try {
            camera.stopPreview();

            camera.setPreviewDisplay(surfaceHolder);
            camera.startPreview();
        } catch (Exception e) {
            // ignore: tried to stop a non-existent preview
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.gallery:
                loadImagefromGallery();
                break;
            case R.id.video:
                if (btn_video.isChecked()) {
                    Log.i("TAG", "isChecked");
                    initMyRecorder();
                } else {
                    Log.i("TAG", "isunChecked");
                    releaseMediaRecorder();
                    convertToFrame();
                }
                break;
            case R.id.flash:
                setFlashButtonState();
                break;
            case R.id.camera:
                try {
                    captureImage();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;

            default:
                break;
        }

    }

    /**
     * Initialize video recorder to record video
     */
    private void initMyRecorder() {
        try {
            recorder = new MediaRecorder();
            camera.stopPreview();
            camera.unlock();
            recorder.setCamera(camera);

            // Step 2: Set sources
            recorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
            recorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);

            // Step 3: Set a CamcorderProfile (requires API Level 8 or higher)
            recorder.setProfile(CamcorderProfile
                    .get(CamcorderProfile.QUALITY_HIGH));

            // Step 4: Set output file
            recorder.setOutputFile(String.valueOf(getOutputMediaFile(MEDIA_TYPE_VIDEO)));
            // Step 5: Set the preview output
            recorder.setPreviewDisplay(surfaceView.getHolder().getSurface());
            // Step 6: Prepare configured MediaRecorder
            recorder.prepare();
            recorder.start();
        } catch (Exception e) {
            Log.e("Error Stating Camera", e.getMessage());
        }
    }

    private void releaseMediaRecorder() {
        if (recorder != null) {
            recorder.reset();
            recorder.release();
            recorder = null;
        }
    }

    private void launchGallery(String imgDecodableString) {
        Intent intent =new Intent(this,ImageEditorActivity.class);
        intent.putExtra(DATA_KEY_GALLERY, imgDecodableString);
        intent.putExtra(DATA_KEY_CAPTURE, "");
        startActivity(intent);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        try {
            // open the camera
            camera = android.hardware.Camera.open();
            try {
                // The Surface has been created, now tell the camera where to draw
                // the preview.
                camera.setDisplayOrientation(90);
                camera.setPreviewDisplay(surfaceHolder);
                camera.startPreview();
            } catch (Exception e) {
                // check for exceptions
                System.err.println(e);
                return;
            }

        } catch (RuntimeException e) {
            // check for exceptions
            System.err.println(e);
            return;
        }

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {}

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

        // stop preview and release camera
        if(camera !=null) {
            camera.stopPreview();
            camera.release();
            camera = null;
        }
    }
    public void loadImagefromGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent,1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i("onActivityResult", "In try");
        try {
            Log.i("onActivityResult", "In try");
            Toast.makeText(this, "In try",
                    Toast.LENGTH_LONG).show();
            super.onActivityResult(requestCode, resultCode, data);
            if (requestCode == 1 && resultCode == RESULT_OK && null != data) {
                // Get the Image from data
                Log.i("onActivityResult","In if loop");
                Uri selectedImage = data.getData();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};

                // Get the cursor
                Cursor cursor = getContentResolver().query(selectedImage,
                        filePathColumn, null, null, null);
                // Move to first row
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                imgDecodableString = cursor.getString(columnIndex);
                cursor.close();
                launchGallery(imgDecodableString);
            }
            else {
                Toast.makeText(this, "Image not picked",
                        Toast.LENGTH_LONG).show();
                Log.i("else onActivityResult", "" + "");
            }
        } catch (Exception e) {
            Toast.makeText(this, "Wrong", Toast.LENGTH_LONG)
                    .show();

        }
    }

    private void setFlashButtonState() {
        try {
            if(getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)) {
                Camera.Parameters p = camera.getParameters();

                if (btn_flash.isChecked()) {
                    p.setFlashMode(Camera.Parameters.FLASH_MODE_ON);
                    camera.setParameters(p);
                    camera.startPreview();

                } else {
                    p.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                    camera.setParameters(p);
                    camera.startPreview();
                }
            } else {
                if(btn_flash.isChecked()) {
                    Toast.makeText(this, R.string.flash_not_supported, Toast.LENGTH_LONG).show();
                }
            }
        }catch(Exception e) {

        }
    }




    /** Create a File for saving an image or video */
    private  File getOutputMediaFile(int type){
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "MyCameraApp");
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                Log.d("MyCameraApp", "failed to create directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE){
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "IMG_"+ timeStamp + ".jpg");
        } else if(type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "VID_"+ timeStamp + ".mp4");

            mediaFilePath = mediaFile.getPath();
        } else {
            return null;
        }

        return mediaFile;
    }

    private void convertToFrame() {
        MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
        mediaMetadataRetriever.setDataSource(mediaFilePath);

        int duration = Integer.parseInt(mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION))/1000;
        Log.i("Duration","duration "+duration);

        frameList = new ArrayList<Bitmap>();
        frameList.clear();

        if(linearLayout != null) {
            linearLayout.removeAllViews();
        }

        for (int i = 0; i < duration; i++) {
//            Bitmap bArray = mediaMetadataRetriever.getFrameAtTime(1000000 * i, MediaMetadataRetriever.OPTION_CLOSEST);
            Matrix matrix = new Matrix();

            matrix.postRotate(90);
            Bitmap bitmap = mediaMetadataRetriever.getFrameAtTime(i * 1000000);
            Bitmap rotatedBitmap = Bitmap.createBitmap(bitmap , 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            frameList.add(rotatedBitmap);
        }

        for (int j = 0; j < frameList.size(); j++) {
            ImageView imageView = new ImageView(this);
            LinearLayout.LayoutParams vp = new LinearLayout.LayoutParams(200, 200);
            imageView.setLayoutParams(vp);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setMaxHeight(150);
            imageView.setMaxWidth(150);
            imageView.setImageBitmap(frameList.get(j));
            linearLayout.addView(imageView);
        }
    }
}
