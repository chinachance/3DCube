package com.haohao.framwork.a3dcube.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

/**
 * Created by Ma1 on 2017/4/28.
 */

public class EmptyView extends View {
    public EmptyView(Context context) {
        super(context);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = 400;
        int height = 400;
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Paint p = new Paint();
        p.setColor(Color.WHITE);
        p.setStrokeWidth(3);
        float[] pts = new float[]{0,0,400,0,0,0,0,400,400,0,400,400,0,400,400,400};
        canvas.drawLines(pts,p);
    }
}
