package cn.qssq666.videoplayerdemo.fullscreen1;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.download.ImageDownloader;

import java.util.ArrayList;
import java.util.List;

import cn.qssq666.progressbar.HorizontalProgressBar;
import cn.qssq666.videoplayer.playermanager.meta.MetaData;
import cn.qssq666.videoplayer.playermanager.ui.MediaPlayerWrapper;
import cn.qssq666.videoplayerdemo.BuildConfig;
import cn.qssq666.videoplayerdemo.R;
import cn.qssq666.videoplayerdemo.VideoModel;

/**
 * Created by qssq on 2018/4/18 qssq666@foxmail.com
 */

public class FullScreenPageActivity extends AppCompatActivity {

    private static final String TAG = "FullScreenPageActivity";
    private RecyclerView recyclerView;
    private HorizontalProgressBar horizontalProgressBar;
    private StandViewListAdapter adapter;
    private PlayConfig playConfig;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.full_screen);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        horizontalProgressBar = (HorizontalProgressBar) findViewById(R.id.progress_bar);
        horizontalProgressBar.setProgressColor(Color.RED);
        ImageLoaderConfiguration configuration = ImageLoaderConfiguration

                .createDefault(this);
        ImageLoader.getInstance().init(configuration);


        StaticPlayerHelper.getInstance().setVideoStateListener(new MediaPlayerWrapper.VideoStateListener() {
            @Override
            public void onVideoPlayTimeChanged(int positionInMilliseconds) {
                horizontalProgressBar.setProgress((int) (StaticPlayerHelper.getInstance().getCurrentPlayer().getDuration() / positionInMilliseconds * 100f));
            }
        });

        playConfig = new PlayConfig();
        adapter = new StandViewListAdapter(playConfig);

        Toast.makeText(this, "翻页是否自动播放:" + playConfig.isAutoPlay(), Toast.LENGTH_SHORT).show();


        List<VideoModel> list = new ArrayList<>();
        list.add(new VideoModel().setImage("http://m9pic.mm999.com/topic/201804/20180427092549414.jpg").setPath("http://m9pic.mm999.com/video/201804/20180427092550022.mp4").setName("测试1"));
        list.add(new VideoModel().setImage(ImageDownloader.Scheme.DRAWABLE.wrap(R.drawable.videocover1 + "")).setPath("test1_new.mp4").setName("测试1"));
        list.add(new VideoModel().setImage(ImageDownloader.Scheme.DRAWABLE.wrap(R.drawable.videocover2 + "")).setPath("test2_new.mp4").setName("测试2"));
        list.add(new VideoModel().setImage(ImageDownloader.Scheme.DRAWABLE.wrap(R.drawable.videocover3 + "")).setPath("test3_new.mp4").setName("测试3"));
        list.add(new VideoModel().setImage(ImageDownloader.Scheme.DRAWABLE.wrap(R.drawable.videocover4 + "")).setPath("test4_new.mp4").setName("测试4"));
        list.add(new VideoModel().setImage(ImageDownloader.Scheme.DRAWABLE.wrap(R.drawable.videocover5 + "")).setPath("test5_new.mp4").setName("测试5"));
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
        });
        adapter.setList(list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapter);
        PagerSnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(recyclerView);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                int actualCurrentPosition = 0;
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    //第一次没法解决
                    autoPlayVideo(recyclerView);

                }

            }
        });
        recyclerView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                autoPlayVideo(recyclerView);
                recyclerView.removeOnLayoutChangeListener(this);
//                Log.w(TAG,"V"+left+",top:"+top+",right:"+right+",bottom:"+bottom+",oldLeft:"+oldLeft+",oldTop:"+oldTop+",oldRight:"+oldRight);

            }
        });

//        recyclerView.add

    }


    private void autoPlayVideo(RecyclerView view) {


        if (!playConfig.isAutoPlay()) {
            return;


        }

        RecyclerView.LayoutManager layoutManager = view.getLayoutManager();
        LinearLayoutManager linearLayoutManager = (LinearLayoutManager) layoutManager;
        int position = linearLayoutManager.findFirstCompletelyVisibleItemPosition();//完全可见


        MetaData metaData = new MetaData() {

        };


        if(position<0||position>=adapter.getList().size()){
            Log.w(TAG,"position 无法寻找:"+position);
            return;
        }else{
            Log.w(TAG,"position 正常:"+position);
        }



        VideoModel model1 = adapter.getList().get(position);
        if (StaticPlayerHelper.getInstance().isCurrent(model1.getPath())) {
            if (BuildConfig.DEBUG) {//无法判断资源

            }
            return;
        }

        SimpleVideoViewViewHolder viewHolder = (SimpleVideoViewViewHolder) view.findViewHolderForLayoutPosition(position);
        if (viewHolder == null) {
            Log.e(TAG, "无法选中position:" + position);
            return;
        }
        viewHolder.videoView.onSelectAndAutoPlay();//不过不做自动播放，那么当不可见的时候需要移除view.调用停止方法


    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        StaticPlayerHelper.releaseAllPlayer();

    }

}
