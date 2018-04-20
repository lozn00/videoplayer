package cn.qssq666.videoplayer.playermanager.player_messages;

import android.media.MediaPlayer;

import cn.qssq666.videoplayer.playermanager.Config;
import cn.qssq666.videoplayer.playermanager.PlayerMessageState;
import cn.qssq666.videoplayer.playermanager.manager.VideoPlayerManagerCallback;
import cn.qssq666.videoplayer.playermanager.ui.MediaPlayerWrapper;
import cn.qssq666.videoplayer.playermanager.ui.VideoPlayerView;
import cn.qssq666.videoplayer.playermanager.utils.Logger;

/**
 * This PlayerMessage calls {@link MediaPlayer#prepare()} on the instance that is used inside {@link VideoPlayerView}
 */
public class Prepare extends PlayerMessage{

    private static final boolean SHOW_LOGS = Config.SHOW_LOGS;
    private static final String TAG = Prepare.class.getSimpleName();

    private PlayerMessageState mResultPlayerMessageState;

    public Prepare(VideoPlayerView videoPlayerView, VideoPlayerManagerCallback callback) {
        super(videoPlayerView, callback);
    }

    @Override
    protected void performAction(VideoPlayerView currentPlayer) {

        currentPlayer.prepare();

        MediaPlayerWrapper.State resultOfPrepare = currentPlayer.getCurrentState();
        if(SHOW_LOGS) Logger.v(TAG, "resultOfPrepare " + resultOfPrepare);

        switch (resultOfPrepare){
            case IDLE:
            case INITIALIZED:
            case PREPARING:
            case STARTED:
            case PAUSED:
            case STOPPED:
            case PLAYBACK_COMPLETED:
            case END:
                throw new RuntimeException("unhandled state " + resultOfPrepare);

            case PREPARED:
                mResultPlayerMessageState = PlayerMessageState.PREPARED;
                break;

            case ERROR:
                mResultPlayerMessageState = PlayerMessageState.ERROR;
                break;
        }
    }

    @Override
    protected PlayerMessageState stateBefore() {
        return PlayerMessageState.PREPARING;
    }

    @Override
    protected PlayerMessageState stateAfter() {
        return mResultPlayerMessageState;
    }
}
