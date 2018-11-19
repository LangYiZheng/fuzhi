package com.guyu.android.utils;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.util.Log;

import com.guyu.android.BuildConfig;
import com.guyu.android.R;
import com.guyu.android.gis.app.GisQueryApplication;

import java.io.File;

public class CameraFilePathUtils {
    private static final String TAG = "FilePathUtils";


    private static final String CAMERA_FILE_PATH = "CameraPhoto/";
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 393217;

    public static int getCameraPermissionRequestCode() {
        return CAMERA_PERMISSION_REQUEST_CODE;
    }



    public static Uri getFileUri(Context context, String filePath){
        Log.d("getFileUri",filePath);
        Uri mUri = null;
        File mFile = new File(filePath);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
            mUri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".FileProvider",mFile);
        }else{
            mUri = Uri.fromFile(mFile);
        }
        return mUri;
    }


    public static String parseGalleryPath(Context context,Uri uri){
        String pathHead = "";
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && DocumentsContract.isDocumentUri(context,uri)){

            String authority = uri.getAuthority();
            DebugUtils.d(TAG,"parseGalleryPath::KITKAT DocumentsContract::authority = " + authority);

            String id = DocumentsContract.getDocumentId(uri);
            DebugUtils.d(TAG,"parseGalleryPath::KITKAT DocumentsContract::id = " + id);

            String[] idArray = id.split(":");
            String type = idArray.length > 0 ? idArray[0] : "";

            Uri contentUri = null;


            if(isExternalStorageDocument(uri)){
                DebugUtils.d(TAG,"parseGalleryPath::KITKAT DocumentsContract::isExternalStorageDocument");
            }else if(isDownloadsDocument(uri)){
                DebugUtils.d(TAG,"parseGalleryPath::KITKAT DocumentsContract::isDownloadsDocument");

                contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"),Long.valueOf(id));
                return pathHead + getDataColumn(context,contentUri,null,null);

            }else if(isMediaDocument(uri)){
                DebugUtils.d(TAG,"parseGalleryPath::KITKAT DocumentsContract::isMediaDocument");

                if("image".equals(type)){
                    DebugUtils.d(TAG,"parseGalleryPath::KITKAT DocumentsContract::isMediaDocument::image");

                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                }else if("video".equals(type)){
                    DebugUtils.d(TAG,"parseGalleryPath::KITKAT DocumentsContract::isMediaDocument::video");

                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                }else if("audio".equals(type)){
                    DebugUtils.d(TAG,"parseGalleryPath::KITKAT DocumentsContract::isMediaDocument::audio");

                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                DebugUtils.d(TAG,"parseGalleryPath::KITKAT DocumentsContract::isMediaDocument::idArray.length = " + idArray.length);

                if(idArray.length >= 2){
                    String selection = "_id = ? ";
                    String[] selectionArgs = new String[]{idArray[1]};
                    return pathHead + getDataColumn(context,contentUri,selection,selectionArgs);
                }
            }

        }else if("content".equalsIgnoreCase(uri.getScheme())){
            String data = getDataColumn(context,uri,null,null);
            DebugUtils.d(TAG,"parseGalleryPath::content::data = " + data);
            return pathHead + data;
        }else if("file".equalsIgnoreCase(uri.getScheme())){
            String filePath = uri.getPath();
            DebugUtils.d(TAG,"parseGalleryPath::file::filePath = " + filePath);
            return pathHead + filePath;
        }
        return "";
    }

    private static String getDataColumn(Context context,Uri uri,String selection,String[] selectionArgs){
        DebugUtils.d(TAG,"getDataColumn::uri = " + uri);
        DebugUtils.d(TAG,"getDataColumn::selection = " + selection);
        DebugUtils.d(TAG,"getDataColumn::selectionArgs = " + selectionArgs);
        String column = "_data";
        String[] projections = new String[]{column};

        ContentResolver cr = context.getContentResolver();
        Cursor mCursor = cr.query(uri,projections,selection,selectionArgs,null);
        if(mCursor != null){
            if(mCursor.moveToFirst()){
                return mCursor.getString(mCursor.getColumnIndex(column));
            }
            mCursor.close();
        }
        return "";
    }


    private static boolean isExternalStorageDocument(Uri uri){
        String authority = uri.getAuthority();
        return "com.android.externalstorage.documents".equals(authority);
    }

    private static boolean isDownloadsDocument(Uri uri){
        String authority = uri.getAuthority();
        return "com.android.providers.downloads.documents".equals(authority);
    }

    private static boolean isMediaDocument(Uri uri){
        String authority = uri.getAuthority();
        return "com.android.providers.media.documents".equals(authority);
    }
}
