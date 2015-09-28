package com.example.parag.audetemi_android;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends Activity implements View.OnClickListener, SurfaceHolder.Callback {

    private Button btn_gallery, btn_camera;
    private ToggleButton btn_flash;
    private InputStream inputStream;
    private BufferedInputStream buf;
    static android.hardware.Camera camera = null;

    private SurfaceView surfaceView;
    private SurfaceHolder surfaceHolder;

    private PictureCallback cameraCallback;

    private String imgDecodableString;
    long currenttime;
    private String PATH ="/sdcard/%d.jpg";


    private final String DATA_KEY = "data";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn_gallery = (Button) findViewById(R.id.gallery);
        btn_flash = (ToggleButton) findViewById(R.id.flash);
        btn_camera = (Button) findViewById(R.id.camera);
        btn_gallery.setOnClickListener(this);
        btn_flash.setOnClickListener(this);
        btn_camera.setOnClickListener(this);

        surfaceView = (SurfaceView) findViewById(R.id.surfaceView);
        surfaceHolder = surfaceView.getHolder();

        // Install a SurfaceHolder.Callback so we get notified when the
        // underlying surface is created and destroyed.
        surfaceHolder.addCallback(this);
        // deprecated setting, but required on Android versions prior to 3.0
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

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
                intent.putExtra(DATA_KEY, filepath);
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
        } catch (Exception e) {
            // ignore: tried to stop a non-existent preview
        }
        // make any resize, rotate or reformatting changes here

        if (this.getResources().getConfiguration().orientation != Configuration.ORIENTATION_LANDSCAPE) {

            camera.setDisplayOrientation(90);

        }
        else {

            camera.setDisplayOrientation(0);

        }
        // set preview size and make any resize, rotate or
        // reformatting changes here
        // start preview with new settings
        try {
            camera.setPreviewDisplay(surfaceHolder);
            camera.startPreview();
        } catch (Exception e) {

        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.gallery:
               loadImagefromGallery();
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


    private void launchGallery(String imgDecodableString) {
        Intent intent =new Intent(this,ImageEditorActivity.class);
        intent.putExtra(DATA_KEY, imgDecodableString);
      //  intent.putExtra("photo", imgDecodableString.getBytes(Charset.forName("UTF-8")));
        startActivity(intent);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        try {
            // open the camera
            camera = android.hardware.Camera.open();
            android.hardware.Camera.Parameters param = camera.getParameters();
            camera.setParameters(param);

            try {
                // The Surface has been created, now tell the camera where to draw
                // the preview.
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
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        refreshCamera();

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

        // stop preview and release camera
        camera.stopPreview();
        camera.release();
        camera = null;

    }
    public void loadImagefromGallery(){
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                                          android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, 1);
    }

        @Override
        protected void onActivityResult(int requestCode, int resultCode, Intent data) {
            try {
                super.onActivityResult(requestCode, resultCode, data);
                if (requestCode == 1 && resultCode == RESULT_OK && null != data) {
                    // Get the Image from data

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
                    Toast.makeText(this, R.string.picked_image,
                            Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {
                Toast.makeText(this, R.string.wrong, Toast.LENGTH_LONG)
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
}
