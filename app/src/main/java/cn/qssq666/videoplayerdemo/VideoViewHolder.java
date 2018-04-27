package cn.qssq666.videoplayerdemo;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import cn.qssq666.videoplayer.playermanager.ui.VideoPlayerView;

/**
 * Created by qssq on 2018/4/27 qssq666@foxmail.com
 */
public class VideoViewHolder extends RecyclerView.ViewHolder {

    public final TextView tvTitle;
    public final ImageView ivBg;
    public final View btnPlay;
    public final TextView tvErrorView;
    public final View viewPlayerMask;
    public final VideoPlayerView videoPlayerView;
    public final TextView tvCacheProgress;

    public VideoViewHolder(View itemView) {
        super(itemView);
        tvTitle = (TextView) itemView.findViewById(R.id.tv_title);
        ivBg = (ImageView) itemView.findViewById(R.id.image);
        videoPlayerView = (VideoPlayerView) itemView.findViewById(R.id.video_player);
        btnPlay = itemView.findViewById(R.id.btn_play);
        viewPlayerMask = itemView.findViewById(R.id.video_player_mask);
        tvCacheProgress = itemView.findViewById(R.id.tv_cache_progress);
        tvErrorView = itemView.findViewById(R.id.tv_error_view);


    }
}
