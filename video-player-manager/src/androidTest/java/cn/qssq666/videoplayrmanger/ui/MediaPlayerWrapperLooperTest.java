package cn.qssq666.videoplayrmanger.ui;

import android.os.Looper;

import cn.qssq666.videoplayer.playermanager.Config;
import cn.qssq666.videoplayer.playermanager.ui.MediaPlayerWrapper;
import cn.qssq666.videoplayer.playermanager.utils.HandlerThreadExtension;
import cn.qssq666.videoplayer.playermanager.utils.Logger;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.assertNotNull;

public class MediaPlayerWrapperLooperTest {

    private static final String TAG = MediaPlayerWrapperLooperTest.class.getSimpleName();
    private static final boolean SHOW_LOGS = Config.SHOW_LOGS;

    private final Object mSyncObject = new Object();

    private MediaPlayerWrapper mMediaPlayerWrapper;
    private MediaPlayerWrapperTest.MockMediaPlayer mMediaPlayer;
    private HandlerThreadExtension mHandlerThread;

    /**
     * 安装
     *
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception {
        if (SHOW_LOGS) Logger.v(TAG, "setUp");
        mHandlerThread = new HandlerThreadExtension(TAG, false);
        mHandlerThread.startThread();
    }

    /**
     * 拆卸
     *
     * @throws Exception
     */

    @After
    public void tearDown() throws Exception {
        if (SHOW_LOGS) Logger.v(TAG, "tearDown");
        mMediaPlayer = null;
        mMediaPlayerWrapper = null;
        mHandlerThread.postQuit();
        mHandlerThread = null;
    }

    /**
     * 轮训
     *
     * @throws Exception
     */
    @Test
    public void testLopper() throws Exception {

        mHandlerThread.post(new Runnable() {

            private int count = 0;

            @Override
            public void run() {

                long startTime = System.currentTimeMillis();
                if (SHOW_LOGS) Logger.v(TAG, "run  thread loopName:" + Looper.myLooper());

                Exception exception = null;
                try {
                    mMediaPlayer = new MediaPlayerWrapperTest.MockMediaPlayer();
                    mMediaPlayerWrapper = new MediaPlayerWrapperTest.MockMediaPlayerWrapper(mMediaPlayer);
                } catch (RuntimeException e) {
                    exception = e;
                }

                assertNotNull(exception);

                synchronized (mSyncObject) {
                    while (count < 5) {
                        try {
                            Thread.sleep(1000);

                            Logger.w(TAG, "已流失时间:" + (System.currentTimeMillis()-startTime));
                            count++;
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                    }


                    Logger.w(TAG, "notify执行开始");
                    mSyncObject.notify();//Exception in thread "Thread-0" java.lang.IllegalMonitorStateException
                    Logger.w(TAG, "notify执行完毕");
                }
            }
        });

        if (SHOW_LOGS) Logger.v(TAG, " loopName:" + Looper.myLooper());

        synchronized (mSyncObject) {
            mSyncObject.wait();
        }
        Logger.w(TAG, "wait执行完毕");
    }
}

/*
04-20 20:20:25.603 21680-21714/? V/MediaPlayerWrapperLooperTest: 5754 setUp
04-20 20:20:25.604 21680-21718/? V/HandlerThreadExtension: onLooperPrepared Thread[MediaPlayerWrapperLooperTest,5,main]
04-20 20:20:25.605 21680-21718/? V/MediaPlayerWrapperLooperTest: 5755 run Looper (MediaPlayerWrapperLooperTest, tid 5755) {6eeb84a}
04-20 20:20:25.615 21680-21718/? V/MockMediaPlayerWrapper@50803643: 5755 constructor of MediaPlayerWrapper
    5755 constructor of MediaPlayerWrapper, main Looper Looper (main, tid 1) {97630d8}
    5755 constructor of MediaPlayerWrapper, my Looper Looper (MediaPlayerWrapperLooperTest, tid 5755) {6eeb84a}
04-20 20:20:25.616 21680-21714/? V/MediaPlayerWrapperLooperTest: 5754 tearDown
 */