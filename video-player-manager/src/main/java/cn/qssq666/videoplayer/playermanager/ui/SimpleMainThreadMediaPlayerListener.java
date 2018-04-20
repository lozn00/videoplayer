package cn.qssq666.videoplayer.playermanager.ui;

import android.media.MediaPlayer;

/**
 * You can use this class and override only those methods you need
 * Created by danylo.volokh on 1/11/2016.
 */
public class SimpleMainThreadMediaPlayerListener implements MediaPlayerWrapper.MainThreadMediaPlayerListener {
    @Override
    public void onVideoSizeChangedMainThread(int width, int height) {

    }

    @Override
    public void onVideoPreparedMainThread() {

    }

    @Override
    public void onProgressUpdate(int percent) {

    }

    @Override
    public void onVideoCompletionMainThread() {

    }

    @Override
    public void onErrorMainThread(int what, int extra) {

    }

    @Override
    public void onBufferingUpdateMainThread(int percent) {

    }

    @Override
    public void onVideoStoppedMainThread() {

    }

    @Override
    public void onPrepared(MediaPlayer mp) {

    }

    @Override
    public void onPrepare() {

    }

    @Override
    public void onVideoPausedMainThread() {

    }

    @Override
    public void onVideoStartedMainThread() {

    }

    @Override
    public void onInfo(MediaPlayer mp, int what, int extra) {

    }
}
