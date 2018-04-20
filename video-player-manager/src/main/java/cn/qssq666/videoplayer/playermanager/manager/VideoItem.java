package cn.qssq666.videoplayer.playermanager.manager;

import cn.qssq666.videoplayer.playermanager.meta.MetaData;
import cn.qssq666.videoplayer.playermanager.ui.VideoPlayerView;

/**
 * This is basic interface for Items in Adapter of the list. Regardless of is it {@link android.widget.ListView}
 * or {@link android.support.v7.widget.RecyclerView}
 */
public interface VideoItem {
    void playNewVideo(MetaData currentItemMetaData, VideoPlayerView player, VideoPlayerManager<MetaData> videoPlayerManager);
    void stopPlayback(VideoPlayerManager videoPlayerManager);
}
