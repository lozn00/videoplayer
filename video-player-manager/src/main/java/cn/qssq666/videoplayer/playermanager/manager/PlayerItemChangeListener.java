package cn.qssq666.videoplayer.playermanager.manager;

import cn.qssq666.videoplayer.playermanager.meta.MetaData;

/**
 * Created by danylo.volokh on 06.01.2016.
 */
public interface PlayerItemChangeListener {
    void onPlayerItemChanged(MetaData currentItemMetaData);
}
