package cn.qssq666.videoplayer.playermanager.player_messages;

import android.media.MediaPlayer;

import cn.qssq666.videoplayer.playermanager.PlayerMessageState;
import cn.qssq666.videoplayer.playermanager.manager.VideoPlayerManagerCallback;
import cn.qssq666.videoplayer.playermanager.ui.VideoPlayerView;

/**
 * This PlayerMessage calls {@link MediaPlayer#reset()} on the instance that is used inside {@link VideoPlayerView}
 */
public class Reset extends PlayerMessage {
    public Reset(VideoPlayerView videoPlayerView, VideoPlayerManagerCallback callback) {
        super(videoPlayerView, callback);
    }

    @Override
    protected void performAction(VideoPlayerView currentPlayer) {
        currentPlayer.reset();
    }

    @Override
    protected PlayerMessageState stateBefore() {
        return PlayerMessageState.RESETTING;
    }

    @Override
    protected PlayerMessageState stateAfter() {
        return PlayerMessageState.RESET;
    }
}
