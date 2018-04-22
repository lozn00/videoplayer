package cn.qssq666.videoplayer.playermanager.player_messages;

import cn.qssq666.videoplayer.playermanager.Config;
import cn.qssq666.videoplayer.playermanager.PlayerMessageState;
import cn.qssq666.videoplayer.playermanager.manager.VideoPlayerManagerCallback;
import cn.qssq666.videoplayer.playermanager.ui.VideoPlayerView;
import cn.qssq666.videoplayer.playermanager.utils.Logger;

/**
 * This is generic interface for PlayerMessage
 */
public abstract class PlayerMessage implements Message {

    private static final String TAG = PlayerMessage.class.getSimpleName();
    private static final boolean SHOW_LOGS = Config.SHOW_LOGS;
    private  VideoPlayerView mCurrentPlayer;
    private final VideoPlayerManagerCallback mCallback;

    public PlayerMessage(VideoPlayerView currentPlayer, VideoPlayerManagerCallback callback) {
        mCurrentPlayer = currentPlayer;
        mCallback = callback;
    }

    public void clear(){
        mCurrentPlayer=null;
    }
    protected final PlayerMessageState getCurrentState(){
        return mCallback.getCurrentPlayerState();
    }

    @Override
    public final void polledFromQueue() {
        mCallback.setVideoPlayerState(mCurrentPlayer, stateBefore());
    }

    @Override
    public final void messageFinished() {
        mCallback.setVideoPlayerState(mCurrentPlayer, stateAfter());
    }

    public final void runMessage(){
        if(SHOW_LOGS) Logger.v(TAG, ">> runMessage, " + getClass().getSimpleName());

        performAction(mCurrentPlayer);
        if(SHOW_LOGS) Logger.v(TAG, "<< runMessage, " + getClass().getSimpleName());
    }

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }

    protected abstract void performAction(VideoPlayerView currentPlayer);
    protected abstract PlayerMessageState stateBefore();
    protected abstract PlayerMessageState stateAfter();

}
