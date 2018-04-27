package cn.qssq666.videoplayerdemo;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.download.ImageDownloader;

import java.util.ArrayList;
import java.util.List;

import cn.qssq666.progressbar.HorizontalProgressBar;
import cn.qssq666.videoplayer.playermanager.manager.PlayerItemChangeListener;
import cn.qssq666.videoplayer.playermanager.manager.SingleVideoPlayerManager;
import cn.qssq666.videoplayer.playermanager.meta.MetaData;
import cn.qssq666.videoplayerdemo.fullscreen1.FullScreenPageActivity;

/**
 * Created by qssq on 2018/4/26 qssq666@foxmail.com
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "MainActivity";
    private SingleVideoPlayerManager<VideoModel> playerManager;
    private RecyclerView recyclerView;
    private HomeCustomAdapter adapter;
    private HorizontalProgressBar horizontalProgressBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        horizontalProgressBar = (HorizontalProgressBar) findViewById(R.id.progress_bar);
        horizontalProgressBar.setProgressColor(Color.RED);

        findViewById(R.id.btn_swtich).setOnClickListener(this);
        findViewById(R.id.btn_pause).setOnClickListener(this);
        findViewById(R.id.btn_continue).setOnClickListener(this);
        ImageLoaderConfiguration configuration = ImageLoaderConfiguration

                .createDefault(this);
        ImageLoader.getInstance().init(configuration);


        playerManager = new SingleVideoPlayerManager<VideoModel>(new PlayerItemChangeListener() {
            @Override
            public void onPlayerItemChanged(final MetaData currentItemMetaData) {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
//                Toast.makeText(MainActivity.this, "current:" + currentItemMetaData, Toast.LENGTH_SHORT).show();

                    }
                });

            }
        }) {

            @Override
            public void onVideoPlayTimeChanged(int positionInMilliseconds) {

                horizontalProgressBar.setProgress((int) (playerManager.getCurrentPlayer().getDuration() / positionInMilliseconds * 100f));
            }

            @Override

            public void onProgressUpdate(int percent) {
                super.onProgressUpdate(percent);
            }
        };
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        findViewById(R.id.btn_pause).setOnClickListener(this);
        findViewById(R.id.btn_continue).setOnClickListener(this);


        adapter = new HomeCustomAdapter();


        List<VideoModel> list = new ArrayList<>();
        list.add(new VideoModel().setImage("http://m9pic.mm999.com/topic/201804/20180427092549414.jpg").setPath("http://m9pic.mm999.com/video/201804/20180427092550022.mp4").setName("测试1"));
        list.add(new VideoModel().setImage(ImageDownloader.Scheme.DRAWABLE.wrap(R.drawable.videocover1 + "")).setPath("test1_new.mp4").setName("测试1"));
        list.add(new VideoModel().setImage(ImageDownloader.Scheme.DRAWABLE.wrap(R.drawable.videocover2 + "")).setPath("test2_new.mp4").setName("测试2"));
        list.add(new VideoModel().setImage(ImageDownloader.Scheme.DRAWABLE.wrap(R.drawable.videocover3 + "")).setPath("test3_new.mp4").setName("测试3"));
        list.add(new VideoModel().setImage(ImageDownloader.Scheme.DRAWABLE.wrap(R.drawable.videocover4 + "")).setPath("test4_new.mp4").setName("测试4"));
        list.add(new VideoModel().setImage(ImageDownloader.Scheme.DRAWABLE.wrap(R.drawable.videocover5 + "")).setPath("test5_new.mp4").setName("测试5"));
        adapter.setList(list);

        adapter.setPlayMnager(playerManager);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

//      PagingScrollHelper scrollHelper = new PagingScrollHelper();
//        scrollHelper.setUpRecycleView(recyclerView);
        //设置页面滚动监听
//        scrollHelper.setOnPageChangeListener(this);
       /* StartSnapHelper snapHelper = new StartSnapHelper();

        snapHelper.attachToRecyclerView(recyclerView);*/
        PagerSnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(recyclerView);
        recyclerView.setAdapter(adapter);

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
         Log.w(TAG,"V"+left+",top:"+top+",right:"+right+",bottom:"+bottom+",oldLeft:"+oldLeft+",oldTop:"+oldTop+",oldRight:"+oldRight);
                recyclerView.removeOnLayoutChangeListener(this);


            }
        });


    }


    private void autoPlayVideo(RecyclerView view) {


        RecyclerView.LayoutManager layoutManager = view.getLayoutManager();
        LinearLayoutManager linearLayoutManager = (LinearLayoutManager) layoutManager;
        int position = linearLayoutManager.findFirstCompletelyVisibleItemPosition();//完全可见


        if (position < 0 || position >= adapter.getList().size()) {
            Log.w(TAG, "position 无法寻找:" + position);
            return;
        } else {
            Log.w(TAG, "position 正常:" + position);
        }


        MetaData metaData = new MetaData() {

        };

        VideoModel model1 = adapter.getList().get(position);

        VideoViewHolder viewHolder = (VideoViewHolder) view.findViewHolderForLayoutPosition(position);
        if (viewHolder == null) {
            return;
        }

        if (playerManager.isCurrent(model1.getPath())) {
            if (playerManager.isPause()) {
                playerManager.continuePlay();

            } else if (playerManager.isPlay()) {
                playerManager.pause();

            } else {

                HomeCustomAdapter.play(playerManager, viewHolder.itemView, model1, viewHolder);

            }
        } else {

            HomeCustomAdapter.play(playerManager, viewHolder.itemView, model1, viewHolder);

        }

    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btn_continue:
                playerManager.onResume();

                break;
            case R.id.btn_pause:
                playerManager.onPause();
                break;

            case R.id.btn_swtich:
                Intent intent = new Intent(this, FullScreenPageActivity.class);
                startActivity(intent);
                break;
        }
    }

//        loginActivityBinding.videoView.setVideoURI(Uri.parse(Constants.VIDEO_PATH));
    // String VIDEO_PATH = "android.resource://" + BuildConfig.APPLICATION_ID + "/" + R.raw.login;


    @Override
    protected void onResume() {
        super.onResume();
        playerManager.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        playerManager.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        playerManager.destory();
    }

}
