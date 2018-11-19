package com.guyu.android.gis.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.print.PrintAttributes;
import android.print.PrintManager;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;

import com.guyu.android.R;
import com.guyu.android.gis.adapter.MyPrintAdapter;
import com.guyu.android.gis.app.GisQueryApplication;
import com.guyu.android.utils.ImageUtil;
import com.guyu.android.utils.Line;
import com.guyu.android.view.ClipImageView;
import com.itextpdf.text.Document;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;

/**
 * Created by YunHuan on 2017/11/15.
 */

public class ClipActivity extends Activity {

    public static com.itextpdf.text.Rectangle A4 = PageSize.A4;
    private ClipImageView imageView;
    private Line line;
    private Bitmap bitmap;
    private static ClipActivity clipActivity;

    public static ClipActivity getInstance() {
        return clipActivity;
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.line_layout);
        clipActivity = this;
        imageView = (ClipImageView) findViewById(R.id.imageView);
        bitmap = MainActivity.getInstance().getScreenBitmap();

        imageView.setImageBitmap(bitmap);
    }




    public void sure(View view) {
        line = imageView.getClipLine();
        Log.i("结果", line.getLeft() + " " + line.getTop() + " " + line.getRight() + " " + line.getBottom());
        bitmap = bitmap.createBitmap(bitmap, (int) line.getLeft(), (int) line.getTop(), (int) line.getRight(), (int) line.getBottom());//截取图片
        bitmap = ImageUtil.drawTextToRightBottom(this, bitmap, "英德市", 28, Color.WHITE, 50, 50);
//        Constants.saveBitmap(bitmap);//保存截取图片
        String path = GisQueryApplication.currentProjectPath + "/print/" + System.currentTimeMillis() + ".pdf";
        saveBitmap(path, bitmap);

//        setResult(RESULT_OK);
//        finish();
    }


    /**
     * 获取图片
     *
     * @return
     */
    public Bitmap getBitmap() {
        return bitmap;
    }

    public void cancel(View view) {
        bitmap = null;
        setResult(RESULT_CANCELED);
        finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            bitmap = null;
            setResult(RESULT_CANCELED);
            finish();
            return false;
        } else {
            return super.onKeyDown(keyCode, event);
        }

    }

    private void onPrintPdf(String path) {
        PrintManager printManager = (PrintManager) getSystemService(Context.PRINT_SERVICE);
        PrintAttributes.Builder builder = new PrintAttributes.Builder();
        builder.setColorMode(PrintAttributes.COLOR_MODE_COLOR);
        printManager.print("test pdf print", new MyPrintAdapter(this, path), builder.build());
    }

    private File saveBitmap(final String path, final Bitmap bitmap) {

        File localFile = new File(path);
        if (!localFile.getParentFile().exists())
            localFile.getParentFile().mkdirs();

        Document localDocument = new Document(A4);
        localDocument.setMargins(0.0F, 0.0F, 0.0F, 0.0F);
        PdfWriter localPdfWriter = null;
        while (true) {
            Image localImage = null;
            try {
                localPdfWriter = PdfWriter.getInstance(localDocument,
                        new FileOutputStream(localFile));
                localPdfWriter.open();
                localDocument.open();
                localImage = Image.getInstance(Bitmap2Bytes(bitmap));
                float f1 = localImage.getHeight() / localImage.getWidth();
                float f2 = A4.getHeight() / A4.getWidth();


                localImage.scaleToFit(A4.getWidth(), A4.getHeight());
                localDocument.add(localImage);
                onPrintPdf(path);
                return localFile;

				/*if (f1 == f2) {
                    localImage.scaleToFit(A4.getWidth(), A4.getHeight());
					localDocument.add(localImage);
					return localFile;
				}
				if (f1 < f2) {
					localImage.scaleToFit(A4.getWidth(), A4.getWidth()
							/ localImage.getWidth() * localImage.getHeight());
					continue;
				}*/
            } catch (Exception localException) {
            } finally {
                try {
                    if (localPdfWriter != null)
                        localPdfWriter.close();
                    if (localDocument != null)
                        localDocument.close();
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }
            localImage.scaleToFit(A4.getHeight() / localImage.getHeight()
                    * localImage.getWidth(), A4.getHeight());
        }

    }

    private byte[] Bitmap2Bytes(Bitmap bm) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();) {
            bm.compress(Bitmap.CompressFormat.JPEG, 50, baos);
            return baos.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }


    }
}