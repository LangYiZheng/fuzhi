package com.guyu.android.utils;

import java.util.Calendar;

/**
 * Created by admin on 2017/9/26.
 */

public class TimeUtil {
    public static String getTime() {
        String s ="-";
        Calendar calendar = Calendar.getInstance();  //获取当前时间，作为图标的名字
        String year = calendar.get(Calendar.YEAR) + "年";
        String month = (calendar.get(Calendar.MONTH) + 1) + "月";
        String day = calendar.get(Calendar.DAY_OF_MONTH) + "日";
        String hour = calendar.get(Calendar.HOUR_OF_DAY) + "时";
        String minute = calendar.get(Calendar.MINUTE) + "分";
        String second = calendar.get(Calendar.SECOND) + "秒";
        String time = year + month + day + hour + minute + second;
        return time;
    }

    public static String getTime2() {
        String s ="_";
        Calendar calendar = Calendar.getInstance();  //获取当前时间，作为图标的名字
        String year = calendar.get(Calendar.YEAR) + s;
        String month = (calendar.get(Calendar.MONTH) + 1) + s;
        String day = calendar.get(Calendar.DAY_OF_MONTH) + s;
        String hour = calendar.get(Calendar.HOUR_OF_DAY) + s;
        String minute = calendar.get(Calendar.MINUTE) + s;
        String second = calendar.get(Calendar.SECOND) + "";
        String time = year + month + day + hour + minute + second;
        return time;
    }

}
