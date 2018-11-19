package com.guyu.android.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import okio.BufferedSink;
import okio.BufferedSource;
import okio.Okio;

public class FileUtil {

    public static String GetFileNameByWholePath(String paramString) {
        int i = paramString.lastIndexOf("/");
        int j = paramString.lastIndexOf(".");
        return paramString.substring(i + 1, j);
    }

    public static String GetFileNameByWholePathWithExName(String paramString) {
        int i = paramString.lastIndexOf("/");
        int j = paramString.length();
        return paramString.substring(i + 1, j);
    }

    public static String GetFromAssets(Context paramContext, String paramString) {
        try {
            BufferedReader localBufferedReader = new BufferedReader(
                    new InputStreamReader(paramContext.getResources()
                            .getAssets().open(paramString)));
            StringBuffer localStringBuffer = new StringBuffer();
            Object localObject = localBufferedReader.readLine();
            if (localObject == null)
                return localStringBuffer.toString();
            localStringBuffer.append(((String) localObject).trim());
            String str = localBufferedReader.readLine();
            localObject = str;
        } catch (Exception localException) {
        }
        return null;
    }

    public static String GetFromSDFile(String paramString) {
        File localFile = new File(paramString);
        try {
            BufferedReader localBufferedReader = new BufferedReader(
                    new InputStreamReader(new FileInputStream(localFile)));
            StringBuffer localStringBuffer = new StringBuffer();
            Object localObject = localBufferedReader.readLine();
            if (localObject == null)
                return localStringBuffer.toString();
            localStringBuffer.append(((String) localObject).trim());
            String str = localBufferedReader.readLine();
            localObject = str;
        } catch (Exception localException) {
        }
        return null;
    }

    public static String GetFromSDFile(String paramString1, String paramString2) {
        File localFile = new File(paramString1);
        try {
            BufferedReader localBufferedReader = new BufferedReader(
                    new InputStreamReader(new FileInputStream(localFile),
                            paramString2));
            StringBuffer localStringBuffer = new StringBuffer();
            Object localObject = localBufferedReader.readLine();
            if (localObject == null)
                return new String(localStringBuffer.toString().getBytes(),
                        "UTF-8");
            localStringBuffer.append(((String) localObject).trim());
            String str = localBufferedReader.readLine();
            localObject = str;
        } catch (Exception localException) {
        }
        return null;
    }

    /**
     * 递归删除目录下的所有文件及子目录下所有文件
     *
     * @param file 将要删除的文件目录
     * @return boolean Returns "true" if all deletions were successful.
     * If a deletion fails, the method stops attempting to
     * delete and returns "false".
     */
    public static boolean deleteDir(File file) {
    boolean b = true;
        if (file.isFile() || file.list().length == 0) {
            b = false;
        } else {
            File[] files = file.listFiles();
            for (int i = 0; i < files.length; i++) {
                b = files[i].delete();
                if(!b){
                    break;
                }
            }

//            if (file.exists())         //如果文件本身就是目录 ，就要删除目录
//                file.delete();
        }

        return true;
    }

    /**
     * 删除文件
     *
     * @param filePath
     * @return
     */
    public static boolean deleteFile(String filePath) {
        File localFile = new File(filePath);
        if (localFile.exists()) {
            return localFile.delete();
        } else {
            return false;
        }

    }

    public static boolean IsExist(String paramString) {
        return new File(paramString).exists();
    }

    public static boolean IsSDCardExsit() {
        return Environment.getExternalStorageState().equals("mounted");
    }

    /**
     * 复制单个媒体文件
     *
     * @param uri
     * @param newPath
     * @param context
     */
    public static void copyFile(Uri uri, String newPath, Context context) {
        try {
            int bytesum = 0;
            int byteread = 0;
            ContentResolver cr = context.getContentResolver();
            InputStream inStream = cr.openInputStream(uri);
            FileOutputStream fs = new FileOutputStream(newPath);
            byte[] buffer = new byte[1444];
            while ((byteread = inStream.read(buffer)) != -1) {
                bytesum += byteread; // 字节数 文件大小
                System.out.println(bytesum);
                fs.write(buffer, 0, byteread);
            }
            inStream.close();
        } catch (Exception e) {
            System.out.println("复制单个文件操作出错");
            e.printStackTrace();

        }

    }

    public static boolean moveFile(String oldFile,String newFile){
        boolean b = false;
        BufferedSink sink = null;
        BufferedSource source = null;
        File old =new File(oldFile) ;
        File newf = new File(newFile);
        try {
            source = Okio.buffer(Okio.source(old));
            sink = Okio.buffer(Okio.sink(newf));
            sink.writeAll(source);
            sink.flush();
            old.delete();
            b = true;
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            try {
                if (null != source) {
                    source.close();
                }

                if (null != sink) {
                    sink.close();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return b;
    }


    public static void copyFile(File inputFile, File outputFile)  {
        if (!outputFile.getParentFile().exists())
            outputFile.getParentFile().mkdirs();

        if (outputFile.exists())
            outputFile.delete();
        try (  BufferedSource source = Okio.buffer(Okio.source(inputFile));
               BufferedSink bufferedSink = Okio.buffer(Okio.sink(outputFile));){
            bufferedSink.writeAll(source);

            bufferedSink.close();
            source.close();
        }catch (Exception e){
            e.printStackTrace();
        }


    }


    /**
     * 复制单个文件
     *
     * @param oldPath 原文件路径 如：c:/fqf.txt
     * @param newPath 复制后路径 如：f:/fqf.txt
     */
    public static void copyFile(String oldPath, String newPath) {
        File inputFile = new File(oldPath);
        File outputFile = new File(newPath);
        if (!outputFile.getParentFile().exists())
            outputFile.getParentFile().mkdirs();

        if (outputFile.exists())
            outputFile.delete();
        try (  BufferedSource source = Okio.buffer(Okio.source(inputFile));
               BufferedSink bufferedSink = Okio.buffer(Okio.sink(outputFile));){
            bufferedSink.writeAll(source);

            bufferedSink.close();
            source.close();
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    /**
     * 复制整个文件夹内容
     *
     * @param oldPath String 原文件路径 如：c:/fqf
     * @param newPath String 复制后路径 如：f:/fqf/ff
     * @return boolean
     */
    public static void copyFolder(String oldPath, String newPath) {

        try {
            (new File(newPath)).mkdirs(); // 如果文件夹不存在 则建立新文件夹
            File a = new File(oldPath);
            String[] file = a.list();
            File temp = null;
            for (int i = 0; i < file.length; i++) {
                if (oldPath.endsWith(File.separator)) {
                    temp = new File(oldPath + file[i]);
                } else {
                    temp = new File(oldPath + File.separator + file[i]);
                }

                if (temp.isFile()) {
                    FileInputStream input = new FileInputStream(temp);
                    FileOutputStream output = new FileOutputStream(newPath
                            + "/" + (temp.getName()).toString());
                    byte[] b = new byte[1024 * 5];
                    int len;
                    while ((len = input.read(b)) != -1) {
                        output.write(b, 0, len);
                    }
                    output.flush();
                    output.close();
                    input.close();
                }
                if (temp.isDirectory()) {// 如果是子文件夹
                    copyFolder(oldPath + "/" + file[i], newPath + "/" + file[i]);
                }
            }
        } catch (Exception e) {
            System.out.println("复制整个文件夹内容操作出错");
            e.printStackTrace();

        }

    }
}