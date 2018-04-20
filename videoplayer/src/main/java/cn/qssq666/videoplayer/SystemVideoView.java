package cn.qssq666.videoplayer;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.util.AttributeSet;
import android.widget.VideoView;

import cn.qssq666.videoplayer.util.MeasureUtil;

/**
 * Created by qssq on 2018/4/18 qssq666@foxmail.com
 */


/**
 * Created by qssq on 2017/7/10 qssq666@foxmail.com
 */

public class SystemVideoView extends VideoView {


    private int videoWidth;//width
    private int videoHeight;
    private int displayAspectRatio;

    public SystemVideoView(Context context) {
        super(context);
    }

    public SystemVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public SystemVideoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
//        setVisibility(INVISIBLE);
    }

    protected void init(Context context) {
        this.videoHeight = context.getResources().getDisplayMetrics().heightPixels;
        this.videoWidth = context.getResources().getDisplayMetrics().widthPixels;

        super.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(final MediaPlayer mp) {

//                setVisibility(VISIBLE);
                SystemVideoView.this.videoWidth = mp.getVideoWidth();
                SystemVideoView.this.videoHeight = mp.getVideoHeight();
                mp.setOnInfoListener(new MediaPlayer.OnInfoListener() {
                    @Override
                    public boolean onInfo(MediaPlayer mp, int what, int extra) {
                        if (what == MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START) {
                            if (onCorveHideListener != null) {
                                onCorveHideListener.requestHide();
                            }
                        }
                        if (onInfoListener != null) {
                            onInfoListener.onInfo(mp, what, extra);
                        }
                        return false;
                    }
                });
            }
        });
    }

    MediaPlayer.OnPreparedListener onPreparedListener = null;

    public interface OnCorveHideListener {
        void requestHide();
    }

    @Override
    public void setOnInfoListener(MediaPlayer.OnInfoListener onInfoListener) {
        this.onInfoListener = onInfoListener;
    }

    MediaPlayer.OnInfoListener onInfoListener;

    public void setOnCorveHideListener(OnCorveHideListener onCorveHideListener) {
        this.onCorveHideListener = onCorveHideListener;
    }

    OnCorveHideListener onCorveHideListener;

    @Override
    public void setOnPreparedListener(MediaPlayer.OnPreparedListener l) {
        this.onPreparedListener = l;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        MeasureUtil.Size measure = MeasureUtil.measure(displayAspectRatio, widthMeasureSpec, heightMeasureSpec, videoWidth, videoHeight);
        setMeasuredDimension(measure.width, measure.height);
    }

       /* int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        int currentHeight = Math.max(height, videoHeight);
        int currentWidth = currentHeight * 9 / 16;
        setMeasuredDimension(currentWidth, currentHeight);*/


//        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
//        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
//        setMeasuredDimension(width, width * 9 / 16);
     /*   int var7 = MeasureSpec.getMode(var1);
        int var8 = MeasureSpec.getSize(var1);
        int var9 = MeasureSpec.getMode(var2);*/


    /*   protected void onMeasure(int var1, int var2) {
//        com.pili.pldroid.player.common.a.a var3 = com.pili.pldroid.player.common.a.a(SystemVideoView.this.getDisplayAspectRatio(), var1, var2, this.videoWidth, this.videoHeight);
//        this.setMeasuredDimension(var3.a, var3.b);
        int var7 = MeasureSpec.getMode(var1);
        int var8 = MeasureSpec.getSize(var1);
        int var9 = MeasureSpec.getMode(var2);
    }*/


    public void setDisplayAspectRatio(int var1) {
        displayAspectRatio = var1;
        this.requestLayout();

    }


    @Override
    public boolean isPlaying() {
        return false;
    }

    public int getDisplayAspectRatio() {
        return displayAspectRatio;
    }

    public void setCorver(int resource) {
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(getResources(), resource, opts);

    }
}