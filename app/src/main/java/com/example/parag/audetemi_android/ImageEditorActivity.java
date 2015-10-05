package com.example.parag.audetemi_android;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.os.Bundle;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

/**
 * Created by Parag on 9/24/15.
 */
public class ImageEditorActivity extends Activity {
    private int _xDelta;
    private int _yDelta;
    private ViewGroup.LayoutParams layoutParams;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        Bundle extras = getIntent().getExtras();

        String photoPath = extras.getString("data");

       // ImageView imageView = (ImageView)findViewById(R.id.imgView);
        final RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.relativelayout);
        View v = findViewById(R.id.circle);
      //  View view_circle = new View(this);
       // Drawable drawable = Integer.parseInt(String.valueOf(R.drawable.circle));
      //  view_circle.setBackground((Drawable)R.drawable.circle);
       // relativeLayout.setBackgroundColor(Color.BLACK);
        final TextView textView = (TextView) findViewById(R.id.text);


        Display display = getWindowManager().getDefaultDisplay();


        final int displayWidth = display.getWidth();
        final int displayHeight = display.getHeight();

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

//        int cropx = 10;
//        int cropy = 20;
//        int cropw = 30;
//        int croph = 40;
//        final Bitmap croppedBitmap = Bitmap.createBitmap(scaled, cropx, cropy, cropw, croph);

          // TODO: Add the rectangle into the center of the screen.

          final Bitmap mutableBitmap = scaledBitmap.copy(Bitmap.Config.ARGB_8888, true);
          final int mutableBitmap_width = mutableBitmap.getWidth();
          final int mutableBitmap_height = mutableBitmap.getHeight();
          //View v = new MyRectangleView(getApplicationContext(),mutableBitmap.getWidth()/2 ,mutableBitmap.getHeight()/2);
//          Canvas canvas = new Canvas(mutableBitmap);
//          canvas.drawBitmap(mutableBitmap, new Matrix(), null);


          //View v = new MyCircleView(getApplicationContext(),mutableBitmap.getWidth()/2 ,mutableBitmap.getHeight()/2);
       // View v = new MyImageView(getApplicationContext(),mutableBitmap.getWidth() ,mutableBitmap.getHeight());

        final View view =new View(getApplicationContext());
        Bitmap bitmapTodraw = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);

        ImageView imageView = new ImageView(ImageEditorActivity.this);

        Matrix matrix = new Matrix();
        matrix.postRotate(270);

        Bitmap rotatedBitmap = Bitmap.createBitmap(bitmapTodraw, 0, 0, bitmapTodraw.getWidth(), bitmapTodraw.getHeight(), matrix, true);
       // canvas.drawBitmap(rotatedBitmap, (mutableBitmap.getWidth() - bitmapTodraw.getWidth()) / 2, (mutableBitmap.getHeight() - bitmapTodraw.getHeight()) / 2, new Paint());
        final RelativeLayout relative  = new RelativeLayout(ImageEditorActivity.this);
        RelativeLayout.LayoutParams position = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        relative.setX(mutableBitmap_width / 2);
        relative.setY(mutableBitmap_height / 2);
        relative.setBackgroundColor(Color.RED);

        Toast.makeText(this,"width"+mutableBitmap.getWidth()+"Height"+mutableBitmap.getHeight(),Toast.LENGTH_SHORT).show();

       // position.addRule(RelativeLayout.CENTER_IN_PARENT);
        final ImageView image = new ImageView(ImageEditorActivity.this);
        image.setLayoutParams(new ViewGroup.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        image.setImageResource(R.drawable.ic_launcher);

        image.setLayoutParams(position);
        //image.setScaleType(ImageView.ScaleType.CENTER_CROP);
        int widthTodraw = (mutableBitmap.getWidth() - bitmapTodraw.getWidth()) / 2;
        int heightTodraw = (mutableBitmap.getHeight() - bitmapTodraw.getHeight()) / 2;
        relative.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int X = (int) event.getRawX();
                int Y = (int) event.getRawY();

                System.out.println("X" + X + "Y" + Y);
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        break;
                    case MotionEvent.ACTION_POINTER_UP:
                        break;
                    case MotionEvent.ACTION_POINTER_DOWN:
                        break;
                    case MotionEvent.ACTION_MOVE:
                        if (X > mutableBitmap_width) {
                            X = mutableBitmap_width;
                        }
                        if (Y > mutableBitmap_height) {
                            Y = mutableBitmap_height;
                        }
                        relative.setX(X);
                        relative.setY(Y);

                        break;

                }
                // relative.invalidate();
                return true;
            }
        });

//          v.draw(canvas);



        imageView.setRotation(90);
        imageView.setImageBitmap(mutableBitmap);
        relativeLayout.addView(imageView);
        relative.addView(image);
        relativeLayout.addView(relative);
       // setContentView(linearlayout);

    }
}
