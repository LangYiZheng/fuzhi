package com.guyu.android.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.widget.ProgressBar;

import com.guyu.android.R;
import com.guyu.android.utils.MeasureUtil;

/**
 * Created by YunHuan on 2017/11/8.
 */

public class MyProgress extends ProgressBar {
    private String text;
    private Paint mPaint;
    private Context context;
    private Rect rect;
    private int x;
    private int y;
    public MyProgress(Context context) {
        super(context);
        this.context = context;
        initText();
    }

    public MyProgress(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
        initText();
    }


    public MyProgress(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        initText();
    }

    @Override
    public synchronized void setProgress(int progress) {

        setText(progress);
        super.setProgress(progress);

    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {

        super.onDraw(canvas);
        //this.setText();

        this.mPaint.getTextBounds(this.text, 0, this.text.length(), rect);
        x = (getWidth() / 2) - rect.centerX();
        y = (getHeight() / 2) - rect.centerY();
        canvas.drawText(this.text, x, y, this.mPaint);
    }

    //初始化，画笔
    private void initText() {
        this.mPaint = new Paint();
        rect = new Rect();
        this.mPaint.setColor(ContextCompat.getColor(context, R.color.near_white));
        this.mPaint.setTextSize(MeasureUtil.sp2px(context, 14));
        this.mPaint.setAntiAlias(true);
    }

    private void setText() {
        setText(this.getProgress());
    }

    //设置文字内容
    private void setText(int progress) {
//        long max = this.getMax();
//        max *= 100;
//        long p = this.getProgress();
//        p *= 100;

        this.text = String.valueOf(this.getProgress() >> 16) + "/" + String.valueOf(this.getMax() >> 16) + "MB";
    }


}