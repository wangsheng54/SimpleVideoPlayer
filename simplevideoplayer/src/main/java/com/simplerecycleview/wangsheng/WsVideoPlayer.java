package com.simplerecycleview.wangsheng;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

/**
 * Created by WangSheng on 2017/8/17.
 */

public class WsVideoPlayer extends RelativeLayout implements View.OnClickListener {
    private Context mContext;
    private View mainView;
    private MyVideoPlayer mVideoPlayer;
    private ImageView img_back;
    private ImageView img_fullscreen;
    private FrameLayout fl_toplayout;
    private LinearLayout ll_bottomlayout;
    private GestureDetector videoPlayerGestrueDetector;
    private FrameLayout fl_videoplayer;
    private OnPreparedListener mOnPreparedListener;
    private ProgressBar progressbar_loading;
    private ImageView img_pauseplayend;
    private OnCompletionListener mOnCompletionListener;
    private TextView tv_duration;
    private TextView tv_process;
    private SeekBar sb_seekBar;
    private boolean ispause;
    private SensorManager mSensorManager;
    private boolean haveChanged = false;

    public boolean isHaveChanged() {
        return haveChanged;
    }

    public void setHaveChanged(boolean haveChanged) {
        this.haveChanged = haveChanged;
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            tv_duration.setText(Utils.msec2HHmmss(getDuration()));
            tv_process.setText(Utils.msec2HHmmss(getCurrentPosition()));
            int currentPosition = (int) (getCurrentPosition() / (1.0f * getDuration()) * sb_seekBar.getMax());
            int bufferSize = (int) ((getBufferPercentage() / 100f) * sb_seekBar.getMax());
            sb_seekBar.setProgress(currentPosition);
            sb_seekBar.setSecondaryProgress(bufferSize);
            if ((getBufferPercentage() / 100f) * getDuration() <= getCurrentPosition() * 1.0f && isPlaying()) {
                progressbar_loading.setVisibility(View.VISIBLE);
            } else {
                progressbar_loading.setVisibility(View.GONE);
            }
            Message message = mHandler.obtainMessage(0);
            mHandler.sendMessageDelayed(message, 500);
            if (currentPosition == sb_seekBar.getMax()) {
                removeUpdateProcess();
            }

        }
    };
    private MySensorEventListener mySensorEventListener;

    public void registerListener() {
        mySensorEventListener = new MySensorEventListener();
        mSensorManager = (SensorManager) mContext.getSystemService(Context.SENSOR_SERVICE);
        Sensor orientation = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
        mSensorManager.registerListener(mySensorEventListener, orientation, SensorManager.SENSOR_DELAY_NORMAL);
    }

    public void unregisterListener() {
//        mSensorManager.unregisterListener(mySensorEventListener);
    }

    public WsVideoPlayer(Context context) {
        this(context, null);
    }

    public WsVideoPlayer(final Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;

        mainView = LayoutInflater.from(mContext).inflate(R.layout.view_player, null);
        sb_seekBar = (SeekBar) mainView.findViewById(R.id.sb_seekBar);
        sb_seekBar.setMax(100);
        sb_seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                seekTo((int) (seekBar.getProgress() / (seekBar.getMax() * 1.0f) * getDuration()));
            }
        });
        tv_process = (TextView) mainView.findViewById(R.id.tv_process);
        tv_duration = (TextView) mainView.findViewById(R.id.tv_duration);
        img_pauseplayend = (ImageView) mainView.findViewById(R.id.img_pauseplayend);
        img_pauseplayend.setOnClickListener(this);
        progressbar_loading = (ProgressBar) mainView.findViewById(R.id.progressbar_loading);
        fl_videoplayer = (FrameLayout) mainView.findViewById(R.id.fl_videoplayer);
        fl_toplayout = (FrameLayout) mainView.findViewById(R.id.fl_toplayout);
        ll_bottomlayout = (LinearLayout) mainView.findViewById(R.id.ll_bottomlayout);
        mVideoPlayer = (MyVideoPlayer) mainView.findViewById(R.id.videoplayer);
        mVideoPlayer.setKeepScreenOn(true);
//        mVideoView.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dp2px(MainActivity.this, 200)));
        mVideoPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                //如果外部设置监听则把事件传递出去
                if (mOnPreparedListener != null) {
                    mOnPreparedListener.onPrepared(mp);
                }
                mp.setOnSeekCompleteListener(new MediaPlayer.OnSeekCompleteListener() {
                    @Override
                    public void onSeekComplete(MediaPlayer mp) {
                        if (!ispause) {
                            start();
                        }
                    }
                });
                progressbar_loading.setVisibility(View.GONE);
                mHandler.sendEmptyMessage(0);

            }
        });
        mVideoPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                if (mOnCompletionListener != null) {
                    mOnCompletionListener.onCompletion(mp);
                }
                img_pauseplayend.setVisibility(View.VISIBLE);
            }
        });
        mVideoPlayer.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                videoPlayerGestrueDetector = new GestureDetector(mContext, new GestureDetector.SimpleOnGestureListener() {
                    @Override
                    public boolean onDown(MotionEvent e) {
                        menuAnim();
                        return true;
                    }

                    @Override
                    public void onLongPress(MotionEvent e) {
                        super.onLongPress(e);
                    }

                    @Override
                    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                        return super.onScroll(e1, e2, distanceX, distanceY);
                    }
                });
                return videoPlayerGestrueDetector.onTouchEvent(event);
            }
        });

        img_back = (ImageView) mainView.findViewById(R.id.img_back);
        img_back.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                back();
            }
        });
        img_fullscreen = (ImageView) mainView.findViewById(R.id.img_fullscreen);
        img_fullscreen.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                changeScreenOrientation();
            }
        });
        addView(mainView);
    }

    private void changeScreenOrientation() {
        if (((Activity) mContext).getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            ((Activity) mContext).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            ((Activity) mContext).getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        } else {
            ((Activity) mContext).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            ((Activity) mContext).getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
    }

    private void menuAnim() {
        int visiable = fl_toplayout.getVisibility();
        if (visiable == View.GONE) {
            fl_toplayout.setVisibility(visiable == View.GONE ? View.VISIBLE : View.GONE);
            ll_bottomlayout.setVisibility(visiable == View.GONE ? View.VISIBLE : View.GONE);
            TranslateAnimation translateAnimation_top_in = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, -1f, Animation.RELATIVE_TO_SELF, 0f);
            translateAnimation_top_in.setDuration(300);
            translateAnimation_top_in.setFillAfter(true);
            fl_toplayout.startAnimation(translateAnimation_top_in);
            TranslateAnimation translateAnimation_bottom_in = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 1f, Animation.RELATIVE_TO_SELF, 0f);
            translateAnimation_bottom_in.setDuration(300);
            translateAnimation_bottom_in.setFillAfter(true);
            ll_bottomlayout.startAnimation(translateAnimation_bottom_in);
        } else {
            fl_toplayout.setVisibility(visiable == View.GONE ? View.VISIBLE : View.GONE);
            ll_bottomlayout.setVisibility(visiable == View.GONE ? View.VISIBLE : View.GONE);
            TranslateAnimation translateAnimation_top_out = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, -1f);
            translateAnimation_top_out.setDuration(300);
            translateAnimation_top_out.setFillAfter(true);
            fl_toplayout.startAnimation(translateAnimation_top_out);
            TranslateAnimation translateAnimation_bottom_out = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 1f);
            translateAnimation_bottom_out.setDuration(300);
            translateAnimation_bottom_out.setFillAfter(true);
            ll_bottomlayout.startAnimation(translateAnimation_bottom_out);
        }
    }

    public WsVideoPlayer setVideoURI(String uri) {
        mVideoPlayer.setVideoURI(Uri.parse(uri));
        return this;
    }

    public void start() {
        mVideoPlayer.start();
    }

    public void pause() {
        mVideoPlayer.pause();
    }

    public int getDuration() {
        return mVideoPlayer.getDuration();
    }

    public int getCurrentPosition() {
        return mVideoPlayer.getCurrentPosition();
    }

    protected void back() {
        if (((Activity) mContext).getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            ((Activity) mContext).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            ((Activity) mContext).getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        } else {
            ((Activity) mContext).finish();
        }
    }

    public void setOnPreparedListener(final OnPreparedListener l) {
        mOnPreparedListener = l;


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_pauseplayend:
                img_pauseplayend.setVisibility(View.GONE);
                seekTo(0);
                mHandler.sendEmptyMessage(0);
                break;
        }
    }

    public interface OnPreparedListener {
        void onPrepared(MediaPlayer mp);
    }

    public void setOnCompletionListener(OnCompletionListener l) {
        mOnCompletionListener = l;
    }

    public interface OnCompletionListener {
        void onCompletion(MediaPlayer mp);
    }

    public void seekTo(int msec) {
        mVideoPlayer.seekTo(msec);
    }

    public void setKeepScreenOn(boolean keepScreenOn) {
        mVideoPlayer.setKeepScreenOn(keepScreenOn);
    }

    public void removeUpdateProcess() {
        mHandler.removeMessages(0);
    }

    //改变屏幕亮度
    public void setLightness(float lightness) {
        WindowManager.LayoutParams layoutParams = ((Activity) mContext).getWindow().getAttributes();
        //屏幕的亮度,最大是255
        layoutParams.screenBrightness = layoutParams.screenBrightness + lightness / 255f;
        if (layoutParams.screenBrightness > 1) {
            layoutParams.screenBrightness = 1;
        } else if (layoutParams.screenBrightness < 0.2) {
            layoutParams.screenBrightness = 0.2f;
        }
        ((Activity) mContext).getWindow().setAttributes(layoutParams);
    }

    public void resume() {
        mVideoPlayer.resume();
    }

    public int getBufferPercentage() {
        int buffer = mVideoPlayer.getBufferPercentage();
        return mVideoPlayer.getBufferPercentage();
    }

    public boolean isPlaying() {
        return mVideoPlayer.isPlaying();
    }

    public boolean requestWsFocus() {
        return mVideoPlayer.requestFocus();
    }

    class MySensorEventListener implements SensorEventListener {

        @Override
        public void onSensorChanged(SensorEvent event) {
            // TODO Auto-generated method stub

            float b = event.values[1];
            float c = event.values[2];
            float a = event.values[0];
            if ((b <= -5.0f || b >= 5) && ((Activity) (mContext)).getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED) {

            } else if ((c <= -5.0f || c >= 5) && ((Activity) (mContext)).getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED) {
                ((Activity) mContext).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                ((Activity) mContext).getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            } else if ((b <= -5.0f || b >= 5) && ((Activity) (mContext)).getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
                if (haveChanged) {
                    changeScreenOrientation();
                    haveChanged = false;
                }
            } else if ((c <= -5.0f || c >= 5) && ((Activity) (mContext)).getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
                if (haveChanged)
                    changeScreenOrientation();
                haveChanged = false;
            }
        }


        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            // TODO Auto-generated method stub

        }

    }
}
