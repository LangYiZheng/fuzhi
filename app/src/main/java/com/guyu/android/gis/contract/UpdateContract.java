package com.guyu.android.gis.contract;

import com.guyu.android.base.BasePresenter;
import com.guyu.android.base.BaseView;
import com.guyu.android.base.CallBack;
import com.guyu.android.gis.common.MapVersion;

import java.io.File;
import java.util.List;

/**
 * 更新抽象契约类
 * Created by admin on 2017/9/28.
 */

public interface UpdateContract {
    interface View extends BaseView<Presenter>{

        /**
         * 提示更新Dialog
         * @param versionName
         */
        void showUpdateDialog(String versionName);
        void showUpdateDialogForMaps(List<MapVersion> versions);
    }
    interface Presenter extends BasePresenter{
        /**
         *检查版本更新
         */
        void checkForUpdates();

        void checkForUpdatesOfMaps();

        /**
         * 下载更新文件
         * @param type 下载类型
         *             1:apk 2:xml 3:sqlite数据库文件
         */
        void downloadFile(int type);

        void downloadStart();
        void downloadPause();
        void downloadContinue();
        void downloadStop();
    }
}
