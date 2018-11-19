package com.guyu.android.utils;

import android.content.Context;
import android.graphics.Point;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

import java.lang.reflect.InvocationTargetException;

/**
 * Created by YunHuan on 2017/11/15.
 */

public class ScreenUtils {

    /**
     * 获取屏幕的宽高，用于APP
     *
     * @param context
     * @return array[0]为宽，array[1]为高
     */
    public static int[] getWidthAndHeight(Context context) {
        Display display = getDisplay(context);
//        int[] array = new int[]{display.getWidth(), display.getHeight()};
        Point point = new Point();
        display.getSize(point);
        int[] array = new int[]{point.x, point.y};
        return array;
    }

    /**
     * 获取屏幕的实际宽高，用于屏幕分辨率
     *
     * @param context
     * @return array[0]为宽，array[1]为高
     */
    public static int[] getRealWidthAndHeight(Context context) {
        Display display = getDisplay(context);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            Point point = new Point();
            display.getRealSize(point);
            return new int[]{point.x, point.y};
        } else {
            try {
                int width = (Integer) Display.class.getMethod("getRawWidth").invoke(display);
                int height = (Integer) Display.class.getMethod("getRawHeight").invoke(display);
                return new int[]{width, height};
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 获取dp密度
     *
     * @param context
     * @return
     */
    public static float getDensity(Context context) {
        return getDisplayMetrics(context).density;
    }

    /**
     * 获取sp密度
     *
     * @param context
     * @return
     */
    public static float getScaledDensity(Context context) {
        return getDisplayMetrics(context).scaledDensity;
    }

    // *********************************************************************************************
    // 私有方法

    private static Display getDisplay(Context context) {
        WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        return manager.getDefaultDisplay();
    }

    private static DisplayMetrics getDisplayMetrics(Context context) {
        DisplayMetrics metrics = new DisplayMetrics();
        getDisplay(context).getMetrics(metrics);
        return metrics;
    }
}
