package com.simplerecycleview.wangsheng;

import android.app.Activity;
import android.view.WindowManager;

/**
 * Created by WangSheng on 2017/8/18.
 */

public class Utils {
    public static String msec2HHmmss(int msec) {
        msec = msec / 1000;
        if (msec / 60 == 0) {
            return "00:" + (msec / 10 > 0 ? msec : ("0" + msec));
        }
        if (msec / 3600 == 0) {
            return (msec / 60 / 10 > 0 ? msec : ("0" + msec / 60)) + ":" + (msec % 60 / 10 > 0 ? (msec % 60) : ("0" + msec % 60));
        } else {
            return (msec / 3600 / 10 > 0 ? (msec / 3600) : ("0" + msec / 3600)) + ":" + (msec % 3600 / 60 / 10 > 0 ? (msec % 3600 / 60) : ("0" + msec % 3600 / 60)) + ":" + (msec % 3600 % 60 / 10 > 0 ? (msec % 3600 % 60) : ("0" + msec % 3600 % 60));
        }
    }


}
