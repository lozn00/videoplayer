package cn.qssq666.videoplayrmanger.ui;


import android.media.MediaPlayer;

import cn.qssq666.videoplayer.playermanager.Config;
import cn.qssq666.videoplayer.playermanager.ui.MediaPlayerWrapper;
import cn.qssq666.videoplayer.playermanager.utils.Logger;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;

public class MediaPlayerWrapperMainThreadListenerTest {

    private static final String TAG = MediaPlayerWrapperMainThreadListenerTest.class.getSimpleName();
    private static final boolean SHOW_LOGS = Config.SHOW_LOGS;

    private final Object mSyncObject = new Object();

    private MediaPlayerWrapper mMediaPlayerWrapper;

    private ExecutorService mExecutorService;

    private MediaPlayerWrapper.MainThreadMediaPlayerListener mMainThreadMediaPlayerListener = new MediaPlayerWrapper.MainThreadMediaPlayerListener() {
        @Override
        public void onVideoSizeChangedMainThread(int width, int height) {
        }

        @Override
        public void onVideoPreparedMainThread() {
            if (SHOW_LOGS) Logger.v(TAG, "onVideoPreparedMainThread");

            assertEquals(Thread.currentThread().getId(), 1);

            synchronized (mSyncObject){
                mSyncObject.notify();
            }
        }

        @Override
        public void onProgressUpdate(int percent) {
            if (SHOW_LOGS) Logger.v(TAG, "onProgressUpdate");


        }

        @Override
        public void onVideoCompletionMainThread() {
            if (SHOW_LOGS) Logger.v(TAG, "onVideoCompletionMainThread");
        }

        @Override
        public void onErrorMainThread(int what, int extra) {
            if (SHOW_LOGS) Logger.v(TAG, "onErrorMainThread");
        }

        @Override
        public void onBufferingUpdateMainThread(int percent) {
            if (SHOW_LOGS) Logger.v(TAG, "onBufferingUpdateMainThread "+percent);
        }


        @Override

        public void onVideoStoppedMainThread() {
            if (SHOW_LOGS) Logger.v(TAG, "onVideoStoppedMainThread ");

        }

        @Override
        public void onPrepared(MediaPlayer mp) {

            if (SHOW_LOGS) Logger.v(TAG, "onPrepared ");
        }

        @Override
        public void onPrepare() {

            if (SHOW_LOGS) Logger.v(TAG, "onPrepare ");
        }

        @Override
        public void onVideoPausedMainThread() {
            if (SHOW_LOGS) Logger.v(TAG, "onVideoPausedMainThread ");

        }

        @Override
        public void onVideoStartedMainThread() {
            if (SHOW_LOGS) Logger.v(TAG, "onVideoStartedMainThread ");

        }

        @Override
        public void onInfo(MediaPlayer mp, int what, int extra) {

            if (SHOW_LOGS) Logger.v(TAG, "onInfo  "+what+",extra:"+extra);
        }
    };

    @Before
    public void setUp() throws Exception {
        if (SHOW_LOGS) Logger.v(TAG, "setUp");
        mExecutorService = Executors.newSingleThreadExecutor();

        mMediaPlayerWrapper = new MediaPlayerWrapperTest.MockMediaPlayerWrapper(new MediaPlayerWrapperTest.MockMediaPlayer());
        mMediaPlayerWrapper.setMainThreadMediaPlayerListener(mMainThreadMediaPlayerListener);
    }

    @After
    public void tearDown() throws Exception {
        if (SHOW_LOGS) Logger.v(TAG, "tearDown");
        mMediaPlayerWrapper = null;
        mExecutorService.shutdown();
        mExecutorService = null;
    }

    @Test
    public void testPrepared() throws Exception {
        if (SHOW_LOGS) Logger.v(TAG, ">> testPrepared");

        mExecutorService.execute(new Runnable() {
            @Override
            public void run() {
                if (SHOW_LOGS) Logger.v(TAG, "testPrepared, run");
                Exception exception = null;
                try {
                    mMediaPlayerWrapper.setDataSource("");
                    mMediaPlayerWrapper.prepare();

                } catch (IOException e) {
                    e.printStackTrace();
                    exception = e;
                }
                assertNull(exception);
            }
        });

        synchronized (mSyncObject){
            mSyncObject.wait();
        }

        assertNotNull(mMediaPlayerWrapper);

        if (SHOW_LOGS) Logger.v(TAG, "<< testPrepared");
    }
}

