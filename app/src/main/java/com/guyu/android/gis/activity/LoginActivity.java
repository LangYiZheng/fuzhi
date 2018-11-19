package com.guyu.android.gis.activity;

import java.io.File;
import java.io.FileReader;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.FileProvider;
import android.support.v4.view.ViewPager;

import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.guyu.android.BuildConfig;
import com.guyu.android.database.sync.DJQDBMExternal;
import com.guyu.android.database.sync.DJZQDBMExternal;
import com.guyu.android.database.task.TrackDBMExternal;
import com.guyu.android.gis.app.GisQueryApplication;
import com.guyu.android.R;
import com.guyu.android.gis.common.MapVersion;
import com.guyu.android.gis.common.VersionName;
import com.guyu.android.gis.contract.UpdateContract;
import com.guyu.android.network.DownloadFileUtil;
import com.guyu.android.gis.presenter.UpdatesPresenter;
import com.guyu.android.utils.FileUtil;
import com.guyu.android.utils.GsonUtil;


public class LoginActivity extends FragmentActivity implements UpdateContract.View,DownloadFileUtil.CallBack {

    private ViewPager pager = null;
    private ArrayList<Fragment> fragmentList;
    private LoginFragment loginFragment;
    //	private AppUpdateFragment appUpdateFragment;
    public Handler mHandler = null;
    UpdateContract.Presenter presenter;
    private AlertDialog dialog, dialog2;
    private UpdateMapsDialogFragment updateMapsDialogFragment;
    private UpdateAppDialogFragment updateAppDialogFragment;
    private static boolean startDownloadMaps;
    private static boolean startDownloadApp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_simple);
        if(AppUtils.getAppVersionCode()==5){
            SharedPreferences s = this.getSharedPreferences("addColumn",Context.MODE_PRIVATE);
            String str = s.getString("addColumn","");
            if(str.equals("")){
                boolean a,b,c;
                a = new TrackDBMExternal(this).addColumn();
                b = new DJQDBMExternal(this).createTable();
                c = new DJZQDBMExternal(this).createTable();
                s.edit().putString("addColumn","addColumn").commit();
                if(a&&b&&c){

                }else{

                }

            }

        }

//		this.mHandler = new MyHandler(Looper.myLooper());

//        final LinearLayout ll = (LinearLayout) this
//                .findViewById(R.id.ll);
//
//        Glide.with(this.getBaseContext()).asBitmap()
//                .load(R.drawable.bk_login).into(new SimpleTarget<Bitmap>() {
//            @Override
//            public void onResourceReady(Bitmap bitmap, Transition<? super Bitmap> transition) {
//                Drawable drawable = new BitmapDrawable(bitmap);
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
//                    ll.setBackground(drawable);   //设置背景
//                }
//            }        //设置宽高
//
//        });

        fragmentList = new ArrayList<Fragment>();
        fragmentList.add(loginFragment = new LoginFragment());
//		fragmentList.add(appUpdateFragment = new AppUpdateFragment());
        pager = (ViewPager) findViewById(R.id.viewpager);
        pager.setAdapter(new MyPageAdapter(this.getSupportFragmentManager(),
                fragmentList));
        pager.setCurrentItem(0);
        new UpdatesPresenter(this,this);
        initHandler();
        loadNewConfig();

    }

    public static void setStartDownloadMaps(boolean startDownloadMaps) {
        LoginActivity.startDownloadMaps = startDownloadMaps;
    }

    public static void setStartDownloadApp(boolean startDownloadApp) {
        LoginActivity.startDownloadApp = startDownloadApp;
    }

    private void initHandler() {
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case 1:
                        GisQueryApplication.getApp().loadProjectConfig();
                        mHandler.sendEmptyMessageDelayed(2, 1000);
                        break;
                    case 2:
                        deleteAllFile();
                        break;
                }
            }
        };
    }

    private void deleteAllFile() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                FileUtils.deleteAllInDir(GisQueryApplication.currentProjectPath + "download/");
                FileUtils.deleteAllInDir(GisQueryApplication.currentProjectPath + "/" + "BackupDatas/");
            }
        }).start();
    }


    private void loadNewConfig() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                VersionName versionName = null;
                File file = new File(GisQueryApplication.currentProjectPath + "download/" + AppUtils.getAppVersionName() + "/appVersion.txt");
                if (file.exists()) {
                    try (FileReader reader=new FileReader(file)){
                        versionName = GsonUtil.getGson().fromJson(reader, VersionName.class);
                        if (versionName != null) {
                            String[] temp = versionName.getXml().split("_");
                            File xmlFileNew = new File(GisQueryApplication.currentProjectPath + "download/" + temp[0] + "/xml/" + temp[1] + "/getProjectconfig().xml");
                            File xmlFileOld = new File(GisQueryApplication.currentProjectPath + "projectconfig.xml");

                            temp = versionName.getDb().split("_");
                            File dbFileNew = new File(GisQueryApplication.currentProjectPath + "download/" + temp[0] + "/db/" + temp[1] + "/iGisQuery.db");
                            File dbFileOld = new File(GisQueryApplication.currentProjectPath + "iGisQuery.db");

                            File vFileNew = new File(GisQueryApplication.currentProjectPath + "download/" + versionName.getApk() + "/appVersion.txt");
                            File vFileOld = new File(GisQueryApplication.currentProjectPath + "appVersion.txt");
                            if (xmlFileNew.exists()) {
                                FileUtil.copyFile(xmlFileNew, xmlFileOld);
                            }

                            if (dbFileNew.exists()) {
                                FileUtil.copyFile(dbFileNew, dbFileOld);
                            }

                            if (vFileNew.exists()) {
                                FileUtil.copyFile(vFileNew, vFileOld);
                            }


                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    mHandler.sendEmptyMessage(1);
                }

            }
        }).start();


    }

    @Override
    public void onResume() {
        super.onResume();
        presenter.start();
    }


    @Override
    public void setPresenter(UpdateContract.Presenter presenter) {
        this.presenter = presenter;
    }





    @Override
    public void setMaxProgress(int max) {
        updateAppDialogFragment.setMaxProgress(max);
    }

    @Override
    public void updateProgress(int progress) {
        updateAppDialogFragment.updateProgress(progress);
    }


    @Override
    public void downloadSuccess(File file) {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
        Intent intent = new Intent(Intent.ACTION_VIEW);

        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(Intent.ACTION_VIEW);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {// android.os.FileUriExposedException
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            Uri installURI = FileProvider.getUriForFile(this, this.getApplicationContext().getPackageName() + ".FileProvider", file);
            intent.setDataAndType(installURI,
                    "application/vnd.android.package-archive");
        } else {
            intent.setDataAndType(Uri.fromFile(file),
                    "application/vnd.android.package-archive");
        }
        this.startActivity(intent);
    }

    @Override
    public void downloadError() {

    }


    @Override
    public void showUpdateDialog(final String versionName) {
        dialog = new AlertDialog.Builder(this).setMessage("应用发现版本更新，马上更新吗？").setPositiveButton("更新",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        updateAppDialogFragment = new UpdateAppDialogFragment();
                        updateAppDialogFragment.setCancelable(false);
                        updateAppDialogFragment.setActivity(LoginActivity.this,versionName);
                        updateAppDialogFragment.show(LoginActivity.this.getFragmentManager(), "Update");
                        startDownloadApp = true;
                        presenter.downloadStart();
                    }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                startDownloadApp = false;
            }
        }).show();
    }

    @Override
    public void showUpdateDialogForMaps(final List<MapVersion> versions) {
        if (!startDownloadMaps) {
            dialog2 = new AlertDialog.Builder(this).setMessage("离线地图发现版本更新，马上更新吗？").setPositiveButton("更新",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            updateMapsDialogFragment = new UpdateMapsDialogFragment();
                            updateMapsDialogFragment.setCancelable(false);
                            updateMapsDialogFragment.setVersions(versions);
                            updateMapsDialogFragment.show(LoginActivity.this.getFragmentManager(), "Update");
                            startDownloadMaps = true;
                        }
                    }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    startDownloadMaps = false;
                }
            }).show();
        }

    }

    private class MyPageAdapter extends FragmentPagerAdapter {
        ArrayList<Fragment> list;

        public MyPageAdapter(FragmentManager fm, ArrayList<Fragment> list) {
            super(fm);
            this.list = list;
        }

        @Override
        public Fragment getItem(int arg0) {
            return this.list.get(arg0);
        }

        @Override
        public int getCount() {
            return this.list.size();
        }

    }

    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent paramIntent) {
        super.onActivityResult(requestCode, resultCode, paramIntent);
        this.loginFragment.onActivityResult(requestCode, resultCode,
                paramIntent);
    }

//	private class MyHandler extends Handler {
//		public MyHandler(Looper paramLooper) {
//			super(paramLooper);
//		}
//
//		public void handleMessage(Message paramMessage) {
//			switch (paramMessage.what) {
//			case 901: {
//				LoginActivity.this.appUpdateFragment.doUpdateByCode(902);
//				break;
//			}
//			case 902: {
//				LoginActivity.this.appUpdateFragment.doUpdateByCode(903);
//				break;
//			}
//			case 903: {
//				LoginActivity.this.appUpdateFragment.doUpdateByCode(904);
//				break;
//			}
//			case 904: {
//				LoginActivity.this.appUpdateFragment.doUpdateByCode(905);
//				break;
//			}
//			case 905: {
//				LoginActivity.this.appUpdateFragment.doUpdateByCode(906);
//				break;
//			}
//			case 508:
//			case 509:
//			case 510:
//			case 511:
//			case 512:
//			case 513:{
//				LoginActivity.this.appUpdateFragment.handleAppDownMessage(paramMessage);
//				break;
//			}
//			}
//		}
//	}
}
