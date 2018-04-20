package cn.qssq666.videoplayer.playermanager.manager;

import cn.qssq666.videoplayer.playermanager.PlayerMessageState;
import cn.qssq666.videoplayer.playermanager.meta.MetaData;
import cn.qssq666.videoplayer.playermanager.ui.VideoPlayerView;
import cn.qssq666.videoplayer.playermanager.player_messages.PlayerMessage;

/**
 * This callback is used by {@link PlayerMessage}
 * to get and set data it needs
 */
public interface VideoPlayerManagerCallback {

    void setCurrentItem(MetaData currentItemMetaData, VideoPlayerView newPlayerView);

    void setVideoPlayerState(VideoPlayerView videoPlayerView, PlayerMessageState playerMessageState);

    PlayerMessageState getCurrentPlayerState();
}
