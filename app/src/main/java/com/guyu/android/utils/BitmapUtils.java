package com.guyu.android.utils;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.util.Base64;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class BitmapUtils {
    private static final String TAG = "BitmapUntil---------------";

    public static void Base64StringToPic(String paramString1,
                                         String paramString2) {
        try {
            byte[] arrayOfByte = Base64.decode(paramString1, 0);
            Bitmap localBitmap = BitmapFactory.decodeByteArray(arrayOfByte, 0,
                    arrayOfByte.length);
            FileOutputStream localFileOutputStream = new FileOutputStream(
                    paramString2);
            localBitmap.compress(Bitmap.CompressFormat.JPEG, 100,
                    localFileOutputStream);
            localFileOutputStream.close();
            return;
        } catch (Exception localException) {
            Log.v("BitmapUntil-----", localException.getMessage());
        }
    }

    public static String PicToBase64String(String paramString, int paramInt) {
        try {
            BitmapFactory.Options localOptions = new BitmapFactory.Options();
            localOptions.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(paramString, localOptions);
            localOptions.inJustDecodeBounds = false;
            int i = (int) (localOptions.outHeight / 800.0F);
            if (i <= 0)
                i = 4;
            localOptions.inSampleSize = i;
            Bitmap localBitmap = BitmapFactory.decodeFile(paramString,
                    localOptions);
            ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
            String localObject = null;
            if (localBitmap != null) {
                localBitmap.compress(Bitmap.CompressFormat.JPEG, paramInt,
                        localByteArrayOutputStream);
                String str = Base64.encodeToString(
                        localByteArrayOutputStream.toByteArray(), 0);
                localObject = str;
            }
            return localObject;
        } catch (Exception localException) {
            Log.e("changchun", "图片转换失败--图片太大");
        }
        return null;
    }

    public static Bitmap getBitmap(String paramString) {
        BitmapFactory.Options localOptions = new BitmapFactory.Options();
        try {
            localOptions.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(paramString, localOptions);
            localOptions.inJustDecodeBounds = false;
            int i = (int) (localOptions.outHeight / 800.0F);
            if (i <= 0)
                i = 2;
            localOptions.inSampleSize = i;
            Bitmap localBitmap = BitmapFactory.decodeFile(paramString,
                    localOptions);
            return localBitmap;
        } catch (Exception localException) {
            Log.e("changchun", "图片解析错误：" + localException.toString());
            localOptions.inSampleSize = 5;
        }
        return BitmapFactory.decodeFile(paramString, localOptions);
    }

    public static Bitmap getBitmapByPath(String paramString, int paramInt1,
                                         int paramInt2) throws FileNotFoundException {
        File localFile = new File(paramString);
        if (!(localFile.exists()))
            throw new FileNotFoundException();
        FileInputStream localFileInputStream = new FileInputStream(localFile);
        BitmapFactory.Options localOptions = getOptions(paramString);
        int i = 0;
        if (localOptions != null) {
            if (paramInt1 <= paramInt2)
                i = paramInt2;
            else
                i = paramInt1;
        }
        Bitmap localBitmap;
        localOptions.inSampleSize = computeSampleSize(localOptions, i,
                paramInt1 * paramInt2);
        localOptions.inJustDecodeBounds = false;
        localBitmap = BitmapFactory.decodeStream(localFileInputStream,
                null, localOptions);
        try {
            localFileInputStream.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return localBitmap;

    }

    public static byte[] getBytes(InputStream paramInputStream)
            throws IOException {
        ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
        byte[] arrayOfByte = new byte[2048];
        while (true) {
            int i = paramInputStream.read(arrayOfByte, 0, 2048);
            if (i == -1) {
                localByteArrayOutputStream.flush();
                return localByteArrayOutputStream.toByteArray();
            }
            localByteArrayOutputStream.write(arrayOfByte, 0, i);
        }
    }

    public static BitmapFactory.Options getOptions(String paramString) {
        BitmapFactory.Options localOptions = new BitmapFactory.Options();
        localOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(paramString, localOptions);
        return localOptions;
    }

    public static String getPicBase64String(String paramString) {
        byte[] arrayOfByte = null;
        FileInputStream localFileInputStream = null;
        try {
            localFileInputStream = new FileInputStream(paramString);
            arrayOfByte = new byte[localFileInputStream.available()];
            localFileInputStream.read(arrayOfByte);
            localFileInputStream.close();
            return Base64.encodeToString(arrayOfByte, 0);
        } catch (Exception localException2) {
            arrayOfByte = null;
        }
        return null;
    }

    /**
     * compute Sample Size
     *
     * @param options
     * @param minSideLength
     * @param maxNumOfPixels
     * @return
     */
    public static int computeSampleSize(BitmapFactory.Options options,
                                        int minSideLength, int maxNumOfPixels) {
        int initialSize = computeInitialSampleSize(options, minSideLength,
                maxNumOfPixels);

        int roundedSize;
        if (initialSize <= 8) {
            roundedSize = 1;
            while (roundedSize < initialSize) {
                roundedSize <<= 1;
            }
        } else {
            roundedSize = (initialSize + 7) / 8 * 8;
        }

        return roundedSize;
    }

    /**
     * compute Initial Sample Size
     *
     * @param options
     * @param minSideLength
     * @param maxNumOfPixels
     * @return
     */
    private static int computeInitialSampleSize(BitmapFactory.Options options,
                                                int minSideLength, int maxNumOfPixels) {
        double w = options.outWidth;
        double h = options.outHeight;

        int lowerBound = (maxNumOfPixels == -1) ? 1 : (int) Math.ceil(Math
                .sqrt(w * h / maxNumOfPixels));
        int upperBound = (minSideLength == -1) ? 128 : (int) Math.min(
                Math.floor(w / minSideLength), Math.floor(h / minSideLength));

        if (upperBound < lowerBound) {
            // return the larger one when there is no overlapping zone.
            return lowerBound;
        }

        if ((maxNumOfPixels == -1) && (minSideLength == -1)) {
            return 1;
        } else if (minSideLength == -1) {
            return lowerBound;
        } else {
            return upperBound;
        }
    }

    public static Bitmap stringtoBitmap(String string) {
        //将字符串转换成Bitmap类型
        Bitmap bitmap = null;
        try {
            byte[] bitmapArray;
            bitmapArray = Base64.decode(string, Base64.DEFAULT);
            bitmap = BitmapFactory.decodeByteArray(bitmapArray, 0, bitmapArray.length);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return bitmap;
    }

    public static String bitmaptoString(Bitmap bitmap) {
    //将Bitmap转换成字符串
        String string = null;
        ByteArrayOutputStream bStream = new ByteArrayOutputStream();
        bitmap.compress(CompressFormat.PNG, 100, bStream);
        byte[] bytes = bStream.toByteArray();
        string = Base64.encodeToString(bytes, Base64.DEFAULT);
        try {
            bStream.close();

            if(!bitmap.isRecycled()){
                bitmap.recycle();
                bitmap = null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return string;
    }

}
