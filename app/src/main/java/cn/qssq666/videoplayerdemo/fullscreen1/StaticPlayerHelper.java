package cn.qssq666.videoplayerdemo.fullscreen1;

import cn.qssq666.videoplayer.playermanager.manager.PlayerItemChangeListener;
import cn.qssq666.videoplayer.playermanager.manager.SingleVideoPlayerManager;
import cn.qssq666.videoplayer.playermanager.meta.MetaData;

/**
 * Created by qssq on 2018/4/27 qssq666@foxmail.com
 */
public class StaticPlayerHelper {


    private static SingleVideoPlayerManager playerManager;

    public static SingleVideoPlayerManager getInstance() {

        if (playerManager == null) {

            synchronized (SingleVideoPlayerManager.class) {
                if (playerManager == null) {
                    playerManager = new SingleVideoPlayerManager(new PlayerItemChangeListener() {
                        @Override
                        public void onPlayerItemChanged(MetaData currentItemMetaData) {

                        }
                    });

                }

            }
        }

        return playerManager;

    }


    /**
     * 只有界面销毁的时候才可以调用，销毁后，不可以再继续拿管理器
     */

    public static void releaseAllPlayer() {


        if (playerManager != null) {
            playerManager.destory();

        }
        playerManager = null;

    }

}
