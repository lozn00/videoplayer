package cn.qssq666.videoplayerdemo.fullscreen1;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import cn.qssq666.videoplayerdemo.R;

/**
 * Created by qssq on 2018/4/27 qssq666@foxmail.com
 */
public class SimpleVideoViewViewHolder extends RecyclerView.ViewHolder {


    public final AutoPlayVideoView videoView;

    public SimpleVideoViewViewHolder(View itemView) {

        super(itemView);


        videoView = (AutoPlayVideoView) itemView.findViewById(R.id.auto_playview);


    }
}
