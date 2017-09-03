package com.simplerecycleview.wangsheng;

import android.content.res.Configuration;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

public class MainActivity extends AppCompatActivity {
    private WsVideoPlayer mVideoView;
    private static final String videoSrc = "http://app.daiyutv.com/statics/uploads/media/videos/20170811/15024375888627.mp4";
    private int currentPosition;
    private boolean isPlaying;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mVideoView = (WsVideoPlayer) findViewById(R.id.videoplayer);
        mVideoView.setVideoURI(videoSrc);
        mVideoView.setOnPreparedListener(new WsVideoPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {

            }
        });
        isPlaying = true;
        mVideoView.start();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        mVideoView.setHaveChanged(true);
        super.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onResume() {
        super.onResume();
//        mVideoView.registerListener();
        mVideoView.resume();
        mVideoView.seekTo(currentPosition);
        if (!isPlaying) {
            mVideoView.pause();
        } else {
            mVideoView.start();
        }
    }

    @Override
    protected void onPause() {
        currentPosition = mVideoView.getCurrentPosition();
        isPlaying = mVideoView.isPlaying();
        mVideoView.pause();
        Log.e("MainActivity", "onStop: ");
//        mVideoView.pause();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mVideoView.unregisterListener();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }
}
