package com.guyu.android.gis.activity;

import java.io.File;


import com.guyu.android.gis.app.GisQueryApplication;
import com.guyu.android.runtime.GuyuRunTime;
import com.guyu.android.runtime.LicenseResult;
import com.guyu.android.utils.HnTimer;
import com.guyu.android.R;
import com.guyu.android.utils.PermissionsChecker;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class SplashActivity extends Activity {
    private PermissionsChecker mPermissionsChecker; // 权限检测器
    private static final int REQUEST_CODE = 0; // 请求码
    private static final String[] PERMISSIONS = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,

//            Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS,
    };

    //    private Bitmap myBitmap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPermissionsChecker = new PermissionsChecker(this);

        this.setContentView(R.layout.activity_splash_normal);

    }

    private void init() {
        GisQueryApplication gisQueryApplication = GisQueryApplication.getApp();

//        final LinearLayout splash = (LinearLayout) this
//                .findViewById(R.id.bk_splash);
//        Drawable d = Drawable.createFromPath(new File(                //设置欢迎界面图片
//                GisQueryApplication.currentProjectPath, "splash.png")
//                .getAbsolutePath());
//        splash.setBackgroundDrawable(d);

//        Glide.with(this.getBaseContext()).asBitmap()
//                .load(R.drawable.splash
////                        new File(GisQueryApplication.currentProjectPath, "splash.png").getAbsolutePath()
//                ).into(
//                new SimpleTarget<Bitmap>() {
//                    @Override
//                    public void onResourceReady(Bitmap bitmap, Transition<? super Bitmap> transition) {
////                myBitmap = bitmap;
//
//                        splash.setBackground(new BitmapDrawable(bitmap));   //设置背景
//
//                    }        //设置宽高
//
//                }
//        );
        String licensekey = gisQueryApplication.getProjectconfig().getLicensekey();//获取解密数据

        //解密
        LicenseResult lr = GuyuRunTime.checkLicenseKey(this, licensekey);//licensekey:解密前的数据
        gisQueryApplication.setLicenseResult(lr);
        if (lr.result == LicenseResult.VALID) {
            HnTimer.setTimeout(800, new HnTimer.OnCompletedListener() {
                public void onCompleted() {
                    Intent it = new Intent(SplashActivity.this, LoginActivity.class);
                    it.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    SplashActivity.this.overridePendingTransition(R.anim.fade,
                            R.anim.hold);
                    SplashActivity.this.startActivity(it);
                    SplashActivity.this.finish();
                }
            });
        } else if (lr.result == LicenseResult.INVALID) {
            HnTimer.setTimeout(800, new HnTimer.OnCompletedListener() {
                public void onCompleted() {
                    SplashActivity.this
                            .startActivity(new Intent(
                                    SplashActivity.this, UnauthorizedActivity.class));
                    SplashActivity.this.overridePendingTransition(R.anim.fade,
                            R.anim.hold);
                    SplashActivity.this.finish();
                }
            });

        } else if (lr.result == LicenseResult.EXPIRED) {
            HnTimer.setTimeout(800, new HnTimer.OnCompletedListener() {
                public void onCompleted() {
                    SplashActivity.this
                            .startActivity(new Intent(
                                    SplashActivity.this, ExpiredActivity.class));
                    SplashActivity.this.overridePendingTransition(R.anim.fade,
                            R.anim.hold);
                    SplashActivity.this.finish();
                }
            });
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        // 缺少权限时, 进入权限配置页面
        if (mPermissionsChecker.lacksPermissions(PERMISSIONS)) {
            startPermissionsActivity();
        } else {
            GisQueryApplication.getApp().init();
            init();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        if (myBitmap != null && !myBitmap.isRecycled()) {
//            myBitmap.recycle();
//            myBitmap = null;
//        }

    }

    private void startPermissionsActivity() {
        PermissionsActivity.startActivityForResult(this, REQUEST_CODE, PERMISSIONS);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // 拒绝时, 关闭页面, 缺少主要权限, 无法运行
        if (requestCode == REQUEST_CODE && resultCode == PermissionsActivity.PERMISSIONS_DENIED) {
            finish();
        }


    }
}
