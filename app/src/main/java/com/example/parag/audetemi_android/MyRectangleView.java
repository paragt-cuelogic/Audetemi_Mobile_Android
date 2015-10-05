package com.example.parag.audetemi_android;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

/**
 * Created by Parag on 9/28/15.
 */
public class MyRectangleView extends View {
    private int x,y;

    public MyRectangleView(Context context,int x,int y) {
        super(context);
        this.x = x;
        this.y = y;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Paint paint = new Paint();
        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(5);

        float imageStartX = (x/2 + (50 / 2)) + (500/2);
        float imageStartY = (y/2 + (50/2)) + (200/2);

        canvas.drawRect(imageStartX, imageStartY, 50, 50, paint);
    }
}
