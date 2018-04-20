package cn.qssq666.videoplayer.playermanager;

import cn.qssq666.videoplayer.playermanager.manager.VideoPlayerManagerCallback;
import cn.qssq666.videoplayer.playermanager.meta.MetaData;
import cn.qssq666.videoplayer.playermanager.player_messages.PlayerMessage;
import cn.qssq666.videoplayer.playermanager.ui.VideoPlayerView;

public class SetNewViewForPlayback extends PlayerMessage {

    private final MetaData mCurrentItemMetaData;
    private final VideoPlayerView mCurrentPlayer;
    private final VideoPlayerManagerCallback mCallback;

    public SetNewViewForPlayback(MetaData currentItemMetaData, VideoPlayerView videoPlayerView, VideoPlayerManagerCallback callback) {
        super(videoPlayerView, callback);
        mCurrentItemMetaData = currentItemMetaData;
        mCurrentPlayer = videoPlayerView;
        mCallback = callback;
    }

    @Override
    public String toString() {
        return SetNewViewForPlayback.class.getSimpleName() + ", mCurrentPlayer " + mCurrentPlayer;
    }

    @Override
    protected void performAction(VideoPlayerView currentPlayer) {
        mCallback.setCurrentItem(mCurrentItemMetaData, mCurrentPlayer);
    }

    @Override
    protected PlayerMessageState stateBefore() {
        return PlayerMessageState.SETTING_NEW_PLAYER;
    }

    @Override
    protected PlayerMessageState stateAfter() {
        return PlayerMessageState.IDLE;
    }
}
