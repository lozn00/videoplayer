package cn.qssq666.videoplayerdemo.fullscreen1;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

import cn.qssq666.videoplayerdemo.R;
import cn.qssq666.videoplayerdemo.VideoModel;

/**
 * Created by qssq on 2018/4/26 qssq666@foxmail.com
 */
public class StandViewListAdapter extends RecyclerView.Adapter<SimpleVideoViewViewHolder> {

    private static final String TAG = "StandViewListAdapter";
    private final PlayConfig playConfig;

    public StandViewListAdapter(PlayConfig playConfig) {
        this.playConfig = playConfig;
    }

    public List<VideoModel> getList() {
        return list;
    }

    public void setList(List<VideoModel> list) {
        this.list = list;
    }

    List<VideoModel> list;

    @NonNull
    @Override
    public SimpleVideoViewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_item_videoview, parent, false);
        return new SimpleVideoViewViewHolder(inflate);

    }

    @Override
    public void onBindViewHolder(@NonNull final SimpleVideoViewViewHolder holder, int position) {
        final VideoModel videoModel = list.get(position);
        holder.videoView.setVideoPath(videoModel.getPath());
        holder.videoView.getHolder().tvTitle.setText(videoModel.getName());
        ImageLoader.getInstance().displayImage(videoModel.getImage(), holder.videoView.getHolder().ivBg);
//        (videoModel.getImage());


    }


    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }


    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
    }

    @Override
    public void onViewAttachedToWindow(@NonNull SimpleVideoViewViewHolder holder) {
        super.onViewAttachedToWindow(holder);
    }

    @Override
    public void onViewDetachedFromWindow(@NonNull SimpleVideoViewViewHolder holder) {
        super.onViewDetachedFromWindow(holder);

        if (!playConfig.isAutoPlay()) {
            holder.videoView.destory();

            Log.w(TAG, "销毁了holder");
        }

    }
}
