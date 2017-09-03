package com.simplerecycleview.wangsheng;

import android.app.Activity;
import android.util.DisplayMetrics;

/**
 * Created by WangSheng on 2017/8/17.
 */

public class WindowSizeUtil {
    public static int getScreenWidth(Activity activity) {
        DisplayMetrics metrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        return metrics.widthPixels;
    }

    public static int getScreenHeight(Activity activity) {
        DisplayMetrics metrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        return metrics.heightPixels;
    }
}
