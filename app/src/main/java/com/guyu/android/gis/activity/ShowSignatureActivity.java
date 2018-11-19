package com.guyu.android.gis.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;

import com.guyu.android.R;
import com.guyu.android.utils.BitmapUtils;

/**
 * Created by admin on 2017/9/20.
 */

public class ShowSignatureActivity extends Activity implements View.OnClickListener {
    private ImageView imgsignature;
    private String signatureString;
    private int index;
    private Bitmap myBitmap;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signature_show);
        findViewById(R.id.buttonCancle).setOnClickListener(this);
        imgsignature = (ImageView) findViewById(R.id.imgSignature);
        index = getIntent().getExtras().getInt("index");
        signatureString = getIntent().getExtras().getString("signatureString");
        try {
            myBitmap = BitmapUtils.stringtoBitmap(signatureString);
            imgsignature.setImageBitmap(myBitmap);
        } catch (Exception e) {

        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(myBitmap!=null&&!myBitmap.isRecycled()){
            myBitmap.recycle();
        }
    }
    @Override
    public void onClick(View view) {
        setResult();
    }
    private void setResult(){
        Intent intent = new Intent();
        intent.putExtra("index", index);
        intent.putExtra("signatureString", signatureString);
        setResult(index, intent);
        this.finish();
    }
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {//点击的是返回键
            if (event.getAction() == KeyEvent.ACTION_DOWN && event.getRepeatCount() == 0) {//按键的按下事件

            } else if (event.getAction() == KeyEvent.ACTION_UP && event.getRepeatCount() == 0) {//按键的抬起事件
                setResult();
            }
        }
        return super.dispatchKeyEvent(event);
    }
}
