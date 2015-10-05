package com.example.parag.audetemi_android;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.View;

/**
 * Created by Parag on 9/30/15.
 */
public class MyCircleView extends View {

    int width,height;

    public MyCircleView(Context context,int width,int height) {
        super(context);
        this.width = width;
        this.height = height;
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        Paint paint = new Paint();
        paint.setColor(Color.GREEN);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(5);
        Log.d("MyCircleView", ""+width);
        Log.d("MyCircleView", ""+height);
        canvas.drawCircle(width, height , 50, paint);
    }
}
