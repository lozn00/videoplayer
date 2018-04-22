package cn.qssq666.videoplayer.playermanager.player_messages;

import cn.qssq666.videoplayer.playermanager.MyMessagesHandlerThread;
import cn.qssq666.videoplayer.playermanager.PlayerMessageState;
import cn.qssq666.videoplayer.playermanager.manager.VideoPlayerManagerCallback;
import cn.qssq666.videoplayer.playermanager.ui.VideoPlayerView;

/**
 * This PlayerMessage clears MediaPlayer instance that was used inside {@link VideoPlayerView}
 */
public class ClearPlayerInstanceAndDestoryThread extends PlayerMessage {

    private MyMessagesHandlerThread myMessagesHandlerThread;

    public ClearPlayerInstanceAndDestoryThread(VideoPlayerView videoPlayerView, VideoPlayerManagerCallback callback, MyMessagesHandlerThread myMessagesHandlerThread) {
        super(videoPlayerView, callback);
        this.myMessagesHandlerThread = myMessagesHandlerThread;
    }

    @Override
    protected void performAction(VideoPlayerView currentPlayer) {
//        currentPlayer.clearPlayerInstance();
        myMessagesHandlerThread.terminate();
        myMessagesHandlerThread = null;
        clear();

    }

    @Override
    protected PlayerMessageState stateBefore() {
        return PlayerMessageState.CLEARING_PLAYER_INSTANCE;
    }

    @Override
    protected PlayerMessageState stateAfter() {
        return PlayerMessageState.PLAYER_INSTANCE_CLEARED;
    }
}
