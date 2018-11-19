package com.guyu.android.gis.activity;

import java.io.File;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.guyu.android.R;
import com.guyu.android.database.DBOptExternal2;
import com.guyu.android.gis.app.GisQueryApplication;
import com.guyu.android.gis.common.CallBack;
import com.guyu.android.gis.common.MapVersion;
import com.guyu.android.gis.common.MapVersionList;
import com.guyu.android.gis.config.LayerConfig;
import com.guyu.android.gis.opt.CantonZoneOpt;
import com.guyu.android.utils.FileUtil;
import com.guyu.android.utils.TimeUtil;

public class PrjSettingActivity extends Activity implements OnClickListener {
    private static final String TAG = "PrjSettingActivity";
    private LinearLayout mLayout;
    private LayoutInflater layoutInflater;

    private Handler mHandler = null;
    private ImageButton zoneNaviSyn;
    private CheckBox remindCheck_offlineData;
    private CheckBox remindCheck_softVersion;

    private File[] backupDataFiles;
    private String[] backupDataFileNames;
    private Map<String, String> versions;

    public void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);
        setContentView(R.layout.activity_prj_setting);
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectDiskReads().detectDiskWrites().detectNetwork().penaltyLog().build());
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectLeakedSqlLiteObjects().detectLeakedClosableObjects().penaltyLog().penaltyDeath().build());
        this.layoutInflater = LayoutInflater.from(this);
        this.mLayout = ((LinearLayout) findViewById(R.id.LinearLayout));

        this.mHandler = new MyHandler(Looper.myLooper());
        initData();
        initWidget();
    }

    private void initData() {

    }

    private void initWidget() {
        findViewById(R.id.btn_cancel).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
//                Intent localIntent = new Intent(
//                        PrjSettingActivity.this, MainActivity.class);
//                localIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
//                startActivity(localIntent);
                finish();

            }
        });
        findViewById(R.id.btn_reboot).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {

                GisQueryApplication.getApp().rebootSystem();

                finish();

            }
        });
        initConfigUI();
    }

    private class MyHandler extends Handler {
        public MyHandler(Looper paramLooper) {
            super(paramLooper);
        }

        public void handleMessage(Message paramMessage) {
            switch (paramMessage.what) {
                case 132000:
                    ToastUtils.showLong("区划导航更新成功！");
                    zoneNaviSyn.setEnabled(true);
                    break;
                case 1:
                    ToastUtils.showLong("清除成功");
                    break;
                case 8:
                    ToastUtils.showLong("清除失败");
                    break;
                case 2:
                    ToastUtils.showLong("备份成功");
                    break;
                case 3:
                    ToastUtils.showLong("备份失败");
                    break;
                case 4:
                    AlertDialog dialog = new AlertDialog.Builder(PrjSettingActivity.this).setTitle("请选择要恢复的备份文件")
                            .setSingleChoiceItems(PrjSettingActivity.this.backupDataFileNames, -1, new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, final int i) {
                                    dialog.dismiss();
                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            File dbFile = new File(GisQueryApplication.currentProjectPath + "/" + "iGisQueryData.db");
                                            File copyFile = backupDataFiles[i];

                                            try {
                                                FileUtil.copyFile(copyFile, dbFile);
                                                mHandler.sendEmptyMessage(6);
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                                mHandler.sendEmptyMessage(7);
                                            }
                                        }
                                    }).start();
                                }
                            })
                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .create();
                    dialog.show();
                    break;
                case 5:
                    ToastUtils.showLong("不存在备份文件");
                    break;
                case 6:
                    ToastUtils.showLong("恢复成功");
                    break;
                case 7:
                    ToastUtils.showLong("恢复失败");
                    break;
            }
        }
    }

    private void initConfigUI() {
        versions = new HashMap();
        List<MapVersion> list = GisQueryApplication.getApp().getProjectconfig().getMapVersion().getData().getList();
        String offlineDataVersion = "";
        for (MapVersion v : list) {
            if (!v.getNAME().equals("LJK_YX")) {
                if (!v.getNAME().equals("offlinedata")) {
                    versions.put(v.getNAME(), v.getMAPVER());
                } else {
                    offlineDataVersion = v.getMAPVER();
                }
            } else {
                versions.put("lj_clip_ProjectRaster", v.getMAPVER());
            }

        }
        addTipRow("底图配置");
        ArrayList<LayerConfig> al_layerconfig_base = GisQueryApplication.getApp().getProjectconfig()
                .getBasemaps();
        ArrayList<LayerConfig> al_layerconfig_opera = GisQueryApplication.getApp().getProjectconfig()
                .getOperationallayers();
        for (LayerConfig l : al_layerconfig_base) {
            if(l.getType().equals("feature")){
                versions.put(l.getLabel(), offlineDataVersion);
            }
        }
        for (LayerConfig l : al_layerconfig_opera) {
            versions.put(l.getLabel(), offlineDataVersion);
        }
        for (int i = 0; i < al_layerconfig_base.size(); i++) {
            addLayerConfigRow(al_layerconfig_base.get(i));
        }
        addTipRow("业务图层配置");

        for (int i = 0; i < al_layerconfig_opera.size(); i++) {
            addLayerConfigRow(al_layerconfig_opera.get(i));
        }
        addTipRow("数据更新配置");
        zoneNaviSyn = addImageButtonRow("区划导航数据更新", R.drawable.sys_update);
        zoneNaviSyn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View paramView) {
                zoneNaviSyn.setEnabled(false);
                Toast.makeText(getBaseContext(), "区划导航更新中...", Toast.LENGTH_LONG).show();
                UpdateThread updateThread = new UpdateThread();
                new Thread(updateThread).start();

            }
        });
        addTipRow("业务数据管理");
        addButton();

//		remindCheck_offlineData = addCheckBoxRow("离线数据有更新提醒我");
//		remindCheck_offlineData
//				.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//					public void onCheckedChanged(
//							CompoundButton paramCompoundButton,
//							boolean paramBoolean) {
//						if (paramBoolean) {
//							gisQueryApplication.SetConfigData(
//									"remindupdateofflinedata", 1);
//						} else {
//							gisQueryApplication.SetConfigData(
//									"remindupdateofflinedata", 0);
//						}
//					}
//				});
//	
//		remindCheck_softVersion = addCheckBoxRow("软件有新版本提醒我");
//		remindCheck_softVersion
//				.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//					public void onCheckedChanged(
//							CompoundButton paramCompoundButton,
//							boolean paramBoolean) {
//						if (paramBoolean) {
//							gisQueryApplication.SetConfigData(
//									"remindupdatesoft", 1);
//						} else {
//							gisQueryApplication.SetConfigData(
//									"remindupdatesoft", 0);
//						}
//					}
//				});
        // 初始化CheckBox默认选择状态
//		int remindupdateofflinedata = gisQueryApplication.GetIntConfigData(
//				"remindupdateofflinedata", 0);
//		int remindupdatesoft = gisQueryApplication.GetIntConfigData(
//				"remindupdatesoft", 0);
//		remindCheck_offlineData.setChecked((remindupdateofflinedata == 1));
//		remindCheck_softVersion.setChecked((remindupdatesoft == 1));
    }

    class UpdateThread implements Runnable {
        public void run() {
            CantonZoneOpt.updateAllCantonZoneList(PrjSettingActivity.this,
                    new CallBack() {
                        @Override
                        public void execute() {
                            mHandler.sendEmptyMessage(132000);
                        }
                    });
        }
    }

    private ImageButton addImageButtonRow(String tipText, int resid) {
        LinearLayout localLinearLayout = (LinearLayout) this.layoutInflater
                .inflate(R.layout.record_item_edit, null).findViewById(
                        R.id.record_item);
        TextView localTextView1 = (TextView) localLinearLayout
                .findViewById(R.id.attribute_name);
        localTextView1.setText(tipText);
        localLinearLayout.findViewById(R.id.vg_record_style_01).setVisibility(
                View.GONE);
        localLinearLayout.findViewById(R.id.vg_record_style_02).setVisibility(
                View.VISIBLE);
        localLinearLayout.findViewById(R.id.vg_record_style_03).setVisibility(
                View.GONE);
        localLinearLayout.findViewById(R.id.rlBtn).setVisibility(
                View.GONE);
        localLinearLayout.findViewById(R.id.vg_record_style_04).setVisibility(
                View.GONE);
        localLinearLayout.findViewById(R.id.vg_record_style_05).setVisibility(
                View.GONE);
        ImageButton localImageButton1 = (ImageButton) localLinearLayout
                .findViewById(R.id.btn_value_setting);
        localImageButton1.setBackgroundResource(resid);
        LinearLayout.LayoutParams localLayoutParams = new LinearLayout.LayoutParams(
                -1, -2);
        this.mLayout.addView(localLinearLayout, localLayoutParams);
        return localImageButton1;
    }

    private CheckBox addCheckBoxRow(String tipText) {
        LinearLayout localLinearLayout = (LinearLayout) this.layoutInflater
                .inflate(R.layout.record_item_edit, null).findViewById(
                        R.id.record_item);
        TextView localTextView1 = (TextView) localLinearLayout
                .findViewById(R.id.attribute_name);
        localTextView1.setText(tipText);
        localLinearLayout.findViewById(R.id.vg_record_style_01).setVisibility(
                View.GONE);
        localLinearLayout.findViewById(R.id.vg_record_style_02).setVisibility(
                View.INVISIBLE);
        localLinearLayout.findViewById(R.id.vg_record_style_03).setVisibility(
                View.GONE);
        localLinearLayout.findViewById(R.id.vg_record_style_04).setVisibility(
                View.GONE);
        localLinearLayout.findViewById(R.id.rlBtn).setVisibility(
                View.GONE);
        localLinearLayout.findViewById(R.id.vg_record_style_05).setVisibility(
                View.VISIBLE);
        CheckBox localCheckBox1 = (CheckBox) localLinearLayout
                .findViewById(R.id.checkbox_value);
        LinearLayout.LayoutParams localLayoutParams = new LinearLayout.LayoutParams(
                -1, -2);
        this.mLayout.addView(localLinearLayout, localLayoutParams);
        return localCheckBox1;
    }

    private void addTipRow(String tipText) {
        LinearLayout localLinearLayout = (LinearLayout) this.layoutInflater
                .inflate(R.layout.record_item_edit, null).findViewById(
                        R.id.record_item);
        TextView localTextView1 = (TextView) localLinearLayout
                .findViewById(R.id.attribute_name);
        localTextView1.setTextSize(18.0F);
        localTextView1.setTextColor(0xFF0000FF);
        localTextView1.setText(tipText);
        localLinearLayout.findViewById(R.id.vg_record_style_01).setVisibility(
                View.INVISIBLE);
        localLinearLayout.findViewById(R.id.vg_record_style_02).setVisibility(
                View.INVISIBLE);
        localLinearLayout.findViewById(R.id.vg_record_style_03).setVisibility(
                View.GONE);
        localLinearLayout.findViewById(R.id.vg_record_style_04).setVisibility(
                View.INVISIBLE);
        localLinearLayout.findViewById(R.id.rlBtn).setVisibility(
                View.GONE);
        localLinearLayout.findViewById(R.id.vg_record_style_05).setVisibility(
                View.GONE);
        LinearLayout.LayoutParams localLayoutParams = new LinearLayout.LayoutParams(
                -1, -2);
        this.mLayout.addView(localLinearLayout, localLayoutParams);
    }


    private void addButton() {
        LinearLayout localLinearLayout = (LinearLayout) this.layoutInflater
                .inflate(R.layout.record_item_edit, null).findViewById(
                        R.id.record_item);
        localLinearLayout
                .findViewById(R.id.attribute_name).setVisibility(
                View.GONE);
        localLinearLayout.findViewById(R.id.vg_record_style_01).setVisibility(
                View.GONE);
        localLinearLayout.findViewById(R.id.vg_record_style_02).setVisibility(
                View.GONE);
        localLinearLayout.findViewById(R.id.vg_record_style_03).setVisibility(
                View.GONE);
        localLinearLayout.findViewById(R.id.vg_record_style_04).setVisibility(
                View.GONE);
        localLinearLayout.findViewById(R.id.rlBtn).setVisibility(
                View.VISIBLE);
        localLinearLayout.findViewById(R.id.vg_record_style_05).setVisibility(
                View.GONE);
        localLinearLayout.findViewById(R.id.line).setVisibility(
                View.GONE);
        localLinearLayout
                .findViewById(R.id.btnClear).setOnClickListener(this);
        localLinearLayout
                .findViewById(R.id.btnBackup).setOnClickListener(this);
        localLinearLayout
                .findViewById(R.id.btnRestoreData).setOnClickListener(this);

        LinearLayout.LayoutParams localLayoutParams = new LinearLayout.LayoutParams(
                -1, -2);
        this.mLayout.addView(localLinearLayout, localLayoutParams);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnClear:
                new AlertDialog.Builder(this).setMessage("确定清空业务数据吗？").setPositiveButton("确定",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        DBOptExternal2.deleteAll();


                                        boolean a = false;
                                        a = FileUtils.deleteAllInDir(GisQueryApplication.currentProjectPath + "/" + "CameraPhoto");
                                        a = a && FileUtils.deleteAllInDir(GisQueryApplication.currentProjectPath + "/" + "CameraVideo");
                                        a = a && FileUtils.deleteAllInDir(GisQueryApplication.currentProjectPath + "/" + "Sounds");
                                        a = a && FileUtils.deleteAllInDir(GisQueryApplication.currentProjectPath + "/" + "TaskUpload");
                                        a = a && FileUtils.deleteAllInDir(GisQueryApplication.currentProjectPath + "/" + "Track");

                                        if (a) {
                                            mHandler.sendEmptyMessage(1);
                                        } else {
                                            mHandler.sendEmptyMessage(8);
                                        }

                                    }
                                }).start();

                            }
                        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();


                break;
            case R.id.btnBackup:
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        boolean b = backupData();
                        if (b) {
                            mHandler.sendEmptyMessage(2);
                        } else {
                            mHandler.sendEmptyMessage(3);
                        }
                    }
                }).start();
                break;
            case R.id.btnRestoreData:
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        File dirFile = getPath("BackupDatas");
                        backupDataFiles = getBackupDatas(dirFile);
                        if (backupDataFiles != null && backupDataFiles.length > 0) {
                            backupDataFileNames = new String[backupDataFiles.length];
                            for (int i = 0; i < backupDataFiles.length; i++) {
                                String[] name = backupDataFiles[i].getName().split("_");
                                if (name != null && name.length >= 2) {
                                    backupDataFileNames[i] = name[1];
                                }
                            }
                            mHandler.sendEmptyMessage(4);
                        } else {
                            mHandler.sendEmptyMessage(5);
                        }
                    }
                }).start();

                break;
        }
    }

    private boolean backupData() {
        boolean b = true;
        File dirFile = getPath("BackupDatas");
        if (dirFile != null) {
            File dbFile = new File(GisQueryApplication.currentProjectPath + "/" + "iGisQueryData.db");
            File copyFile = new File(dirFile, "iGisQueryData.db_" + TimeUtil.getTime());
            try {
                FileUtil.copyFile(dbFile, copyFile);
            } catch (Exception ex) {
                ex.printStackTrace();
                b = false;
            }
        } else {
            b = false;
        }

        return b;
    }

    private File[] getBackupDatas(File dirFile) {
        File[] files = null;
        if (dirFile.exists()) {
            files = dirFile.listFiles();
        } else {
            Log.e("备份：", "不存在备份文件");
        }
        return files;
    }

    private File getPath(String name) {
        //判断是否存在sd卡
        boolean sdExist = android.os.Environment.MEDIA_MOUNTED.equals(android.os.Environment.getExternalStorageState());
        if (!sdExist) {//如果不存在,
            Log.e("SD卡管理：", "SD卡不存在，请加载SD卡");
            return null;
        } else {//如果存在
            //获取sd卡路径
            String dbDir = GisQueryApplication.currentProjectPath + "/" + name;//数据库所在目录
            //判断目录是否存在，不存在则创建该目录
            File dirFile = new File(dbDir);
            if (!dirFile.exists() && !dirFile.isDirectory()) {
                dirFile.mkdirs();
            }


            return dirFile;
        }
    }

    private void addLayerConfigRow(final LayerConfig layerConfig) {
        LinearLayout localLinearLayout = (LinearLayout) this.layoutInflater
                .inflate(R.layout.record_item_edit, null).findViewById(
                        R.id.record_item);
        TextView localTextView1 = (TextView) localLinearLayout
                .findViewById(R.id.attribute_name);
        localTextView1.setTextSize(16.0F);
        localTextView1.setText(layerConfig.getName() + "_" + versions.get(layerConfig.getLabel()));
        localLinearLayout.findViewById(R.id.vg_record_style_01).setVisibility(
                View.INVISIBLE);
        localLinearLayout.findViewById(R.id.vg_record_style_02).setVisibility(
                View.INVISIBLE);
        localLinearLayout.findViewById(R.id.vg_record_style_03).setVisibility(
                View.INVISIBLE);
        localLinearLayout.findViewById(R.id.vg_record_style_04).setVisibility(
                View.VISIBLE);
        localLinearLayout.findViewById(R.id.rlBtn).setVisibility(
                View.GONE);
        localLinearLayout.findViewById(R.id.vg_record_style_05).setVisibility(
                View.GONE);
        Switch btn_switch = (Switch) localLinearLayout
                .findViewById(R.id.btn_switch);
        btn_switch.setTextOff("离线");
        btn_switch.setTextOn("在线");
        btn_switch.setChecked(!layerConfig.getUseoffline());
        btn_switch.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                if (isChecked) {
                    layerConfig.setUseoffline(false);
                } else {
                    layerConfig.setUseoffline(true);
                }
                saveLayerConfig(layerConfig);

            }
        });
        LinearLayout.LayoutParams localLayoutParams = new LinearLayout.LayoutParams(
                -1, -2);
        this.mLayout.addView(localLinearLayout, localLayoutParams);
    }

    /**
     * 保存图层离线在线设置
     *
     * @param layerConfig
     */
    private void saveLayerConfig(LayerConfig layerConfig) {
        File f_projectconfig = GisQueryApplication.getApp().getFile("projectconfig.xml",
                GisQueryApplication.getApp().getProjectsconfig().getCurrentProjectName(), "");
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        Document document = null;
        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            document = builder.parse(f_projectconfig);
            Element root = document.getDocumentElement();
            // 修改图层配置开始
            NodeList nl_layers = root.getElementsByTagName("layer");
            for (int i = 0; i < nl_layers.getLength(); i++) {
                Element el_layer = (Element) nl_layers.item(i);
                if (el_layer.getAttribute("label").equals(
                        layerConfig.getLabel())) {
                    el_layer.setAttribute("useoffline",
                            "" + layerConfig.getUseoffline());
                    break;
                }
            }
            // 修改图层配置结束
            TransformerFactory tFactory = TransformerFactory.newInstance();
            Transformer transformer = null;

            try {
                transformer = tFactory.newTransformer();
            } catch (TransformerConfigurationException e) {
                e.printStackTrace();
            }

            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            DOMSource source = new DOMSource(document);
            StreamResult result = new StreamResult(f_projectconfig);
            try {
                transformer.transform(source, result);
            } catch (TransformerException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}