package com.example.parag.audetemi_android;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.view.View;

/**
 * Created by Parag on 9/30/15.
 */
public class MyImageView extends View {

    private float x,y;
    public Bitmap rotatedBitmap;

    public MyImageView(Context context,float x,float y) {
        super(context);
        this.x = x;
        this.y = y;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);

        Matrix matrix = new Matrix();
        matrix.postRotate(270);

        rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        canvas.drawBitmap(rotatedBitmap, (x - bitmap.getWidth()) / 2, (y - bitmap.getHeight()) / 2, new Paint());
    }
}
