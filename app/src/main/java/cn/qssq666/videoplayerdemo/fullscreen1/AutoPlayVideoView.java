package cn.qssq666.videoplayerdemo.fullscreen1;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.MediaPlayer;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.Locale;

import cn.qssq666.videoplayer.playermanager.meta.MetaData;
import cn.qssq666.videoplayer.playermanager.ui.MediaPlayerWrapper;
import cn.qssq666.videoplayer.playermanager.ui.VideoPlayerView;
import cn.qssq666.videoplayerdemo.R;

/**
 * Created by qssq on 2018/4/27 qssq666@foxmail.com
 */
public class AutoPlayVideoView extends FrameLayout {

    private VideoPlayerView videoPlayerView;
    private MediaPlayerWrapper.MainThreadMediaPlayerListener listener;

    public String getVideoPath() {
        return videoPath;
    }

    public void setVideoPath(String videoPath) {
        this.videoPath = videoPath;
    }

    String videoPath;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    int type;

    public DetailInfoVideoViewHolder getHolder() {
        return holder;
    }

    private DetailInfoVideoViewHolder holder;

    public AutoPlayVideoView(Context context) {
        super(context);
        init(context);
    }

    public AutoPlayVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public AutoPlayVideoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @SuppressLint("NewApi")
    public AutoPlayVideoView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(final Context context) {


        videoPlayerView = new VideoPlayerView(context);


        addView(videoPlayerView, MarginLayoutParams.MATCH_PARENT, MarginLayoutParams.MATCH_PARENT);


//view_item_video
        View view = LayoutInflater.from(context).inflate(R.layout.view_item_stand_videoview, this, false);

        holder = new DetailInfoVideoViewHolder(view);


        listener = new MediaPlayerWrapper.MainThreadMediaPlayerListener() {
            @Override
            public void onVideoSizeChangedMainThread(int width, int height) {


            }

            @Override
            public void onVideoPreparedMainThread() {


            }

            @Override
            public void onProgressUpdate(int percent) {


            }

            @Override
            public void onVideoCompletionMainThread() {

                holder.btnPlay.setVisibility(VISIBLE);
                holder.ivBg.setVisibility(VISIBLE);
                holder.tvErrorView.setVisibility(INVISIBLE);
                if (loop) {
                    holder.videoPlayerView.restart();
                }
            }

            @Override
            public void onErrorMainThread(int what, int extra) {


                holder.btnPlay.setVisibility(VISIBLE);
                holder.tvCacheProgress.setVisibility(INVISIBLE);
                holder.ivBg.setVisibility(VISIBLE);
                holder.tvErrorView.setVisibility(VISIBLE);
                holder.tvErrorView.setText("出现错误 errCode:" + what + " extra:" + extra);
            }

            @Override
            public void onBufferingUpdateMainThread(int percent) {


                if (percent >= 100) {
                    holder.tvCacheProgress.setText("0%");
                    holder.tvCacheProgress.setVisibility(INVISIBLE);
                } else {

                    holder.tvCacheProgress.setText(percent + "%");
                    holder.tvCacheProgress.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onVideoStoppedMainThread() {

                holder.tvCacheProgress.setVisibility(View.INVISIBLE);
                holder.btnPlay.setVisibility(VISIBLE);


            }

            @Override
            public void onPrepared(MediaPlayer mp) {


                holder.btnPlay.setVisibility(View.INVISIBLE);
                holder.tvStartTime.setText("" + generateTime(videoPlayerView.getCurrentPosition()));
                holder.tvEndTime.setText("" + generateTime(videoPlayerView.getDuration()));
            }

            @Override
            public void onPrepare() {

                AutoPlayVideoView.this.onPrepare();
            }

            @Override
            public void onVideoPausedMainThread() {


                holder.ivBg.setVisibility(View.VISIBLE);
                holder.btnPlay.setVisibility(View.VISIBLE);

            }

            @Override
            public void onVideoStartedMainThread() {


                holder.ivBg.setVisibility(View.INVISIBLE);
                holder.btnPlay.setVisibility(View.INVISIBLE);
                holder.tvCacheProgress.setVisibility(View.INVISIBLE);

            }

            @Override
            public void onInfo(MediaPlayer mp, int what, int extra) {

                if (what == MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START) {
                    holder.ivBg.setVisibility(View.INVISIBLE);
                }

            }
        };
        videoPlayerView.addMediaPlayerListener(listener);
        videoPlayerView.addVideoProgressUpdateListener(new MediaPlayerWrapper.VideoStateListener() {
            @Override
            public void onVideoPlayTimeChanged(int positionInMilliseconds) {

                holder.seekBar.setProgress((int) (positionInMilliseconds * 1.0f / videoPlayerView.getDuration() * 1000l));
            }
        });

        holder.viewPlayerMask.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {


                if (StaticPlayerHelper.getInstance().isCurrent(videoPath)) {

                    if (StaticPlayerHelper.getInstance().isPlay()) {
                        StaticPlayerHelper.getInstance().pause();
                    } else if (StaticPlayerHelper.getInstance().isPause()) {
                        StaticPlayerHelper.getInstance().continuePlay();


                    } else {

                        AutoPlayVideoView.this.onPrepare();

                        playNewVideo();
                    }

                } else {

                    AutoPlayVideoView.this.onPrepare();

                    playNewVideo();

                }


            }


        });

        holder.seekBar.setMax(1000);
        holder.seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                if (fromUser) {
                    if (StaticPlayerHelper.getInstance().isPause() || StaticPlayerHelper.getInstance().isPlay()) {
                        videoPlayerView.seekToPercent((long) (progress / 1000f * 100l));

                    } else {
                        holder.seekBar.setProgress(0);
                    }
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        addView(view);
    }

    private void onPrepare() {
        holder.tvErrorView.setVisibility(View.INVISIBLE);
        holder.btnPlay.setVisibility(View.INVISIBLE);
        holder.tvCacheProgress.setText("0%");
        holder.tvCacheProgress.setVisibility(View.VISIBLE);
    }


    /**
     * 如果是列表选中自动播放，如果本来就是当前的话可以采取暂停转播放
     *
     * @return
     */
    public boolean needPlayNew() {
        return !StaticPlayerHelper.getInstance().isCurrent(videoPath);
    }


    /**
     * 当选中的时候 就进行播放
     */
    public void onSelectAndAutoPlay() {
        if (StaticPlayerHelper.getInstance().isCurrent(videoPath)) {

            if (StaticPlayerHelper.getInstance().isPause()) {
                StaticPlayerHelper.getInstance().onResume();
            }
        } else {

            AutoPlayVideoView.this.onPrepare();
            playNewVideo();
        }
    }


    public void playNewVideo() {
        if (videoPath != null) {

            if (videoPath.startsWith("http")) {
                StaticPlayerHelper.getInstance().playNewVideo(new MetaData() {
                }, videoPlayerView, videoPath);


            } else {

                try {
                    StaticPlayerHelper.getInstance().playNewVideo(new MetaData() {
                    }, videoPlayerView, videoPath, getContext().getAssets().openFd(videoPath));
                } catch (IOException e) {

                    e.printStackTrace();
                    listener.onErrorMainThread(-1, 250);


                }

            }
        } else {
            Toast.makeText(getContext(), "video address is empty!", Toast.LENGTH_SHORT).show();
        }
    }


    public static String generateTime(long position) {
        if (position <= 0) {
            return "00:00";
        }
        int totalSeconds = (int) (position / 1000);

        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        int hours = totalSeconds / 3600;

        if (hours > 0) {
            return String.format(Locale.US, "%02d:%02d:%02d", hours, minutes,
                    seconds).toString();
        } else {
            return String.format(Locale.US, "%02d:%02d", minutes, seconds)
                    .toString();
        }
    }

    public void destory() {
        StaticPlayerHelper.releaseAllPlayer();


    }

    public boolean isLoop() {
        return loop;
    }

    public void setLoop(boolean loop) {
        this.loop = loop;
    }

    boolean loop;

    public class DetailInfoVideoViewHolder extends RecyclerView.ViewHolder {

        public final TextView tvTitle;
        public final ImageView ivBg;
        public final View btnPlay;
        public final View viewPlayerMask;
        public final VideoPlayerView videoPlayerView;
        public final SeekBar seekBar;
        public final TextView tvStartTime;
        public final TextView tvCacheProgress;
        public final TextView tvErrorView;
        public TextView tvEndTime;

        public DetailInfoVideoViewHolder(View itemView) {
            super(itemView);
            tvTitle = (TextView) itemView.findViewById(R.id.tv_title);
            ivBg = (ImageView) itemView.findViewById(R.id.image);
            videoPlayerView = (VideoPlayerView) itemView.findViewById(R.id.video_player);
            btnPlay = itemView.findViewById(R.id.btn_play);
            viewPlayerMask = itemView.findViewById(R.id.video_player_mask);
            seekBar = ((SeekBar) itemView.findViewById(R.id.seek_bar_play_process));
            //tv_start_time
            tvStartTime = itemView.findViewById(R.id.tv_start_time);
            tvCacheProgress = itemView.findViewById(R.id.tv_cache_progress);
            tvEndTime = itemView.findViewById(R.id.tv_end_time);
            tvErrorView = itemView.findViewById(R.id.tv_error_view);


        }
    }


}
