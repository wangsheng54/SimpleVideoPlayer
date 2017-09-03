package com.simplerecycleview.wangsheng;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.util.AttributeSet;
import android.widget.VideoView;

/**
 * Created by WangSheng on 2017/8/16.
 */

public class MyVideoPlayer extends VideoView {

    private Context mContext;

    public MyVideoPlayer(Context context) {
        this(context, null);
    }

    public MyVideoPlayer(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //Log.i("@@@@", "onMeasure(" + MeasureSpec.toString(widthMeasureSpec) + ", "
        //        + MeasureSpec.toString(heightMeasureSpec) + ")");
        int width;
        int height;

        if (((Activity) mContext).getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            width = WindowSizeUtil.getScreenWidth((Activity) mContext);
            height = WindowSizeUtil.getScreenHeight((Activity) mContext);
        } else {
            width = WindowSizeUtil.getScreenWidth((Activity) mContext);
            height = width * 9 / 16;
        }
        setMeasuredDimension(width, height);
    }


}
