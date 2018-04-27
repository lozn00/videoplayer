package cn.qssq666.videoplayerdemo;

import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.IOException;
import java.util.List;

import cn.qssq666.videoplayer.playermanager.manager.VideoPlayerManager;
import cn.qssq666.videoplayer.playermanager.ui.MediaPlayerWrapper;

/**
 * Created by qssq on 2018/4/26 qssq666@foxmail.com
 */
public class HomeCustomAdapter extends RecyclerView.Adapter<VideoViewHolder> {
    private static final String TAG = "HomeCustomAdapter";
    private VideoPlayerManager<VideoModel> playMnager;

    public List<VideoModel> getList() {
        return list;
    }

    public void setList(List<VideoModel> list) {
        this.list = list;
    }

    List<VideoModel> list;

    @NonNull
    @Override
    public VideoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_item_video,parent,false);
        return new VideoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final VideoViewHolder holder, int position) {

        final VideoModel videoModel = list.get(position);
        holder.tvTitle.setText(videoModel.getName());
        ImageLoader.getInstance().displayImage(videoModel.getImage(), holder.ivBg);


        holder.viewPlayerMask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Toast.makeText(v.getContext(), "你点击了播放", Toast.LENGTH_SHORT).show();

                play(playMnager,v, videoModel, holder);

            }

        });

        MediaPlayerWrapper.MainThreadMediaPlayerListener listener = new MediaPlayerWrapper.MainThreadMediaPlayerListener() {
            @Override
            public void onVideoSizeChangedMainThread(int width, int height) {


                Log.w(TAG,"onVideoSizeChangedMainThread");

            }

            @Override
            public void onVideoPreparedMainThread() {
                Log.w(TAG,"onVideoPreparedMainThread");

            }

            @Override
            public void onProgressUpdate(int percent) {
                Log.w(TAG,"onProgressUpdate");

            }

            @Override
            public void onVideoCompletionMainThread() {
                Log.w(TAG,"onVideoCompletionMainThread");
                holder.btnPlay.setVisibility(View.VISIBLE);
                holder.ivBg.setVisibility(View.VISIBLE);
                holder.tvErrorView.setVisibility(View.INVISIBLE);
              /*  if (loop) {
                    holder.videoPlayerView.restart();
                }*/
            }

            @Override
            public void onErrorMainThread(int what, int extra) {
                Log.w(TAG,"onErrorMainThread");

                holder.btnPlay.setVisibility(View.VISIBLE);
                holder.tvCacheProgress.setVisibility(View.INVISIBLE);
                holder.ivBg.setVisibility(View.VISIBLE);
                holder.tvErrorView.setVisibility(View.VISIBLE);
                holder.tvErrorView.setText("出现错误 errCode:" + what + " extra:" + extra);
            }

            @Override
            public void onBufferingUpdateMainThread(int percent) {
                Log.w(TAG,"onBufferingUpdateMainThread");
                if (percent >= 100) {
                    holder.tvCacheProgress.setText("0%");
                    holder.tvCacheProgress.setVisibility(View.INVISIBLE);
                } else {

                    holder.tvCacheProgress.setText(percent + "%");
                    holder.tvCacheProgress.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onVideoStoppedMainThread() {
                Log.w(TAG,"onVideoStoppedMainThread");
//                holder.tvCacheProgress.setVisibility(View.INVISIBLE);
                holder.btnPlay.setVisibility(View.VISIBLE);


            }

            @Override
            public void onPrepared(MediaPlayer mp) {
                Log.w(TAG,"onPrepared");

                holder.btnPlay.setVisibility(View.INVISIBLE);
//                holder.tvStartTime.setText("" + generateTime(videoPlayerView.getCurrentPosition()));
//                holder.tvEndTime.setText("" + generateTime(videoPlayerView.getDuration()));
            }

            @Override
            public void onPrepare() {
                Log.w(TAG,"onPrepare");
                holder.btnPlay.setVisibility(View.INVISIBLE);
                holder.tvErrorView.setVisibility(View.INVISIBLE);

            }

            @Override
            public void onVideoPausedMainThread() {
                Log.w(TAG,"onVideoPausedMainThread");

                holder.ivBg.setVisibility(View.VISIBLE);
                holder.btnPlay.setVisibility(View.VISIBLE);

            }

            @Override
            public void onVideoStartedMainThread() {

                Log.w(TAG,"onVideoStartedMainThread");
                holder.ivBg.setVisibility(View.INVISIBLE);
                holder.btnPlay.setVisibility(View.INVISIBLE);
//                holder.tvCacheProgress.setVisibility(View.INVISIBLE);

            }

            @Override
            public void onInfo(MediaPlayer mp, int what, int extra) {
                Log.w(TAG,"onInfo");
                if (what == MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START) {
                    holder.ivBg.setVisibility(View.INVISIBLE);
                }

            }
        };

        holder.videoPlayerView.addMediaPlayerListener(listener);


    }

    public static  void play(VideoPlayerManager playMnager,View v, VideoModel videoModel, @NonNull VideoViewHolder holder) {
        if (videoModel.getPath().startsWith("http")) {
            playMnager.playNewVideo(videoModel, holder.videoPlayerView, videoModel.getPath());
        } else {

            AssetManager assets = v.getContext().getAssets();

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {

                AssetFileDescriptor afd = null;
                try {
                    afd = assets.openFd(videoModel.getPath());
                    playMnager.playNewVideo(videoModel, holder.videoPlayerView,videoModel.getPath(), afd);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(v.getContext(), "手机版本过低", Toast.LENGTH_SHORT).show();
            }


        }
    }


    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    public void setPlayMnager(VideoPlayerManager<VideoModel> playMnager) {
        this.playMnager = playMnager;
    }
}
