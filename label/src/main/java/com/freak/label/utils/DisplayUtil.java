package com.freak.label.utils;

import android.content.Context;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;

public class DisplayUtil {
    /**
     * 获取屏幕宽高
     *
     * @param context
     * @param type    0：宽 1：高
     */
    public static int getScreenRelatedInformation(Context context, int type) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        int value = 0;
        if (windowManager != null) {
            DisplayMetrics outMetrics = new DisplayMetrics();
            windowManager.getDefaultDisplay().getMetrics(outMetrics);
            //可用显示大小的绝对宽度（以像素为单位）。
            int widthPixels = outMetrics.widthPixels;
            //可用显示大小的绝对高度（以像素为单位）。
            int heightPixels = outMetrics.heightPixels;
            //屏幕密度表示为每英寸点数。
            int densityDpi = outMetrics.densityDpi;
            //显示器的逻辑密度。
            float density = outMetrics.density;
            //显示屏上显示的字体缩放系数。
            float scaledDensity = outMetrics.scaledDensity;
            Log.d("display", "widthPixels = " + widthPixels + ",heightPixels = " + heightPixels + "\n" +
                    ",densityDpi = " + densityDpi + "\n" +
                    ",density = " + density + ",scaledDensity = " + scaledDensity);
            if (type == 1) {
                value = heightPixels;
            } else {
                value = widthPixels;
            }
        }
        return value;
    }

    /**
     * 获取屏幕宽高
     *
     * @param context
     * @param type    0：宽 1：高
     */
    public static int getRealScreenRelatedInformation(Context context, int type) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        int value = 0;
        if (windowManager != null) {
            DisplayMetrics outMetrics = new DisplayMetrics();
            windowManager.getDefaultDisplay().getRealMetrics(outMetrics);
            //可用显示大小的绝对宽度（以像素为单位）。
            int widthPixels = outMetrics.widthPixels;
            //可用显示大小的绝对高度（以像素为单位）。
            int heightPixels = outMetrics.heightPixels;
            //屏幕密度表示为每英寸点数。
            int densityDpi = outMetrics.densityDpi;
            //显示器的逻辑密度。
            float density = outMetrics.density;
            //显示屏上显示的字体缩放系数。
            float scaledDensity = outMetrics.scaledDensity;
            Log.d("display", "widthPixels = " + widthPixels + ",heightPixels = " + heightPixels + "\n" +
                    ",densityDpi = " + densityDpi + "\n" +
                    ",density = " + density + ",scaledDensity = " + scaledDensity);
            if (type == 1) {
                value = heightPixels;
            } else {
                value = widthPixels;
            }
        }
        return value;
    }
}
