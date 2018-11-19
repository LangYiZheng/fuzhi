package com.guyu.android.widget.selectimgs;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;

import com.guyu.android.R;
import com.guyu.android.widget.selectimgs.ImgsAdapter.OnItemClickClass;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;

public class ImgsActivity extends Activity {

    Bundle bundle;
    FileTraversal fileTraversal;
    GridView imgGridView;
    ImgsAdapter imgsAdapter;
    LinearLayout select_layout;
    Util util;
    RelativeLayout relativeLayout2;
    HashMap<Integer, ImageView> hashImage;
    Button choise_button;
    ArrayList<String> filelist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photogallery);

        imgGridView = (GridView) findViewById(R.id.gridView1);
        bundle = getIntent().getExtras();
        fileTraversal = bundle.getParcelable("data");
        imgsAdapter = new ImgsAdapter(this, fileTraversal.filecontent, onItemClickClass);
        imgGridView.setAdapter(imgsAdapter);
        select_layout = (LinearLayout) findViewById(R.id.selected_image_layout);
        relativeLayout2 = (RelativeLayout) findViewById(R.id.relativeLayout2);
        choise_button = (Button) findViewById(R.id.button3);
        hashImage = new HashMap<Integer, ImageView>();
        filelist = new ArrayList<String>();
//		imgGridView.setOnItemClickListener(this);
        util = new Util(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (imgsAdapter != null) {
            Bitmap[] bitmaps = imgsAdapter.getBitmaps();
            for (Bitmap b : bitmaps) {
                if (b!=null&&!b.isRecycled()) {
                    b.recycle();
                    b = null;
                }
            }
        }

    }

    class BottomImgIcon implements OnItemClickListener {

        int index;

        public BottomImgIcon(int index) {
            this.index = index;
        }

        @Override
        public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                long arg3) {

        }
    }

    @SuppressLint("NewApi")
    public ImageView iconImage(String filepath, int index, CheckBox checkBox) throws FileNotFoundException {
        LinearLayout.LayoutParams params = new LayoutParams(relativeLayout2.getMeasuredHeight() - 10, relativeLayout2.getMeasuredHeight() - 10);
        ImageView imageView = new ImageView(this);
        imageView.setLayoutParams(params);
        imageView.setBackgroundResource(R.drawable.imgbg);
        float alpha = 1;
        imageView.setAlpha(alpha);
        util.imgExcute(imageView, imgCallBack, filepath);
        imageView.setOnClickListener(new ImgOnclick(filepath, checkBox));
        return imageView;
    }

    ImgCallBack imgCallBack = new ImgCallBack() {
        @Override
        public void resultImgCall(ImageView imageView, Bitmap bitmap) {
            imageView.setImageBitmap(bitmap);
        }
    };

    class ImgOnclick implements OnClickListener {
        String filepath;
        CheckBox checkBox;

        public ImgOnclick(String filepath, CheckBox checkBox) {
            this.filepath = filepath;
            this.checkBox = checkBox;
        }

        @Override
        public void onClick(View arg0) {
            checkBox.setChecked(false);
            select_layout.removeView(arg0);
            choise_button.setText("已选择(" + select_layout.getChildCount() + ")张");
            filelist.remove(filepath);
        }
    }


    ImgsAdapter.OnItemClickClass onItemClickClass = new OnItemClickClass() {
        @Override
        public void OnItemClick(View v, int Position, CheckBox checkBox) {
            String filapath = fileTraversal.filecontent.get(Position);
            if (checkBox.isChecked()) {
                checkBox.setChecked(false);
                select_layout.removeView(hashImage.get(Position));
                filelist.remove(filapath);
                choise_button.setText("已选择(" + select_layout.getChildCount() + ")张");
            } else {
                try {
                    checkBox.setChecked(true);
                    Log.i("img", "img choise position->" + Position);
                    ImageView imageView = iconImage(filapath, Position, checkBox);
                    if (imageView != null) {
                        hashImage.put(Position, imageView);
                        filelist.add(filapath);
                        select_layout.addView(imageView);
                        choise_button.setText("已选择(" + select_layout.getChildCount() + ")张");
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    public void tobreak(View view) {
        finish();
    }

    /**
     * FIXME
     * 亲只需要在这个方法把选中的文档目录已list的形式传过去即可
     *
     * @param view
     */
    public void sendfiles(View view) {
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putStringArrayList("files", filelist);
        intent.putExtras(bundle);
        setResult(-1, intent);
        finish();
        return;

    }
}