package cn.qssq666.videoplayer.playermanager.player_messages;

import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;

import cn.qssq666.videoplayer.playermanager.manager.VideoPlayerManagerCallback;
import cn.qssq666.videoplayer.playermanager.ui.VideoPlayerView;

import java.io.FileDescriptor;

/**
 * This PlayerMessage calls {@link MediaPlayer#setDataSource(FileDescriptor)} on the instance that is used inside {@link VideoPlayerView}
 */
public class SetAssetsDataSourceMessage extends SetDataSourceMessage{

    private final AssetFileDescriptor mAssetFileDescriptor;

    public SetAssetsDataSourceMessage(VideoPlayerView videoPlayerView, AssetFileDescriptor assetFileDescriptor, VideoPlayerManagerCallback callback) {
        super(videoPlayerView, callback);
        mAssetFileDescriptor = assetFileDescriptor;
    }

    @Override
    protected void performAction(VideoPlayerView currentPlayer) {
        currentPlayer.setDataSource(mAssetFileDescriptor);
    }
}
