package cn.qssq666.videoplayer.playermanager.test;

import android.annotation.SuppressLint;
import android.os.Build;
import android.view.Choreographer;

/**
 * Created by qssq on 2018/4/22 qssq666@foxmail.com
 */
public class TestUtil {

        public static void start() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                Choreographer.getInstance()
                        .postFrameCallback(new Choreographer.FrameCallback() {
                            @SuppressLint("NewApi")
                            @Override
                            public void doFrame(long l) {
                                if (LogMonitor.getInstance().isMonitor()) {
                                    LogMonitor.getInstance().removeMonitor();
                                }
                                LogMonitor.getInstance().startMonitor();
                                Choreographer.getInstance().postFrameCallback(this);
                            }
                        });
            }
        }
}
